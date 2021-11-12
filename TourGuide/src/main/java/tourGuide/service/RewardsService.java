package tourGuide.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tourguide.commons.model.Attraction;
import com.tourguide.commons.model.VisitedLocation;
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
	private RewardCentralFeign rewardCentralFeign;
	
/*
	public RewardsService(GpsUtilFeign gpsUtilFeign, RewardCentralFeign rewardCentralFeign) {
		this.gpsUtilFeign = gpsUtilFeign;
		this.rewardCentralFeign = rewardCentralFeign;
	}
*/


	public List<UserReward> getUserRewards(User user) throws UserNotFoundException {
		logger.info("** Processing to get user rewards");

		if(!InternalTestHelper.getInternalUserMap().containsKey(user.getUserName())) throw new UserNotFoundException("User not found");

		return user.getUserRewards();
	}

	public void calculateRewards(User user) throws UserNotFoundException {
		logger.info("** Processing to calculate rewards. User: "+user.getUserName());

		if(!InternalTestHelper.getInternalUserMap().containsKey(user.getUserName())) throw new UserNotFoundException("User not found");

		CopyOnWriteArrayList<VisitedLocation> userLocations = new CopyOnWriteArrayList<>();
		CopyOnWriteArrayList<Attraction> attractions = new CopyOnWriteArrayList<>();

		userLocations.addAll(user.getVisitedLocations());
		attractions.addAll(gpsUtilFeign.getAttractions());

		for(VisitedLocation visitedLocation : userLocations) {
			for(Attraction attraction : attractions) {
				if (user.getUserRewards().stream()
						.noneMatch(r -> r.attraction.getAttractionName().equals(attraction.getAttractionName()))
						&& DistanceCalculator.nearAttraction(visitedLocation, attraction)) {
					user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
				}
			}
		}
	}

	public void calculateRewardsMultiThread(List<User> userList) {
		logger.info("** Multithread ** Processing to calculate all rewards");

		ExecutorService executorService = Executors.newFixedThreadPool(200);

		List<Attraction> attractions = gpsUtilFeign.getAttractions();
		List<Future<?>> listFuture = new ArrayList<>();

		for(User user : userList) {
			Future<?> future = executorService.submit( () -> {

				for(Attraction attraction : attractions) {
					//this condition is needed to avoid useless call to reward calculation that won't be stored...
					if(user.getUserRewards().stream().noneMatch(r -> r.attraction.attractionName.equals(attraction.attractionName))) {
						//rewards are calculated on the last Location only:
						VisitedLocation lastVisitedLocation = user.getVisitedLocations().get(user.getVisitedLocations().size()-1);
						if(DistanceCalculator.nearAttraction(lastVisitedLocation, attraction)) {
							try {
								user.addUserReward(new UserReward(lastVisitedLocation, attraction, getRewardPoints(attraction, user)));
							} catch (UserNotFoundException e) {
								e.printStackTrace();
							}
						}
					}
				}
			});
			listFuture.add(future);
		}

		listFuture.stream().forEach(f->{
			try {
				f.get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

	}

	private int getRewardPoints(Attraction attraction, User user) throws UserNotFoundException {
		logger.info("** Processing to get reward points");

		if(!InternalTestHelper.getInternalUserMap().containsKey(user.getUserName())) throw new UserNotFoundException("User not found");

		return rewardCentralFeign.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}


}
