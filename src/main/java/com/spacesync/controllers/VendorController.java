package com.spacesync.controllers;

import com.spacesync.models.*;
import com.spacesync.repositories.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/vendor")
public class VendorController {

    @Autowired private CoworkingSpaceRepository spaceRepository;
    @Autowired private VendorRepository vendorRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private AmenityRepository amenityRepository;
    @Autowired private MeetingRoomRepository roomRepository;
    @Autowired private OpenDeskAreaRepository deskRepository;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Integer vendorId = (Integer) session.getAttribute("userId");
        if (!"VENDOR".equals(session.getAttribute("role"))) return "redirect:/login";

        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow();

        // Logic: If Vendor is not verified yet, show a "Pending" message
        if (vendor.getVerificationDate() == null) {
            return "vendor/pending";
        }

        List<CoworkingSpace> mySpaces = spaceRepository.findByVendor_VendorId(vendorId);
        BigDecimal revenue = paymentRepository.calculateTotalRevenue(vendorId);

        model.addAttribute("mySpaces", mySpaces);
        model.addAttribute("totalRevenue", revenue != null ? revenue : BigDecimal.ZERO);
        return "vendor/dashboard";
    }

    @GetMapping("/add-space")
    public String showAddSpace(HttpSession session, Model model) {
        if (!"VENDOR".equals(session.getAttribute("role"))) return "redirect:/login";

        // Even if the repository is an empty interface, this method works!
        List<Amenity> allAvailableAmenities = amenityRepository.findAll();

        model.addAttribute("allAmenities", allAvailableAmenities);
        return "vendor/add-space";
    }

    @PostMapping("/add-space")
    public String processAddSpace(@ModelAttribute CoworkingSpace space,
                                  @RequestParam List<Integer> amenityIds,
                                  @RequestParam BigDecimal roomRate,
                                  @RequestParam Integer roomCapacity,
                                  @RequestParam BigDecimal deskRate,
                                  @RequestParam Double lat, @RequestParam Double lng,
                                  HttpSession session) {
        Integer vendorId = (Integer) session.getAttribute("userId");

        // 1. Save the Space
        space.setVendor(vendorRepository.getReferenceById(vendorId));
        space.setLatitude(BigDecimal.valueOf(lat));
        space.setLongitude(BigDecimal.valueOf(lng));
        space.setApprovalStatus("Pending");
        space.setAmenities(amenityRepository.findAllById(amenityIds));
        CoworkingSpace savedSpace = spaceRepository.save(space);

        // 2. Automatically create the first Meeting Room
        MeetingRoom room = new MeetingRoom();
        room.setSpaceId(savedSpace.getSpaceId());
        room.setRoomName("Main Boardroom");
        room.setCapacity(roomCapacity);
        room.setHourlyRate(roomRate);
        roomRepository.save(room);

        // 3. Automatically create the Open Desk Area
        OpenDeskArea desk = new OpenDeskArea();
        desk.setSpaceId(savedSpace.getSpaceId());
        desk.setTotalSeats(50); // Default
        desk.setDailyRate(deskRate);
        deskRepository.save(desk);

        return "redirect:/vendor/dashboard?success=added";
    }

    @PostMapping("/space/delete/{id}")
    public String vendorDeleteSpace(@PathVariable Integer id, HttpSession session) {
        // Security: Only allow delete if space belongs to this vendor
        CoworkingSpace space = spaceRepository.findById(id).orElseThrow();
        if (space.getVendor().getVendorId().equals(session.getAttribute("userId"))) {
            spaceRepository.deleteById(id);
        }
        return "redirect:/vendor/dashboard?deleted=true";
    }

    @GetMapping("/space/edit/{id}")
    public String editSpace(@PathVariable Integer id, Model model) {
        CoworkingSpace space = spaceRepository.findById(id).orElseThrow();
        model.addAttribute("space", space);
        model.addAttribute("allAmenities", amenityRepository.findAll());
        return "vendor/edit-space";
    }

    @PostMapping("/space/update")
    public String updateSpace(@ModelAttribute CoworkingSpace space, @RequestParam List<Integer> amenityIds) {
        CoworkingSpace existing = spaceRepository.findById(space.getSpaceId()).orElseThrow();
        existing.setName(space.getName());
        existing.setStreet(space.getStreet());
        existing.setCity(space.getCity());
        existing.setOpeningTime(space.getOpeningTime());
        existing.setClosingTime(space.getClosingTime());
        existing.setAmenities(amenityRepository.findAllById(amenityIds));
        spaceRepository.save(existing);
        return "redirect:/vendor/dashboard?updated=true";
    }
}