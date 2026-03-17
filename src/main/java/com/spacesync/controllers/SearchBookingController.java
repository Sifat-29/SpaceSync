package com.spacesync.controllers;

import com.spacesync.models.*;
import com.spacesync.repositories.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/search")
public class SearchBookingController {

    @Autowired private CoworkingSpaceRepository spaceRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private GroupMembershipRepository membershipRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private MeetingRoomRepository roomRepository;
    @Autowired private OpenDeskAreaRepository deskRepository;
    @Autowired private TeamGroupRepository teamGroupRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private ReviewRepository reviewRepository;

    @GetMapping("/{groupId}")
    public String searchSpaces(
            @PathVariable Integer groupId,
            @RequestParam String type,
            @RequestParam(required = false) String roomDate,
            @RequestParam(required = false) Integer startHour,
            @RequestParam(required = false) Integer endHour,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) {

        LocalDateTime startDT;
        LocalDateTime endDT;
        long duration; // hours for ROOM, days for DESK

        // 1. Handle Temporal Logic & Duration
        if ("ROOM".equals(type)) {
            startDT = LocalDateTime.of(LocalDate.parse(roomDate), LocalTime.of(startHour, 0));
            endDT = LocalDateTime.of(LocalDate.parse(roomDate), LocalTime.of(endHour == 24 ? 23 : endHour, endHour == 24 ? 59 : 0));
            duration = java.time.Duration.between(startDT, endDT).toHours();
        } else {
            startDT = LocalDateTime.of(LocalDate.parse(startDate), LocalTime.MIN);
            endDT = LocalDateTime.of(LocalDate.parse(endDate), LocalTime.MAX);
            duration = ChronoUnit.DAYS.between(startDT.toLocalDate(), endDT.toLocalDate()) + 1;
        }

        // 2. Fetch Group Members
        List<Integer> memberIds = membershipRepository.findByGroupId(groupId)
                .stream().map(GroupMembership::getUserId).collect(Collectors.toList());
        List<User> members = userRepository.findAllById(memberIds);
        int groupSize = members.size();

        // 3. Search Spaces & Calculate Stats
        List<CoworkingSpace> allApproved = spaceRepository.findByApprovalStatus("Approved");
        List<Map<String, Object>> availableResults = new ArrayList<>();

        for (CoworkingSpace space : allApproved) {
            List<Map<String, Object>> inventoryList = new ArrayList<>();

            if ("ROOM".equals(type)) {
                List<MeetingRoom> rooms = roomRepository.findAll().stream()
                        .filter(r -> r.getSpaceId().equals(space.getSpaceId()) && r.getCapacity() >= groupSize)
                        .filter(r -> isRoomAvailable(r, startDT, endDT))
                        .collect(Collectors.toList());
                for(MeetingRoom r : rooms) {
                    Map<String, Object> inv = new HashMap<>();
                    inv.put("id", r.getRoomId());
                    inv.put("name", r.getRoomName());
                    inv.put("totalPrice", r.getHourlyRate().multiply(new BigDecimal(duration)));
                    inventoryList.add(inv);
                }
            } else {
                OpenDeskArea desk = deskRepository.findAll().stream()
                        .filter(d -> d.getSpaceId().equals(space.getSpaceId()))
                        .filter(d -> isDeskAreaAvailable(d, startDT, endDT, groupSize))
                        .findFirst().orElse(null);
                if (desk != null) {
                    Map<String, Object> inv = new HashMap<>();
                    inv.put("id", desk.getAreaId());
                    inv.put("name", "Shared Desk Area");
                    inv.put("totalPrice", desk.getDailyRate().multiply(new BigDecimal(groupSize)).multiply(new BigDecimal(duration)));
                    inventoryList.add(inv);
                }
            }

            if (!inventoryList.isEmpty()) {
                Map<String, Object> res = new HashMap<>();
                res.put("space", space);
                res.put("inventory", inventoryList);

                // Distances for each member
                List<Map<String, Object>> memberDists = new ArrayList<>();
                List<Double> distVals = new ArrayList<>();
                for (User u : members) {
                    double d = calculateHaversine(u.getLatitude().doubleValue(), u.getLongitude().doubleValue(),
                            space.getLatitude().doubleValue(), space.getLongitude().doubleValue());
                    memberDists.add(Map.of("name", u.getName(), "dist", d));
                    distVals.add(d);
                }
                res.put("memberDistances", memberDists);
                res.put("fairnessScore", calculateStdDev(distVals));

                // Ratings & Reviews logic
                List<Review> reviews = reviewRepository.findAll().stream()
                        .filter(rev -> {
                            Booking b = bookingRepository.findById(rev.getBookingId()).orElse(null);
                            return b != null && b.getSpace().getSpaceId().equals(space.getSpaceId());
                        }).collect(Collectors.toList());
                res.put("avgRating", reviews.stream().mapToInt(Review::getRating).average().orElse(0.0));
                res.put("reviewCount", reviews.size());
                res.put("reviews", reviews);

                availableResults.add(res);
            }
        }

        availableResults.sort(Comparator.comparingDouble(m -> (double) m.get("fairnessScore")));
        model.addAttribute("results", availableResults);
        model.addAttribute("groupId", groupId);
        model.addAttribute("startDT", startDT);
        model.addAttribute("endDT", endDT);
        model.addAttribute("type", type);
        model.addAttribute("members", members);

        return "search/map-view";
    }

