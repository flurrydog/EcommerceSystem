package org.monicandy.ecommerceapp.controller;

import org.monicandy.ecommerceapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "registered", required = false) String registered,
            Model model) {

        if (error != null) {
            model.addAttribute("error", "用户名或密码错误");
        }

        if (logout != null) {
            model.addAttribute("message", "您已成功登出");
        }

        if (registered != null) {
            model.addAttribute("success", "注册成功！请登录");
        }

        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        // 传递一个空的Map用于表单绑定
        model.addAttribute("formData", new HashMap<String, String>());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes,
            Model model) {

        Map<String, String> formData = new HashMap<>();
        formData.put("username", username);
        formData.put("email", email);
        formData.put("password", password);
        formData.put("confirmPassword", confirmPassword);

        // 前端验证
        if (username == null || username.trim().length() < 3) {
            model.addAttribute("error", "用户名至少3个字符");
            model.addAttribute("formData", formData);
            return "register";
        }

        if (email == null || !email.contains("@")) {
            model.addAttribute("error", "邮箱格式不正确");
            model.addAttribute("formData", formData);
            return "register";
        }

        if (password == null || password.length() < 6) {
            model.addAttribute("error", "密码至少6位");
            model.addAttribute("formData", formData);
            return "register";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "两次输入的密码不一致");
            model.addAttribute("formData", formData);
            return "register";
        }

        try {
            userService.registerUser(username, email, password);
            redirectAttributes.addAttribute("registered", true);
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("formData", formData);
            return "register";
        }
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/products";
    }
}