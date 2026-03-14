package com.spacesync.repositories;

import com.spacesync.models.Review;
import com.spacesync.models.keys.ReviewId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, ReviewId> {

    // Change return type to Integer to avoid boolean conversion errors
    @Query(value = "SELECT COUNT(*) FROM Review WHERE Booking_ID = :bid AND User_ID = :uid", nativeQuery = true)
    Integer countExistingReviews(@Param("bid") Integer bookingId, @Param("uid") Integer userId);
}