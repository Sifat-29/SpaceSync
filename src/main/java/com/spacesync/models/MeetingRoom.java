package com.spacesync.models;

import com.spacesync.models.keys.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "Meeting_Room")
@IdClass(MeetingRoomId.class)
public class MeetingRoom {
    @Id @Column(name = "Space_ID") private Integer spaceId;
    @Id @Column(name = "Room_ID") private Integer roomId;
    @Column(name = "Room_Name") private String roomName;
    @Column(name = "Capacity") private Integer capacity;
    @Column(name = "Hourly_Rate") private BigDecimal hourlyRate;

    public Integer getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(Integer spaceId) {
        this.spaceId = spaceId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
