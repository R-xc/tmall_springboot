package com.how2java.tmall.service;

import com.how2java.tmall.dao.ProductDAO;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    @Autowired
    ProductDAO productDAO;
    @Autowired
    CategoryService categoryService;

    public void add(Product bean){
        productDAO.save(bean);
    }

    public void Delete(int id){
        productDAO.delete(id);
    }

    public void update(Product bean){
        productDAO.save(bean);
    }
    public Product get(int id){
        return productDAO.findOne(id);
    }

    public Page4Navigator list(int cid,int start,int size,int navigatePages){

        Pageable pageable =new PageRequest(start,size,new Sort(Sort.Direction.ASC,"id"));

        Page page = productDAO.findByCategory(categoryService.get(cid), pageable);

        Page4Navigator page4Navigator = new Page4Navigator(page,navigatePages);
        return page4Navigator;
    }
}
