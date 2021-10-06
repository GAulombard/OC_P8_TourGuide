package tourGuide.util;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;

public class DistanceCalculator {

    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
    private static int defaultProximityBuffer = 60; // proximity in miles (10 by default but systematically find 0 reward)
    private static int proximityBuffer = defaultProximityBuffer; // proximity in miles
    private static int attractionProximityRange = 200; // proximity in miles (200 by default)

    public static void setProximityBuffer(int proximityBufferToSet) {
        proximityBuffer = proximityBufferToSet;
    }

    public void setDefaultProximityBuffer() {
        proximityBuffer = defaultProximityBuffer;
    }

    public static boolean isWithinAttractionProximity(Attraction attraction, Location location) {

        return getDistance(attraction, location) > attractionProximityRange ? false : true;
    }

    public static boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
        return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
    }

    public static double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
    }

}