package com.how2java.tmall.service;

import com.how2java.tmall.dao.PropertyDAO;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Property;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;


@Service
public class PropertyService {
    @Autowired
    PropertyDAO propertyDAO;
    @Autowired
    CategoryService categoryService;

    public void add(Property bean){

       propertyDAO.save(bean);
    }

    public void delete(int id){
        propertyDAO.delete(id);
    }

    public Property get(int id){

        return  propertyDAO.findOne(id);
    }

    public void update(Property bean){

         propertyDAO.save(bean);
    }

    public Page4Navigator<Property> list(int cid, int start ,int size,int navigatePages){
        Category category = categoryService.get(cid);
        Pageable pageable = new PageRequest(start,size,new Sort(Sort.Direction.DESC,"id"));
        Page<Property> page = propertyDAO.findByCategory(category,pageable);
        return new Page4Navigator<>(page,navigatePages);
    }
}

