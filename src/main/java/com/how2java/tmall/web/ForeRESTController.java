package com.how2java.tmall.web;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.how2java.tmall.pojo.*;
import com.how2java.tmall.service.*;
import com.how2java.tmall.util.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.util.*;

@RestController

public class ForeRESTController {
    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    UserService userService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    OrderService orderService;

    @GetMapping("/forehome")
    //1. 查询所有分类
    //2. 为这些分类填充产品集合
    //3. 为这些分类填充推荐产品集合
    //4. 移除产品里的分类信息，以免出现重复递归
    public Object home(){
        List<Category> categories = categoryService.list();
        productService.fill(categories);
        productService.fillByRow(categories);
        categoryService.removeCategoryFromProduct(categories);
        return categories;
    }

    @PostMapping("/foreregister")
    public Result register(@RequestBody User user){
        String name = user.getName();
        //通过HtmlUtils.htmlEscape(name)，把账号里的特殊符号进行转义,防止恶意注册
        name = HtmlUtils.htmlEscape(name);
        user.setName(name);

        if (userService.isExist(name)){
            return Result.fail("存在同名用户");
        }

        userService.add(user);

        return Result.success();

    }

    @PostMapping("/forelogin")
    public Object login(@RequestBody User user, HttpSession session){
        //通过HtmlUtils.htmlEscape(name)，注册时转义了特殊符号
        user.setName(HtmlUtils.htmlEscape(user.getName()));
        User bean = userService.login(user.getName(), user.getPassWord());
        if (bean ==null){
            return Result.fail("用户名或密码错误");
        }

            session.setAttribute("user", bean);
            return Result.success();
    }

    @GetMapping("/foreproduct/{pid}")
    public Object product(@PathVariable int pid){

        Product product = productService.get(pid);

        List<ProductImage> productSingleImages = productImageService.listSingleProductImages(product);
        List<ProductImage> productDetailImages = productImageService.listDetailProductImages(product);
        product.setProductSingleImages(productSingleImages);
        product.setProductDetailImages(productDetailImages);
        //获取产品的所有属性值
        List<PropertyValue> pvs = propertyValueService.list(product);
        //获取产品对应的所有的评价
        List<Review> reviews = reviewService.list(product);
        //设置产品的销量和评价数量
        productService.setSaleAndReviewNumber(product);
        productImageService.setFirstProdutImage(product);

        Map<String,Object> map= new HashMap<>();
        map.put("product", product);
        map.put("pvs", pvs);
        map.put("reviews", reviews);

        return Result.success(map);


    }

    @GetMapping("forecheckLogin")
    public Result checklogin(HttpSession session){
        if (session.getAttribute("user")==null){
            return Result.fail("未登录");
        }
        return Result.success();

    }

    @GetMapping("forecategory/{cid}")
    public Category categoryPage(@PathVariable int cid){

        Category category = categoryService.get(cid);
        List<Product> products = productService.listByCategory(category);
        productService.fill(category);
        productService.setSaleAndReviewNumber(products);
        categoryService.removeCategoryFromProduct(category);
        return category;
    }
//搜索
    @PostMapping("foresearch")
    public Object search(String keyword){
        if(null==keyword)
            keyword = "";
        List<Product> ps= productService.search(keyword,0,20);
        productImageService.setFirstProdutImages(ps);
        productService.setSaleAndReviewNumber(ps);
        return ps;
    }
    //点击购买
    @GetMapping("forebuyone")
    public int buyone(int pid, int num, HttpSession session) {
        return buyoneAndAddCart(pid,num,session);
    }
    //加入购物车
    @GetMapping("foreaddCart")
    public Object addCart(int pid, int num, HttpSession session) {
        buyoneAndAddCart(pid,num,session);
        return Result.success();
    }
    //    返回当前订单项id
    private int buyoneAndAddCart(int pid, int num, HttpSession session) {
        Product product = productService.get(pid);
        int orderItemID = 0;
        User user = (User) session.getAttribute("user");
        List<OrderItem> orderItems = orderItemService.listByUser(user);
        //该用户是否存在该商品的订单项
        boolean isOrderItems = false;
        for (OrderItem o:orderItems){
            if (o.getProduct().getId() == product.getId()){
                //订单项中商品数量增加，并更新数据
                o.setNumber(o.getNumber()+num);
                orderItemService.update(o);
                orderItemID = o.getId();
                isOrderItems = true;
                break;
            }
        }
        if (!isOrderItems){
            OrderItem orderItem = new OrderItem();
            orderItem.setUser(user);
            orderItem.setNumber(num);
            orderItem.setProduct(product);
            orderItemService.add(orderItem);
            orderItemID = orderItem.getId();
        }
        return orderItemID;
    }

