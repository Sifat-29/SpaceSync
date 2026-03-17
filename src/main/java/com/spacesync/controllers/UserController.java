package com.spacesync.controllers;

import com.spacesync.models.User;
import com.spacesync.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // View Profile and Current Location
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userRepository.findById(userId).orElseThrow();
        model.addAttribute("user", user);
        return "user/profile";
    }

    // Update Latitude and Longitude via Map Click
    @PostMapping("/profile/update-location")
    public String updateLocation(@RequestParam Double lat, @RequestParam Double lng, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userRepository.findById(userId).orElseThrow();
        user.setLatitude(BigDecimal.valueOf(lat));
        user.setLongitude(BigDecimal.valueOf(lng));

        userRepository.save(user);
        return "redirect:/user/profile?success=true";
    }
}