package com.how2java.tmall.web;

import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.service.ProductImageService;
import com.how2java.tmall.service.ProductService;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {
    @Autowired
    ProductService productService;
    @Autowired
    ProductImageService productImageService;

    @PostMapping("/product")
    public Product add(@RequestBody Product bean) throws Exception{
        productService.add(bean);
        return bean;
    }
    @PutMapping("/product")
    public Product update(@RequestBody Product bean) throws Exception{
        productService.update(bean);
        return bean;
    }

    @GetMapping("/categories/{cid}/product")
    public Page4Navigator<Product> getPage(@PathVariable(name = "cid") int cid,
                                           @RequestParam(name = "start",defaultValue="0") int start,
                                           @RequestParam(name = "size",defaultValue = "5") int size

    )throws Exception{
        Page4Navigator page = productService.list(cid, start, size, 5);
        productImageService.setFirstProdutImages(page.getContent());
        return page;
    }

    @DeleteMapping("/product/{id}")
    public String delete(@PathVariable int id)throws Exception{
        productService.Delete(id);
        return null;
    }

    @GetMapping("/product/{id}")
    public Product get(@PathVariable int id)throws Exception{

        return productService.get(id);
    }
}
