//package com.spacesync.models;
//
//import com.spacesync.models.keys.*;
//import jakarta.persistence.*;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.List;
//
//@Entity
//@Table(name = "Vendor")
//public class Vendor {
//    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "Vendor_ID") private Integer vendorId;
//    @Column(name = "Business_Name") private String businessName;
//    @Column(name = "Business_License") private String businessLicense;
//    @Column(name = "Contact_Email") private String contactEmail;
//    @Column(name = "Verification_Date") private LocalDateTime verificationDate;
//    @Column(name = "Username", unique = true) private String username;
//    @Column(name = "Password", nullable = false) private String password;
//    @Column(name = "Approval_Status") private String approvalStatus;
//
//    @ManyToOne
//    @JoinColumn(name = "Approved_By_Admin_ID")
//    private SystemAdmin approvedByAdmin;
//
//    public Integer getVendorId() {
//        return vendorId;
//    }
//
//    public void setVendorId(Integer vendorId) {
//        this.vendorId = vendorId;
//    }
//
//    public String getBusinessName() {
//        return businessName;
//    }
//
//    public void setBusinessName(String businessName) {
//        this.businessName = businessName;
//    }
//
//    public String getBusinessLicense() {
//        return businessLicense;
//    }
//
//    public void setBusinessLicense(String businessLicense) {
//        this.businessLicense = businessLicense;
//    }
//
//    public String getContactEmail() {
//        return contactEmail;
//    }
//
//    public void setContactEmail(String contactEmail) {
//        this.contactEmail = contactEmail;
//    }
//
//    public LocalDateTime getVerificationDate() {
//        return verificationDate;
//    }
//
//    public void setVerificationDate(LocalDateTime verificationDate) {
//        this.verificationDate = verificationDate;
//    }
//
//    public SystemAdmin getApprovedByAdmin() {
//        return approvedByAdmin;
//    }
//
//    public void setApprovedByAdmin(SystemAdmin approvedByAdmin) {
//        this.approvedByAdmin = approvedByAdmin;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getApprovalStatus() { return approvalStatus; }
//
//    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
//}
package com.spacesync.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Vendor")
public class Vendor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Vendor_ID") private Integer vendorId;

    // FIX: Added length limits to prevent overflow crashes
    @Column(name = "Business_Name", length = 100) private String businessName;
    @Column(name = "Business_License", unique = true, length = 50) private String businessLicense;
    @Column(name = "Contact_Email", length = 100) private String contactEmail;

    @Column(name = "Verification_Date") private LocalDateTime verificationDate;

    @Column(name = "Username", unique = true, length = 50) private String username;
    @Column(name = "Password", nullable = false) private String password;
    @Column(name = "Approval_Status", length = 20) private String approvalStatus;

    @ManyToOne
    @JoinColumn(name = "Approved_By_Admin_ID")
    private SystemAdmin approvedByAdmin;

    // Getters and Setters
    public Integer getVendorId() { return vendorId; }
    public void setVendorId(Integer vendorId) { this.vendorId = vendorId; }
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public String getBusinessLicense() { return businessLicense; }
    public void setBusinessLicense(String businessLicense) { this.businessLicense = businessLicense; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public LocalDateTime getVerificationDate() { return verificationDate; }
    public void setVerificationDate(LocalDateTime verificationDate) { this.verificationDate = verificationDate; }
    public SystemAdmin getApprovedByAdmin() { return approvedByAdmin; }
    public void setApprovedByAdmin(SystemAdmin approvedByAdmin) { this.approvedByAdmin = approvedByAdmin; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
}