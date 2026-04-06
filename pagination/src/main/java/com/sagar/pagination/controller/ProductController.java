package com.sagar.pagination.controller;

import com.sagar.pagination.model.Product;
import com.sagar.pagination.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    /**
     * EXAMPLE 2: Pagination with Sorting
     *
     * Request: GET /api/products/sorted?page=0&size=5&sortBy=price&sortDir=DESC
     *
     * Explanation:
     * - Sort by: column to sort by
     * - Sort direction: ASC (ascending) or DESC (descending)
     *
     * Real world: Sort products by price, rating, date, etc.
     */
    @GetMapping("/sorted")
    public Map<String, Object> getProductsSorted(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {

        log.info("Get products sorted by {} {}", sortBy, sortDir);

        // Create Sort object
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Sort sort = Sort.by(direction, sortBy);

        // Create Pageable with Sort
        Pageable pageable = PageRequest.of(page, size, sort);

        // Get sorted and paginated products
        Page<Product> products = productRepository.findAll(pageable);

        return Map.of(
                "content", products.getContent(),
                "totalElements", products.getTotalElements(),
                "totalPages", products.getTotalPages(),
                "sortBy", sortBy,
                "sortDirection", sortDir
        );
    }

    /**
     * EXAMPLE 3: Multiple Sorts
     *
     * Request: GET /api/products/multi-sort?page=0&size=5
     *
     * Sort by price descending, then by name ascending
     */
    @GetMapping("/multi-sort")
    public Map<String, Object> getProductsMultiSort(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        log.info("Get products with multiple sort criteria");

        // Create Sort with multiple criteria
        Sort sort = Sort.by(
                new Sort.Order(Sort.Direction.DESC, "price"),  // First sort by price descending
                new Sort.Order(Sort.Direction.ASC, "name")     // Then by name ascending
        );

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> products = productRepository.findAll(pageable);

        return Map.of(
                "content", products.getContent(),
                "totalElements", products.getTotalElements(),
                "totalPages", products.getTotalPages()
        );
    }

    /**
     * EXAMPLE 4: Filtered Pagination (Search)
     *
     * Request: GET /api/products/search?name=laptop&page=0&size=5
     *
     * Real world: User types "laptop" in search box
     * We find all products with "laptop" in name and paginate results
     */
    @GetMapping("/search")
    public Map<String, Object> searchProducts(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        log.info("Search products by name: {}", name);

        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        Page<Product> products = productRepository.findByNameContaining(name, pageable);

        return Map.of(
                "searchTerm", name,
                "content", products.getContent(),
                "totalElements", products.getTotalElements(),
                "totalPages", products.getTotalPages(),
                "message", "Found " + products.getTotalElements() + " products matching '" + name + "'"
        );
    }

    /**
     * EXAMPLE 5: Price Range Filter with Pagination
     *
     * Request: GET /api/products/price-range?minPrice=100&maxPrice=500&page=0&size=5
     *
     * Real world: User filters products by price range
     */
    @GetMapping("/price-range")
    public Map<String, Object> getProductsByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        log.info("Get products between {} and {}", minPrice, maxPrice);

        Pageable pageable = PageRequest.of(page, size, Sort.by("price"));
        Page<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice, pageable);

        return Map.of(
                "minPrice", minPrice,
                "maxPrice", maxPrice,
                "content", products.getContent(),
                "totalElements", products.getTotalElements(),
                "totalPages", products.getTotalPages()
        );
    }

    /**
     * EXAMPLE 6: Custom Query with Pagination
     *
     * Request: GET /api/products/expensive?minPrice=500&page=0&size=5
     *
     * Real world: Show expensive products (using custom query)
     */
    @GetMapping("/expensive")
    public Map<String, Object> getExpensiveProducts(
            @RequestParam Double minPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        log.info("Get products with price > {}", minPrice);

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findExpensiveProducts(minPrice, pageable);

        return Map.of(
                "minPrice", minPrice,
                "content", products.getContent(),
                "totalElements", products.getTotalElements(),
                "totalPages", products.getTotalPages(),
                "message", "Showing expensive products (>" + minPrice + ")"
        );
    }

    /**
     * EXAMPLE 7: Complete Pagination Response with Navigation
     *
     * Request: GET /api/products/complete?page=1&size=10&sortBy=price&sortDir=DESC&search=phone
     *
     * This shows a real-world pagination response with all necessary information
     */
    @GetMapping("/complete")
    public Map<String, Object> getCompletePageResponse(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir,
            @RequestParam(required = false) String search) {

        log.info("Get complete page response");

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> products;
        if (search != null && !search.isEmpty()) {
            products = productRepository.findByNameContaining(search, pageable);
        } else {
            products = productRepository.findAll(pageable);
        }

        return Map.of(
                // Pagination info
                "pagination", Map.of(
                        "currentPage", products.getNumber(),
                        "pageSize", products.getSize(),
                        "totalElements", products.getTotalElements(),
                        "totalPages", products.getTotalPages(),
                        "hasNext", products.hasNext(),
                        "hasPrevious", products.hasPrevious()
                ),
                // Navigation URLs (for frontend)
                "navigation", Map.of(
                        "nextPage", products.hasNext() ? page + 1 : null,
                        "previousPage", products.hasPrevious() ? page - 1 : null,
                        "firstPage", 0,
                        "lastPage", products.getTotalPages() - 1
                ),
                // Data
                "data", products.getContent(),
                // Search info
                "search", search != null ? search : "none"
        );
    }

    /**
     * EXAMPLE 8: Manual Pagination (if you need to do it without Spring Data)
     *
     * Request: GET /api/products/manual?offset=10&limit=5
     *
     * Sometimes you need to understand the underlying logic
     */
    @GetMapping("/manual")
    public Map<String, Object> manualPagination(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "5") int limit) {

        log.info("Manual pagination - offset: {}, limit: {}", offset, limit);

        // Get total count
        long totalCount = productRepository.count();

        // Calculate pages
        long totalPages = (totalCount + limit - 1) / limit;  // Ceiling division
        long currentPage = offset / limit;

        // Get data using Spring's pagination
        Pageable pageable = PageRequest.of((int)currentPage, limit);
        Page<Product> products = productRepository.findAll(pageable);

        return Map.of(
                "offset", offset,
                "limit", limit,
                "totalCount", totalCount,
                "totalPages", totalPages,
                "currentPage", currentPage,
                "hasMore", offset + limit < totalCount,
                "nextOffset", offset + limit,
                "data", products.getContent()
        );
    }

}
