package tourGuide.service;

import java.util.*;
import java.util.stream.Collectors;

import com.jsoniter.output.JsonStream;
import gpsUtil.location.Location;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.exception.UserAlreadyExistsException;
import tourGuide.exception.UserNotFoundException;
import tourGuide.exception.UsersGatheringException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.NearbyAttraction;
import tourGuide.model.UserPreferences;
import tourGuide.tracker.Tracker;
import tourGuide.model.User;
import tourGuide.util.DistanceCalculator;
import tripPricer.Provider;
import tripPricer.TripPricer;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

@Service
public class TourGuideService {

	private final Logger logger = LoggerFactory.getLogger(TourGuideService.class);

	//Libs
	private final GpsUtil gpsUtil;
	private final RewardCentral rewardCentral = new RewardCentral();
	private final TripPricer tripPricer = new TripPricer();


	private final RewardsService rewardsService;
	public final Tracker tracker;
	private final InternalTestHelper internalTestHelper = new InternalTestHelper();
	boolean testMode = true;

	
	public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService) {
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;
		
		if(testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			internalTestHelper.initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this);
		addShutDownHook();
	}
	
	public VisitedLocation getUserLocation(User user) throws UserNotFoundException {
		logger.info("** Processing to get user location. User: "+user.getUserName());

		if(!internalTestHelper.getInternalUserMap().containsKey(user.getUserName())) throw new UserNotFoundException("User not found");

		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ? user.getLastVisitedLocation() : trackUserLocation(user);

		return visitedLocation;
	}
	
	public User getUser(String userName) throws UserNotFoundException {
		logger.info("** Processing to get user by username");

		if(!internalTestHelper.getInternalUserMap().containsKey(userName)) throw new UserNotFoundException("User not found");

		return internalTestHelper.getInternalUserMap().get(userName);
	}
	
	public List<User> getAllUsers() throws UsersGatheringException {
		logger.info("** Processing to get all users");

		List<User> users = new ArrayList<>();

		try {
			users = internalTestHelper.getInternalUserMap().values().stream().collect(Collectors.toList());
		}catch (Exception e) {
			logger.error("ERROR: Impossible to get all users");
			throw new UsersGatheringException(e.getMessage());
		}
		return users;
	}
	
	public void addUser(User user) throws UserAlreadyExistsException {
		logger.info("** Processing to add new user: "+user.getUserName());

		if(!internalTestHelper.getInternalUserMap().containsKey(user.getUserName())) {
			internalTestHelper.getInternalUserMap().put(user.getUserName(), user);
		} else {
			throw new UserAlreadyExistsException("ERROR: User already exists");
		}
	}

	public List<Provider> getTripDeals(User user) throws UserNotFoundException {
		logger.info("** Processing to get trip deals. User: "+user.getUserName());
		List<Provider> providers = new ArrayList<>();

		if(!internalTestHelper.getInternalUserMap().containsKey(user.getUserName())) throw new UserNotFoundException("User not found");

		int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();



		List<Provider> tempProviders = tripPricer.getPrice(internalTestHelper.getTripPricerApiKey(), user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);

		tempProviders.forEach(provider -> {
			if(provider.price <= user.getUserPreferences().getHighPricePoint().getNumber().doubleValueExact()) {
				logger.info("** --> Provider found: "+provider.name);
				providers.add(provider);
			}
		});

		user.setTripDeals(providers);

		return providers;
	}
	
	public VisitedLocation trackUserLocation(User user) throws UserNotFoundException {
		logger.info("** Processing to track user location. User: "+user.getUserName());
		Locale.setDefault(new Locale("en", "US"));

		if(!internalTestHelper.getInternalUserMap().containsKey(user.getUserName())) throw new UserNotFoundException("User not found");

		VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user); //fixme: why is this here ?

		return visitedLocation;
	}

	public List<NearbyAttraction> getNearByAttractions(VisitedLocation visitedLocation,User user) {// Get the closest five tourist attractions to the user - no matter how far away they are.
		logger.info("** Processing to get nearby attractions.");
		logger.info("** -->User: "+user.getUserName());
		logger.info("** -->Latitude: "+visitedLocation.location.latitude+" Longitude: "+visitedLocation.location.longitude);


		List<NearbyAttraction> nearbyAttractions = new ArrayList<>();
		List<Attraction> attractions = gpsUtil.getAttractions();

		attractions.forEach(attraction -> {
			NearbyAttraction nearbyAttraction = new NearbyAttraction();
			nearbyAttraction.setAttractionName(attraction.attractionName);
			nearbyAttraction.setAttractionLatitude(attraction.latitude);
			nearbyAttraction.setAttractionLongitude(attraction.longitude);
			nearbyAttraction.setUserLatitude(visitedLocation.location.latitude);
			nearbyAttraction.setUserLongitude(visitedLocation.location.longitude);
			nearbyAttraction.setDistanceBetweenUserAndAttraction(DistanceCalculator.getDistance(attraction,visitedLocation.location));
			nearbyAttraction.setRewardPoint(rewardCentral.getAttractionRewardPoints(attraction.attractionId,user.getUserId()));
			nearbyAttractions.add(nearbyAttraction);
		});

		Collections.sort(nearbyAttractions,Comparator.comparingDouble(NearbyAttraction::getDistanceBetweenUserAndAttraction));
		nearbyAttractions.subList(5,nearbyAttractions.size()).clear();

		return nearbyAttractions;
	}

	public Map<String, Location> getAllCurrentLocation() throws UsersGatheringException {
		logger.info("** Processing to get all user's current location");
		Map<String,Location> allCurrentLocations = new HashMap<>();
		List<User> users = getAllUsers();

		users.forEach(user -> {
			allCurrentLocations.put(user.getUserId().toString(),user.getLastVisitedLocation().location);
		});

		return allCurrentLocations;
	}

	public void updatePreferences(User user, UserPreferences newPreferences) {
		logger.info("** Processing to update preferences");

		//CurrencyUnit currency = Monetary.getCurrency("USD");
		UserPreferences actualPreferences = user.getUserPreferences();

		actualPreferences.setNumberOfAdults(newPreferences.getNumberOfAdults());
		actualPreferences.setNumberOfChildren(newPreferences.getNumberOfChildren());
		actualPreferences.setAttractionProximity(newPreferences.getAttractionProximity());
/*		actualPreferences.setHighPricePoint(Money.of(newPreferences.getHighPricePoint(),currency));
		actualPreferences.setLowerPricePoint(Money.of(newPreferences.getLowerPricePoint(),currency));*/
		actualPreferences.setHighPricePoint(newPreferences.getHighPricePoint().getNumber().intValue());
		actualPreferences.setLowerPricePoint(newPreferences.getLowerPricePoint().getNumber().intValue());
		actualPreferences.setTicketQuantity(newPreferences.getTicketQuantity());
		actualPreferences.setTripDuration(newPreferences.getTripDuration());

	}

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		      public void run() {
		        tracker.stopTracking();
		      } 
		    }); 
	}



}
