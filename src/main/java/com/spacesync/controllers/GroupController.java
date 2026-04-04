package com.spacesync.controllers;

import com.spacesync.models.*;
import com.spacesync.models.keys.GroupMembershipId;
import com.spacesync.repositories.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional; // <-- ADDED FOR DELETE

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/groups")
public class GroupController {

    @Autowired private TeamGroupRepository teamGroupRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private InvitationRepository invitationRepository;
    @Autowired private GroupMembershipRepository membershipRepository;
    @Autowired private CoworkingSpaceRepository spaceRepository;
    @Autowired private AmenityRepository amenityRepository;
    @Autowired private BookingRepository bookingRepository; // <-- ADDED FOR DELETE

    @GetMapping
    public String viewMyGroups(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        model.addAttribute("adminGroups", teamGroupRepository.findByAdmin_UserId(userId));

        List<Integer> myMemberGroupIds = membershipRepository.findByUserId(userId)
                .stream().map(GroupMembership::getGroupId).collect(Collectors.toList());

        List<TeamGroup> joinedGroups = teamGroupRepository.findAllById(myMemberGroupIds)
                .stream()
                .filter(g -> !g.getAdmin().getUserId().equals(userId))
                .collect(Collectors.toList());

        model.addAttribute("joinedGroups", joinedGroups);
        return "groups/list";
    }

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

    @GetMapping("/{groupId}")
    public String groupDetails(@PathVariable Integer groupId, Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        TeamGroup group = teamGroupRepository.findById(groupId).orElseThrow();

        List<Integer> userIds = membershipRepository.findByGroupId(groupId)
                .stream().map(GroupMembership::getUserId).collect(Collectors.toList());
        List<User> members = userRepository.findAllById(userIds);

        model.addAttribute("group", group);
        model.addAttribute("members", members);

        // Send all amenities to the frontend for the filter checkboxes
        model.addAttribute("allAmenities", amenityRepository.findAll());

        return "groups/details";
    }

    @PostMapping("/{groupId}/invite")
    public String inviteUser(@PathVariable Integer groupId, @RequestParam String identifier, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        TeamGroup group = teamGroupRepository.findById(groupId).orElseThrow();

        // Security Check - Only Admin can invite
        if (!group.getAdmin().getUserId().equals(userId)) {
            return "redirect:/groups/" + groupId + "?error=unauthorized";
        }

        User recipient = userRepository.findByUsernameOrPhone(identifier, identifier);
        if (recipient == null) return "redirect:/groups/" + groupId + "?error=user_not_found";

        // Check if the user is ALREADY in the group
        boolean isAlreadyMember = membershipRepository.findByGroupId(groupId).stream()
                .anyMatch(m -> m.getUserId().equals(recipient.getUserId()));
        if (isAlreadyMember) {
            return "redirect:/groups/" + groupId + "?error=already_member";
        }

        // Check if a "Pending" invite already exists for this exact user and group
        boolean hasPendingInvite = invitationRepository.findAll().stream()
                .anyMatch(i -> i.getGroup() != null && i.getGroup().getGroupId().equals(groupId)
                        && i.getRecipient() != null && i.getRecipient().getUserId().equals(recipient.getUserId())
                        && "Pending".equals(i.getStatus()));

        if (hasPendingInvite) {
            return "redirect:/groups/" + groupId + "?error=already_invited";
        }

        Invitation invite = new Invitation();
        invite.setSender(userRepository.getReferenceById(userId));
        invite.setRecipient(recipient);
        invite.setGroup(group);
        invite.setStatus("Pending");
        invite.setSentAt(LocalDateTime.now());
        invitationRepository.save(invite);

        return "redirect:/groups/" + groupId + "?success=invited";
    }

    @PostMapping("/{groupId}/leave")
    public String leaveGroup(@PathVariable Integer groupId, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        GroupMembershipId id = new GroupMembershipId();
        id.setGroupId(groupId);
        id.setUserId(userId);
        membershipRepository.deleteById(id);
        return "redirect:/groups?left=true";
    }

    @PostMapping("/{groupId}/favorite/{spaceId}")
    public String setFavorite(@PathVariable Integer groupId, @PathVariable Integer spaceId, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        TeamGroup group = teamGroupRepository.findById(groupId).orElseThrow();

        if (!group.getAdmin().getUserId().equals(userId)) {
            return "redirect:/groups/" + groupId + "?error=unauthorized";
        }

        // Explicitly fetched the space from DB to ensure Hibernate saves it correctly
        CoworkingSpace space = spaceRepository.findById(spaceId).orElseThrow();
        group.setFavouriteSpace(space);
        teamGroupRepository.save(group);
        return "redirect:/groups/" + groupId + "?favSuccess=true";
    }

    // FIX: The "Bulldozer" Delete Method
    @PostMapping("/{groupId}/delete")
    @Transactional
    public String deleteGroup(@PathVariable Integer groupId, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        TeamGroup group = teamGroupRepository.findById(groupId).orElseThrow();

        // Security Check - Only Admin can delete the group
        if (!group.getAdmin().getUserId().equals(userId)) {
            return "redirect:/groups/" + groupId + "?error=unauthorized";
        }

        try {
            // 1. Wipe out any bookings tied to this group so the DB doesn't block us
            List<Booking> bookings = bookingRepository.findAll().stream()
                    .filter(b -> b.getGroup() != null && b.getGroup().getGroupId().equals(groupId))
                    .collect(Collectors.toList());
            bookingRepository.deleteAll(bookings);

            // 2. Wipe out any pending invitations sent from this group
            List<Invitation> invites = invitationRepository.findAll().stream()
                    .filter(i -> i.getGroup() != null && i.getGroup().getGroupId().equals(groupId))
                    .collect(Collectors.toList());
            invitationRepository.deleteAll(invites);

            // 3. Remove all users from the group (delete memberships)
            List<GroupMembership> memberships = membershipRepository.findAll().stream()
                    .filter(m -> m.getGroupId().equals(groupId))
                    .collect(Collectors.toList());
            membershipRepository.deleteAll(memberships);

            // 4. Finally, delete the empty group!
            teamGroupRepository.deleteById(groupId);

            // Redirect back to the previous page (the "My Groups" list)
            return "redirect:/groups?deleted=true";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/groups/" + groupId + "?error=has_bookings";
        }
    }
}