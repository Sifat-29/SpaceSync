package com.spacesync.repositories;

import com.spacesync.models.CoworkingSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoworkingSpaceRepository extends JpaRepository<CoworkingSpace, Integer> {

    /**
     * THE FAIRNESS ALGORITHM
     * Calculates the Standard Deviation of distances for all members in a group to each space.
     * Returns spaces ordered by the most "fair" (lowest standard deviation).
     */
    @Query(value = """
        SELECT cs.* 
        FROM Coworking_Space cs
        CROSS JOIN Group_Membership gm
        JOIN User u ON gm.User_ID = u.User_ID
        WHERE gm.Group_ID = :groupId AND cs.Approval_Status = 'Approved'
        GROUP BY cs.Space_ID
        ORDER BY STDDEV(
            6371 * acos(cos(radians(u.Latitude)) * cos(radians(cs.Latitude)) * 
            cos(radians(cs.Longitude) - radians(u.Longitude)) + 
            sin(radians(u.Latitude)) * sin(radians(cs.Latitude)))
        ) ASC
        LIMIT 10
    """, nativeQuery = true)
    List<CoworkingSpace> findFairSpacesForGroup(@Param("groupId") Integer groupId);
}