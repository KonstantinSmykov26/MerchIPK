package com.example.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.dto.ProductDto;
import com.example.dto.UserDto;
import com.example.entity.ProductEntity;
import com.example.entity.UserEntity;
import com.example.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

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
        ProductEntity productEntity = mapToEntity(productDto);
        productRepository.save(productEntity);
    }

    @Override
    public void update(ProductDto productDto) {
        log.info("Product updated");
        ProductEntity productEntity = mapToEntity(productDto);
        productRepository.save(productEntity);
    }

    @Override
    public void delete(Integer id) {
        log.info("Product deleted with ID - " + id);
        productRepository.deleteById(id);
    }

    public static ProductDto mapToDto(ProductEntity productEntity) {
        ProductDto productDto = new ProductDto();
        productDto.setId(productEntity.getId());
        productDto.setName(productEntity.getName());
        productDto.setDescription(productEntity.getDescription());
        return productDto;
    }

    public static ProductEntity mapToEntity (ProductDto productDto) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(productDto.getId());
        productEntity.setName(productDto.getName());
        productEntity.setDescription(productDto.getDescription());
        return productEntity;
    }
}
