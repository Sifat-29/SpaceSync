package com.spacesync.models.keys;

import java.io.Serializable;
import java.util.Objects;

public class AuditLogId implements Serializable {
    private Integer bookingId;
    private Integer logId;
    @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; AuditLogId that = (AuditLogId) o; return Objects.equals(bookingId, that.bookingId) && Objects.equals(logId, that.logId); }
    @Override public int hashCode() { return Objects.hash(bookingId, logId); }

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }
}
