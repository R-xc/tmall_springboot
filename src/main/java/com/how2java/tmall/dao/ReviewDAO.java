package com.how2java.tmall.dao;


import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewDAO extends JpaRepository<Review,Integer> {
    //返回评价内容集合
    List<Review> findByProductOrderByIdDesc(Product product);
    //返回评价数量
    int countByProduct(Product product);

}
