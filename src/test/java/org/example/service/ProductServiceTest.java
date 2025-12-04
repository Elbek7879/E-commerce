package org.example.service;


import org.example.dto.request.ProductRequest;
import org.example.dto.response.ProductResponse;
import org.example.entity.Product;
import org.example.exception.ProductNotFoundException;
import org.example.repository.ProductRepository;
import org.example.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    private ProductRepository repo;
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        repo = mock(ProductRepository.class);
        productService = new ProductServiceImpl(repo);
    }

    @Test
    void testGetAllProducts() {
        Product p1 = new Product();
        p1.setId(1L);
        p1.setName("Laptop");
        p1.setPrice(BigDecimal.valueOf(1200));
        p1.setStock(10);
        p1.setCategory("Electronics");
        p1.setIsActive(true);
        p1.setCreatedAt(LocalDateTime.now());

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(p1));

        when(repo.findAll(pageable)).thenReturn(page);

        Page<ProductResponse> result = productService.getAllProducts(pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Laptop");
    }

    @Test
    void testGetProductById_Found() {
        Product p = new Product();
        p.setId(1L);
        p.setName("Laptop");

        when(repo.findById(1L)).thenReturn(Optional.of(p));

        ProductResponse response = productService.getProductById(1L);
        assertThat(response.getName()).isEqualTo("Laptop");
    }

    @Test
    void testGetProductById_NotFound() {
        when(repo.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById(100L);
        });
    }

    @Test
    void testCreateProduct() {
        ProductRequest request = new ProductRequest();
        request.setName("Phone");
        request.setPrice(BigDecimal.valueOf(500));
        request.setStock(20);
        request.setCategory("Electronics");
        request.setIsActive(true);

        Product saved = new Product();
        saved.setId(1L);
        saved.setName("Phone");
        saved.setPrice(BigDecimal.valueOf(500));
        saved.setStock(20);
        saved.setCategory("Electronics");
        saved.setIsActive(true);
        saved.setCreatedAt(LocalDateTime.now());

        when(repo.save(any(Product.class))).thenReturn(saved);

        ProductResponse response = productService.createProduct(request);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(repo, times(1)).save(captor.capture());

        Product captured = captor.getValue();
        assertThat(captured.getName()).isEqualTo("Phone");
        assertThat(response.getName()).isEqualTo("Phone");
    }

    @Test
    void testUpdateProduct_Found() {
        Product existing = new Product();
        existing.setId(1L);
        existing.setName("Old Laptop");
        existing.setPrice(BigDecimal.valueOf(1000));
        existing.setStock(5);
        existing.setCategory("Electronics");
        existing.setIsActive(true);

        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(existing);

        ProductRequest updateRequest = new ProductRequest();
        updateRequest.setName("New Laptop");
        updateRequest.setPrice(BigDecimal.valueOf(1200));
        updateRequest.setStock(10);
        updateRequest.setCategory("Electronics");
        updateRequest.setIsActive(true);

        ProductResponse response = productService.updateProduct(1L, updateRequest);

        assertThat(response.getName()).isEqualTo("New Laptop");
        assertThat(response.getPrice()).isEqualTo(BigDecimal.valueOf(1200));
    }

    @Test
    void testUpdateProduct_NotFound() {
        when(repo.findById(100L)).thenReturn(Optional.empty());

        ProductRequest updateRequest = new ProductRequest();
        updateRequest.setName("New Product");
        updateRequest.setPrice(BigDecimal.valueOf(100));
        updateRequest.setStock(1);
        updateRequest.setCategory("Test");
        updateRequest.setIsActive(true);

        assertThrows(ProductNotFoundException.class, () -> {
            productService.updateProduct(100L, updateRequest);
        });
    }

    @Test
    void testDeleteProduct() {
        doNothing().when(repo).deleteById(1L);
        productService.deleteProduct(1L);
        verify(repo, times(1)).deleteById(1L);
    }

    @Test
    void testSearchProducts() {
        Product p1 = new Product();
        p1.setName("Laptop");
        p1.setCategory("Electronics");

        when(repo.findByNameContainingAndCategoryContaining("Lap","Electronics"))
                .thenReturn(List.of(p1));

        List<ProductResponse> results = productService.searchProducts("Lap", "Electronics");

        assertThat(results).isNotEmpty();

        assertThat(results.get(0).getName()).isEqualTo("Laptop");

        assertThat(results.get(0).getCategory()).isEqualTo("Electronics");
    }

}
