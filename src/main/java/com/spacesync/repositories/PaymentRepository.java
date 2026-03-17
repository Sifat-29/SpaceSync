package com.spacesync.repositories;

import com.spacesync.models.*;
import com.spacesync.models.keys.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    @Query("SELECT SUM(p.amount) FROM Payment p JOIN p.booking b WHERE b.space.vendor.vendorId = :vendorId")
    java.math.BigDecimal calculateTotalRevenue(@Param("vendorId") Integer vendorId);
}
