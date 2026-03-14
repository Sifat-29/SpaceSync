package com.spacesync.models.keys;

import java.io.Serializable;
import java.util.Objects;

public class MeetingRoomId implements Serializable {
    private Integer spaceId;
    private Integer roomId;
    // Must override equals and hashCode for JPA composite keys
    @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; MeetingRoomId that = (MeetingRoomId) o; return Objects.equals(spaceId, that.spaceId) && Objects.equals(roomId, that.roomId); }
    @Override public int hashCode() { return Objects.hash(spaceId, roomId); }

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
}
