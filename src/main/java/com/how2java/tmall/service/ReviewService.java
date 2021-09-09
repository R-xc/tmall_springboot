package com.how2java.tmall.service;

import com.how2java.tmall.dao.ReviewDAO;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "review")
public class ReviewService {
    @Autowired
    ReviewDAO reviewDAO;
    @CacheEvict(allEntries = true)
    public void add(Review review){
        reviewDAO.save(review);
    }

    @Cacheable(key = "'Reviews'+#p0")
    public List<Review> list(Product product){
        return reviewDAO.findByProductOrderByIdDesc(product);
    }

    @Cacheable(key = "'ReviewsCounts'+#p0")
    public int getCount(Product product){
        return reviewDAO.countByProduct(product);
    }
}
