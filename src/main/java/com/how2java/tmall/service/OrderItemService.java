package com.how2java.tmall.service;

import com.how2java.tmall.dao.OrderItemDAO;
import com.how2java.tmall.pojo.Order;
import com.how2java.tmall.pojo.OrderItem;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.User;
import com.how2java.tmall.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.List;

@Service
@CacheConfig(cacheNames = "orderItems")
public class OrderItemService {
    @Autowired
    OrderItemDAO orderItemDAO;
    @Autowired
    ProductImageService productImageService;


    @CacheEvict(allEntries = true)
//    @Caching(put = {@CachePut(key = "#p0.id"),@CachePut(key = "#p0.user"),@CachePut(key = "#p0.product"),@CachePut(key = "#p0.order")})
    public void add(OrderItem orderItem){
        orderItemDAO.save(orderItem);
    }
    @CacheEvict(allEntries = true)
//    @Caching(put = {@CachePut(key = "#p0.id"),@CachePut(key = "#p0.user"),@CachePut(key = "#p0.product"),@CachePut(key = "#p0.order")})
    public void update(OrderItem bean) {
        orderItemDAO.save(bean);
    }


    public void fill(List<Order> orders) {
        for (Order order : orders)
            fill(order);
    }

    @Cacheable(key = "#id")
    public OrderItem get(int id){
      return  orderItemDAO.getOne(id);
    }

    public void fill(Order order) {
        // 从fill方法里直接调用 listByCategory 方法， aop拦截不到，不会走缓存。故使用工具类调用orderItemService。
        OrderItemService orderItemService = SpringContextUtil.getBean(OrderItemService.class);
        List<OrderItem> orderItems = orderItemService.listByOrder(order);
        float total = 0;//商品总价格
        int totalNumber = 0;
        for (OrderItem oi :orderItems) {
            total+=oi.getNumber()*oi.getProduct().getPromotePrice();//数量*商品价格
            totalNumber+=oi.getNumber();
            productImageService.setFirstProdutImage(oi.getProduct());
        }
        order.setTotal(total);
        order.setOrderItems(orderItems);
        order.setTotalNumber(totalNumber); //订单数量
    }

    @Cacheable(key = "#order")
    public List<OrderItem> listByOrder(Order order) {
        return orderItemDAO.findByOrderOrderByIdDesc(order);
    }


    public int getSaleCount(Product product) {
        OrderItemService orderItemService = SpringContextUtil.getBean(OrderItemService.class);
        List<OrderItem> ois = orderItemService.listByProduct(product);
        int result =0;
        for (OrderItem oi : ois) {
            if(null!=oi.getOrder())
                if(null!= oi.getOrder() && null!=oi.getOrder().getPayDate())
                    result+=oi.getNumber();
        }
        return result;
    }

    @Cacheable(key = "#product")
    public List<OrderItem> listByProduct(Product product) {
       return orderItemDAO.findByProduct(product);
    }

    //根据用户获取订单项
    @Cacheable(key = "#user")
    public List<OrderItem> listByUser(User user){
       return orderItemDAO.findByUserAndOrderIsNull(user);
    }

    @CacheEvict(allEntries = true)
    public void delete(int oiid) {
        orderItemDAO.delete(oiid);
    }

}
