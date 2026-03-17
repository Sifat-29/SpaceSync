package com.spacesync.controllers;

import com.spacesync.models.*;
import com.spacesync.repositories.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    @Autowired private BookingRepository bookingRepository;
    @Autowired private GroupMembershipRepository membershipRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private UserRepository userRepository; // Added this

    @GetMapping("/active")
    public String activeBookings(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        List<Integer> groupIds = membershipRepository.findByUserId(userId)
                .stream().map(GroupMembership::getGroupId).collect(Collectors.toList());

        List<Booking> active = bookingRepository.findAll().stream()
                .filter(b -> groupIds.contains(b.getGroup().getGroupId()) && "Confirmed".equals(b.getStatus()))
                .collect(Collectors.toList());

        model.addAttribute("bookings", active);
        return "bookings/active";
    }

    @GetMapping("/history")
    public String bookingHistory(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        List<Integer> groupIds = membershipRepository.findByUserId(userId)
                .stream().map(GroupMembership::getGroupId).collect(Collectors.toList());

        List<Booking> history = bookingRepository.findAll().stream()
                .filter(b -> groupIds.contains(b.getGroup().getGroupId()) && "Completed".equals(b.getStatus()))
                .collect(Collectors.toList());

        model.addAttribute("bookings", history);
        return "bookings/history";
    }

    @PostMapping("/review")
    public String addReview(@RequestParam Integer bookingId,
                            @RequestParam Integer rating,
                            @RequestParam String comment,
                            HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        Review review = new Review();
        review.setBookingId(bookingId);

        // FIX: Fetch the actual User object from the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        review.setUser(user); // Use setUser() instead of setUserId()

        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());

        // Manual ID assignment for the weak entity's partial key
        review.setReviewId((int)(Math.random() * 100000));

        reviewRepository.save(review);
        return "redirect:/bookings/history?reviewed=true";
    }

    @PostMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable Integer id, HttpSession session) {
        Booking b = bookingRepository.findById(id).orElseThrow();
        b.setStatus("Cancelled");
        bookingRepository.save(b); // This will trigger the Audit Log trigger!
        return "redirect:/bookings/active?cancelled=true";
    }
}