    private boolean isRoomAvailable(MeetingRoom r, LocalDateTime start, LocalDateTime end) {
        return bookingRepository.findAll().stream()
                .noneMatch(b -> b.getBookedRoomId() != null && b.getBookedRoomId().equals(r.getRoomId()) &&
                        "Confirmed".equals(b.getStatus()) && b.getStartTime().isBefore(end) && b.getEndTime().isAfter(start));
    }

    private boolean isDeskAreaAvailable(OpenDeskArea d, LocalDateTime start, LocalDateTime end, int size) {
        long bookedInWindow = bookingRepository.findAll().stream()
                .filter(b -> b.getBookedAreaId() != null && b.getBookedAreaId().equals(d.getAreaId()) && "Confirmed".equals(b.getStatus()))
                .filter(b -> b.getStartTime().isBefore(end) && b.getEndTime().isAfter(start)).count();
        return (d.getTotalSeats() - (bookedInWindow * size)) >= size;
    }

    private double calculateHaversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 6371 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private double calculateStdDev(List<Double> list) {
        double mean = list.stream().mapToDouble(d -> d).average().orElse(0.0);
        double sum = list.stream().mapToDouble(d -> Math.pow(d - mean, 2)).sum();
        return Math.sqrt(sum / list.size());
    }

    @PostMapping("/book")
    public String book(@RequestParam Integer groupId, @RequestParam Integer spaceId, @RequestParam Integer invId,
                       @RequestParam String type, @RequestParam String start, @RequestParam String end,
                       @RequestParam BigDecimal amount) {
        Booking b = new Booking();
        b.setGroup(teamGroupRepository.getReferenceById(groupId));
        b.setSpace(spaceRepository.getReferenceById(spaceId));
        b.setStartTime(LocalDateTime.parse(start));
        b.setEndTime(LocalDateTime.parse(end));
        b.setStatus("Confirmed");
        if ("ROOM".equals(type)) b.setBookedRoomId(invId); else b.setBookedAreaId(invId);
        Booking saved = bookingRepository.save(b);

        Payment p = new Payment();
        p.setBooking(saved);
        p.setAmount(amount);
        p.setPaymentMethod("Credit Card");
        p.setTransactionId("TXN" + System.currentTimeMillis());
        paymentRepository.save(p);
        return "redirect:/bookings/active";
    }
}