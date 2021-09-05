package com.how2java.tmall.service;

import com.how2java.tmall.dao.OrderItemDAO;
import com.how2java.tmall.pojo.Order;
import com.how2java.tmall.pojo.OrderItem;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {
    @Autowired
    OrderItemDAO orderItemDAO;
    @Autowired
    ProductImageService productImageService;

    public void add(OrderItem orderItem){
        orderItemDAO.save(orderItem);
    }

    public void update(OrderItem bean) {
        orderItemDAO.save(bean);
    }

    public void fill(List<Order> orders) {
        for (Order order : orders)
            fill(order);
    }
    public OrderItem get(int id){
      return  orderItemDAO.getOne(id);
    }

    public void fill(Order order) {
        List<OrderItem> orderItems = listByOrder(order);
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

    public List<OrderItem> listByOrder(Order order) {
        return orderItemDAO.findByOrderOrderByIdDesc(order);
    }


    public int getSaleCount(Product product) {
        List<OrderItem> ois =listByProduct(product);
        int result =0;
        for (OrderItem oi : ois) {
            if(null!=oi.getOrder())
                if(null!= oi.getOrder() && null!=oi.getOrder().getPayDate())
                    result+=oi.getNumber();
        }
        return result;
    }

    private List<OrderItem> listByProduct(Product product) {
       return orderItemDAO.findByProduct(product);
    }

    //根据用户获取订单项
    public List<OrderItem> listByUser(User user){
       return orderItemDAO.findByUserAndOrderIsNull(user);
    }

    public void delete(int oiid) {
        orderItemDAO.delete(oiid);
    }
}
