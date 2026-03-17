package com.spacesync.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/set-persona")
    public String setPersona(@RequestParam Integer id, @RequestParam String role, @RequestParam String name, HttpSession session) {
        // Set the session variables that Person A and B's code are looking for
        session.setAttribute("userId", id);
        session.setAttribute("userRole", role);
        session.setAttribute("userName", name);

        return "redirect:/"; // Refresh the home page with the new identity
    }
}