package com.spacesync.repositories;

import com.spacesync.models.*;
import com.spacesync.models.keys.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository public interface VendorRepository extends JpaRepository<Vendor, Integer> {
    Vendor findByUsername(String username);

    Vendor findByUsernameOrContactEmail(String username, String email);

    List<Vendor> findByVerificationDateIsNull();
}
