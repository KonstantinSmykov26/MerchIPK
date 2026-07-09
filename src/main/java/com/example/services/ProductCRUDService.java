package com.example.services;

import com.example.dto.ProductDto;
import com.example.entity.ProductEntity;
import com.example.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Slf4j
@Service
public class ProductCRUDService implements CRUDService<ProductDto> {
    private final ProductRepository productRepository;

    public ProductCRUDService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductDto getById(Integer id) {
        log.info("Product with ID - " + id);
        ProductEntity productEntity = productRepository.findById(id).orElseThrow();
        return mapToDto(productEntity);
    }

    @Override
    public Collection<ProductDto> getAll() {
        log.info("Products");
        return productRepository.findAll()
                .stream()
                .map(ProductCRUDService::mapToDto)
                .toList();
    }

    @Override
    public void create(ProductDto productDto) {
        log.info("Product created");
        productDto.setIsActive(true);
        ProductEntity productEntity = mapToEntity(productDto);
        productRepository.save(productEntity);
    }

    @Override
    public void update(ProductDto productDto) {
        log.info("Product updated with ID - " + productDto.getId());

        // 1. Достаем оригинальную сущность из БД
        ProductEntity productEntity = productRepository.findById(productDto.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 2. Обновляем только те поля, которые разрешено редактировать из формы
        productEntity.setName(productDto.getName());
        productEntity.setDescription(productDto.getDescription());
        productEntity.setPrice(productDto.getPrice());

        // Поле isActive мы не трогаем, оно остается таким, каким было в базе!

        // 3. Сохраняем обновленную сущность
        productRepository.save(productEntity);
    }

    @Override
    public void delete(Integer id) {
        log.info("Product deleted with ID - " + id);
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));

        if (Boolean.FALSE.equals(productEntity.getIsActive())) {
            throw new IllegalArgumentException("Товар уже был удален ранее!");
        }

        productEntity.setIsActive(false);
        productRepository.save(productEntity);
    }

    public void returnProduct(Integer id) {
        log.info("Product returned with ID - " + id);
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));

        if (Boolean.TRUE.equals(productEntity.getIsActive())) {
            throw new IllegalArgumentException("Товар уже активен!");
        }

        productEntity.setIsActive(true);
        productRepository.save(productEntity);
    }

    public static ProductDto mapToDto(ProductEntity productEntity) {
        ProductDto productDto = new ProductDto();
        productDto.setId(productEntity.getId());
        productDto.setName(productEntity.getName());
        productDto.setDescription(productEntity.getDescription());
        productDto.setPrice(productEntity.getPrice());
        productDto.setIsActive(productEntity.getIsActive());
        return productDto;
    }

    public static ProductEntity mapToEntity (ProductDto productDto) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(productDto.getId());
        productEntity.setName(productDto.getName());
        productEntity.setDescription(productDto.getDescription());
        productEntity.setPrice(productDto.getPrice());
        productEntity.setIsActive(productDto.getIsActive());
        return productEntity;
    }
}
