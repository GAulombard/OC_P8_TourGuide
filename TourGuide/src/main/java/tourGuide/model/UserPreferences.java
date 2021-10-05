package tourGuide.model;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import org.javamoney.moneta.Money;


public class UserPreferences {
	
	private int attractionProximity = Integer.MAX_VALUE; //Integer.MAX_VALUE by default
	private CurrencyUnit currency = Monetary.getCurrency("USD");
/*	private Money lowerPricePoint = Money.of(0, currency);
	private Money highPricePoint = Money.of(Integer.MAX_VALUE, currency);*/
	private double lowerPricePoint = 0; //0 by default
	private double highPricePoint = Integer.MAX_VALUE; //Integer.MAX_VALUE by default
	private int tripDuration = 1; //1 by default
	private int ticketQuantity = 1; //1 by default
	private int numberOfAdults = 1; //1 by default
	private int numberOfChildren = 0; //0 by default
	
	/*public UserPreferences() {
	}*/
	
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
