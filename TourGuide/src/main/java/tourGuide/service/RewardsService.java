package tourGuide.service;

import java.util.List;

import com.jsoniter.output.JsonStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.model.User;
import tourGuide.model.UserReward;

@Service
public class RewardsService {

	private Logger logger = LoggerFactory.getLogger(RewardsService.class);

    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
    private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;
	
	public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
	}
	
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}

	//FIXME: never used
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}
	
	public void calculateRewards(User user) {
		logger.info("********** Processing **********");
		logger.info("** Processing to calculate rewards. User: "+user.getUserName());

		List<VisitedLocation> userLocations = user.getVisitedLocations();
		List<Attraction> attractions = gpsUtil.getAttractions();

		for(VisitedLocation visitedLocation : userLocations) {
			for(Attraction attraction : attractions) {
				int countRewardForAttraction = (int) user.getUserRewards()
						.stream()
						.filter(r -> r.attraction.attractionName.equals(attraction.attractionName))
						.count();
				if(countRewardForAttraction == 0 && nearAttraction(visitedLocation,attraction)) {
						//logger.debug("countRewardForAttraction: "+countRewardForAttraction+" nearAttraction: "+nearAttraction(visitedLocation,attraction));
						user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
				}
			}
		}

		logger.info("** User rewards found: "+ user.getUserRewards().size());
	}
	
	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {

		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}
	
	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}
	
	private int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}
	
	public double getDistance(Location loc1, Location loc2) {
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
