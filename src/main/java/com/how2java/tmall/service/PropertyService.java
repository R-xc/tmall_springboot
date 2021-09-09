package com.how2java.tmall.service;

import com.how2java.tmall.dao.PropertyDAO;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Property;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;


@Service
@CacheConfig(cacheNames = "property")
public class PropertyService {
    @Autowired
    PropertyDAO propertyDAO;
    @Autowired
    CategoryService categoryService;

    @CacheEvict(allEntries = true)
    public void add(Property bean){

       propertyDAO.save(bean);
    }
    @CacheEvict(allEntries = true)
    public void delete(int id){
        propertyDAO.delete(id);
    }

    @Cacheable(key = "#p0")
    public Property get(int id){

        return  propertyDAO.findOne(id);
    }
    @CacheEvict(allEntries = true)
    public void update(Property bean){

         propertyDAO.save(bean);
    }
    @Cacheable(key= "'page'+#p1+'-'+#p2")
    public Page4Navigator<Property> list(int cid, int start ,int size,int navigatePages){
        Category category = categoryService.get(cid);
        Pageable pageable = new PageRequest(start,size,new Sort(Sort.Direction.DESC,"id"));
        Page<Property> page = propertyDAO.findByCategory(category,pageable);
        return new Page4Navigator<>(page,navigatePages);
    }
    @Cacheable(key = "#p0")
    public List<Property> listByCategory(Category category){
        return propertyDAO.findByCategory(category);
    }
}

