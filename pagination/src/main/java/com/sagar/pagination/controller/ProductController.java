package com.sagar.pagination.controller;

import com.sagar.pagination.model.Product;
import com.sagar.pagination.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/products")
@Slf4j
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("Hello");
    }

    /**
     * EXAMPLE 1: Basic Pagination
     *
     * Request: GET /api/products/basic?page=0&size=5
     *
     * Explanation:
     * - page=0: First page (0-indexed)
     * - size=5: 5 products per page
     *
     * Response includes:
     * - content: The actual products
     * - totalElements: Total number of products
     * - totalPages: Number of pages
     * - currentPage: Current page number
     */
    @GetMapping("/basic")
    public Map<String, Object> getProductsBasic(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size) {

        log.info("Get products - page: {}, size: {}", page, size);

        // Create Pageable object
        // PageRequest is the Spring Data implementation of Pageable
        Pageable pageable = (Pageable) PageRequest.of(page, size);

        // Get page of products
        Page<Product> products = productRepository.findAll((pageable));

        // Return useful information
        return Map.of(
                "content", products.getContent(),
                "totalElements", products.getTotalElements(),
                "totalPages", products.getTotalPages(),
                "currentPage", products.getNumber(),
                "pageSize", products.getSize(),
                "hasNext", products.hasNext(),
                "hasPrevious", products.hasPrevious()
        );
    }

}
