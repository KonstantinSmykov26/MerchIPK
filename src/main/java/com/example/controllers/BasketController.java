package com.example.controllers;

import com.example.dto.BasketDto;
import com.example.services.BasketCRUDService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Controller
public class BasketController {
    private final BasketCRUDService basketService;

    public BasketController(BasketCRUDService basketService) {
        this.basketService = basketService;
    }

    @GetMapping("/basket/{id}")
    public String getById(@PathVariable Integer id, Model model) {
        BasketDto basketDto = basketService.getById(id);

        model.addAttribute("basketDto", basketDto);

        return "show-basket";
    }

    @ResponseBody
    @GetMapping(path = "/baskets")
    public Collection<BasketDto> getBaskets() {
        return basketService.getAll();
    }

    @PostMapping("/addbasket")
    public String addBasket(@RequestParam("productId") Integer productId,
                            @RequestParam("quantity") Integer quantity,
                            HttpSession session) {
        String currentUserEmail = (String) session.getAttribute("currentUser");

        if (currentUserEmail == null) {
            return "redirect:/signin";
        }

        basketService.addProductToUserBasket(currentUserEmail, productId, quantity);

        return "redirect:/";
    }

    @PutMapping("/edit_basket/{id}")
    public String updateBasket(@PathVariable Integer id, @ModelAttribute("basketDto") BasketDto basketDto) {
        basketDto.setId(id);
        basketService.update(basketDto);
        return "redirect:/";
    }

    @DeleteMapping("/delete_basket/{id}")
    public String deleteBasket(@PathVariable Integer id) {
        basketService.delete(id);
        return "redirect:/";
    }

    @GetMapping("/my_basket")
    public String showBasket(HttpSession session, Model model) {
        String currentUserEmail = (String) session.getAttribute("currentUser");

        if (currentUserEmail == null) {
            return "redirect:/signin";
        }

        Collection<BasketDto> userItems = basketService.getItemsByUserEmail(currentUserEmail);

        double total = userItems.stream().mapToDouble(basketDto -> basketDto.getQuantity() * basketDto.getProductDto().getPrice()).sum();

        model.addAttribute("basketItems", userItems);
        model.addAttribute("totalPrice", total);

        return "show-basket";
    }

    @GetMapping("/make_order")
    public String makeOrder(HttpSession session) {
        String currentUserEmail = (String) session.getAttribute("currentUser");

        if (currentUserEmail == null) {
            return "redirect:/signin";
        }

        basketService.makeOrder(currentUserEmail);

        return "redirect:/";
    }
}
