package tourGuide.model;

import com.tourguide.commons.model.Attraction;
import com.tourguide.commons.model.VisitedLocation;

/**
 * The type User reward.
 */
public class UserReward {

    /**
     * The Visited location.
     */
    public final VisitedLocation visitedLocation;
    /**
     * The Attraction.
     */
    public final Attraction attraction;
	private int rewardPoints;

    /**
     * Instantiates a new User reward.
     *
     * @param visitedLocation the visited location
     * @param attraction      the attraction
     * @param rewardPoints    the reward points
     */
    public UserReward(VisitedLocation visitedLocation, Attraction attraction, int rewardPoints) {
		this.visitedLocation = visitedLocation;
		this.attraction = attraction;
		this.rewardPoints = rewardPoints;
	}

    /**
     * Instantiates a new User reward.
     *
     * @param visitedLocation the visited location
     * @param attraction      the attraction
     */
    public UserReward(VisitedLocation visitedLocation, Attraction attraction) {
		this.visitedLocation = visitedLocation;
		this.attraction = attraction;
	}

    /**
     * Sets reward points.
     *
     * @param rewardPoints the reward points
     */
    public void setRewardPoints(int rewardPoints) {
		this.rewardPoints = rewardPoints;
	}

    /**
     * Gets reward points.
     *
     * @return the reward points
     */
    public int getRewardPoints() {
		return rewardPoints;
	}
	
}
