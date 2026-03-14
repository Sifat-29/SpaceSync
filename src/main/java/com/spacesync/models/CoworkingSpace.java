package com.spacesync.models;

import com.spacesync.models.keys.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "Coworking_Space")
public class CoworkingSpace {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Space_ID") private Integer spaceId;
    @Column(name = "Name") private String name;
    @Column(name = "Street") private String street;
    @Column(name = "City") private String city;
    @Column(name = "Zip") private String zip;
    @Column(name = "Latitude", precision = 10, scale = 8) private BigDecimal latitude;
    @Column(name = "Longitude", precision = 11, scale = 8) private BigDecimal longitude;
    @Column(name = "Opening_Time") private LocalTime openingTime;
    @Column(name = "Closing_Time") private LocalTime closingTime;
    @Column(name = "Approval_Status") private String approvalStatus;

    @ManyToOne
    @JoinColumn(name = "Vendor_ID")
    private Vendor vendor;

    // Handles the M:N Space_Amenities junction table automatically!
    @ManyToMany
    @JoinTable(
            name = "Space_Amenities",
            joinColumns = @JoinColumn(name = "Space_ID"),
            inverseJoinColumns = @JoinColumn(name = "Amenity_ID")
    )
    private List<Amenity> amenities;

    public Integer getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(Integer spaceId) {
        this.spaceId = spaceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public List<Amenity> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<Amenity> amenities) {
        this.amenities = amenities;
    }

}
