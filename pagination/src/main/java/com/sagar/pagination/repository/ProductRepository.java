package com.sagar.pagination.repository;

import com.sagar.pagination.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    //Page<Product> findAll(Pageable pageable);
}
