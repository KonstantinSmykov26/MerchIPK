package com.example.controllers;

import com.example.dto.UserDto;
import com.example.services.UserCRUDService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Controller
public class UserController {

    private final UserCRUDService userService;

    public UserController(UserCRUDService userService) {
        this.userService = userService;
    }

    @ResponseBody
    @GetMapping("/users/{id}")
    public UserDto getById(@PathVariable Integer id) {
        return userService.getById(id);
    }

    @ResponseBody
    @GetMapping(path = "/users")
    public Collection<UserDto> getUsers() {
        return userService.getAll();
    }

    @PostMapping("/adduser")
    public String addUser (@ModelAttribute("userDto") UserDto userDto, BindingResult result, HttpSession session) {
        if (result.hasErrors() || userDto.getEmail().isEmpty() || userDto.getPassword().isEmpty()) {
            return "add-user";
        }

        userService.create(userDto);

        if (session.getAttribute("currentUser") == null) {
            return "redirect:/signin";
        }

        return "redirect:/";
    }

    @PutMapping("/edit_user/{id}")
    public String updateUser(@PathVariable Integer id, @ModelAttribute("userDto") UserDto userDto) {
        userDto.setId(id);
        userService.update(userDto);
        return "redirect:/";
    }

    @DeleteMapping("/delete_user/{id}")
    public String deleteUser(@PathVariable Integer id) {
        userService.delete(id);
        return "redirect:/";
    }

    @PostMapping("/signin")
    public String authenticate(Model model,
                               @ModelAttribute("userDto") UserDto userDto,
                               HttpServletResponse response,
                               HttpSession session) {
        if (userDto.getEmail().isEmpty() || userDto.getPassword().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error", "Убедитесь, что все поля заполнены");
            return "signin-form";
        }

        if (!userService.authenticate(userDto.getEmail(), userDto.getPassword())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            model.addAttribute("error", "Неверный email или пароль");
            return "signin-form";
        }

        session.setAttribute("currentUser", userDto.getEmail());

        return "redirect:/";
    }

    @GetMapping("/signout")
    public String signOut(HttpSession session) {
        session.invalidate();

        return "redirect:/";
    }

    @GetMapping("/")
    public String showIndexPage(Model model) {
        Collection<UserDto> users = (userService.getAll().isEmpty()) ? null : userService.getAll();
        model.addAttribute("users", users);
        return "index";
    }

    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "add-user";
    }

    @GetMapping("/signin")
    public String showSignInForm(Model model) {
        model.addAttribute("userDto", new UserDto());

        return "signin-form";
    }

    @GetMapping("/edit_user/{id}")
    public String showUserUpdatePage(@PathVariable Integer id, Model model) {
        UserDto userDto = userService.getById(id);
        userDto.setPassword("");

        model.addAttribute("userDto", userDto);
        return "update-user";
    }
}
