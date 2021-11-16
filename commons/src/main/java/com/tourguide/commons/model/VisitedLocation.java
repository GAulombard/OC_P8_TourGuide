package com.tourguide.commons.model;

import java.util.Date;
import java.util.UUID;

/**
 * The type Visited location.
 */
public class VisitedLocation {

    /**
     * The User id.
     */
    public UUID userId;
    /**
     * The Location.
     */
    public Location location;
    /**
     * The Time visited.
     */
    public Date timeVisited;

    /**
     * Instantiates a new Visited location.
     */
    public VisitedLocation() {
    }

    /**
     * Instantiates a new Visited location.
     *
     * @param userId      the user id
     * @param location    the location
     * @param timeVisited the time visited
     */
    public VisitedLocation(UUID userId, Location location, Date timeVisited) {
        this.userId = userId;
        this.location = location;
        this.timeVisited = timeVisited;
    }

    /**
     * Gets user id.
     *
     * @return the user id
     */
    public UUID getUserId() {
        return this.userId;
    }

    /**
     * Sets user id.
     *
     * @param userId the user id
     */
    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    /**
     * Gets location.
     *
     * @return the location
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * Sets location.
     *
     * @param location the location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Gets time visited.
     *
     * @return the time visited
     */
    public Date getTimeVisited() {
        return this.timeVisited;
    }

    /**
     * Sets time visited.
     *
     * @param timeVisited the time visited
     */
    public void setTimeVisited(Date timeVisited) {
        this.timeVisited = timeVisited;
    }

}
