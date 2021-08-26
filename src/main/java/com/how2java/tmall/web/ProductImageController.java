package com.how2java.tmall.web;

import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.ProductImage;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.service.ProductImageService;
import com.how2java.tmall.service.ProductService;
import com.how2java.tmall.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductImageController {
    @Autowired
    ProductService productService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    CategoryService categoryService;


    @GetMapping("/product/{pid}/productImages")
    public List<ProductImage> list(@RequestParam("type") String type, @PathVariable("pid") int pid)
            throws Exception {


        Product product = productService.get(pid);

        if(ProductImageService.type_single.equals(type)) {
            List<ProductImage> singles =  productImageService.listSingleProductImages(product);
            return singles;
        }

        else if(ProductImageService.type_detail.equals(type)) {
            List<ProductImage> details =  productImageService.listDetailProductImages(product);
            return details;
        }

        else {
            return new ArrayList<>();
        }
    }

    @PostMapping("/productImages")
    public ProductImage add(@RequestParam("pid") int pid, @RequestParam("type") String type,
                    MultipartFile image, HttpServletRequest request)
    throws Exception{

        ProductImage bean = new ProductImage();
        Product product =productService.get(pid);

        bean.setProduct(product);
        bean.setType(type);

        productImageService.add(bean);
        //定位到指定文件路径
        String folder = "img/";
        if(ProductImageService.type_single.equals(bean.getType())){
            folder +="productSingle";
        }
        else{
            folder +="productDetail";
        }
        //通过将给定的路径名字符串转换为抽象路径名来创建新的 File实例。
        File imageFolder= new File(request.getServletContext().getRealPath(folder));

        File file = new File(imageFolder,bean.getId()+".jpg");
        String fileName = file.getName();
        //测试此抽象路径名表示的文件或目录是否存在。
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        try {
            //将前端传来的文件保存到file文件中
            image.transferTo(file);
            //将图片转为jpg格式
            BufferedImage img = ImageUtil.change2jpg(file);
            //使用支持给定格式的任意 ImageWriter写入图像到 File 。
            ImageIO.write(img, "jpg", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
            //转换图片大小
        if(ProductImageService.type_single.equals(bean.getType())){
            String imageFolder_small= request.getServletContext().getRealPath("img/productSingle_small");
            String imageFolder_middle= request.getServletContext().getRealPath("img/productSingle_middle");

            File f_small = new File(imageFolder_small, fileName);
            File f_middle = new File(imageFolder_middle, fileName);
            f_small.getParentFile().mkdirs();
            f_middle.getParentFile().mkdirs();
            ImageUtil.resizeImage(file, 56, 56, f_small);
            ImageUtil.resizeImage(file, 217, 190, f_middle);
        }

        return bean;
    }
    @DeleteMapping("/productImages/{id}")
    public String delete(@PathVariable("id") int id, HttpServletRequest request)  throws Exception {
        ProductImage bean = productImageService.get(id);
        productImageService.delete(id);

        String folder = "img/";
        if(ProductImageService.type_single.equals(bean.getType()))
            folder +="productSingle";
        else
            folder +="productDetail";

        File  imageFolder= new File(request.getServletContext().getRealPath(folder));
        File file = new File(imageFolder,bean.getId()+".jpg");
        String fileName = file.getName();
        file.delete();
        if(ProductImageService.type_single.equals(bean.getType())){
            String imageFolder_small= request.getServletContext().getRealPath("img/productSingle_small");
            String imageFolder_middle= request.getServletContext().getRealPath("img/productSingle_middle");
            File f_small = new File(imageFolder_small, fileName);
            File f_middle = new File(imageFolder_middle, fileName);
            f_small.delete();
            f_middle.delete();
        }

        return null;
    }

}
