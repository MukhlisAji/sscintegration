package com.mii.sscintegration.domain.mitra;

import com.mii.sscintegration.domain.traveloka.*;
import java.text.DecimalFormat;
import java.util.List;

public class BookingMitra {
	public FlightProductDetail flightProductDetail;
	public String trNumber, approvalRequestId, bookingId, employeeId, currency, productType, paymentTimeLimit, bookingDetail;
        public Float totalFare;
	//public long paymentTimeLimit;
	public boolean isReschedule;
	public HotelProductDetail hotelProductDetail;
        public TrainProductDetail trainProductDetail;
	
	//untuk print
	public List<TravelFlightPrint> travelFlightPrint;
	public List <TravelHotelPrint> travelHotelPrint;
	public String penumpang;
}
