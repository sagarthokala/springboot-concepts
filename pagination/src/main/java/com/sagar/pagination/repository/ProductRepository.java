package com.sagar.pagination.repository;

import com.sagar.pagination.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Product Repository
     *
     * Spring Data JPA automatically provides pagination support!
     * Just extend JpaRepository and use Pageable parameter
     */

        /**
         * Find products by name with pagination
         *
         * Usage:
         * Page<Product> products = productRepository.findByNameContaining("laptop", pageable);
         */
        Page<Product> findByNameContaining(String name, Pageable pageable);

        /**
         * Find products with price range and pagination
         */
        Page<Product> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);

        /**
         * Custom query with pagination
         */
        @Query("SELECT p FROM Product p WHERE p.price > :minPrice ORDER BY p.price DESC")
        Page<Product> findExpensiveProducts(@Param("minPrice") Double minPrice, Pageable pageable);

        /**
         * Count total products (useful for manual pagination)
         */
        long countByPriceGreaterThan(Double price);
}
