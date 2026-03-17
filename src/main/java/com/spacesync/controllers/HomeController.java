package com.spacesync.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        String role = (String) session.getAttribute("role");
        System.out.println("LOGGED IN ROLE: " + role);

        if (role == null) {
            return "redirect:/login";
        }

        // Use "redirect:" to force the app to go through the specific Controller logic
        if ("USER".equals(role)) return "index";
        if ("VENDOR".equals(role)) return "redirect:/vendor/dashboard";
        if ("ADMIN".equals(role)) return "redirect:/admin/dashboard";

        return "redirect:/login";
    }
}