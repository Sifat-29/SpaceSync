package com.spacesync.repositories;

import com.spacesync.models.TeamGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamGroupRepository extends JpaRepository<TeamGroup, Integer> {
    // Person C mapped 'admin' as an object, so we use _UserId to extract the ID
    List<TeamGroup> findByAdmin_UserId(Integer adminId);
}