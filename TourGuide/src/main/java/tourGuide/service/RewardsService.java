package tourGuide.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.exception.UserNotFoundException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tourGuide.service.feign.GpsUtilFeign;
import tourGuide.service.feign.RewardCentralFeign;
import tourGuide.util.DistanceCalculator;

@Service
public class RewardsService {

	private Logger logger = LoggerFactory.getLogger(RewardsService.class);

	@Autowired
	private GpsUtilFeign gpsUtilFeign;
	@Autowired
	private RewardCentralFeign rewardsCentralFeign;
	
	public RewardsService(GpsUtilFeign gpsUtilFeign, RewardCentralFeign rewardCentralFeign) {
		this.gpsUtilFeign = gpsUtilFeign;
		this.rewardsCentralFeign = rewardCentralFeign;
	}


	public List<UserReward> getUserRewards(User user) throws UserNotFoundException {
		logger.info("** Processing to get user rewards");

		if(!InternalTestHelper.getInternalUserMap().containsKey(user.getUserName())) throw new UserNotFoundException("User not found");

		return user.getUserRewards();
	}

	public void calculateRewards(User user) throws UserNotFoundException {
		logger.info("** Processing to calculate rewards. User: "+user.getUserName());

		if(!InternalTestHelper.getInternalUserMap().containsKey(user.getUserName())) throw new UserNotFoundException("User not found");

		List<VisitedLocation> userLocations = user.getVisitedLocations();
		List<Attraction> attractions = gpsUtilFeign.getAttractions();

		for(VisitedLocation visitedLocation : userLocations) {
			for(Attraction attraction : attractions) {
				int countRewardForAttraction = (int) user.getUserRewards()
						.stream()
						.filter(r -> r.attraction.attractionName.equals(attraction.attractionName))
						.count();
				if(countRewardForAttraction == 0 && DistanceCalculator.nearAttraction(visitedLocation,attraction)) {
						user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
				}
			}
		}

		logger.info("** --> User reward found: "+ user.getUserRewards().size());
	}
	
	private int getRewardPoints(Attraction attraction, User user) throws UserNotFoundException {
		logger.info("** Processing get reward points");

		if(!InternalTestHelper.getInternalUserMap().containsKey(user.getUserName())) throw new UserNotFoundException("User not found");

		return rewardsCentralFeign.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}


}
