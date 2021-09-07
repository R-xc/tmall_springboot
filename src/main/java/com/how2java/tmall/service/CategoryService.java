package com.how2java.tmall.service;

import java.util.List;

import com.how2java.tmall.pojo.Product;
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

import com.how2java.tmall.dao.CategoryDAO;
import com.how2java.tmall.pojo.Category;


@Service
@CacheConfig(cacheNames = "categorys")
public class CategoryService {
	@Autowired CategoryDAO categoryDAO;


	//分页
	@Cacheable(key = "'page'+#start+'-'+#size")
	public Page4Navigator<Category> list(int start, int size, int navigatePages) {
		//Pageable 是 Spring 封装的分页实现类，使用的时候需要传入页数start、每页条数size和排序规则sort。
		Sort sort = new Sort(Sort.Direction.DESC, "id");
		Pageable pageable = new PageRequest(start, size,sort);
		//Page<User> findALL(Pageable pageable); 在查询的方法中，需要传入参数Pageable
		Page pageFromJPA =categoryDAO.findAll(pageable);

		return new Page4Navigator<>(pageFromJPA,navigatePages);
	}

	//查询
	@Cacheable(key = "#root.methodName")
	public List<Category> list() {
    	Sort sort = new Sort(Sort.Direction.DESC, "id");
		return categoryDAO.findAll(sort);
	}

	//添加
	@CacheEvict(allEntries = true)
	public void add(Category bean) {
		categoryDAO.save(bean);
	}

	@CacheEvict(allEntries = true)
	public void delete(int id) {
		categoryDAO.delete(id);
	}

	@Cacheable(key = "#id")
	public Category get(int id) {
		Category c= categoryDAO.findOne(id);
		return c;
	}

	@CacheEvict(allEntries = true)
	public void update(Category bean) {
		categoryDAO.save(bean);
	}

	public void removeCategoryFromProduct(List<Category> cs) {
		for (Category category : cs) {
			removeCategoryFromProduct(category);
		}
	}

	public void removeCategoryFromProduct(Category category) {
		List<Product> products =category.getProducts();
		if(null!=products) {
			for (Product product : products) {
				product.setCategory(null);
			}
		}

		List<List<Product>> productsByRow =category.getProductsByRow();
		if(null!=productsByRow) {
			for (List<Product> ps : productsByRow) {
				for (Product p: ps) {
					p.setCategory(null);
				}
			}
		}
	}
}
