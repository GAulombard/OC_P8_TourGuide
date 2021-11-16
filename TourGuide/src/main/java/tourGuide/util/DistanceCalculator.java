package tourGuide.util;

import com.tourguide.commons.model.Attraction;
import com.tourguide.commons.model.Location;
import com.tourguide.commons.model.VisitedLocation;

/**
 * The type Distance calculator.
 */
public class DistanceCalculator {

    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
    private static int defaultProximityBuffer = 10; // proximity in miles (10 by default)
    private static int proximityBuffer = defaultProximityBuffer; // proximity in miles
    private static int attractionProximityRange = 200; // proximity in miles (200 by default)

    /**
     * Sets proximity buffer.
     *
     * @param proximityBufferToSet the proximity buffer to set
     */
    public static void setProximityBuffer(int proximityBufferToSet) {
        proximityBuffer = proximityBufferToSet;
    }

    /**
     * Gets default proximity buffer.
     *
     * @return the default proximity buffer
     */
    public static int getDefaultProximityBuffer() {
        return defaultProximityBuffer;
    }

    /**
     * Gets proximity buffer.
     *
     * @return the proximity buffer
     */
    public static int getProximityBuffer() {
        return proximityBuffer;
    }

    /**
     * Sets default proximity buffer.
     */
    public void setDefaultProximityBuffer() {
        proximityBuffer = defaultProximityBuffer;
    }

    /**
     * Sets attraction proximity range.
     *
     * @param attractionProximityRange the attraction proximity range
     */
    public static void setAttractionProximityRange(int attractionProximityRange) {
        DistanceCalculator.attractionProximityRange = attractionProximityRange;
    }

    /**
     * Is within attraction proximity boolean.
     *
     * @param attraction the attraction
     * @param location   the location
     * @return the boolean
     */
    public static boolean isWithinAttractionProximity(Attraction attraction, Location location) {

        return getDistance(attraction, location) > attractionProximityRange ? false : true;
    }

    /**
     * Near attraction boolean.
     *
     * @param visitedLocation the visited location
     * @param attraction      the attraction
     * @return the boolean
     */
    public static boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
        return (getDistance(attraction, visitedLocation.getLocation()) < proximityBuffer);
    }

    /**
     * Gets distance.
     *
     * @param loc1 the loc 1
     * @param loc2 the loc 2
     * @return the distance
     */
    public static double getDistance(Attraction loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.getLatitude());
        double lon1 = Math.toRadians(loc1.getLongitude());
        double lat2 = Math.toRadians(loc2.getLatitude());
        double lon2 = Math.toRadians(loc2.getLongitude());

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
    }

}
