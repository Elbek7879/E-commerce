package org.example.service;

import org.example.dto.request.ProductRequest;
import org.example.dto.response.ProductResponse;
import org.springframework.data.domain.*;

import java.util.List;

public interface ProductService {

    Page<ProductResponse> getAllProducts(Pageable pageable);
    ProductResponse getProductById(Long id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
    List<ProductResponse> searchProducts(String name, String category);



}
