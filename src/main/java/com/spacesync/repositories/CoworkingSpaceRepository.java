package com.spacesync.repositories;

import com.spacesync.models.CoworkingSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map; // Important import

@Repository
public interface CoworkingSpaceRepository extends JpaRepository<CoworkingSpace, Integer> {

    // Returns a Map so we can display the calculated scores in the HTML table
    @Query(value = """
    SELECT cs.Space_ID AS spaceId, cs.Name AS name, cs.Latitude AS latitude, cs.Longitude AS longitude,
           cs.Street AS street, cs.City AS city,
           STDDEV(6371 * acos(cos(radians(u.Latitude)) * cos(radians(cs.Latitude)) * 
           cos(radians(cs.Longitude) - radians(u.Longitude)) + 
           sin(radians(u.Latitude)) * sin(radians(cs.Latitude)))) AS fairnessScore
    FROM Coworking_Space cs
    CROSS JOIN Group_Membership gm
    JOIN User u ON gm.User_ID = u.User_ID
    WHERE gm.Group_ID = :groupId AND cs.Approval_Status = 'Approved'
    GROUP BY cs.Space_ID, cs.Name, cs.Latitude, cs.Longitude, cs.Street, cs.City
    ORDER BY fairnessScore ASC
    """, nativeQuery = true)
    List<Map<String, Object>> findFairSpacesForGroup(@Param("groupId") Integer groupId);

    // Find spaces with 'Pending' status
    List<CoworkingSpace> findByApprovalStatus(String status);

    List<CoworkingSpace> findByVendor_VendorId(Integer vendorId);
}