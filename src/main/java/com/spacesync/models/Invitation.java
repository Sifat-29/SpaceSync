package com.spacesync.models;

import com.spacesync.models.keys.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "Invitation")
public class Invitation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Invite_ID") private Integer inviteId;

    @ManyToOne @JoinColumn(name = "Sender_ID") private User sender;
    @ManyToOne @JoinColumn(name = "Recipient_ID") private User recipient;
    @ManyToOne @JoinColumn(name = "Group_ID") private TeamGroup group;

    @Column(name = "Status") private String status;
    @Column(name = "Sent_At") private LocalDateTime sentAt;

    public Integer getInviteId() {
        return inviteId;
    }

    public void setInviteId(Integer inviteId) {
        this.inviteId = inviteId;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public TeamGroup getGroup() {
        return group;
    }

    public void setGroup(TeamGroup group) {
        this.group = group;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
