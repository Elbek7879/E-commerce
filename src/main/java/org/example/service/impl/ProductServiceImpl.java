package org.example.service.impl;

import org.example.dto.request.ProductRequest;
import org.example.dto.response.ProductResponse;
import org.example.entity.Product;
import org.example.exception.ProductNotFoundException;
import org.example.repository.ProductRepository;
import org.example.service.ProductService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;

    public ProductServiceImpl(ProductRepository repo) { this.repo = repo; }

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return repo.findAll(pageable).map(this::toResponse);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product p = new Product();
        p.setName(request.getName());
        p.setPrice(request.getPrice());
        p.setStock(request.getStock());
        p.setCategory(request.getCategory());
        p.setIsActive(request.getIsActive());
        p.setCreatedAt(LocalDateTime.now());
        return toResponse(repo.save(p));
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product p = findById(id);
        p.setName(request.getName());
        p.setPrice(request.getPrice());
        p.setStock(request.getStock());
        p.setCategory(request.getCategory());
        p.setIsActive(request.getIsActive());
        return toResponse(repo.save(p));
    }

    @Override
    public void deleteProduct(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<ProductResponse> searchProducts(String name, String category) {
        return repo.findByNameContainingAndCategoryContaining(
                name != null ? name : "",
                category != null ? category : ""
        ).stream().map(this::toResponse).toList();
    }

    private Product findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    private ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .price(p.getPrice())
                .stock(p.getStock())
                .category(p.getCategory())
                .isActive(p.getIsActive())
                .createdAt(p.getCreatedAt())
                .build();
    }


}
