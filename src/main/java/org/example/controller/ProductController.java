package org.example.controller;

import org.example.dto.request.ProductRequest;
import org.example.dto.response.ProductResponse;
import org.example.service.ProductService;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) { this.service = service; }

    @GetMapping
    public Page<ProductResponse> getAll(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        return service.getAllProducts(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Long id) {
        return service.getProductById(id);
    }

    @PostMapping
    public ProductResponse create(@RequestBody @Valid ProductRequest request) {
        return service.createProduct(request);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @RequestBody @Valid ProductRequest request) {
        return service.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteProduct(id);
    }

    @GetMapping("/search")
    public List<ProductResponse> search(@RequestParam(required = false) String name,
                                        @RequestParam(required = false) String category) {
        return service.searchProducts(name, category);
    }




}
