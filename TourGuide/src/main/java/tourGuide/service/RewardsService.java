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

/**
 * The type Rewards service.
 */
@Service
public class RewardsService {

	private final Logger logger = LoggerFactory.getLogger(RewardsService.class);

	@Autowired
	private GpsUtilFeign gpsUtilFeign;
	@Autowired
	private RewardCentralFeign rewardCentralFeign;


	/**
	 * Gets user rewards.
	 *
	 * @param user the user
	 * @return the user rewards
	 * @throws UserNotFoundException the user not found exception
	 */
	public List<UserReward> getUserRewards(User user) throws UserNotFoundException {
		logger.info("** Processing to get user rewards");

		if(!InternalTestHelper.getInternalUserMap().containsKey(user.getUserName())) throw new UserNotFoundException("User not found");

		calculateRewards(user);

		return user.getUserRewards();
	}

	/**
	 * Calculate rewards for a specific user.
	 *
	 * @param user the user
	 * @throws UserNotFoundException the user not found exception
	 */
	public void calculateRewards(User user) throws UserNotFoundException {
		logger.info("** Processing to calculate rewards. User: "+user.getUserName());

		if(!InternalTestHelper.getInternalUserMap().containsKey(user.getUserName())) throw new UserNotFoundException("User not found");

		List<User> userList = new ArrayList<>();
		userList.add(user);

		calculateRewardsMultiThread(userList);
	}

	/**
	 * Calculate rewards for all users.
	 *
	 * @param userList the user list
	 */
	public void calculateRewardsMultiThread(List<User> userList) {
		logger.info("** Multithreading ** Processing to calculate all rewards");

		ExecutorService executorService = Executors.newFixedThreadPool(200);

		List<Attraction> attractions = gpsUtilFeign.getAttractions();
		List<Future<?>> listFuture = new ArrayList<>();

		for(User user : userList) {
			Future<?> future = executorService.submit( () -> {

				for(Attraction attraction : attractions) {

					if(user.getUserRewards().stream().noneMatch(r -> r.attraction.attractionName.equals(attraction.attractionName))) {

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

				e.printStackTrace();
			}
		});
		executorService.shutdown();
	}

	/**
	 * Get rewards points for a specific user and for a specific attraction.
	 *
	 * @param attraction the attraction
	 * @param user the user
	 */
	private int getRewardPoints(Attraction attraction, User user) throws UserNotFoundException {
		logger.info("** Processing to get reward points");

		if(!InternalTestHelper.getInternalUserMap().containsKey(user.getUserName())) throw new UserNotFoundException("User not found");

		return rewardCentralFeign.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}


}
