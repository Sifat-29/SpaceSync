////package com.spacesync.controllers;
////
////import com.spacesync.models.Review;
////import com.spacesync.repositories.BookingRepository;
////import com.spacesync.repositories.ReviewRepository;
////import jakarta.servlet.http.HttpSession;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.stereotype.Controller;
////import org.springframework.ui.Model;
////import org.springframework.web.bind.annotation.*;
////import java.time.LocalDateTime;
////
////@Controller
////public class FeedbackController {
////
////    @Autowired private BookingRepository bookingRepo;
////    @Autowired private ReviewRepository reviewRepo;
////
////    @GetMapping("/user/reviews")
////    public String showReviews(Model model, HttpSession session) {
////        Integer userId = (Integer) session.getAttribute("userId");
////        if (userId == null) return "redirect:/";
////        model.addAttribute("completedBookings", bookingRepo.findCompletedBookingsByUser(Long.valueOf(userId)));
////        return "user/reviews";
////    }
////
////    @PostMapping("/user/submit-review")
////    public String submitReview(@RequestParam Integer bookingId, @RequestParam Integer rating, @RequestParam String comment, HttpSession session) {
////        Review r = new Review();
////        r.setBookingId(bookingId);
////        r.getUser().setUserId((Integer) session.getAttribute("userId"));
////        r.setRating(rating);
////        r.setComment(comment);
////        r.setCreatedAt(LocalDateTime.now());
////        reviewRepo.save(r);
////        return "redirect:/user/reviews?success";
////    }
////}
//package com.spacesync.controllers;
//
//import com.spacesync.models.User;
//import com.spacesync.models.Review;
//import com.spacesync.models.Booking;
//import com.spacesync.repositories.UserRepository;
//import com.spacesync.repositories.ReviewRepository;
//import com.spacesync.repositories.BookingRepository;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Controller
//public class FeedbackController {
//
//    @Autowired
//    private BookingRepository bookingRepo;
//
//    @Autowired
//    private ReviewRepository reviewRepo;
//
//    @Autowired
//    private UserRepository userRepo;
//
//    @GetMapping("/user/reviews")
//    public String userReviews(Model model, HttpSession session) {
//        Integer userId = (Integer) session.getAttribute("userId");
//        if (userId == null) {
//            return "redirect:/dev/login-user";
//        }
//
//        // Uses the native query we added to the repository earlier
//        List<Booking> completedBookings = bookingRepo.findCompletedBookingsByUser(Long.valueOf(userId));
//        model.addAttribute("completedBookings", completedBookings);
//        return "user/reviews";
//    }
//
////    @PostMapping("/user/submit-review")
////    public String submitReview(@RequestParam Integer bookingId,
////                               @RequestParam Integer rating,
////                               @RequestParam String comment,
////                               HttpSession session) {
////
////        Integer userId = (Integer) session.getAttribute("userId");
////        if (userId == null) return "redirect:/dev/login-user";
////
////        try {
////            // 1. Fetch the User object (Required because Review.java has a 'User' field, not just userId)
////            User currentUser = userRepo.findById(userId)
////                    .orElseThrow(() -> new RuntimeException("User not found"));
////
////            // 2. Create the Review object
////            Review review = new Review();
////
////            // 3. Set values based on your model's exact method names
////            review.setBookingId(bookingId); // Matches your @Id field
////
////            // Generate a random ID for Review_ID since Java isn't auto-generating it
////            review.setReviewId((int)(System.currentTimeMillis() % 1000000));
////
////            review.setUser(currentUser);    // Matches your User object field
////            review.setRating(rating);
////            review.setComment(comment);
////            review.setCreatedAt(LocalDateTime.now());
////
////            // 4. Save to database
////            reviewRepo.save(review);
////
////        } catch (Exception e) {
////            e.printStackTrace();
////            return "redirect:/user/reviews?error=true";
////        }
////
////        return "redirect:/user/reviews?success=true";
////    }
////@PostMapping("/user/submit-review")
////public String submitReview(@RequestParam Integer bookingId,
////                           @RequestParam Integer rating,
////                           @RequestParam String comment,
////                           HttpSession session) {
////
////    Integer userId = (Integer) session.getAttribute("userId");
////    if (userId == null) return "redirect:/dev/login-user";
////
////    try {
////        User currentUser = userRepo.findById(userId).orElseThrow();
////
////        // --- NEW: PREVENT DUPLICATE REVIEWS ---
////        if (reviewRepo.existsByBookingIdAndUser(bookingId, currentUser)) {
////            return "redirect:/user/reviews?error=AlreadyReviewed";
////        }
////        // --------------------------------------
////
////        Review review = new Review();
////        review.setBookingId(bookingId);
////        review.setReviewId((int)(System.currentTimeMillis() % 1000000));
////        review.setUser(currentUser);
////        review.setRating(rating);
////        review.setComment(comment);
////        review.setCreatedAt(LocalDateTime.now());
////
////        reviewRepo.save(review);
////
////    } catch (Exception e) {
////        e.printStackTrace();
////        return "redirect:/user/reviews?error=true";
////    }
////
////    return "redirect:/user/reviews?success=true";
////}
////@PostMapping("/user/submit-review")
////public String submitReview(@RequestParam Integer bookingId,
////                           @RequestParam Integer rating,
////                           @RequestParam String comment,
////                           HttpSession session) {
////
////    Integer userId = (Integer) session.getAttribute("userId");
////    if (userId == null) return "redirect:/dev/login-user";
////
////    try {
////        User currentUser = userRepo.findById(userId).orElseThrow();
////
////        // --- NEW: PREVENT MULTIPLE RATINGS ---
////        if (reviewRepo.existsByBookingIdAndUser(bookingId, currentUser)) {
////            return "redirect:/user/reviews?error=AlreadyReviewed";
////        }
////        // --------------------------------------
////
////        Review review = new Review();
////        review.setBookingId(bookingId);
////        review.setReviewId((int)(System.currentTimeMillis() % 1000000));
////        review.setUser(currentUser);
////        review.setRating(rating);
////        review.setComment(comment);
////        review.setCreatedAt(LocalDateTime.now());
////
////        reviewRepo.save(review);
////
////    } catch (Exception e) {
////        e.printStackTrace();
////        return "redirect:/user/reviews?error=true";
////    }
////
////    return "redirect:/user/reviews?success=true";
////}
////@PostMapping("/user/submit-review")
////public String submitReview(@RequestParam Integer bookingId,
////                           @RequestParam Integer rating,
////                           @RequestParam String comment,
////                           HttpSession session) {
////
////    Integer userId = (Integer) session.getAttribute("userId");
////    if (userId == null) return "redirect:/dev/login-user";
////
////    try {
////        // --- UPDATED CHECK: Uses the Native Query ---
////        if (reviewRepo.hasUserAlreadyReviewed(bookingId, userId)) {
////            System.out.println("DEBUG: User already reviewed this booking.");
////            return "redirect:/user/reviews?error=AlreadyReviewed";
////        }
////        // --------------------------------------------
////
////        User currentUser = userRepo.findById(userId).orElseThrow();
////        Review review = new Review();
////        review.setBookingId(bookingId);
////        review.setReviewId((int)(System.currentTimeMillis() % 1000000));
////        review.setUser(currentUser);
////        review.setRating(rating);
////        review.setComment(comment);
////        review.setCreatedAt(LocalDateTime.now());
////
////        reviewRepo.save(review);
////
////    } catch (Exception e) {
////        e.printStackTrace();
////        return "redirect:/user/reviews?error=true";
////    }
////
////    return "redirect:/user/reviews?success=true";
////}
//@PostMapping("/user/submit-review")
//public String submitReview(@RequestParam Integer bookingId,
//                           @RequestParam Integer rating,
//                           @RequestParam String comment,
//                           HttpSession session) {
//
//    Integer userId = (Integer) session.getAttribute("userId");
//    if (userId == null) return "redirect:/dev/login-user";
//
//    try {
//        // --- FIXED CHECK: Uses Integer comparison ---
//        Integer existingCount = reviewRepo.countExistingReviews(bookingId, userId);
//        if (existingCount != null && existingCount > 0) {
//            return "redirect:/user/reviews?error=AlreadyReviewed";
//        }
//        // --------------------------------------------
//
//        User currentUser = userRepo.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        Review review = new Review();
//        review.setBookingId(bookingId);
//
//        // Ensure this ID is unique for the composite key
//        review.setReviewId((int)(System.currentTimeMillis() % 1000000));
//
//        review.setUser(currentUser);
//        review.setRating(rating);
//        review.setComment(comment);
//        review.setCreatedAt(LocalDateTime.now());
//
//        reviewRepo.save(review);
//
//    } catch (Exception e) {
//        e.printStackTrace(); // Look at IntelliJ console for the red text if it still fails
//        return "redirect:/user/reviews?error=true";
//    }
//
//    return "redirect:/user/reviews?success=true";
//}
//}