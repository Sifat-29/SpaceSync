package com.spacesync.repositories;

import com.spacesync.models.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Integer> {
    // Looks inside the 'recipient' object and matches the status
    List<Invitation> findByRecipient_UserIdAndStatus(Integer recipientId, String status);
}