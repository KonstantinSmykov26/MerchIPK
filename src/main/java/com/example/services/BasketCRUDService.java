package com.example.services;

import com.example.dto.BasketDto;
import com.example.entity.BasketEntity;
import com.example.entity.ProductEntity;
import com.example.entity.UserEntity;
import com.example.repositories.BasketRepository;
import com.example.repositories.ProductRepository;
import com.example.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class BasketCRUDService implements CRUDService<BasketDto> {
    private final BasketRepository basketRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    public BasketCRUDService(BasketRepository basketRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.basketRepository = basketRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    public BasketDto getById(Integer id) {
        log.info("Basket with ID - " + id);
        BasketEntity basketEntity = basketRepository.findById(id).orElseThrow();
        return mapToDto(basketEntity);
    }

    @Override
    public Collection<BasketDto> getAll() {
        log.info("Baskets");
        return basketRepository.findAll()
                .stream()
                .map(BasketCRUDService::mapToDto)
                .toList();
    }

    @Override
    public void create(BasketDto basketDto) {
        log.info("Basket created");
        BasketEntity basketEntity = mapToEntity(basketDto);
        basketRepository.save(basketEntity);
    }

    @Override
    public void update(BasketDto basketDto) {
        log.info("Basket updated");
        BasketEntity basketEntity = mapToEntity(basketDto);
        basketRepository.save(basketEntity);
    }

    @Override
    public void delete(Integer id) {
        log.info("Basket deleted with ID - " + id);
        basketRepository.deleteById(id);
    }

    public void addProductToUserBasket(String email, Integer productId, Integer quantity) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));

        Optional<BasketEntity> existingItem = basketRepository.findAll().stream()
                .filter(basket -> basket.getUserEntity().getId().equals(userEntity.getId()))
                .filter(basket -> basket.getProductEntity().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            BasketEntity basketEntity = existingItem.get();
            basketEntity.setQuantity(basketEntity.getQuantity() + quantity);
            basketRepository.save(basketEntity);
        } else {
            BasketEntity basketEntity = new BasketEntity();
            basketEntity.setUserEntity(userEntity);
            basketEntity.setProductEntity(productEntity);
            basketEntity.setQuantity(quantity);
            basketRepository.save(basketEntity);
        }
    }

    public Collection<BasketDto> getItemsByUserEmail(String email) {
        log.info("Fetching basket items for user: " + email);

        return basketRepository.findAll()
                .stream()
                .filter(basket -> basket.getUserEntity() != null && email.equals(basket.getUserEntity().getEmail()))
                .map(BasketCRUDService::mapToDto)
                .toList();
    }

    public static BasketDto mapToDto(BasketEntity basketEntity) {
        BasketDto basketDto = new BasketDto();
        basketDto.setId(basketEntity.getId());
        basketDto.setUserDto(UserCRUDService.mapToDto(basketEntity.getUserEntity()));
        basketDto.setProductDto(ProductCRUDService.mapToDto(basketEntity.getProductEntity()));
        basketDto.setQuantity(basketEntity.getQuantity());
        return basketDto;
    }

    public static BasketEntity mapToEntity (BasketDto basketDto) {
        BasketEntity basketEntity = new BasketEntity();
        basketEntity.setId(basketDto.getId());
        basketEntity.setUserEntity(UserCRUDService.mapToEntity(basketDto.getUserDto()));
        basketEntity.setProductEntity(ProductCRUDService.mapToEntity(basketDto.getProductDto()));
        basketEntity.setQuantity(basketDto.getQuantity());
        return basketEntity;
    }
}
