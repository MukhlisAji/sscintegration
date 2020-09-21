package com.mii.sscintegration.domain.traveloka;

import java.util.List;

public class BookingTraveloka {
	public FlightProductDetail flightProductDetail;
	public String corporateReferenceId, approvalRequestId, bookingId, employeeId, currency, totalFare, productType, paymentTimeLimit, bookingDetail;
	//public long paymentTimeLimit;
	public boolean isReschedule;
	public HotelProductDetail hotelProductDetail;
	
	//untuk print
	public List<TravelFlightPrint> travelFlightPrint;
	public List <TravelHotelPrint> travelHotelPrint;
	public String penumpang;
}
