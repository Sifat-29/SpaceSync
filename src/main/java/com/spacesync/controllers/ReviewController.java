//package com.spacesync.controllers;
//
//import com.spacesync.models.Booking;
//import com.spacesync.models.Review;
//import com.spacesync.models.User;
//import com.spacesync.repositories.BookingRepository;
//import com.spacesync.repositories.ReviewRepository;
//import com.spacesync.repositories.UserRepository;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Controller
//@RequestMapping("/reviews")
//public class ReviewController {
//
//    @Autowired
//    private BookingRepository bookingRepository;
//
//    @Autowired
//    private ReviewRepository reviewRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @GetMapping
//    public String viewCompletedBookings(HttpSession session, Model model) {
//        Integer userId = (Integer) session.getAttribute("userId");
//        if (userId == null) return "redirect:/"; // Redirect to fake login if no session
//
//        // Logic: Find bookings that are 'Completed' and belong to groups the user is in
//        // Note: Person C likely provided a custom query for this, if not, we filter via group membership
//        List<Booking> completedBookings = bookingRepository.findCompletedByUserGroups(userId);
//
//        model.addAttribute("bookings", completedBookings);
//        return "reviews";
//    }
//
//    @PostMapping("/submit")
//    public String submitReview(@RequestParam("bookingId") Integer bookingId,
//                               @RequestParam("rating") Integer rating,
//                               @RequestParam("comment") String comment,
//                               HttpSession session) {
//        Integer userId = (Integer) session.getAttribute("userId");
//        if (userId == null) return "redirect:/";
//
//        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
//        User user = userRepository.findById(userId).orElseThrow();
//
//        Review review = new Review();
//        review.setBookingId(bookingId);
//        review.setUser(user);
//        review.setRating(rating);
//        review.setComment(comment);
//        review.setCreatedAt(LocalDateTime.now());
//
//        reviewRepository.save(review);
//
//        // Optional: In Task 5, Person C has a trigger that updates Coworking_Space rating
//        // so we don't need to update the space rating manually in Java!
//
//        return "redirect:/reviews?success=true";
//    }
//}