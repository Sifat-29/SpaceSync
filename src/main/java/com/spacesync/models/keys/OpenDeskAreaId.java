package com.spacesync.models.keys;

import java.io.Serializable;
import java.util.Objects;

public class OpenDeskAreaId implements Serializable {
    private Integer spaceId;
    private Integer areaId;
    @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; OpenDeskAreaId that = (OpenDeskAreaId) o; return Objects.equals(spaceId, that.spaceId) && Objects.equals(areaId, that.areaId); }
    @Override public int hashCode() { return Objects.hash(spaceId, areaId); }

    public Integer getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(Integer spaceId) {
        this.spaceId = spaceId;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }
}
