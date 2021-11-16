package tourGuide.model;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.javamoney.moneta.Money;
import tourGuide.validator.PriceCheck;

/**
 * The type User preferences.
 */
@PriceCheck
public class UserPreferences {

	private CurrencyUnit currency = Monetary.getCurrency("USD");
	@Min(value = 1, message = "The proximity should be a positive number and at least 1")
	private int attractionProximity = Integer.MAX_VALUE; //Integer.MAX_VALUE by default
	@Min(value = 0, message = "Lower price should be a positive number or 0")
	private double lowerPricePoint = 0; //0 by default
	@Min(value = 0, message = "High price should be a positive number")
	private double highPricePoint = Integer.MAX_VALUE; //Integer.MAX_VALUE by default
	@Min(value = 1, message = "Trip duration should be positive and at least 1")
	private int tripDuration = 1; //1 by default
	@Min(value = 1, message = "Ticket Quantity should be positive and at least 1")
	private int ticketQuantity = 1; //1 by default
	@Min(value = 1, message = "Number of adults should be positive and at least 1")
	private int numberOfAdults = 1; //1 by default
	@Min(value = 0, message = "Number of children should be positive and at least 0")
	private int numberOfChildren = 0; //0 by default

	/**
	 * Instantiates a new User preferences.
	 */
	public UserPreferences() {
	}

	/**
	 * Sets attraction proximity.
	 *
	 * @param attractionProximity the attraction proximity
	 */
	public void setAttractionProximity(int attractionProximity) {
		this.attractionProximity = attractionProximity;
	}

	/**
	 * Gets attraction proximity.
	 *
	 * @return the attraction proximity
	 */
	public int getAttractionProximity() {
		return attractionProximity;
	}

	/**
	 * Gets lower price point.
	 *
	 * @return the lower price point
	 */
	public Money getLowerPricePoint() {
		return Money.of(lowerPricePoint, currency);
	}

	/**
	 * Gets currency.
	 *
	 * @return the currency
	 */
	public CurrencyUnit getCurrency() {
		return currency;
	}

	/**
	 * Sets currency.
	 *
	 * @param currency the currency
	 */
	public void setCurrency(CurrencyUnit currency) {
		this.currency = currency;
	}

	/**
	 * Sets lower price point.
	 *
	 * @param lowerPricePoint the lower price point
	 */
	public void setLowerPricePoint(double lowerPricePoint) {
		this.lowerPricePoint = lowerPricePoint;
	}

	/**
	 * Gets high price point.
	 *
	 * @return the high price point
	 */
	public Money getHighPricePoint() {
		return Money.of(highPricePoint, currency);
	}

	/**
	 * Sets high price point.
	 *
	 * @param highPricePoint the high price point
	 */
	public void setHighPricePoint(double highPricePoint) {
		this.highPricePoint = highPricePoint;
	}

	/**
	 * Gets trip duration.
	 *
	 * @return the trip duration
	 */
	public int getTripDuration() {
		return tripDuration;
	}

	/**
	 * Sets trip duration.
	 *
	 * @param tripDuration the trip duration
	 */
	public void setTripDuration(int tripDuration) {
		this.tripDuration = tripDuration;
	}

	/**
	 * Gets ticket quantity.
	 *
	 * @return the ticket quantity
	 */
	public int getTicketQuantity() {
		return ticketQuantity;
	}

	/**
	 * Sets ticket quantity.
	 *
	 * @param ticketQuantity the ticket quantity
	 */
	public void setTicketQuantity(int ticketQuantity) {
		this.ticketQuantity = ticketQuantity;
	}

	/**
	 * Gets number of adults.
	 *
	 * @return the number of adults
	 */
	public int getNumberOfAdults() {
		return numberOfAdults;
	}

	/**
	 * Sets number of adults.
	 *
	 * @param numberOfAdults the number of adults
	 */
	public void setNumberOfAdults(int numberOfAdults) {
		this.numberOfAdults = numberOfAdults;
	}

	/**
	 * Gets number of children.
	 *
	 * @return the number of children
	 */
	public int getNumberOfChildren() {
		return numberOfChildren;
	}

	/**
	 * Sets number of children.
	 *
	 * @param numberOfChildren the number of children
	 */
	public void setNumberOfChildren(int numberOfChildren) {
		this.numberOfChildren = numberOfChildren;
	}

}
