package com.spacesync.models;

import com.spacesync.models.keys.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "Audit_Log")
@IdClass(AuditLogId.class)
public class AuditLog {
    @Id @Column(name = "Booking_ID") private Integer bookingId;
    @Id @Column(name = "Log_ID") private Integer logId;

    @Column(name = "Old_Status") private String oldStatus;
    @Column(name = "New_Status") private String newStatus;
    @Column(name = "Timestamp") private LocalDateTime timestamp;

    @ManyToOne @JoinColumn(name = "Changed_By_User_ID") private User changedByUser;

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public User getChangedByUser() {
        return changedByUser;
    }

    public void setChangedByUser(User changedByUser) {
        this.changedByUser = changedByUser;
    }
}
