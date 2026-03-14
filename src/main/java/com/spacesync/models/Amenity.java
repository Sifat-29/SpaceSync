package com.spacesync.models;

import com.spacesync.models.keys.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "Amenity")
public class Amenity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Amenity_ID") private Integer amenityId;
    @Column(name = "Amenity_Name") private String amenityName;

    @ManyToOne
    @JoinColumn(name = "Created_By_Admin_ID")
    private SystemAdmin createdByAdmin;

    public Integer getAmenityId() {
        return amenityId;
    }

    public void setAmenityId(Integer amenityId) {
        this.amenityId = amenityId;
    }

    public String getAmenityName() {
        return amenityName;
    }

    public void setAmenityName(String amenityName) {
        this.amenityName = amenityName;
    }

    public SystemAdmin getCreatedByAdmin() {
        return createdByAdmin;
    }

    public void setCreatedByAdmin(SystemAdmin createdByAdmin) {
        this.createdByAdmin = createdByAdmin;
    }
}
