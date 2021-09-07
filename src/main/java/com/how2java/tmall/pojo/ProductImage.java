package com.how2java.tmall.pojo;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name ="productImage")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })
public class ProductImage implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String type;

    @ManyToOne
    @JoinColumn(name = "pid")
    @JsonBackReference//防止json序列化ProductImage和Product时死循环
    private Product product;

}
