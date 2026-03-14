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

@Controller
@RequestMapping("/groups")
public class GroupController {

    @Autowired private TeamGroupRepository teamGroupRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private InvitationRepository invitationRepository;
    @Autowired private GroupMembershipRepository membershipRepository;

    @GetMapping
    public String viewMyGroups(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/"; // Fake login check

        model.addAttribute("myGroups", teamGroupRepository.findByAdmin_UserId(userId));
        model.addAttribute("memberships", membershipRepository.findByUserId(userId));
        return "groups/list"; // We will create this HTML file next
    }

    @PostMapping("/create")
    public String createGroup(@RequestParam String groupName, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        User currentUser = userRepository.findById(userId).orElseThrow();

        // 1. Create the Group
        TeamGroup newGroup = new TeamGroup();
        newGroup.setGroupName(groupName);
        newGroup.setAdmin(currentUser);
        newGroup.setCreatedAt(LocalDateTime.now());
        TeamGroup savedGroup = teamGroupRepository.save(newGroup);

        // 2. Add the Admin to the Membership table (using Integers as per Person C's IdClass)
        GroupMembership membership = new GroupMembership();
        membership.setGroupId(savedGroup.getGroupId());
        membership.setUserId(userId);
        membership.setJoinedAt(LocalDateTime.now());
        membershipRepository.save(membership);

        return "redirect:/groups";
    }

    @PostMapping("/{groupId}/invite/{recipientId}")
    public String sendInvite(@PathVariable Integer groupId, @PathVariable Integer recipientId, HttpSession session) {
        Integer senderId = (Integer) session.getAttribute("userId");
        
        Invitation invite = new Invitation();
        invite.setSender(userRepository.getReferenceById(senderId));
        invite.setRecipient(userRepository.getReferenceById(recipientId));
        invite.setGroup(teamGroupRepository.getReferenceById(groupId)); // Person C named it 'group' not 'teamGroup'
        invite.setStatus("Pending");
        invite.setSentAt(LocalDateTime.now());
        invitationRepository.save(invite);

        return "redirect:/groups?inviteSuccess=true";
    }
}