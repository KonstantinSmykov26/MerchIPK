package com.example.controllers;

import com.example.dto.ProductDto;
import com.example.services.ProductCRUDService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Controller
public class ProductController {

    private final ProductCRUDService productService;

    public ProductController(ProductCRUDService productService) {
        this.productService = productService;
    }

    @GetMapping("/product/{id}")
    public String getById(@PathVariable Integer id, Model model, HttpSession session) {
        ProductDto productDto = productService.getById(id);

        // Если товар удален (не активен)
        if (Boolean.FALSE.equals(productDto.getIsActive())) {
            String currentUser = (String) session.getAttribute("currentUser");

            // Если пользователя нет в сессии (гость) ИЛИ он не админ — выкидываем на главную
            if (currentUser == null || !currentUser.equals("admin")) {
                return "redirect:/";
            }
        }

        model.addAttribute("productDto", productDto);
        return "show-product";
    }

    @ResponseBody
    @GetMapping(path = "/products")
    public Collection<ProductDto> getProducts() {
        return productService.getAll();
    }

    @PostMapping("/addproduct")
    public String addProduct(@ModelAttribute("productDto") ProductDto productDto, BindingResult result) {
        if (result.hasErrors() || productDto.getName().isEmpty() || productDto.getDescription().isEmpty()) {
            return "";
        }

        productService.create(productDto);

        return "redirect:/";
    }

    @PutMapping("/edit_product/{id}")
    public String updateProduct(@PathVariable Integer id, @ModelAttribute("productDto") ProductDto productDto,
                                HttpSession session) {
        String currentUser = (String) session.getAttribute("currentUser");
        if (currentUser == null || !currentUser.equals("admin")) {
            return "redirect:/signin";
        }

        productDto.setId(id);
        productService.update(productDto);
        return "redirect:/";
    }

    @DeleteMapping("/delete_product/{id}")
    public String deleteProduct(@PathVariable Integer id, HttpSession session) {
        String currentUser = (String) session.getAttribute("currentUser");
        if (currentUser == null || !currentUser.equals("admin")) {
            return "redirect:/signin"; // Гостей и обычных юзеров отправляем на вход
        }

        productService.delete(id);
        return "redirect:/";
    }

    @PutMapping("/return_product/{id}")
    public String returnProduct(@PathVariable Integer id, HttpSession session) {
        String currentUser = (String) session.getAttribute("currentUser");
        if (currentUser == null || !currentUser.equals("admin")) {
            return "redirect:/signin";
        }

        productService.returnProduct(id);
        return "redirect:/";
    }

    @GetMapping("/")
    public String showIndexPage(Model model) {
        Collection<ProductDto> products = productService.getAll();
        model.addAttribute("products", products);
        return "index";
    }

    @GetMapping("/addproduct")
    public String showAddProductPage(Model model, HttpSession session) {
        String currentUser = (String) session.getAttribute("currentUser");
        if (currentUser == null || !currentUser.equals("admin")) {
            return "redirect:/signin";
        }

        model.addAttribute("productDto", new ProductDto());
        return "add-product";
    }

    @GetMapping("/edit_product/{id}")
    public String showProductUpdatePage(@PathVariable Integer id, Model model, HttpSession session) {
        String currentUser = (String) session.getAttribute("currentUser");
        if (currentUser == null || !currentUser.equals("admin")) {
            return "redirect:/signin";
        }

        ProductDto productDto = productService.getById(id);

        model.addAttribute("productDto", productDto);
        return "update-product";
    }
}
