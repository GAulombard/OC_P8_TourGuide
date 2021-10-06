package tourGuide.model;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.javamoney.moneta.Money;
import tourGuide.validator.PriceCheck;

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
	
	public UserPreferences() {
	}
	
	public void setAttractionProximity(int attractionProximity) {
		this.attractionProximity = attractionProximity;
	}
	
	public int getAttractionProximity() {
		return attractionProximity;
	}
	
	public Money getLowerPricePoint() {
		return Money.of(lowerPricePoint, currency);
	}

	public CurrencyUnit getCurrency() {
		return currency;
	}

	public void setCurrency(CurrencyUnit currency) {
		this.currency = currency;
	}

	public void setLowerPricePoint(double lowerPricePoint) {
		this.lowerPricePoint = lowerPricePoint;
	}

	public Money getHighPricePoint() {
		return Money.of(highPricePoint, currency);
	}

	public void setHighPricePoint(double highPricePoint) {
		this.highPricePoint = highPricePoint;
	}
	
	public int getTripDuration() {
		return tripDuration;
	}

	public void setTripDuration(int tripDuration) {
		this.tripDuration = tripDuration;
	}

	public int getTicketQuantity() {
		return ticketQuantity;
	}

	public void setTicketQuantity(int ticketQuantity) {
		this.ticketQuantity = ticketQuantity;
	}
	
	public int getNumberOfAdults() {
		return numberOfAdults;
	}

	public void setNumberOfAdults(int numberOfAdults) {
		this.numberOfAdults = numberOfAdults;
	}

	public int getNumberOfChildren() {
		return numberOfChildren;
	}

	public void setNumberOfChildren(int numberOfChildren) {
		this.numberOfChildren = numberOfChildren;
	}

}
