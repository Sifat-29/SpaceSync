package com.spacesync.models;

import com.spacesync.models.keys.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "Team_Group")
public class TeamGroup {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Group_ID") private Integer groupId;
    @Column(name = "Group_Name") private String groupName;
    @Column(name = "Created_At") private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "Admin_ID")
    private User admin;

    @ManyToOne
    @JoinColumn(name = "Favourite_Space_ID")
    private CoworkingSpace favouriteSpace;

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public CoworkingSpace getFavouriteSpace() {
        return favouriteSpace;
    }

    public void setFavouriteSpace(CoworkingSpace favouriteSpace) {
        this.favouriteSpace = favouriteSpace;
    }
}
