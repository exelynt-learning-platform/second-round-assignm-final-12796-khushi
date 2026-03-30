package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService service;

    // ✅ ADMIN ONLY
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Product save(@RequestBody Product p) {
        return service.save(p);
    }

    // ✅ ADMIN ONLY
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Product update(@PathVariable Long id, @RequestBody Product p) {
        return service.update(id, p);
    }

    // ✅ ADMIN ONLY
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // ✅ PUBLIC
    @GetMapping
    public List<Product> getAll() {
        return service.getAll();
    }

    // ✅ PUBLIC
    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return service.getById(id);
    }
}