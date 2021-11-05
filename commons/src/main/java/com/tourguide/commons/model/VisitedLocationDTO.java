package com.tourguide.commons.model;

import java.util.Date;
import java.util.UUID;

public class VisitedLocationDTO {

    private UUID userId;
    private Location location;
    private Date timeVisited;

    public VisitedLocationDTO() {
    }

    public VisitedLocationDTO(UUID userId, Location location, Date timeVisited) {
        this.userId = userId;
        this.location = location;
        this.timeVisited = timeVisited;
    }

    public UUID getUserId() {
        return this.userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Date getTimeVisited() {
        return this.timeVisited;
    }

    public void setTimeVisited(Date timeVisited) {
        this.timeVisited = timeVisited;
    }

}
