package com.tourguide.commons.model;

import java.util.UUID;

/**
 * The type Attraction.
 */
public class Attraction extends Location {
    /**
     * The Attraction name.
     */
    public String attractionName;
    private String city;
    private String state;
    /**
     * The Attraction id.
     */
    public UUID attractionId;

    /**
     * Instantiates a new Attraction.
     */
    public Attraction() {
    }

    /**
     * Instantiates a new Attraction.
     *
     * @param longitude the longitude
     * @param latitude  the latitude
     */
    public Attraction(double longitude, double latitude) {
        super(longitude, latitude);
    }

    /**
     * Instantiates a new Attraction.
     *
     * @param attractionName the attraction name
     * @param city           the city
     * @param state          the state
     * @param attractionId   the attraction id
     */
    public Attraction(String attractionName, String city, String state, UUID attractionId) {
        this.attractionName = attractionName;
        this.city = city;
        this.state = state;
        this.attractionId = attractionId;
    }

    /**
     * Gets attraction name.
     *
     * @return the attraction name
     */
    public String getAttractionName() {
        return this.attractionName;
    }

    /**
     * Sets attraction name.
     *
     * @param attractionName the attraction name
     */
    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }

    /**
     * Gets city.
     *
     * @return the city
     */
    public String getCity() {
        return this.city;
    }

    /**
     * Sets city.
     *
     * @param city the city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets state.
     *
     * @return the state
     */
    public String getState() {
        return this.state;
    }

    /**
     * Sets state.
     *
     * @param state the state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets attraction id.
     *
     * @return the attraction id
     */
    public UUID getAttractionId() {
        return this.attractionId;
    }

    /**
     * Sets attraction id.
     *
     * @param attractionId the attraction id
     */
    public void setAttractionId(UUID attractionId) {
        this.attractionId = attractionId;
    }
}

