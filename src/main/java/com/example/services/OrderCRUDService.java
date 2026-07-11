package com.example.services;

import com.example.dto.OrderDto;
import com.example.dto.OrderItemDto;
import com.example.entity.BasketEntity;
import com.example.entity.OrderEntity;
import com.example.entity.OrderItemEntity;
import com.example.entity.UserEntity;
import com.example.repositories.BasketRepository;
import com.example.repositories.OrderItemRepository;
import com.example.repositories.OrderRepository;
import com.example.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;

@Slf4j
@Service
public class OrderCRUDService implements CRUDService<OrderDto> {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BasketRepository basketRepository;
    private final UserRepository userRepository;

    public OrderCRUDService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, BasketRepository basketRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.basketRepository = basketRepository;
        this.userRepository = userRepository;
    }

    @Override
    public OrderDto getById(Integer id) {
        log.info("Order with ID - " + id);
        OrderEntity orderEntity = orderRepository.findById(id).orElseThrow();
        return mapToDto(orderEntity);
    }

    @Override
    public Collection<OrderDto> getAll() {
        log.info("Orders");
        return orderRepository.findAll()
                .stream()
                .map(OrderCRUDService::mapToDto)
                .toList();
    }

    @Override
    public void create(OrderDto orderDto) {
        String email = orderDto.getUserDto().getEmail();

        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow();

        Collection<BasketEntity> basketItems = basketRepository.findAll().stream()
                .filter(basket -> basket.getUserEntity() != null &&
                        email.equals(basket.getUserEntity().getEmail()) &&
                        basket.getProductEntity().getIsActive() == true)
                .toList();

        if (basketItems.isEmpty()) {
            throw new IllegalStateException("Корзина пуста!");
        }

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUserEntity(userEntity);
        orderEntity.setCreatedAt(LocalDateTime.now());

        double totalPrice = basketItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getProductEntity().getPrice())
                .sum();

        orderEntity.setTotalPrice(totalPrice);

        orderRepository.save(orderEntity);

        for (BasketEntity basket : basketItems) {
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setProductId(basket.getProductEntity().getId());
            orderItemEntity.setProductName(basket.getProductEntity().getName());
            orderItemEntity.setPrice(basket.getProductEntity().getPrice());
            orderItemEntity.setQuantity(basket.getQuantity());

            orderItemEntity.setOrderEntity(orderEntity);

            orderItemRepository.save(orderItemEntity);
        }

        basketRepository.deleteAll(basketItems);
    }

    @Override
    public void update(OrderDto orderDto) {
        log.info("Order updated");

        OrderEntity orderEntity = orderRepository.findById(orderDto.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        orderEntity.setUserEntity(UserCRUDService.mapToEntity(orderDto.getUserDto()));
        orderEntity.setTotalPrice(orderDto.getTotalPrice());
        orderEntity.setTotalPrice(orderDto.getTotalPrice());
        orderEntity.setCreatedAt(orderDto.getCreatedAt());

        orderRepository.save(orderEntity);
    }

    @Override
    public void delete(Integer id) {
        log.info("Order deleted with ID - " + id);
        orderRepository.deleteById(id);
    }

//    public Collection<OrderDto> getOrdersByUserEmail(String email) {
//        log.info("Fetching orders for user: " + email);
//
//        return orderRepository.findAll()
//                .stream()
//                .filter(orderEntity -> orderEntity.getUserEmail() != null &&
//                        email.equals(orderEntity.getUserEmail()))
//                .map(OrderCRUDService::mapToDto)
//                .toList();
//    }

    public static OrderDto mapToDto(OrderEntity orderEntity) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(orderEntity.getId());
        orderDto.setUserDto(UserCRUDService.mapToDto(orderEntity.getUserEntity()));
        orderDto.setTotalPrice(orderEntity.getTotalPrice());
        orderDto.setCreatedAt(orderEntity.getCreatedAt());

        // Безопасная проверка на null, чтобы не поймать ошибку при пустом заказе
        if (orderEntity.getItems() != null) {
            orderDto.setItems(orderEntity.getItems().stream().map(OrderCRUDService::mapOrderItemToDto).toList());
        }

        return orderDto;
    }

    public static OrderEntity mapToEntity (OrderDto orderDto) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(orderDto.getId());
        orderEntity.setUserEntity(UserCRUDService.mapToEntity(orderDto.getUserDto()));
        orderEntity.setTotalPrice(orderDto.getTotalPrice());
        orderEntity.setCreatedAt(orderDto.getCreatedAt());

        if (orderDto.getItems() != null) {
            orderEntity.setItems(orderDto.getItems().stream().map(OrderCRUDService::mapOrderItemToEntity).toList());
        }

        return orderEntity;
    }

    public static OrderItemDto mapOrderItemToDto(OrderItemEntity orderItemEntity) {
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setId(orderItemEntity.getId());
        orderItemDto.setProductId(orderItemEntity.getProductId());
        orderItemDto.setProductName(orderItemEntity.getProductName());
        orderItemDto.setPrice(orderItemEntity.getPrice());
        orderItemDto.setQuantity(orderItemEntity.getQuantity());
        return orderItemDto;
    }

    public static OrderItemEntity mapOrderItemToEntity (OrderItemDto orderItemDto) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setId(orderItemDto.getId());
        orderItemEntity.setProductId(orderItemDto.getProductId());
        orderItemEntity.setProductName(orderItemDto.getProductName());
        orderItemEntity.setPrice(orderItemDto.getPrice());
        orderItemEntity.setQuantity(orderItemDto.getQuantity());
        return orderItemEntity;
    }
}
