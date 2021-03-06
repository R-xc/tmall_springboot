package com.how2java.tmall.service;

import com.how2java.tmall.dao.ProductDAO;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.util.Page4Navigator;
import com.how2java.tmall.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(cacheNames = "products")
public class ProductService {
    @Autowired
    ProductDAO productDAO;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;

    @CacheEvict(allEntries = true)
    public void add(Product bean){
        productDAO.save(bean);
    }
    @CacheEvict(allEntries = true)
    public void Delete(int id){
        productDAO.delete(id);
    }
    @CacheEvict(allEntries = true)
    public void update(Product bean){
        productDAO.save(bean);
    }

    @Cacheable(key = "#p0")
    public Product get(int id){
        Product product = productDAO.findOne(id);
        return product;
    }

    @Cacheable(key= "'page'+#p1+'-'+#p2")
    public Page4Navigator list(int cid,int start,int size,int navigatePages){

        Pageable pageable =new PageRequest(start,size,new Sort(Sort.Direction.ASC,"id"));

        Page page = productDAO.findByCategory(categoryService.get(cid), pageable);

        Page4Navigator page4Navigator = new Page4Navigator(page,navigatePages);

        return page4Navigator;
    }


    public void fill(List<Category> categorys) {
        for (Category category : categorys) {
            fill(category);
        }
    }

    public void fill(Category category) {
        ProductService productService = SpringContextUtil.getBean(ProductService.class);
        List<Product> products = productService.listByCategory(category);
        productImageService.setFirstProdutImages(products);
        category.setProducts(products);
    }

    public void fillByRow(List<Category> categorys) {
        int productNumberEachRow = 8;
        for (Category category : categorys) {
            List<Product> products =  category.getProducts();
            List<List<Product>> productsByRow =  new ArrayList<>();

            for (int i = 0; i < products.size(); i+=productNumberEachRow) {
                int size = i+productNumberEachRow;
                size= size>products.size()?products.size():size;
                List<Product> productsOfEachRow =products.subList(i, size);
                productsByRow.add(productsOfEachRow);
            }
            category.setProductsByRow(productsByRow);
        }
    }
    //??????????????????????????????
    @Cacheable(key = "#p0")
    public List<Product> listByCategory(Category category){
        return productDAO.findByCategoryOrderById(category);
    }


    //???????????????????????????
    public void setSaleAndReviewNumber(Product product) {
        int saleCount = orderItemService.getSaleCount(product);
        product.setSaleCount(saleCount);

        int reviewCount = reviewService.getCount(product);
        product.setReviewCount(reviewCount);

    }

    public void setSaleAndReviewNumber(List<Product> products) {
        for (Product product : products)
            setSaleAndReviewNumber(product);
    }

    @Cacheable(key = "'search='+#p0")
    public List<Product> search(String search,int start,int size){

        PageRequest pageRequest = new PageRequest(start, size, new Sort(Sort.Direction.ASC,"id"));
        return productDAO.findByNameLike("%"+search+"%",pageRequest);
    }
}
