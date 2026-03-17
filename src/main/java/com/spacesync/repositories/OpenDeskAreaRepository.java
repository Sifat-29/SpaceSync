package com.spacesync.repositories;

import com.spacesync.models.*;
import com.spacesync.models.keys.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpenDeskAreaRepository extends JpaRepository<OpenDeskArea, OpenDeskAreaId> {
    List<OpenDeskArea> findBySpaceId(Integer spaceId);
}
