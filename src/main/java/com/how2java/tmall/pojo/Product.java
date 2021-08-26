package com.how2java.tmall.pojo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "product")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })
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
}
