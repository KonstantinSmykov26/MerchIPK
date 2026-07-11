package com.example.controllers;

import com.example.dto.OrderDto;
import com.example.dto.UserDto;
import com.example.services.OrderCRUDService;
import com.example.services.UserCRUDService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Controller
public class OrderController {
    private final OrderCRUDService orderService;

    public OrderController(OrderCRUDService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/order/{id}")
    public String getById(@PathVariable Integer id, Model model) {
        OrderDto orderDto = orderService.getById(id);

        model.addAttribute("orderDto", orderDto);

        return "show-order";
    }

    @ResponseBody
    @GetMapping(path = "/orders")
    public Collection<OrderDto> getOrders() {
        return orderService.getAll();
    }

    @GetMapping("/addorder")
    public String addOrder(HttpSession session) {
        String currentUserEmail = (String) session.getAttribute("currentUser");

        if (currentUserEmail == null) {
            return "redirect:/signin";
        }

        UserDto userDto = new UserDto();
        userDto.setEmail(currentUserEmail);

        OrderDto orderDto = new OrderDto();
        orderDto.setUserDto(userDto);
        orderService.create(orderDto);

        return "redirect:/";
    }

    @PutMapping("/edit_order/{id}")
    public String updateOrder(@PathVariable Integer id, @ModelAttribute("orderDto") OrderDto orderDto) {
        orderDto.setId(id);
        orderService.update(orderDto);
        return "redirect:/";
    }

    @DeleteMapping("/delete_order/{id}")
    public String deleteOrder(@PathVariable Integer id) {
        orderService.delete(id);
        return "redirect:/";
    }
}
