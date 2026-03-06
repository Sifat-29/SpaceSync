package com.spacesync.models;

import com.spacesync.models.keys.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "Booking")
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Booking_ID") private Integer bookingId;

    @ManyToOne @JoinColumn(name = "Group_ID") private TeamGroup group;
    @ManyToOne @JoinColumn(name = "Space_ID") private CoworkingSpace space;

    @Column(name = "Booked_Room_ID") private Integer bookedRoomId;
    @Column(name = "Booked_Area_ID") private Integer bookedAreaId;

    @Column(name = "Start_Time") private LocalDateTime startTime;
    @Column(name = "End_Time") private LocalDateTime endTime;
    @Column(name = "Status") private String status;

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public TeamGroup getGroup() {
        return group;
    }

    public void setGroup(TeamGroup group) {
        this.group = group;
    }

    public CoworkingSpace getSpace() {
        return space;
    }

    public void setSpace(CoworkingSpace space) {
        this.space = space;
    }

    public Integer getBookedAreaId() {
        return bookedAreaId;
    }

    public void setBookedAreaId(Integer bookedAreaId) {
        this.bookedAreaId = bookedAreaId;
    }

    public Integer getBookedRoomId() {
        return bookedRoomId;
    }

    public void setBookedRoomId(Integer bookedRoomId) {
        this.bookedRoomId = bookedRoomId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
