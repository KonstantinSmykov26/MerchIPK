package com.example.config;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.entity.ProductEntity;
import com.example.entity.UserEntity;
import com.example.repositories.ProductRepository;
import com.example.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public DatabaseSeeder(UserRepository userRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            String password = BCrypt.withDefaults().hashToString(10, "admin".toCharArray());
            UserEntity userEntity = new UserEntity("admin", password);

            ProductEntity productEntity1 = new ProductEntity("Кофта", "Тёплая кофта", 500.0);
            ProductEntity productEntity2 = new ProductEntity("Браслет", "Красивый браслет", 100.0);
            ProductEntity productEntity3 = new ProductEntity("Ручка", "Синяя ручка", 50.0);
            ProductEntity productEntity4 = new ProductEntity("Футболка", "Удобная футболка",1500.0);
            ProductEntity productEntity5 = new ProductEntity("Штаны", "Стильные штаны", 1000.0);

            userRepository.save(userEntity);
            productRepository.saveAll(List.of(productEntity1, productEntity2, productEntity3, productEntity4, productEntity5));

            log.info("База данных успешно наполнена начальными данными!");
        }
    }
}