package com.example.controllers;

import com.example.dto.ProductDto;
import com.example.services.ProductCRUDService;
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
    public String getById(@PathVariable Integer id, Model model) {
        ProductDto productDto = productService.getById(id);

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
    public String updateProduct(@PathVariable Integer id, @ModelAttribute("productDto") ProductDto productDto) {
        productDto.setId(id);
        productService.update(productDto);
        return "redirect:/";
    }

    @DeleteMapping("/delete_product/{id}")
    public String deleteProduct(@PathVariable Integer id) {
        productService.delete(id);
        return "redirect:/";
    }

    @GetMapping("/")
    public String showIndexPage(Model model) {
        Collection<ProductDto> products = productService.getAll();
        model.addAttribute("products", products);
        return "index";
    }

    @GetMapping("/addproduct")
    public String showAddProductPage(Model model) {
        model.addAttribute("productDto", new ProductDto());
        return "add-product";
    }

    @GetMapping("/edit_product/{id}")
    public String showProductUpdatePage(@PathVariable Integer id, Model model) {
        ProductDto productDto = productService.getById(id);

        model.addAttribute("productDto", productDto);
        return "update-product";
    }
}
