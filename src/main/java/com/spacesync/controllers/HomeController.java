package com.spacesync.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // This catches requests to the root URL "/"
    @GetMapping("/")
    public String home() {
        // This tells Spring Boot to render the "index.html" file in the templates folder
        return "index";
    }
    @GetMapping("/test-login")
    public String testLogin(HttpSession session) {
        session.setAttribute("userId", 1);
        session.setAttribute("role", "MEMBER");
        return "redirect:/reviews"; // <--- This goes straight to your review page
    }
}
