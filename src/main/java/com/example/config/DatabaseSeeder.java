package com.example.config;

import com.example.entity.ProductEntity;
import com.example.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DatabaseSeeder(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {

            ProductEntity productEntity1 = new ProductEntity("Кофта", "Тёплая кофта");
            ProductEntity productEntity2 = new ProductEntity("Браслет", "Красивый браслет");
            ProductEntity productEntity3 = new ProductEntity("Ручка", "Синяя ручка");
            ProductEntity productEntity4 = new ProductEntity("Футболка", "Удобная футболка");
            ProductEntity productEntity5 = new ProductEntity("Штаны", "Стильные штаны");

            productRepository.saveAll(List.of(productEntity1, productEntity2, productEntity3, productEntity4, productEntity5));

            log.info("База данных успешно наполнена начальными товарами!");
        }
    }
}