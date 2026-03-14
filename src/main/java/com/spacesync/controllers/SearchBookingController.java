package com.spacesync.controllers;

import com.spacesync.models.*;
import com.spacesync.repositories.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/search")
public class SearchBookingController {

    @Autowired private CoworkingSpaceRepository spaceRepository;
    @Autowired private TeamGroupRepository groupRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private PaymentRepository paymentRepository;

    @GetMapping("/{groupId}")
    public String searchSpaces(@PathVariable Integer groupId, Model model) {
        // Run Persona A's Custom Haversine Algorithm
        List<CoworkingSpace> fairSpaces = spaceRepository.findFairSpacesForGroup(groupId);
        
        model.addAttribute("groupId", groupId);
        model.addAttribute("spaces", fairSpaces);
        return "search/map-view"; // We will create this HTML file next
    }

    @PostMapping("/book")
    public String confirmBooking(
            @RequestParam Integer groupId,
            @RequestParam Integer spaceId,
            @RequestParam(required = false) Integer roomId,
            @RequestParam(required = false) Integer areaId,
            @RequestParam BigDecimal amount) {

        // 1. Create Booking
        Booking booking = new Booking();
        booking.setGroup(groupRepository.getReferenceById(groupId));
        booking.setSpace(spaceRepository.getReferenceById(spaceId));
        
        // XOR Logic (Room OR Desk)
        if (roomId != null) booking.setBookedRoomId(roomId);
        if (areaId != null) booking.setBookedAreaId(areaId);

        // Dummy times for the demo
        booking.setStartTime(LocalDateTime.now().plusDays(1));
        booking.setEndTime(LocalDateTime.now().plusDays(1).plusHours(4));
        booking.setStatus("Confirmed");
        
        Booking savedBooking = bookingRepository.save(booking);

        // 2. Create Payment Transaction (Satisfies the 1:1 relation)
        Payment payment = new Payment();
        payment.setBooking(savedBooking);
        payment.setAmount(amount);
        payment.setPaymentMethod("Credit Card");
        payment.setTransactionId(UUID.randomUUID().toString().substring(0, 10)); // Fake TXN ID
        paymentRepository.save(payment);

        return "redirect:/groups?bookingSuccess=true";
    }
}