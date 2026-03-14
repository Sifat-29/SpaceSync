package com.spacesync.repositories;

import com.spacesync.models.*;
import com.spacesync.models.keys.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Searches for users by matching part of their username OR phone number
    List<User> findByUsernameContainingOrPhoneContaining(String username, String phone);
}
