//package com.spacesync.controllers;
//
//import com.spacesync.models.*;
//import com.spacesync.repositories.*;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//@Controller
//@RequestMapping("/vendor")
//public class VendorController {
//
//    @Autowired private CoworkingSpaceRepository spaceRepository;
//    @Autowired private VendorRepository vendorRepository;
//    @Autowired private PaymentRepository paymentRepository;
//    @Autowired private AmenityRepository amenityRepository;
//    @Autowired private MeetingRoomRepository roomRepository;
//    @Autowired private OpenDeskAreaRepository deskRepository;
//
//    @GetMapping("/dashboard")
//    public String dashboard(HttpSession session, Model model) {
//        Integer vendorId = (Integer) session.getAttribute("userId");
//        if (!"VENDOR".equals(session.getAttribute("role"))) return "redirect:/login";
//
//        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow();
//
//        // Logic: If Vendor is not verified yet, show a "Pending" message
//        if (vendor.getVerificationDate() == null) {
//            return "vendor/pending";
//        }
//
//        List<CoworkingSpace> mySpaces = spaceRepository.findByVendor_VendorId(vendorId);
//        BigDecimal revenue = paymentRepository.calculateTotalRevenue(vendorId);
//
//        model.addAttribute("mySpaces", mySpaces);
//        model.addAttribute("totalRevenue", revenue != null ? revenue : BigDecimal.ZERO);
//        return "vendor/dashboard";
//    }
//
//    @GetMapping("/add-space")
//    public String showAddSpace(HttpSession session, Model model) {
//        if (!"VENDOR".equals(session.getAttribute("role"))) return "redirect:/login";
//
//        // Even if the repository is an empty interface, this method works!
//        List<Amenity> allAvailableAmenities = amenityRepository.findAll();
//
//        model.addAttribute("allAmenities", allAvailableAmenities);
//        return "vendor/add-space";
//    }
//
//    @PostMapping("/add-space")
//    public String processAddSpace(@ModelAttribute CoworkingSpace space,
//                                  @RequestParam List<Integer> amenityIds,
//                                  @RequestParam BigDecimal roomRate,
//                                  @RequestParam Integer roomCapacity,
//                                  @RequestParam BigDecimal deskRate,
//                                  @RequestParam Double lat, @RequestParam Double lng,
//                                  HttpSession session) {
//        Integer vendorId = (Integer) session.getAttribute("userId");
//
//        // 1. Save the Space
//        space.setVendor(vendorRepository.getReferenceById(vendorId));
//        space.setLatitude(BigDecimal.valueOf(lat));
//        space.setLongitude(BigDecimal.valueOf(lng));
//        space.setApprovalStatus("Pending");
//        space.setAmenities(amenityRepository.findAllById(amenityIds));
//        CoworkingSpace savedSpace = spaceRepository.save(space);
//
//        // 2. Automatically create the first Meeting Room
//        MeetingRoom room = new MeetingRoom();
//        room.setSpaceId(savedSpace.getSpaceId());
//        room.setRoomName("Main Boardroom");
//        room.setCapacity(roomCapacity);
//        room.setHourlyRate(roomRate);
//        roomRepository.save(room);
//
//        // 3. Automatically create the Open Desk Area
//        OpenDeskArea desk = new OpenDeskArea();
//        desk.setSpaceId(savedSpace.getSpaceId());
//        desk.setTotalSeats(50); // Default
//        desk.setDailyRate(deskRate);
//        deskRepository.save(desk);
//
//        return "redirect:/vendor/dashboard?success=added";
//    }
//
//    @PostMapping("/space/delete/{id}")
//    public String vendorDeleteSpace(@PathVariable Integer id, HttpSession session) {
//        // Security: Only allow delete if space belongs to this vendor
//        CoworkingSpace space = spaceRepository.findById(id).orElseThrow();
//        if (space.getVendor().getVendorId().equals(session.getAttribute("userId"))) {
//            spaceRepository.deleteById(id);
//        }
//        return "redirect:/vendor/dashboard?deleted=true";
//    }
//
//    @GetMapping("/space/edit/{id}")
//    public String editSpace(@PathVariable Integer id, Model model) {
//        CoworkingSpace space = spaceRepository.findById(id).orElseThrow();
//        model.addAttribute("space", space);
//        model.addAttribute("allAmenities", amenityRepository.findAll());
//        return "vendor/edit-space";
//    }
//
//    @PostMapping("/space/update")
//    public String updateSpace(@ModelAttribute CoworkingSpace space, @RequestParam List<Integer> amenityIds) {
//        CoworkingSpace existing = spaceRepository.findById(space.getSpaceId()).orElseThrow();
//        existing.setName(space.getName());
//        existing.setStreet(space.getStreet());
//        existing.setCity(space.getCity());
//        existing.setOpeningTime(space.getOpeningTime());
//        existing.setClosingTime(space.getClosingTime());
//        existing.setAmenities(amenityRepository.findAllById(amenityIds));
//        spaceRepository.save(existing);
//        return "redirect:/vendor/dashboard?updated=true";
//    }
//}
package com.spacesync.controllers;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.spacesync.models.*;
import com.spacesync.repositories.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        if (vendor.getVerificationDate() == null) {
            return "vendor/pending";
        }

        List<CoworkingSpace> mySpaces = spaceRepository.findByVendor_VendorId(vendorId);
        BigDecimal revenue = paymentRepository.calculateTotalRevenue(vendorId);

        // FIX: Provide multiple attribute names to ensure the frontend can grab it regardless of what it expects
        model.addAttribute("mySpaces", mySpaces);
        model.addAttribute("spaces", mySpaces);
        model.addAttribute("totalRevenue", revenue != null ? revenue : BigDecimal.ZERO);
        return "vendor/dashboard";
    }

    @GetMapping("/add-space")
    public String showAddSpace(HttpSession session, Model model) {
        if (!"VENDOR".equals(session.getAttribute("role"))) return "redirect:/login";
        model.addAttribute("allAmenities", amenityRepository.findAll());
        return "vendor/add-space";
    }

    @PostMapping("/add-space")
    public String processAddSpace(@ModelAttribute CoworkingSpace space,
                                  @RequestParam(required = false) List<Integer> amenityIds, // FIX: required=false prevents crash if no boxes checked
                                  @RequestParam BigDecimal roomRate,
                                  @RequestParam Integer roomCapacity,
                                  @RequestParam BigDecimal deskRate,
                                  @RequestParam Double lat, @RequestParam Double lng,
                                  HttpSession session) {
        Integer vendorId = (Integer) session.getAttribute("userId");

        space.setVendor(vendorRepository.getReferenceById(vendorId));
        space.setLatitude(BigDecimal.valueOf(lat));
        space.setLongitude(BigDecimal.valueOf(lng));
        space.setApprovalStatus("Pending");

        if (amenityIds != null) {
            space.setAmenities(amenityRepository.findAllById(amenityIds));
        }

        CoworkingSpace savedSpace = spaceRepository.save(space);

        // FIX: Set Room_ID manually to 1 because it's not Auto_Increment in the database!
        MeetingRoom room = new MeetingRoom();
        room.setSpaceId(savedSpace.getSpaceId());
        room.setRoomId(1);
        room.setRoomName("Main Boardroom");
        room.setCapacity(roomCapacity);
        room.setHourlyRate(roomRate);
        roomRepository.save(room);

        // FIX: Set Area_ID manually to 1
        OpenDeskArea desk = new OpenDeskArea();
        desk.setSpaceId(savedSpace.getSpaceId());
        desk.setAreaId(1);
        desk.setTotalSeats(50);
        desk.setDailyRate(deskRate);
        deskRepository.save(desk);

        return "redirect:/vendor/dashboard?success=added";
    }

    @PostMapping("/space/delete/{id}")
        public String vendorDeleteSpace(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
            CoworkingSpace space = spaceRepository.findById(id).orElseThrow();
            
            // Security check: ensure the logged-in vendor actually owns this space
            if (space.getVendor().getVendorId().equals(session.getAttribute("userId"))) {
                try {
                    // Attempt to delete the space
                    spaceRepository.deleteById(id);
                    redirectAttributes.addFlashAttribute("successMessage", "Listing removed successfully.");
                } catch (Exception e) {
                    // If it fails (like having active bookings), catch the error gracefully
                    redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete: There are active bookings for this space.");
                }
            } else {
                // If they don't own the space, don't let them delete it
                redirectAttributes.addFlashAttribute("errorMessage", "You do not have permission to delete this space.");
            }
            
            return "redirect:/vendor/dashboard";
        }

    @GetMapping("/space/edit/{id}")
    public String editSpace(@PathVariable Integer id, Model model) {
        CoworkingSpace space = spaceRepository.findById(id).orElseThrow();
        model.addAttribute("space", space);
        model.addAttribute("allAmenities", amenityRepository.findAll());

        // FIX: Fetch existing rates to display on the edit page
        List<MeetingRoom> rooms = roomRepository.findBySpaceId(id);
        if (!rooms.isEmpty()) model.addAttribute("room", rooms.get(0));

        List<OpenDeskArea> desks = deskRepository.findBySpaceId(id);
        if (!desks.isEmpty()) model.addAttribute("desk", desks.get(0));

        return "vendor/edit-space";
    }

    @PostMapping("/space/update")
    public String updateSpace(@ModelAttribute CoworkingSpace space,
                              @RequestParam(required = false) List<Integer> amenityIds,
                              @RequestParam BigDecimal roomRate,
                              @RequestParam Integer roomCapacity,
                              @RequestParam BigDecimal deskRate) {

        CoworkingSpace existing = spaceRepository.findById(space.getSpaceId()).orElseThrow();
        existing.setName(space.getName());
        existing.setStreet(space.getStreet());
        existing.setCity(space.getCity());
        existing.setOpeningTime(space.getOpeningTime());
        existing.setClosingTime(space.getClosingTime());
        existing.setLatitude(space.getLatitude());
        existing.setLongitude(space.getLongitude());

        if (amenityIds != null) {
            existing.setAmenities(amenityRepository.findAllById(amenityIds));
        } else {
            existing.getAmenities().clear();
        }
        spaceRepository.save(existing);

        // FIX: Update the Room & Desk rates (or create them if they were corrupted earlier)
        List<MeetingRoom> rooms = roomRepository.findBySpaceId(space.getSpaceId());
        if (!rooms.isEmpty()) {
            MeetingRoom room = rooms.get(0);
            room.setHourlyRate(roomRate);
            room.setCapacity(roomCapacity);
            roomRepository.save(room);
        } else {
            MeetingRoom room = new MeetingRoom();
            room.setSpaceId(space.getSpaceId());
            room.setRoomId(1);
            room.setCapacity(roomCapacity);
            room.setHourlyRate(roomRate);
            roomRepository.save(room);
        }

        List<OpenDeskArea> desks = deskRepository.findBySpaceId(space.getSpaceId());
        if (!desks.isEmpty()) {
            OpenDeskArea desk = desks.get(0);
            desk.setDailyRate(deskRate);
            deskRepository.save(desk);
        } else {
            OpenDeskArea desk = new OpenDeskArea();
            desk.setSpaceId(space.getSpaceId());
            desk.setAreaId(1);
            desk.setTotalSeats(50);
            desk.setDailyRate(deskRate);
            deskRepository.save(desk);
        }

        return "redirect:/vendor/dashboard?updated=true";
    }
}