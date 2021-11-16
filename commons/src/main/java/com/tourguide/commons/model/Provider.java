package com.tourguide.commons.model;

import java.util.UUID;

/**
 * The type Provider.
 */
public class Provider {

    /**
     * The Name.
     */
    public String name;
    /**
     * The Price.
     */
    public double price;
    private UUID tripId;

    /**
     * Instantiates a new Provider.
     */
    public Provider() {
    }

    /**
     * Instantiates a new Provider.
     *
     * @param tripId the trip id
     * @param name   the name
     * @param price  the price
     */
    public Provider(UUID tripId, String name, double price) {
        this.name = name;
        this.price = price;
        this.tripId = tripId;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets price.
     *
     * @return the price
     */
    public double getPrice() {
        return this.price;
    }

    /**
     * Sets price.
     *
     * @param price the price
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Gets trip id.
     *
     * @return the trip id
     */
    public UUID getTripId() {
        return this.tripId;
    }

    /**
     * Sets trip id.
     *
     * @param tripId the trip id
     */
    public void setTripId(UUID tripId) {
        this.tripId = tripId;
    }

}
