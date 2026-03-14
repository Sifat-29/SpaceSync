package com.spacesync.repositories;

import com.spacesync.models.*;
import com.spacesync.models.keys.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoworkingSpaceRepository extends JpaRepository<CoworkingSpace, Integer> {

}
