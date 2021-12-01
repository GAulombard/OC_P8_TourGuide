package tourGuide.helper;

import com.tourguide.commons.model.Location;
import com.tourguide.commons.model.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tourGuide.model.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.IntStream;


/**
 * The type Internal test helper.
 * This class is for test purpose only.
 * <p>
 * Database connection will be used for external users,
 * but for testing purposes internal users are provided and stored in memory.
 * </p>
 */
public class InternalTestHelper {

	private Logger logger = LoggerFactory.getLogger(InternalTestHelper.class);
	private static int internalUserNumber = 100;// Set this default up to 100,000 for testing
	private static final String tripPricerApiKey = "test-server-api-key";
	private static Map<String, User> internalUserMap = new HashMap<>();

	/**
	 * Instantiates a new Internal test helper.
	 */
	public InternalTestHelper() {
	}

	/**********************************************************************************
	 *
	 * Methods Below: For Internal Testing
	 *
	 * @param internalUserNumber the internal user number
	 */
	public static void setInternalUserNumber(int internalUserNumber) {
		InternalTestHelper.internalUserNumber = internalUserNumber;
	}

	/**
	 * Free internal user map.
	 */
	public static void freeInternalUserMap() {
		internalUserMap.clear();
	}

	/**
	 * Gets internal user number.
	 *
	 * @return the internal user number
	 */
	public static int getInternalUserNumber() {
		return internalUserNumber;
	}

	/**
	 * Gets trip pricer api key.
	 *
	 * @return the trip pricer api key
	 */
	public String getTripPricerApiKey() {
		return tripPricerApiKey;
	}

	/**
	 * Gets internal user map.
	 *
	 * @return the internal user map
	 */
	public static Map<String,User> getInternalUserMap() {
		return internalUserMap;
	}

	/**
	 * Sets internal user map to null.
	 */
	public static void setInternalUserMapToNull() { //for testing purpose only
		InternalTestHelper.internalUserMap = null;
	}

	/**
	 * Initialize internal users.
	 */
	public void initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);

			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
	}

	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i-> {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}

	private double generateRandomLongitude() {
		double leftLimit = -149.88; //-180 by default
		double rightLimit = -71.07; //180 by default
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private double generateRandomLatitude() {
		double leftLimit = 33.6; //-85.05112878 by default
		double rightLimit = 61.22; //85.05112878 by default
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}
}
