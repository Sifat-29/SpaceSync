package com.spacesync.controllers;

import com.spacesync.models.*;
import com.spacesync.repositories.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

    @Controller
    public class VendorAdminController {

        @Autowired private VendorRepository vendorRepo;
        @Autowired private CoworkingSpaceRepository spaceRepo;
        @Autowired private AuditLogRepository auditRepo;
        @Autowired private PaymentRepository paymentRepo;

        // --- SYSTEM ADMIN DASHBOARD ---
        @GetMapping("/admin/dashboard")
        public String adminDashboard(Model model, HttpSession session) {
            model.addAttribute("pendingVendors", vendorRepo.findAll().stream()
                    .filter(v -> v.getVerificationDate() == null).toList());
            model.addAttribute("pendingSpaces", spaceRepo.findAll().stream()
                    .filter(s -> "Pending".equals(s.getApprovalStatus())).toList());
            model.addAttribute("auditLogs", auditRepo.findAll());
            return "admin/dashboard";
        }

        @PostMapping("/admin/approve-vendor/{id}")
        public String approveVendor(@PathVariable Integer id) {
            Vendor v = vendorRepo.findById(id).orElseThrow();
            v.setVerificationDate(LocalDateTime.now());
            vendorRepo.save(v);
            return "redirect:/admin/dashboard";
        }

        @PostMapping("/admin/approve-space/{id}")
        public String approveSpace(@PathVariable Integer id) {
            CoworkingSpace s = spaceRepo.findById(id).orElseThrow();
            s.setApprovalStatus("Approved");
            spaceRepo.save(s);
            return "redirect:/admin/dashboard";
        }

        // --- VENDOR DASHBOARD ---
        @GetMapping("/vendor/dashboard")
        public String vendorDashboard(Model model, HttpSession session) {
            Integer vendorId = (Integer) session.getAttribute("userId");
            if (vendorId == null) return "redirect:/"; // Fake login check

            Double revenue = paymentRepo.findTotalRevenueByVendorId(Long.valueOf(vendorId));
            model.addAttribute("totalRevenue", revenue != null ? revenue : 0.0);
            model.addAttribute("mySpaces", spaceRepo.findAll().stream()
                    .filter(s -> s.getVendor().getVendorId().equals(vendorId)).toList());
            return "vendor/dashboard";
        }

//        @GetMapping("/vendor/add-space")
//        public String addSpaceForm() { return "vendor/add-space"; }

        @GetMapping("/vendor/add-space")
        public String addSpaceForm(Model model, HttpSession session) {
            // Check if the user is a Vendor
            if (!"Vendor".equals(session.getAttribute("role"))) {
                return "redirect:/?error=Unauthorized";
            }

            // Provide an empty object so the form has fields to fill
            model.addAttribute("coworkingSpace", new CoworkingSpace());
            return "vendor/add-space";
        }

//        // 2. Process the Form Submission
//        @PostMapping("/vendor/add-space")
//        public String processAddSpace(@ModelAttribute("coworkingSpace") CoworkingSpace space, HttpSession session) {
//            Integer vendorId = (Integer) session.getAttribute("userId");
//
//            // Logic: Force status to Pending and link to logged-in Vendor
//            space.getVendor().setVendorId(vendorId);
//            space.setApprovalStatus("Pending");
//
//            // Save to Database
//            spaceRepo.save(space);
//
//            // Redirect back to dashboard to see the new 'Pending' entry
//            return "redirect:/vendor/dashboard";
//        }
@PostMapping("/vendor/add-space")
public String processAddSpace(@ModelAttribute("coworkingSpace") CoworkingSpace space, HttpSession session) {
    // 1. Get the Vendor ID from the session
    Integer loggedInVendorId = (Integer) session.getAttribute("userId");

    if (loggedInVendorId == null) {
        return "redirect:/dev/login-vendor";
    }

    try {
        // 2. Fetch the actual Vendor entity from the database
        // This is necessary because CoworkingSpace uses a Vendor OBJECT, not just an ID
        Vendor currentVendor = vendorRepo.findById(loggedInVendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        // 3. Attach the Vendor object to the Space
        space.setVendor(currentVendor);

        // 4. Set the default status
        space.setApprovalStatus("Pending");

        // 5. Save the new space
        spaceRepo.save(space);

    } catch (Exception e) {
        e.printStackTrace(); // This helps you see other errors in the console
        return "redirect:/vendor/add-space?error=true";
    }

    return "redirect:/vendor/dashboard";
}
        @GetMapping("/dev/login-admin")
        public String loginAsAdmin(HttpSession session) {
            session.setAttribute("userId", 1); // Sifat from seed data
            session.setAttribute("role", "System Admin");
            return "redirect:/admin/dashboard";
        }

        @GetMapping("/dev/login-vendor")
        public String loginAsVendor(HttpSession session) {
            session.setAttribute("userId", 1); // Innovate Corp from seed data
            session.setAttribute("role", "Vendor");
            return "redirect:/vendor/dashboard";
        }

        @GetMapping("/dev/login-user")
        public String loginAsUser(HttpSession session) {
            session.setAttribute("userId", 1); // Rahul
            session.setAttribute("role", "Member");
            return "redirect:/user/reviews";
        }
    }

