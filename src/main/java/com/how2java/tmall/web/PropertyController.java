package com.how2java.tmall.web;

import com.how2java.tmall.pojo.Property;
import com.how2java.tmall.service.PropertyService;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class PropertyController {
    @Autowired
    PropertyService propertyService;

    @GetMapping("/categories/{cid}/properties")
    public Page4Navigator<Property> getPage(@PathVariable("cid")int cid,
                                  @RequestParam(value = "start", defaultValue = "0") int start,
                                  @RequestParam(value = "size", defaultValue = "5") int size)
            throws Exception{
        Page4Navigator<Property> page = propertyService.list(cid,start,size,5);
        return page;
    }


    @PostMapping("/property")
    public Property add(@RequestBody Property bean){
      propertyService.add(bean);
      return bean;
    }

    @GetMapping("/properties/{id}")
    public Property get(@PathVariable("id") int id) throws Exception {
        Property bean= propertyService.get(id);

        return bean;
    }

    @PutMapping("/properties")
    public Object update(@RequestBody Property bean) throws Exception {
        propertyService.update(bean);
        return bean;
    }

    @DeleteMapping("/properties/{id}")
    public String delete(@PathVariable("id") int id, HttpServletRequest request)  throws Exception {
        propertyService.delete(id);
        return null;
    }

}