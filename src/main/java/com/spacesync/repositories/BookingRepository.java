package com.spacesync.repositories;

import com.spacesync.models.*;
import com.spacesync.models.keys.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    // Inside BookingRepository.java
    @Query(value = "SELECT b.* FROM Booking b " +
            "JOIN Team_Group g ON b.Group_ID = g.Group_ID " +
            "JOIN Group_Membership gm ON g.Group_ID = gm.Group_ID " +
            "WHERE gm.User_ID = :userId AND b.Status = 'Completed' " +
            "AND b.Booking_ID NOT IN (SELECT r.Booking_ID FROM Review r)",
            nativeQuery = true)
    List<Booking> findCompletedByUserGroups(@Param("userId") Integer userId);
}
