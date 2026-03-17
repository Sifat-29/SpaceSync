package com.spacesync.models;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "User")
public class User {

    @Id // for primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // telling this is auto increment
    @Column(name = "User_ID")
    private Integer userId;

    @Column(name = "Username", unique = true, nullable = false)
    private String username;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "Phone")
    private String phone;

    // We use BigDecimal for decimals to keep the 10,8 precision
    @Column(name = "Latitude", precision = 10, scale = 8, nullable = false)
    private BigDecimal latitude;

    @Column(name = "Longitude", precision = 11, scale = 8, nullable = false)
    private BigDecimal longitude;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
