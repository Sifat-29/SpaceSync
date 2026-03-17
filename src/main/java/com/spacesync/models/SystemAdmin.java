package com.spacesync.models;

import jakarta.persistence.*;

@Entity
@Table(name = "System_Admin")
public class SystemAdmin {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Admin_ID") private Integer adminId;
    @Column(name = "Username") private String username;
    @Column(name = "Role_Level") private String roleLevel;
    @Column(name = "Password", nullable = false) private String password;

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoleLevel() {
        return roleLevel;
    }

    public void setRoleLevel(String roleLevel) {
        this.roleLevel = roleLevel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
