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
@RequestMapping("/admin")
public class AdminController {

    @Autowired private VendorRepository vendorRepository;
    @Autowired private CoworkingSpaceRepository spaceRepository;
    @Autowired private AuditLogRepository auditLogRepository;
    @Autowired private AmenityRepository amenityRepository;
    @Autowired private SystemAdminRepository adminRepository;

    // 1. Main Dashboard (Stats)
    @GetMapping("/dashboard") // This maps to /admin/dashboard because of the @RequestMapping("/admin") at the top
    public String dashboard(HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) return "redirect:/login";

        // Logic to add attributes to the model
        model.addAttribute("pendingVendorsCount", vendorRepository.findByVerificationDateIsNull().size());
        model.addAttribute("pendingSpacesCount", spaceRepository.findByApprovalStatus("Pending").size());

        return "admin/dashboard"; // renders templates/admin/dashboard.html
    }

    // 2. Verification Page (Vendors & Spaces)
    @GetMapping("/verify")
    public String verifyPage(HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) return "redirect:/login";

        model.addAttribute("pendingVendors", vendorRepository.findByVerificationDateIsNull());
        model.addAttribute("pendingSpaces", spaceRepository.findByApprovalStatus("Pending"));
        return "admin/verifications";
    }

    @PostMapping("/verify/vendor/{id}")
    public String approveVendor(@PathVariable Integer id, HttpSession session) {
        Vendor vendor = vendorRepository.findById(id).orElseThrow();
        vendor.setVerificationDate(LocalDateTime.now());

        // Link to the admin who approved it
        Integer adminId = (Integer) session.getAttribute("userId");
        vendor.setApprovedByAdmin(adminRepository.getReferenceById(adminId));

        vendorRepository.save(vendor);
        return "redirect:/admin/verify?success=vendor";
    }

    @PostMapping("/verify/space/{id}")
    public String approveSpace(@PathVariable Integer id) {
        CoworkingSpace space = spaceRepository.findById(id).orElseThrow();
        space.setApprovalStatus("Approved");
        spaceRepository.save(space);
        return "redirect:/admin/verify?success=space";
    }

    // 3. Audit Log Viewer
    @GetMapping("/audit")
    public String viewAuditLog(HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) return "redirect:/login";

        // Sort by timestamp descending to see newest changes first
        model.addAttribute("logs", auditLogRepository.findAll());
        return "admin/audit-log";
    }

    // 4. Amenity Management
    @GetMapping("/amenities")
    public String manageAmenities(HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) return "redirect:/login";

        model.addAttribute("amenities", amenityRepository.findAll());
        return "admin/amenities";
    }

    @PostMapping("/amenities/add")
    public String addAmenity(@RequestParam String name, HttpSession session) {
        Integer adminId = (Integer) session.getAttribute("userId");

        Amenity amenity = new Amenity();
        amenity.setAmenityName(name);
        amenity.setCreatedByAdmin(adminRepository.getReferenceById(adminId));

        amenityRepository.save(amenity);
        return "redirect:/admin/amenities";
    }

    // 1. Delete/Reject a Vendor
    @PostMapping("/vendor/delete/{id}")
    public String deleteVendor(@PathVariable Integer id) {
        vendorRepository.deleteById(id);
        return "redirect:/admin/verify?deleted=vendor";
    }

    // 2. Delete a Coworking Space
    @PostMapping("/space/delete/{id}")
    public String deleteSpace(@PathVariable Integer id) {
        spaceRepository.deleteById(id);
        return "redirect:/admin/dashboard?deleted=space";
    }

    // 3. Delete an Amenity
    @PostMapping("/amenity/delete/{id}")
    public String deleteAmenity(@PathVariable Integer id) {
        amenityRepository.deleteById(id);
        return "redirect:/admin/amenities?deleted=amenity";
    }
}