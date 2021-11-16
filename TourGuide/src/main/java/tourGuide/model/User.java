package tourGuide.model;

import java.util.*;

import com.tourguide.commons.model.VisitedLocation;
import org.springframework.stereotype.Component;
import com.tourguide.commons.model.Provider;


/**
 * The type User.
 */
public class User {

	private final UUID userId;
	private final String userName;
	private String phoneNumber;
	private String emailAddress;
	private Date latestLocationTimestamp;
	private List<VisitedLocation> visitedLocations = new ArrayList<>();
	private List<UserReward> userRewards = new ArrayList<>();
	private UserPreferences userPreferences = new UserPreferences();
	private List<Provider> tripDeals = new ArrayList<>();


	/**
	 * Instantiates a new User.
	 *
	 * @param userId       the user id
	 * @param userName     the user name
	 * @param phoneNumber  the phone number
	 * @param emailAddress the email address
	 */
	public User(UUID userId, String userName, String phoneNumber, String emailAddress) {
		this.userId = userId;
		this.userName = userName;
		this.phoneNumber = phoneNumber;
		this.emailAddress = emailAddress;
	}

	/**
	 * Gets user id.
	 *
	 * @return the user id
	 */
	public UUID getUserId() {
		return userId;
	}

	/**
	 * Gets user name.
	 *
	 * @return the user name
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Sets phone number.
	 *
	 * @param phoneNumber the phone number
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * Gets phone number.
	 *
	 * @return the phone number
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * Sets email address.
	 *
	 * @param emailAddress the email address
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * Gets email address.
	 *
	 * @return the email address
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * Sets latest location timestamp.
	 *
	 * @param latestLocationTimestamp the latest location timestamp
	 */
	public void setLatestLocationTimestamp(Date latestLocationTimestamp) { //fixme:?? always null
		this.latestLocationTimestamp = latestLocationTimestamp;
	}

	/**
	 * Gets latest location timestamp.
	 *
	 * @return the latest location timestamp
	 */
	public Date getLatestLocationTimestamp() {
		return latestLocationTimestamp;
	}

	/**
	 * Add to visited locations.
	 *
	 * @param visitedLocation the visited location
	 */
	public void addToVisitedLocations(VisitedLocation visitedLocation) {
		visitedLocations.add(visitedLocation);
	}

	/**
	 * Gets visited locations.
	 *
	 * @return the visited locations
	 */
	public List<VisitedLocation> getVisitedLocations() {
		return visitedLocations;
	}

	/**
	 * Clear visited locations.
	 */
	public void clearVisitedLocations() {
		visitedLocations.clear();
	}

	/**
	 * Add user reward.
	 *
	 * @param userReward the user reward
	 */
	public void addUserReward(UserReward userReward) {
		if(userRewards
				.stream()
				.noneMatch(r -> r.attraction.getAttractionName().equals(userReward.attraction.getAttractionName()))) {
			userRewards.add(userReward);
		}
	}

	/**
	 * Gets user rewards.
	 *
	 * @return the user rewards
	 */
	public List<UserReward> getUserRewards() {
		return userRewards;
	}

	/**
	 * Gets user preferences.
	 *
	 * @return the user preferences
	 */
	public UserPreferences getUserPreferences() {
		return userPreferences;
	}

	/**
	 * Sets user preferences.
	 *
	 * @param userPreferences the user preferences
	 */
	public void setUserPreferences(UserPreferences userPreferences) {
		this.userPreferences = userPreferences;
	}

	/**
	 * Gets last visited location.
	 *
	 * @return the last visited location
	 */
	public VisitedLocation getLastVisitedLocation() {
		List<VisitedLocation> visitedLocationList = getVisitedLocations();
		Comparator<VisitedLocation> byTimeVisited = new Comparator<VisitedLocation>() {

			public int compare(VisitedLocation o1, VisitedLocation o2) {
				return Long.valueOf(o1.timeVisited.getTime()).compareTo(o2.timeVisited.getTime());
			}
		};
		Collections.sort(visitedLocationList,byTimeVisited.reversed());

		return visitedLocationList.get(0);
	}

	/**
	 * Sets trip deals.
	 *
	 * @param tripDeals the trip deals
	 */
	public void setTripDeals(List<Provider> tripDeals) {
		this.tripDeals = tripDeals;
	}

	/**
	 * Gets trip deals.
	 *
	 * @return the trip deals
	 */
	public List<Provider> getTripDeals() {
		return tripDeals;
	}

}
