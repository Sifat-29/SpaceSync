package com.spacesync.repositories;

import com.spacesync.models.GroupMembership;
import com.spacesync.models.keys.GroupMembershipId; // Required for the Composite Key
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMembershipRepository extends JpaRepository<GroupMembership, GroupMembershipId> {
    List<GroupMembership> findByUserId(Integer userId);
    List<GroupMembership> findByGroupId(Integer groupId);
}