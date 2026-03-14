package com.spacesync.repositories;

import com.spacesync.models.*;
import com.spacesync.models.keys.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    @Query(value = "SELECT SUM(p.Amount) FROM Payment p " +
            "JOIN Booking b ON p.Booking_ID = b.Booking_ID " +
            "JOIN Coworking_Space cs ON b.Space_ID = cs.Space_ID " +
            "WHERE cs.Vendor_ID = :vendorId AND b.Status != 'Cancelled'",
            nativeQuery = true)
    Double findTotalRevenueByVendorId(@Param("vendorId") Long vendorId);
}
