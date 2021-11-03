package com.tourguide.commons.model;

import java.util.UUID;

public class Attraction extends Location {
    private String attractionName;
    private String city;
    private String state;
    public UUID attractionId;

    public Attraction() {
    }

    public Attraction(double longitude, double latitude) {
        super(longitude, latitude);
    }

    public Attraction(String attractionName, String city, String state, UUID attractionId) {
        this.attractionName = attractionName;
        this.city = city;
        this.state = state;
        this.attractionId = attractionId;
    }

    public String getAttractionName() {
        return this.attractionName;
    }

    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public UUID getAttractionId() {
        return this.attractionId;
    }

    public void setAttractionId(UUID attractionId) {
        this.attractionId = attractionId;
    }
}

