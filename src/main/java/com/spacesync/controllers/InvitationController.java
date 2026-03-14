package com.spacesync.controllers;

import com.spacesync.models.GroupMembership;
import com.spacesync.models.Invitation;
import com.spacesync.repositories.GroupMembershipRepository;
import com.spacesync.repositories.InvitationRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/invites")
public class InvitationController {

    @Autowired 
    private InvitationRepository invitationRepository;
    
    @Autowired 
    private GroupMembershipRepository membershipRepository;

    // View My Pending Invites
    @GetMapping("/my")
    public String viewMyInvites(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/"; // Fake login check

        // Fetch only 'Pending' invites for this user
        model.addAttribute("pendingInvites", 
            invitationRepository.findByRecipient_UserIdAndStatus(userId, "Pending"));
            
        return "groups/my-invites"; 
    }

    // Accept or Reject the Invite
    @PostMapping("/{inviteId}/respond")
    public String respondToInvite(@PathVariable Integer inviteId, @RequestParam String action) {
        // Fetch the invitation from the DB
        Invitation invite = invitationRepository.findById(inviteId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid invite Id"));
        
        if ("ACCEPT".equals(action)) {
            invite.setStatus("Accepted");
            
            // Add user to Group_Membership using Person C's Integer IDs
            GroupMembership gm = new GroupMembership();
            gm.setGroupId(invite.getGroup().getGroupId());
            gm.setUserId(invite.getRecipient().getUserId());
            gm.setJoinedAt(LocalDateTime.now());
            membershipRepository.save(gm);
            
        } else if ("REJECT".equals(action)) {
            invite.setStatus("Rejected");
        }
        
        // Save the updated status back to the database
        invitationRepository.save(invite);
        
        return "redirect:/invites/my?success=true";
    }
}