    //订单项页面
    @GetMapping("forebuy")
    public Result buy(int[] oiid, HttpSession session){

        float totalPrice = 0;
        ArrayList<OrderItem> orderItems = new ArrayList<>();

        for (int oi:oiid){
            OrderItem orderItem = orderItemService.get(oi);
            orderItems.add(orderItem);
            totalPrice += orderItem.getProduct().getPromotePrice()*orderItem.getNumber();
        }
        productImageService.setFirstProdutImagesOnOrderItems(orderItems);

        session.setAttribute("ois",orderItems);
        HashMap<String, Object> map = new HashMap<>();
        map.put("orderItems",orderItems);
        map.put("total",totalPrice);

        return Result.success(map);

    }

    //购物车
    @GetMapping("forecart")
    public List<OrderItem> cart(HttpSession session){
        User user = (User) session.getAttribute("user");
        List<OrderItem> ois = orderItemService.listByUser(user);
        productImageService.setFirstProdutImagesOnOrderItems(ois);
        return ois;
    }

    @GetMapping("forechangeOrderItem")
    public Result changeOrderItem(int pid ,int num,HttpSession session){
        User user = (User) session.getAttribute("user");
        if (user!=null){
            List<OrderItem> orderItems = orderItemService.listByUser(user);
            for (OrderItem oi:orderItems){
                if (oi.getProduct().getId()==pid){
                    oi.setNumber(num);
                    orderItemService.update(oi);
                    break;
                }
            }
            return Result.success();
        }
        return Result.fail("未登录");
    }
    @GetMapping("foredeleteOrderItem")
    public Object deleteOrderItem(HttpSession session,int oiid){
        User user =(User)  session.getAttribute("user");
        if(null==user){
            return Result.fail("未登录");
        }
        orderItemService.delete(oiid);
        return Result.success();
    }


    @PostMapping("/forecreateOrder")
    public Result createOrder(@RequestBody Order order,HttpSession session){

        User user = (User) session.getAttribute("user");
        List<OrderItem> orderItems = (List<OrderItem>) session.getAttribute("ois");

        Date dateNow = DateUtil.date(System.currentTimeMillis());
        String ordercode = DateUtil.format(dateNow,"yyyyMMddHHmmss")+RandomUtil.randomNumbers(4);

        order.setOrderCode(ordercode);
        order.setCreateDate(DateUtil.parse(DateUtil.now()));
        order.setUser(user);
        order.setStatus(OrderService.waitPay);

        float total = orderService.add(order, orderItems);
        Map<String, Object> map = new HashMap<>();
        map.put("total",total);
        map.put("oid",order.getId());

        return Result.success(map);
    }

    @GetMapping("forepayed")
    public Object payed(int oid) {

        Order order = orderService.get(oid);

        System.out.println( order.getStatus());
        if (!order.getStatus().equals("waitPay")){
            return Result.fail("订单状态错误");
        }
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        return order;
    }

    @GetMapping("forebought")
    public Object list(HttpSession session){
        User user = (User) session.getAttribute("user");
        if (user == null){return Result.fail("未登录");}

        List<Order> os = orderService.listByUserAndNotDeleted(user);


        orderItemService.fill(os);
        //防止序列化时无限递归
        orderService.removeOrderFromOrderItem(os);
        return Result.success(os);
    }

    @GetMapping("foreconfirmPay")
    public Object confirmPay(int oid) {
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        orderService.cacl(o);
        orderService.removeOrderFromOrderItem(o);
        return o;
    }
//确认支付
    @GetMapping("foreorderConfirmed")
    public Object orderConfirmed( int oid) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.waitReview);
        o.setConfirmDate(new Date());
        orderService.update(o);
        return Result.success();
    }
//删除订单（实为更改订单状态为删除）
    @PutMapping("foredeleteOrder")
    public Object deleteOrder(int oid){
        Order o = orderService.get(oid);
        o.setStatus(OrderService.delete);
        orderService.update(o);
        return Result.success();
    }
}
