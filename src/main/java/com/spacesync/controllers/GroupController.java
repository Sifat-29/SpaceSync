package com.spacesync.controllers;

import com.spacesync.models.*;
import com.spacesync.models.keys.GroupMembershipId;
import com.spacesync.repositories.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors; // <--- ADD THIS LINE

@Controller
@RequestMapping("/groups")
public class GroupController {

    @Autowired private TeamGroupRepository teamGroupRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private InvitationRepository invitationRepository;
    @Autowired private GroupMembershipRepository membershipRepository;
    @Autowired private CoworkingSpaceRepository spaceRepository;

    // 1. List all groups
    @GetMapping
    public String viewMyGroups(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        // 1. Groups where I am the Boss
        model.addAttribute("adminGroups", teamGroupRepository.findByAdmin_UserId(userId));

        // 2. Groups where I am just a member
        List<Integer> myMemberGroupIds = membershipRepository.findByUserId(userId)
                .stream().map(GroupMembership::getGroupId).collect(Collectors.toList());

        List<TeamGroup> joinedGroups = teamGroupRepository.findAllById(myMemberGroupIds)
                .stream()
                .filter(g -> !g.getAdmin().getUserId().equals(userId))
                .collect(Collectors.toList());

        model.addAttribute("joinedGroups", joinedGroups);
        return "groups/list";
    }

    // 2. Create a group
    @PostMapping("/create")
    public String createGroup(@RequestParam String groupName, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        User currentUser = userRepository.findById(userId).orElseThrow();

        TeamGroup newGroup = new TeamGroup();
        newGroup.setGroupName(groupName);
        newGroup.setAdmin(currentUser);
        newGroup.setCreatedAt(LocalDateTime.now());
        TeamGroup savedGroup = teamGroupRepository.save(newGroup);

        GroupMembership membership = new GroupMembership();
        membership.setGroupId(savedGroup.getGroupId());
        membership.setUserId(userId);
        membership.setJoinedAt(LocalDateTime.now());
        membershipRepository.save(membership);

        return "redirect:/groups/" + savedGroup.getGroupId();
    }

    // 3. View a specific group's details
    @GetMapping("/{groupId}")
    public String groupDetails(@PathVariable Integer groupId, Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        TeamGroup group = teamGroupRepository.findById(groupId).orElseThrow();

        // Fetch the actual User objects for everyone in this group
        List<Integer> userIds = membershipRepository.findByGroupId(groupId)
                .stream().map(m -> m.getUserId()).collect(Collectors.toList());
        List<User> members = userRepository.findAllById(userIds);

        model.addAttribute("group", group);
        model.addAttribute("members", members); // Passing actual User objects
        return "groups/details";
    }

    // 4. Invite a user by Username or Phone
    @PostMapping("/{groupId}/invite")
    public String inviteUser(@PathVariable Integer groupId, @RequestParam String identifier, HttpSession session) {
        User recipient = userRepository.findByUsernameOrPhone(identifier, identifier);
        if (recipient == null) return "redirect:/groups/" + groupId + "?error=user_not_found";

        Invitation invite = new Invitation();
        invite.setSender(userRepository.getReferenceById((Integer)session.getAttribute("userId")));
        invite.setRecipient(recipient);
        invite.setGroup(teamGroupRepository.getReferenceById(groupId));
        invite.setStatus("Pending");
        invite.setSentAt(LocalDateTime.now());
        invitationRepository.save(invite);
        return "redirect:/groups/" + groupId + "?success=invited";
    }

    @PostMapping("/{groupId}/leave")
    public String leaveGroup(@PathVariable Integer groupId, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        // Standard JPA composite key deletion
        GroupMembershipId id = new GroupMembershipId();
        id.setGroupId(groupId);
        id.setUserId(userId);
        membershipRepository.deleteById(id);
        return "redirect:/groups?left=true";
    }

    @PostMapping("/{groupId}/favorite/{spaceId}")
    public String setFavorite(@PathVariable Integer groupId, @PathVariable Integer spaceId) {
        TeamGroup group = teamGroupRepository.findById(groupId).orElseThrow();
        group.setFavouriteSpace(spaceRepository.getReferenceById(spaceId));
        teamGroupRepository.save(group);
        return "redirect:/groups/" + groupId + "?favSuccess=true";
    }
}