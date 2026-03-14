package com.spacesync.models.keys;

import java.io.Serializable;
import java.util.Objects;

public class GroupMembershipId implements Serializable {
    private Integer groupId;
    private Integer userId;
    @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; GroupMembershipId that = (GroupMembershipId) o; return Objects.equals(groupId, that.groupId) && Objects.equals(userId, that.userId); }
    @Override public int hashCode() { return Objects.hash(groupId, userId); }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
