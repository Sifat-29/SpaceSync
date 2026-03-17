package com.spacesync.controllers;

import com.spacesync.models.*;
import com.spacesync.repositories.*;
import com.spacesync.utils.PasswordUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
public class AuthController {

    @Autowired private UserRepository userRepository;
    @Autowired private VendorRepository vendorRepository;
    @Autowired private SystemAdminRepository adminRepository;

    // 1. Show Login Page
    @GetMapping("/login")
    public String showLogin() {
        return "auth/login";
    }

    // 2. Process Login
    @PostMapping("/login")
    public String processLogin(@RequestParam String identifier, @RequestParam String password, HttpSession session, Model model) {
        String hashedInput = PasswordUtil.hashPassword(password); // Hash the input!

        User user = userRepository.findByUsernameOrPhone(identifier, identifier);
        if (user != null && user.getPassword().equals(hashedInput)) {
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("role", "USER");
            session.setAttribute("userName", user.getName());
            return "redirect:/";
        }

        Vendor vendor = vendorRepository.findByUsernameOrContactEmail(identifier, identifier);
        if (vendor != null && vendor.getPassword().equals(hashedInput)) {
            // STRICT CHECK: Is the vendor verified?
            if (vendor.getVerificationDate() == null) {
                model.addAttribute("error", "Access Denied: Your vendor account is pending admin approval.");
                return "auth/login";
            }

            session.setAttribute("userId", vendor.getVendorId());
            session.setAttribute("role", "VENDOR");
            session.setAttribute("userName", vendor.getBusinessName());
            return "redirect:/";
        }

        SystemAdmin admin = adminRepository.findByUsername(identifier);
        if (admin != null) {
            // DEBUG: Add this line to see what is happening in your console
            System.out.println("DB Hash: " + admin.getPassword());
            System.out.println("Input Hash: " + hashedInput);

            if (admin.getPassword().equals(hashedInput)) {
                session.setAttribute("userId", admin.getAdminId());
                session.setAttribute("role", "ADMIN");
                session.setAttribute("userName", admin.getUsername());
                return "redirect:/";
            }
        }

        model.addAttribute("error", "Invalid credentials!");
        return "auth/login";
    }

    // 3. Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // 4. Registration Pages
    @GetMapping("/register/user")
    public String showUserRegister() { return "auth/register-user"; }

    @GetMapping("/register/vendor")
    public String showVendorRegister() { return "auth/register-vendor"; }

    // 5. Process User Registration
    @PostMapping("/register/user")
    public String registerUser(@ModelAttribute User newUser, @RequestParam Double lat, @RequestParam Double lng, Model model) {
        try {
            if (!newUser.getPhone().matches("\\d{10}")) {
                model.addAttribute("error", "Phone must be 10 digits.");
                return "auth/register-user";
            }
            newUser.setPassword(PasswordUtil.hashPassword(newUser.getPassword()));
            newUser.setLatitude(BigDecimal.valueOf(lat));
            newUser.setLongitude(BigDecimal.valueOf(lng));
            userRepository.save(newUser);
            return "redirect:/login?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", "Username or Phone already exists in our system.");
            return "auth/register-user";
        }
    }

    // 6. Process Vendor Registration
    @PostMapping("/register/vendor")
    public String registerVendor(@ModelAttribute Vendor newVendor) {
        // Hash password and set as pending
        newVendor.setPassword(PasswordUtil.hashPassword(newVendor.getPassword()));
        newVendor.setVerificationDate(null); // NULL = Pending Admin approval
        vendorRepository.save(newVendor);
        return "redirect:/login?registered=true";
    }
}