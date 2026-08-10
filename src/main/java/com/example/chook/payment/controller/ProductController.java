package com.example.chook.payment.controller;

import com.example.chook.payment.entity.Product;
import com.example.chook.payment.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/products")
    public List<Product> getProducts() {
        return productRepository.findAll();
    }
}