package com.how2java.tmall.web;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.util.ImageUtil;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
public class CategoryController {
	@Autowired CategoryService categoryService;

	@GetMapping("/categories")
	public Page4Navigator<Category> list(@RequestParam(value = "start", defaultValue = "0") int start,
										 @RequestParam(value = "size", defaultValue = "5") int size)
			throws Exception {
		start = start<0?0:start;
		Page4Navigator<Category> page =categoryService.list(start, size, 5);  //5表示导航分页最多有5个，像 [1,2,3,4,5] 这样
		return page;
	}


//HttpServletRequest对象代表客户端的请求，当客户端通过HTTP协议访问服务器时，
// HTTP请求头中的所有信息都封装在这个对象中，通过这个对象提供的方法，可以获得客户端请求的所有信息。

	@PostMapping("/categories")
	public Object add(Category bean, MultipartFile image, HttpServletRequest request) throws Exception {
		categoryService.add(bean);
		saveOrUpdateImageFile(bean, image, request);
		return bean;
	}
	//MultipartFile这个类一般是用来接受前台传过来的文件
	public void saveOrUpdateImageFile(Category bean, MultipartFile image, HttpServletRequest request)
			throws IOException {
		//根据资源虚拟路径，返回实际路径。通过路径定位到img/category
		File imageFolder= new File(request.getServletContext().getRealPath("img/category"));
		//根据ID创造图片
		File file = new File(imageFolder,bean.getId()+".jpg");

		//测试路径是否存在
		if(!file.getParentFile().exists())
			// 创建由此抽象路径名命名的目录，包括任何必需但不存在的父目录。
			file.getParentFile().mkdirs();

		//将浏览器传来的文件转为file文件
		image.transferTo(file);
		BufferedImage img = ImageUtil.change2jpg(file);
		ImageIO.write(img, "jpg", file);
	}

	@DeleteMapping("/categories/{id}")
	public String delete(@PathVariable("id") int id, HttpServletRequest request)  throws Exception {
		categoryService.delete(id);
		File  imageFolder= new File(request.getServletContext().getRealPath("img/category"));
		File file = new File(imageFolder,id+".jpg");
		file.delete();

		//返回后会变成长度为0的字符串，通过这个来判断删除是否成功
		return null;
	}

	@GetMapping("/categories/{id}")
	public Category get(@PathVariable("id") int id) throws Exception {
		Category bean=categoryService.get(id);
		return bean;
	}

	@PutMapping("/categories/{id}")
	public Object update(Category bean, MultipartFile image,HttpServletRequest request) throws Exception {
		//获取请求体中参数
		String name = request.getParameter("name");
		bean.setName(name);
		categoryService.update(bean);

		if(image!=null) {
			saveOrUpdateImageFile(bean, image, request);
		}
		return bean;
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

