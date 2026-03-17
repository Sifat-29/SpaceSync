package com.spacesync.repositories;

import com.spacesync.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Exact match for inviting users
    User findByUsernameOrPhone(String username, String phone);
}