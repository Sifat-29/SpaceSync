package com.spacesync.controllers;

import com.spacesync.models.Invitation;
import com.spacesync.repositories.InvitationRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private InvitationRepository invitationRepository;

    @ModelAttribute("pendingInvitesCount")
    public Integer getPendingInvitesCount(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userId != null && "USER".equals(role)) {
            // Count how many "Pending" invites this user has received
            List<Invitation> pending = invitationRepository.findAll().stream()
                    .filter(i -> i.getRecipient() != null && i.getRecipient().getUserId().equals(userId))
                    .filter(i -> "Pending".equals(i.getStatus()))
                    .collect(Collectors.toList());

            return pending.size();
        }
        return 0;
    }
}