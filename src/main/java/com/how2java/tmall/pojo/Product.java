package com.how2java.tmall.pojo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "product")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })

/**
 * 商品
 */
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    //商品名称
    private String name;
    //商品标题
    private String subTitle;
    //原价格
    private float originalPrice;
    //促销价格
    private float promotePrice;
    //库存
    private int stock;
    @ManyToOne
    @JoinColumn(name = "cid")
    private Category category;
    //日期
    private Date createDAte;

    @Transient
    private ProductImage firstProductImage;

    @Transient
    //商品图片集合
    private List<ProductImage> productSingleImages;
    @Transient
    //商品详情图片集合
    private List<ProductImage> productDetailImages;
    @Transient
    //商品销量
    private int reviewCount;
    @Transient
    //累计评价
    private int saleCount;

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", originalPrice=" + originalPrice +
                ", promotePrice=" + promotePrice +
                ", stock=" + stock +
                ", category=" + category +
                ", createDAte=" + createDAte +
                '}';
    }
}
