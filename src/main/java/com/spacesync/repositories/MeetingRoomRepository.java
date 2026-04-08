package com.spacesync.repositories;

import com.spacesync.models.*;
import com.spacesync.models.keys.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, MeetingRoomId> {

    List<MeetingRoom> findBySpaceId(Integer spaceId);

    // TASK 6: CONCURRENCY CONTROL (PESSIMISTIC LOCKING)
    // Executes "SELECT ... FOR UPDATE" in MySQL to prevent Double-Booking race conditions.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM MeetingRoom m WHERE m.spaceId = :spaceId AND m.roomId = :roomId")
    Optional<MeetingRoom> findByIdForUpdate(@Param("spaceId") Integer spaceId, @Param("roomId") Integer roomId);
}