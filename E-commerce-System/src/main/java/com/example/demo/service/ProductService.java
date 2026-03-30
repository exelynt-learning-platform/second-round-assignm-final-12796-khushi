package com.example.demo.service;

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repo;

    // ✅ UPDATE PRODUCT
    public Product update(Long id, Product p) {

        Product existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        existing.setName(p.getName());
        existing.setPrice(p.getPrice());
        existing.setDescription(p.getDescription());
        existing.setStock(p.getStock());
        existing.setImageUrl(p.getImageUrl());

        return repo.save(existing);
    }

    // ✅ SAVE PRODUCT
    public Product save(Product p) {
        return repo.save(p);
    }

    // ✅ GET ALL PRODUCTS
    public List<Product> getAll() {
        return repo.findAll();
    }

    // ✅ DELETE PRODUCT
    public void delete(Long id) {

        // optional check before delete
        if (!repo.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }

        repo.deleteById(id);
    }

    // ✅ GET BY ID
    public Product getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }
}