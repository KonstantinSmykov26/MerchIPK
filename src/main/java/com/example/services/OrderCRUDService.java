package com.example.services;

import com.example.dto.OrderDto;
import com.example.dto.OrderItemDto;
import com.example.entity.OrderEntity;
import com.example.entity.OrderItemEntity;
import com.example.repositories.OrderItemRepository;
import com.example.repositories.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Slf4j
@Service
public class OrderCRUDService implements CRUDService<OrderDto> {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderCRUDService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
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
        log.info("Order created");
        OrderEntity orderEntity = mapToEntity(orderDto);
        orderRepository.save(orderEntity);
    }

    @Override
    public void update(OrderDto orderDto) {
        log.info("Order updated");

        OrderEntity orderEntity = orderRepository.findById(orderDto.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        orderEntity.setUserEmail(orderDto.getUserEmail());
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

    public static OrderDto mapToDto(OrderEntity orderEntity) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(orderEntity.getId());
        orderDto.setUserEmail(orderEntity.getUserEmail());
        orderDto.setTotalPrice(orderEntity.getTotalPrice());
        orderDto.setCreatedAt(orderEntity.getCreatedAt());
        orderDto.setItems(orderEntity.getItems().stream().map(OrderCRUDService::mapOrderItemToDto).toList());
        return orderDto;
    }

    public static OrderEntity mapToEntity (OrderDto orderDto) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(orderDto.getId());
        orderEntity.setUserEmail(orderDto.getUserEmail());
        orderEntity.setTotalPrice(orderDto.getTotalPrice());
        orderEntity.setCreatedAt(orderDto.getCreatedAt());
        orderEntity.setItems(orderDto.getItems().stream().map(OrderCRUDService::mapOrderItemToEntity).toList());
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
