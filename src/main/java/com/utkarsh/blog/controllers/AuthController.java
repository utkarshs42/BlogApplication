package com.utkarsh.blog.controllers;

import com.utkarsh.blog.models.User;
import com.utkarsh.blog.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private UserService userService;

    public AuthController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/login-page")
    public String getLoginPage(){
        return "login-page";
    }

    @GetMapping("/signup")
    public String getSignUpPage(Model model){
        User user = new User();
        model.addAttribute("user",user);
        return "sign-up";
    }

    @PostMapping("/signup")
    public String handleSignup(@ModelAttribute("user") User user, Model model) {
        if (userService.findByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "Email already in use");
            return "sign-up";
        }
        userService.addUser(user);
        return "redirect:/login-page?signupSuccess";
    }
}
