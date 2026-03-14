package com.spacesync.models.keys;

import java.io.Serializable;
import java.util.Objects;

public class ReviewId implements Serializable {
    private Integer bookingId;
    private Integer reviewId;
    @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; ReviewId that = (ReviewId) o; return Objects.equals(bookingId, that.bookingId) && Objects.equals(reviewId, that.reviewId); }
    @Override public int hashCode() { return Objects.hash(bookingId, reviewId); }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }
}
