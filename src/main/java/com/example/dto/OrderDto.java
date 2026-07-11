package com.example.dto;

import java.time.LocalDateTime;
import java.util.Collection;

public class OrderDto {
    private Integer id;
    private UserDto userDto;
    private Double totalPrice;
    private LocalDateTime createdAt;
    private Collection<OrderItemDto> items;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Collection<OrderItemDto> getItems() {
        return items;
    }

    public void setItems(Collection<OrderItemDto> items) {
        this.items = items;
    }
}
