package com.tourguide.commons.model;

import java.util.UUID;

public class Provider {

    private String name;
    private double price;
    private UUID tripId;

    public Provider() {
    }

    public Provider(UUID tripId, String name, double price) {
        this.name = name;
        this.price = price;
        this.tripId = tripId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public UUID getTripId() {
        return this.tripId;
    }

    public void setTripId(UUID tripId) {
        this.tripId = tripId;
    }

}
