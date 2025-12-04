package org.example.repository;


import org.example.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;


    @Test
    void testSaveAndFindProduct() {
        Product product = new Product();
        product.setName("Mouse");
        product.setPrice(new BigDecimal("2500.50"));
        product.setStock(50);
        product.setCategory("Electronics");
        product.setIsActive(true);

        Product saved = productRepository.save(product);

        Optional<Product> fetched = productRepository.findById(saved.getId());
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getName()).isEqualTo("Mouse");
    }
}
