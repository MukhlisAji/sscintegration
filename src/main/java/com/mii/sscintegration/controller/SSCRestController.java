package com.mii.sscintegration.controller;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.CoordinateInfo;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.EntryListInfo;
import com.bmc.arsys.api.Value;
import com.mii.sscintegration.bmc.RemedyAPI;
import com.mii.sscintegration.bmc.RemedyConnection;
import static com.mii.sscintegration.controller.SSCController.logger;
import com.mii.sscintegration.domain.ConfigFile;
import com.mii.sscintegration.domain.ConfigurationValue;
import com.mii.sscintegration.domain.MitraResponse;
import com.mii.sscintegration.domain.TestingDomain;
import com.mii.sscintegration.domain.TravelokaIssuanceTicket;
import com.mii.sscintegration.domain.TravelokaResponse;
import com.mii.sscintegration.domain.mitra.BookingMitra;
import com.mii.sscintegration.domain.mitra.TrainSegment;
import com.mii.sscintegration.domain.mitra.TrainTripSegment;
import com.mii.sscintegration.domain.traveloka.BookingTraveloka;
import com.mii.sscintegration.domain.traveloka.FlightProductDetail;
import com.mii.sscintegration.domain.traveloka.FlightSegment;
import com.mii.sscintegration.domain.traveloka.Journeys;
import com.mii.sscintegration.domain.traveloka.PassengerAdult;
import com.mii.sscintegration.domain.traveloka.PassengerChildren;
import com.mii.sscintegration.domain.traveloka.PassengerInfant;
import com.mii.sscintegration.domain.traveloka.PassengerName;
import com.mii.sscintegration.domain.traveloka.TripSegment;
import com.mii.sscintegration.domain.traveloka.TravelokaFlight;

;

@RestController
public class SSCRestController {

    private static final String template = "Hello, %s!";
    protected static Logger logger = Logger.getLogger("SSCRestController: ");
    public Long tanggalAwalDinas, tanggalAkhirDinas;
    public String reqNumberTrip = "REQ";
    public String NumberTrip = "TripNo";

    @PostMapping("/issuanceTiket")
    public TravelokaResponse issuanceTiket(
            @RequestBody TravelokaIssuanceTicket travelokaIssuanceTicket) {

        logger.info("bookingid=" + travelokaIssuanceTicket.bookingId);

        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();

        Entry entryremedy = new Entry();
        entryremedy.put(536870913, new Value(travelokaIssuanceTicket.bookingId));
        entryremedy.put(536870915, new Value(travelokaIssuanceTicket.employeeId));
        entryremedy.put(536870916, new Value(travelokaIssuanceTicket.corporateReferenceId));
        entryremedy.put(536870917, new Value(travelokaIssuanceTicket.issuanceStatus));

        remedyAPI.insertNewRecord(remedySession, "PTM:SSC:HR:Travel:IssuanceTicketStatus", entryremedy);

        return new TravelokaResponse("SUCCESSFUL", "-", "-");
    }

    @PostMapping("/issuanceTicket")
    public TravelokaResponse issuanceTicket(
            @RequestBody TravelokaIssuanceTicket travelokaIssuanceTicket) {

        logger.info("bookingid=" + travelokaIssuanceTicket.bookingId);

        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();

        Entry entryremedy = new Entry();
        entryremedy.put(536870913, new Value(travelokaIssuanceTicket.bookingId));
        entryremedy.put(536870915, new Value(travelokaIssuanceTicket.employeeId));
        entryremedy.put(536870916, new Value(travelokaIssuanceTicket.corporateReferenceId));
        entryremedy.put(536870917, new Value(travelokaIssuanceTicket.issuanceStatus));

        remedyAPI.insertNewRecord(remedySession, "PTM:SSC:HR:Travel:IssuanceTicketStatus", entryremedy);

        return new TravelokaResponse("SUCCESSFUL", "-", "-");
    }

    @PostMapping("/bookingAPI")
    public TravelokaResponse TravelokaFlight(@RequestBody BookingTraveloka bookingTraveloka) {
        SSCController sscController = new SSCController();
        int hariMenginap = 0;
        int tarifPesawat = 0;
        int tarifHotel = 0;
        if (bookingTraveloka.bookingId == null) {
            return new TravelokaResponse("FAILED", "A001", "Booking ID cannot be null");
        }
        if (bookingTraveloka.corporateReferenceId == null) {
            return new TravelokaResponse("FAILED", "A002", "Corporate Reference cannot be null");
        }

        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();

        if (bookingTraveloka.productType.equalsIgnoreCase("flight")
                || bookingTraveloka.productType.equalsIgnoreCase("hotel")) {
            logger.info("product type is valid," + bookingTraveloka.productType);
        } else {
            sscController.addWorkInfo(
                    "Booking " + bookingTraveloka.bookingId + " is invalid",
                    "Product type for Booking " + bookingTraveloka.bookingId + " is invalid, Please book again with valid product type (flight/hotel)",
                    bookingTraveloka.corporateReferenceId,
                    remedySession);
            return new TravelokaResponse("FAILED", "A006", "Product type " + bookingTraveloka.productType + " is invalid");
        }

        TravelokaResponse hasilCheckingValid = isBookingValid(
                bookingTraveloka.corporateReferenceId,
                remedySession,
                bookingTraveloka.bookingId, bookingTraveloka.isReschedule);
        if (hasilCheckingValid.getStatus().equals("FAILED")) {
            return hasilCheckingValid;
        }

        logger.info("hasil cek=" + hasilCheckingValid.getStatus() + ", " + hasilCheckingValid.getErrorMessage());

        //insert to form PTM:SSC:HR:Travel:INT:Traveloka:MAIN
        String travelokaMainForm = "PTM:SSC:HR:Travel:INT:Traveloka:MAIN";
        Entry entryTravelokaMainForm = new Entry();
        entryTravelokaMainForm.put(536870913, new Value(bookingTraveloka.corporateReferenceId));
        entryTravelokaMainForm.put(536870914, new Value(bookingTraveloka.approvalRequestId));
        entryTravelokaMainForm.put(536870915, new Value(bookingTraveloka.bookingId));
        entryTravelokaMainForm.put(536870916, new Value(bookingTraveloka.employeeId));
        entryTravelokaMainForm.put(536870917, new Value(bookingTraveloka.paymentTimeLimit));
        //entryTravelokaMainForm.put(536870924, new Value(bookingTraveloka.paymentTimeLimit));
        entryTravelokaMainForm.put(536870918, new Value(bookingTraveloka.currency));
        entryTravelokaMainForm.put(536870919, new Value(bookingTraveloka.totalFare));
        entryTravelokaMainForm.put(536870931, new Value(bookingTraveloka.totalFare));
        entryTravelokaMainForm.put(536870920, new Value(bookingTraveloka.productType));
        entryTravelokaMainForm.put(536870921, new Value(String.valueOf(bookingTraveloka.isReschedule)));
        //entryTravelokaMainForm.put(8, new Value(bookingTraveloka.bookingId));
        entryTravelokaMainForm.put(2, new Value("appadmin"));
        entryTravelokaMainForm.put(7, new Value(0));
        //remedyAPI.insertNewRecord(remedySession, travelokaMainForm, entryTravelokaMainForm);
        logger.info("insert bookingId:" + bookingTraveloka.bookingId + " sucessfuly to BMC Remedy");

        long timeLimit = Long.parseLong(bookingTraveloka.paymentTimeLimit);
        Timestamp tsLimit = new Timestamp(timeLimit);
        logger.info("ts diterima=" + tsLimit.toString());
        timeLimit -= 600000;
        tsLimit = new Timestamp(timeLimit);
        logger.info("ts dikurangi 10=" + tsLimit.toString());
        Date tanggalLimit = new Date(tsLimit.getTime());

        //format uang rupiah
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("IDR ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        String bookingWorkInfo = "Booking ID=" + bookingTraveloka.bookingId
                + "\nTime Limit=" + new SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(tanggalLimit) + " WIB";

        bookingWorkInfo += (bookingTraveloka.currency.equalsIgnoreCase("IDR"))
                ? "\nTotal Fare=" + kursIndonesia.format(Double.parseDouble(bookingTraveloka.totalFare))
                : "\nTotal Fare=" + bookingTraveloka.currency + " " + bookingTraveloka.totalFare;

        String bookingInformation = "Booking from traveloka, ID=" + bookingTraveloka.bookingId;
        //String formTravelokaNotification = "PTM:SSC:HR:Travel:SendTravelokaBookingtoWorkInfo";

        //insert flight detail to BMC (if any)
        List<FlightSegment> flightList = new ArrayList<FlightSegment>();
        if (bookingTraveloka.flightProductDetail != null) {
            tarifPesawat += Integer.parseInt(bookingTraveloka.totalFare);
            String travelokaFlightForm = "PTM:SSC:HR:Travel:INT:Traveloka:flightProductDetail";
            Entry entryTravelokaFlight = new Entry();
            //getting flightDetail
            List<TripSegment> tripSegments = new ArrayList<TripSegment>();
            tripSegments = bookingTraveloka.flightProductDetail.journeys;
            int segmentNumber = 0;
            int flightNumber = 0;
            for (TripSegment tripSegment : tripSegments) {
                segmentNumber++;
                flightList = tripSegment.segments;
                for (FlightSegment flightSegment : flightList) {
                    flightNumber++;
                    logger.info("segment:" + segmentNumber + "-flightNumber:" + flightNumber);
                    logger.info("get DepartureDate=" + flightSegment.departureDateTime.date.day);
                    entryTravelokaFlight.put(536870915, new Value(bookingTraveloka.bookingId));
                    entryTravelokaFlight.put(536870916, new Value(flightSegment.sourceAirport));
                    entryTravelokaFlight.put(536870917, new Value(flightSegment.destinationAirport));

                    //setting departureDateTime
                    String departureDateTime = String.valueOf(flightSegment.departureDateTime.date.year) + "-"
                            + return2Char(String.valueOf(flightSegment.departureDateTime.date.month)) + "-"
                            + return2Char(String.valueOf(flightSegment.departureDateTime.date.day)) + " "
                            + return2Char(String.valueOf(flightSegment.departureDateTime.time.hour)) + ":"
                            + return2Char(String.valueOf(flightSegment.departureDateTime.time.minute)) + ":00";
                    entryTravelokaFlight.put(536870918, new Value(departureDateTime));

                    //date validation
                    if (!isValidDate(departureDateTime)) {
                        sscController.addWorkInfo(
                                "Booking " + bookingTraveloka.bookingId + " date is invalid",
                                "Booking " + bookingTraveloka.bookingId + " date is invalid, Please book again with valid travel date using same travel reff number",
                                bookingTraveloka.corporateReferenceId,
                                remedySession);
                        return new TravelokaResponse("FAILED", "A006", "Booking date is invalid");
                    }

                    //setting arrivalDateTime
                    String arrivalDateTime = String.valueOf(flightSegment.arrivalDateTime.date.year);
                    arrivalDateTime += "-" + String.valueOf(flightSegment.arrivalDateTime.date.month);
                    arrivalDateTime += "-" + String.valueOf(flightSegment.arrivalDateTime.date.day);
                    arrivalDateTime += " " + String.valueOf(flightSegment.arrivalDateTime.time.hour);
                    arrivalDateTime += ":" + String.valueOf(flightSegment.arrivalDateTime.time.minute);
                    entryTravelokaFlight.put(536870919, new Value(arrivalDateTime));

                    entryTravelokaFlight.put(536870920, new Value(flightSegment.airlineCode));
                    entryTravelokaFlight.put(536870921, new Value(flightSegment.seatClass));

                    bookingWorkInfo += "\nFrom " + flightSegment.sourceAirport + " to " + flightSegment.destinationAirport
                            + "\nDeparture Date=" + departureDateTime
                            + "\nArrival Date=" + arrivalDateTime
                            + "\nAirline Code=" + flightSegment.airlineCode
                            + "\nSeat Class=" + flightSegment.seatClass;

                    remedyAPI.insertNewRecord(remedySession, travelokaFlightForm, entryTravelokaFlight);
                }
            }

            Entry entryTravelokaPassenger = new Entry();
            String travelokaPassengerForm = "PTM:SSC:HR:Travel:INT:Traveloka:Passenger";
            //getting adultPassenger
            if (bookingTraveloka.flightProductDetail.passengers.adults != null) {
                List<PassengerAdult> adultPasssengers = bookingTraveloka.flightProductDetail.passengers.adults;
                for (PassengerAdult passengerAdult : adultPasssengers) {
                    entryTravelokaPassenger.put(536870915, new Value(bookingTraveloka.bookingId));
                    entryTravelokaPassenger.put(536870917, new Value(passengerAdult.firstName));
                    entryTravelokaPassenger.put(536870918, new Value(passengerAdult.lastName));
                    entryTravelokaPassenger.put(536870919, new Value("Adult"));

                    //passengersName = passengerAdult.adults;
                    logger.info("passenger adult:" + passengerAdult.firstName + " + " + passengerAdult.lastName);
                    remedyAPI.insertNewRecord(remedySession, travelokaPassengerForm, entryTravelokaPassenger);
                    bookingWorkInfo += "\nPassenger:" + passengerAdult.firstName + " " + passengerAdult.lastName + " (Adult)";
                }
            }

            //getting childrenPassenger
            if (bookingTraveloka.flightProductDetail.passengers.children != null) {
                List<PassengerChildren> childrenPasssengers = bookingTraveloka.flightProductDetail.passengers.children;
                for (PassengerChildren passengerChildren : childrenPasssengers) {
                    entryTravelokaPassenger.put(536870915, new Value(bookingTraveloka.bookingId));
                    entryTravelokaPassenger.put(536870917, new Value(passengerChildren.firstName));
                    entryTravelokaPassenger.put(536870918, new Value(passengerChildren.lastName));
                    entryTravelokaPassenger.put(536870919, new Value("Children"));

                    logger.info("passenger children:" + passengerChildren.firstName + "+" + passengerChildren.lastName);
                    remedyAPI.insertNewRecord(remedySession, travelokaPassengerForm, entryTravelokaPassenger);
                    bookingWorkInfo += "\nPassenger:" + passengerChildren.firstName + " " + passengerChildren.lastName + " (Children)";
                }
            }

            //getting childrenPassenger
            if (bookingTraveloka.flightProductDetail.passengers.infants != null) {
                List<PassengerInfant> infantPassengers = bookingTraveloka.flightProductDetail.passengers.infants;
                for (PassengerInfant passengerInfant : infantPassengers) {
                    entryTravelokaPassenger.put(536870915, new Value(bookingTraveloka.bookingId));
                    entryTravelokaPassenger.put(536870917, new Value(passengerInfant.firstName));
                    entryTravelokaPassenger.put(536870918, new Value(passengerInfant.lastName));
                    entryTravelokaPassenger.put(536870919, new Value("Infant"));

                    logger.info("passenger infant:" + passengerInfant.firstName + "+" + passengerInfant.lastName);
                    remedyAPI.insertNewRecord(remedySession, travelokaPassengerForm, entryTravelokaPassenger);
                    bookingWorkInfo += "\nPassenger:" + passengerInfant.firstName + " " + passengerInfant.lastName + " (Infant)";
                }
            }
        }

        //getting hotel product
        if (bookingTraveloka.hotelProductDetail != null) {
            tarifHotel += Integer.parseInt(bookingTraveloka.totalFare);
            String hotelName = bookingTraveloka.hotelProductDetail.hotelName;

            if (!hotelName.isEmpty()) {
                String travelokaHotelForm = "PTM:SSC:HR:Travel:INT:Traveloka:hotelProductDetail";
                Entry entryTravelokaHotel = new Entry();

                logger.info("hotel name=" + hotelName);
                entryTravelokaHotel.put(536870915, new Value(bookingTraveloka.bookingId));
                entryTravelokaHotel.put(536870916, new Value(hotelName));
                entryTravelokaHotel.put(536870917, new Value(bookingTraveloka.hotelProductDetail.hotelCity));
                entryTravelokaHotel.put(536870918, new Value(bookingTraveloka.hotelProductDetail.hotelCountry));

                //setting checkin date
                String checkinDate = bookingTraveloka.hotelProductDetail.checkInDate.year + "-"
                        + return2Char(bookingTraveloka.hotelProductDetail.checkInDate.month) + "-"
                        + return2Char(bookingTraveloka.hotelProductDetail.checkInDate.day);

                //checkinDate += (bookingTraveloka.hotelProductDetail.checkInDate.day.length()<2) ? 
                //		"-0"+ bookingTraveloka.hotelProductDetail.checkInDate.day :
                //			"-"+bookingTraveloka.hotelProductDetail.checkInDate.day;
                if (!isValidDate(checkinDate + " 10:00:00")) {
                    sscController.addWorkInfo(
                            "Booking " + bookingTraveloka.bookingId + " date is invalid",
                            "Booking " + bookingTraveloka.bookingId + " date is invalid, Please book again with valid travel date using same travel reff number",
                            bookingTraveloka.corporateReferenceId,
                            remedySession);
                    return new TravelokaResponse("FAILED", "A006", "Booking date is invalid");
                }

                entryTravelokaHotel.put(536870919, new Value(checkinDate));
                entryTravelokaHotel.put(536870920, new Value(bookingTraveloka.hotelProductDetail.numOfNights));
                entryTravelokaHotel.put(536870921, new Value(bookingTraveloka.hotelProductDetail.numOfRooms));
                entryTravelokaHotel.put(536870928, new Value(bookingTraveloka.hotelProductDetail.guestName));
                entryTravelokaHotel.put(536870927, new Value(bookingTraveloka.hotelProductDetail.roomName));
                entryTravelokaHotel.put(536870925, new Value(bookingTraveloka.hotelProductDetail.pricePerRoomPerNight.currency
                        + " " + bookingTraveloka.hotelProductDetail.pricePerRoomPerNight.fare));
                entryTravelokaHotel.put(536870926, new Value(bookingTraveloka.hotelProductDetail.currency));
                entryTravelokaHotel.put(536870929, new Value(bookingTraveloka.hotelProductDetail.fare));
                remedyAPI.insertNewRecord(remedySession, travelokaHotelForm, entryTravelokaHotel);

                bookingWorkInfo += "\nHotel Name=" + hotelName
                        + "\nCity=" + bookingTraveloka.hotelProductDetail.hotelCity
                        + "\nCountry=" + bookingTraveloka.hotelProductDetail.hotelCountry
                        + "\nNumber of night=" + bookingTraveloka.hotelProductDetail.numOfNights
                        + "\nNumber of Rooms=" + bookingTraveloka.hotelProductDetail.numOfRooms
                        + "\nGuest Name=" + bookingTraveloka.hotelProductDetail.guestName
                        + "\nRoom Name =" + bookingTraveloka.hotelProductDetail.roomName;
                hariMenginap += bookingTraveloka.hotelProductDetail.numOfNights;

                /*
				bookingWorkInfo += (bookingTraveloka.currency.equalsIgnoreCase("IDR")) ? 
						"\nTotal Fare="+kursIndonesia.format(Double.parseDouble(bookingTraveloka.totalFare)) : 
							"\nTotal Fare="+bookingTraveloka.currency+" "+bookingTraveloka.totalFare;*/
            }

        }

        /*
		Entry entryTravelokaNotif = new Entry();
		entryTravelokaNotif.put(536870916, new Value(bookingWorkInfo));
		entryTravelokaNotif.put(536870915, new Value(bookingInformation));
		entryTravelokaNotif.put(8, new Value(bookingTraveloka.corporateReferenceId));
		remedyAPI.insertNewRecord(remedySession, formTravelokaNotification, entryTravelokaNotif);*/
        //update harga OTA di PTM:SSC:HR:SKPD
        List<EntryListInfo> eListHRSKPDs = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:HR:SKPD", "'SR-Request ID'=\"" + reqNumberTrip + "\" ");
        try {
            for (EntryListInfo eListHRSKPD : eListHRSKPDs) {
                Entry recordSKPD = remedySession.getEntry("PTM:SSC:HR:SKPD", eListHRSKPD.getEntryID(), null);

                List<EntryListInfo> eListTotals = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:HR:Total:Traveloka", "'No Trip__c'=\"" + NumberTrip + "\" ");

                try {
                    for (EntryListInfo eListTotal : eListTotals) {
                        Entry recordTotal = remedySession.getEntry("PTM:SSC:HR:Total:Traveloka", eListTotal.getEntryID(), null);

                        //Check harga total fare pesawat apakah melebihi estimated cost
                        Integer TotalFare = Integer.valueOf(bookingTraveloka.totalFare);

                        if (bookingTraveloka.productType.equals("FLIGHT")) {
                            Integer FlightAmount = Integer.valueOf(recordSKPD.get(536871019).getIntValue());
                            Integer FlightAmountTemp = Integer.valueOf(recordTotal.get(536870915).getIntValue());
                            TotalFare += FlightAmountTemp;

                            System.out.println("TotalFare : " + TotalFare + " : " + FlightAmount);
                            if (TotalFare > FlightAmount) {

                                sscController.addWorkInfo("Booking " + bookingTraveloka.bookingId + " is rejected",
                                        "Booking " + bookingTraveloka.bookingId + " is rejected. Flight Booking Anda melebihi budget yang telah ditentukan. Silakan lakukan pemesanan kembali dengan budget yang sesuai",
                                        bookingTraveloka.corporateReferenceId, remedySession);

                                return new TravelokaResponse("FAILED", "A008", "Exceed budget of Flight");
                            }
                        }

                        //Check harga total hotel
                        if (bookingTraveloka.productType.equals("HOTEL")) {
                            Integer HotelAmount = Integer.valueOf(recordSKPD.get(536871078).getIntValue());
                            Integer HotelAmountTemp = Integer.valueOf(recordTotal.get(536870916).getIntValue());
                            TotalFare += HotelAmountTemp;
                            if (TotalFare > HotelAmount) {

                                logger.info("HotelAmount :" + HotelAmount);
                                sscController.addWorkInfo("Booking " + bookingTraveloka.bookingId + " is rejected",
                                        "Booking " + bookingTraveloka.bookingId + " is rejected. Hotel Booking Anda melebihi budget yang telah ditentukan, silakan lakukan pemesanan kembali dengan budget yang sesuai",
                                        bookingTraveloka.corporateReferenceId, remedySession);

                                return new TravelokaResponse("FAILED", "A009", "Exceed budget of Hotel");
                            }
                        }

                    }
                } catch (ARException e) {
                    logger.info("ARException Error on OTA Calculation : " + e.toString());
                }

                logger.info("traveloka:" + travelokaMainForm);
                remedyAPI.insertNewRecord(remedySession, travelokaMainForm, entryTravelokaMainForm);
                sscController.addWorkInfo(bookingInformation,
                        bookingWorkInfo,
                        bookingTraveloka.corporateReferenceId,
                        remedySession);

                //update harga OTA
                hariMenginap += recordSKPD.get(536870935).getIntValue();
                tarifPesawat += recordSKPD.get(536871140).getIntValue();
                tarifHotel += recordSKPD.get(536871141).getIntValue();

                recordSKPD.put(536870935, new Value(hariMenginap)); //jumlah hari
                recordSKPD.put(536871140, new Value(tarifPesawat)); //penerbangan
                recordSKPD.put(536871141, new Value(tarifHotel)); //hotel

                remedySession.setEntry("PTM:SSC:HR:SKPD", recordSKPD.getEntryId(), recordSKPD, null, 0);

            }
        } catch (ARException e) {
            logger.info("ARException Error on OTA Calculation : " + e.toString());
        }

        return new TravelokaResponse("SUCCESSFUL", "-", "-");
    }

    public TravelokaResponse isBookingValid(
            String corporateRefId,
            ARServerUser remedySession,
            String bookingId, boolean isReschedule) {

        boolean isValid = false;
        RemedyAPI remedyAPI = new RemedyAPI();
        SSCController sscController = new SSCController();

        try {
            List<EntryListInfo> eListValidSRMs = remedyAPI.getRemedyRecordByQuery(
                    remedySession,
                    "SRM:Request",
                    "'CorporateReferenceID__c'=\"" + corporateRefId + "\"");
            logger.info("corp=" + corporateRefId);
            logger.info("piro see" + eListValidSRMs.size());
            if (eListValidSRMs.size() < 1) {
                return new TravelokaResponse("FAILED", "A003", "Corporate Reference ID is invalid");
            } else {
                for (EntryListInfo eListValidSRM : eListValidSRMs) {
                    Entry srmRecord = remedySession.getEntry("SRM:Request", eListValidSRM.getEntryID(), null);
                    String reqNumber = srmRecord.get(1000000829).getValue().toString();
                    String tripNumber = srmRecord.get(536870966).getValue().toString();
//                    String reqNumber = srmRecord.get(7).getValue().toString();
                    reqNumberTrip = reqNumber;
                    NumberTrip = tripNumber;

                    //Check Estimated cost
                    //check apakah change?
                    String typeSKPD = srmRecord.get(536871100).getValue().toString();
                    System.out.println(typeSKPD + " : " + isReschedule);
                    if (typeSKPD.equals("Change") && !isReschedule) {

                        sscController.addWorkInfo("Booking " + bookingId + " is rejected",
                                "TRN ini hnya dapat digunakan untuk melakukan reschedule, silahkan mencoba kembali melalui menu Easy Reschedule Traveloka.", corporateRefId, remedySession);

                        return new TravelokaResponse("FAILED", "A005", "Request Should be From Change Menu");
                    }

                    //check apa sudah di approved atau belum
                    String Status = srmRecord.get(7).getValue().toString();
                    logger.info("Status = " + Status);
                    if (Status.equals("4000")) {
                        logger.info("work info approved");

                        String InstanceID = srmRecord.get(179).getValue().toString();

                        sscController.addWorkInfo("Booking " + bookingId + " is rejected", "Anda telah melakukan Approval sebelum booking tiket di Traveloka. "
                                + "TRN anda telah expired. Silahkan lakukan pembelian tiket dengan metode Non CBT.", corporateRefId, remedySession);

                        return new TravelokaResponse("FAILED", "A004", "Booking is rejected, since ticket has been approved before book");
                    }
                    logger.info("tidak ada work info approved:" + reqNumber);

                    //check apakah diluar tanggal dinas?
                    List<EntryListInfo> eListSKPDs = remedyAPI.getRemedyRecordByQuery(
                            remedySession,
                            "PTM:SSC:HR:Travel:INT:SAP",
                            "'REQNUMBER__c'=\"" + reqNumber + "\" AND 'Status__c'=\"1\" ");

                    if (eListSKPDs.size() == 0) {
                        return new TravelokaResponse("FAILED", "A006", "Corporate Reference ID is invalid");
                    } else {
                        logger.info("testing skpd=" + eListSKPDs.size());
                        for (EntryListInfo eListSKPD : eListSKPDs) {
                            Entry skpdRecord = remedySession.getEntry("PTM:SSC:HR:Travel:INT:SAP", eListSKPD.getEntryID(), null);
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                            Timestamp tsBerangkatDinas = new Timestamp(
                                    df.parse(skpdRecord.get(536870919).getValue().toString()
                                            + " " + skpdRecord.get(536870920).getValue().toString()).getTime());
                            tanggalAwalDinas = tsBerangkatDinas.getTime();

                            Timestamp tsPulangDinas = new Timestamp(
                                    df.parse(skpdRecord.get(536870921).getValue().toString()
                                            + " " + skpdRecord.get(536870922).getValue().toString()).getTime());
                            tanggalAkhirDinas = tsPulangDinas.getTime();

                            logger.info("timestamp berangkat=" + tsBerangkatDinas.toString() + ":" + tanggalAwalDinas);
                            logger.info("ts pulang=" + tsPulangDinas.toString() + ":" + tanggalAkhirDinas);
                        }
                    }

                }
            }
        } catch (ARException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata: " + e.toString());
        } catch (ParseException e) {
            logger.info("ARException Error on parse datetime: " + e.toString());
        }

        return new TravelokaResponse("SUCCESSFUL", "-", "On progress checking");
    }

    public boolean isValidDate(String dateString) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        try {
            Timestamp tsDate = new Timestamp(df.parse(dateString).getTime());
            Long longDateCheck = tsDate.getTime();
            logger.info("Date to check:" + longDateCheck);

            if (longDateCheck >= tanggalAwalDinas && longDateCheck <= tanggalAkhirDinas) {
                logger.info("booking is valid");
                return true;
            }

        } catch (ParseException e) {
            logger.info("error on converting timestamp" + e.toString());
        }
        logger.info("booking is invalid");
        return false;

    }

    public String return2Char(String chartocheck) {
        return (chartocheck.length() < 2) ? "0" + chartocheck : chartocheck;
    }

    @PostMapping("/mitraBookingAPI")
    public MitraResponse MitraFlight(@RequestBody BookingMitra bookingMitra) {
        SSCController sscController = new SSCController();
        int hariMenginap = 0;
        int tarifPesawat = 0;
        int tarifHotel = 0;
        if (bookingMitra.bookingId == null) {
            return new MitraResponse("FAILED", "A001", "Booking ID cannot be null");
        }
        if (bookingMitra.trNumber == null) {
            return new MitraResponse("FAILED", "A002", "Corporate Reference cannot be null");
        }

        //Get configuration value from sscconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //get remedy connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedySession = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();

        if (bookingMitra.productType.equalsIgnoreCase("flight")
                || bookingMitra.productType.equalsIgnoreCase("hotel") || (bookingMitra.productType.equalsIgnoreCase("train"))) {
            logger.info("product type is valid," + bookingMitra.productType);
        } else {
            sscController.addWorkInfo(
                    "Booking " + bookingMitra.bookingId + " is invalid",
                    "Product type for Booking " + bookingMitra.bookingId + " is invalid, Please book again with valid product type (flight/hotel/train)",
                    bookingMitra.trNumber,
                    remedySession);
            return new MitraResponse("FAILED", "A006", "Product type " + bookingMitra.productType + " is invalid");
        }

        MitraResponse hasilCheckingValid = isBookingMitraValid(
                bookingMitra.trNumber,
                remedySession,
                bookingMitra.bookingId);
        if (hasilCheckingValid.getStatus().equals("FAILED")) {
            return hasilCheckingValid;
        }

        logger.info("hasil cek=" + hasilCheckingValid.getStatus() + ", " + hasilCheckingValid.getErrorMessage());

        //insert to form PTM:SSC:HR:Travel:INT:Mitra:MAIN
        String mitraMainForm = "PTM:SSC:HR:Travel:INT:Mitra:MAIN";
        Entry entryMitrakaMainForm = new Entry();
        entryMitrakaMainForm.put(536870913, new Value(bookingMitra.trNumber));
        entryMitrakaMainForm.put(536870914, new Value(bookingMitra.approvalRequestId));
        entryMitrakaMainForm.put(536870915, new Value(bookingMitra.bookingId));
        entryMitrakaMainForm.put(536870916, new Value(bookingMitra.employeeId));
        int totalFare = (int) Math.round(bookingMitra.totalFare);
//        entryMitrakaMainForm.put(536870917, new Value(bookingMitra.paymentTimeLimit));
        //entryMitrakaMainForm.put(536870924, new Value(bookingMitra.paymentTimeLimit));
        entryMitrakaMainForm.put(536870918, new Value(bookingMitra.currency));
        entryMitrakaMainForm.put(536870919, new Value(String.valueOf(totalFare)));
        entryMitrakaMainForm.put(536870931, new Value(String.valueOf(totalFare)));
        entryMitrakaMainForm.put(536870920, new Value(bookingMitra.productType));
//        entryMitrakaMainForm.put(536870921, new Value(String.valueOf(bookingMitra.isReschedule)));
        //entryMitrakaMainForm.put(8, new Value(bookingMitra.bookingId));
        entryMitrakaMainForm.put(2, new Value("appadmin"));
        entryMitrakaMainForm.put(7, new Value(0));
        //remedyAPI.insertNewRecord(remedySession, mitraMainForm, entryMitrakaMainForm);
        logger.info("insert bookingId:" + bookingMitra.bookingId + " sucessfuly to BMC Remedy");

//        long timeLimit = Long.parseLong(bookingMitra.paymentTimeLimit);
//        Timestamp tsLimit = new Timestamp(timeLimit);
//        logger.info("ts diterima=" + tsLimit.toString());
//        timeLimit -= 600000;
//        tsLimit = new Timestamp(timeLimit);
//        logger.info("ts dikurangi 10=" + tsLimit.toString());
//        Date tanggalLimit = new Date(tsLimit.getTime());
        //format uang rupiah
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("IDR ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        String bookingWorkInfo = "Booking ID=" + bookingMitra.bookingId;
//                + "\nTime Limit=" + new SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(tanggalLimit) + " WIB";

        bookingWorkInfo += (bookingMitra.currency.equalsIgnoreCase("IDR"))
                ? "\nTotal Fare=" + kursIndonesia.format(bookingMitra.totalFare)
                : "\nTotal Fare=" + bookingMitra.currency + " " + bookingMitra.totalFare;

        String bookingInformation = "Booking from mitra tour, ID=" + bookingMitra.bookingId;
        //String formTravelokaNotification = "PTM:SSC:HR:Travel:SendTravelokaBookingtoWorkInfo";

        //insert flight detail to BMC (if any)
        List<FlightSegment> flightList = new ArrayList<FlightSegment>();
        if (bookingMitra.flightProductDetail != null) {
            tarifPesawat += totalFare;
            String travelokaFlightForm = "PTM:SSC:HR:Travel:INT:Mitra:flightProductDetail";
            Entry entryTravelokaFlight = new Entry();
            //getting flightDetail
            List<TripSegment> tripSegments = new ArrayList<TripSegment>();
            tripSegments = bookingMitra.flightProductDetail.journeys;
            int segmentNumber = 0;
            int flightNumber = 0;
            for (TripSegment tripSegment : tripSegments) {
                segmentNumber++;
                flightList = tripSegment.segments;
                for (FlightSegment flightSegment : flightList) {
                    flightNumber++;
                    logger.info("segment:" + segmentNumber + "-flightNumber:" + flightNumber);
                    logger.info("get DepartureDate=" + flightSegment.departureDateTime.date.day);
                    entryTravelokaFlight.put(536870915, new Value(bookingMitra.bookingId));
                    entryTravelokaFlight.put(536870916, new Value(flightSegment.sourceAirport));
                    entryTravelokaFlight.put(536870917, new Value(flightSegment.destinationAirport));

                    //setting departureDateTime
                    String departureDateTime = String.valueOf(flightSegment.departureDateTime.date.year) + "-"
                            + return2Char(String.valueOf(flightSegment.departureDateTime.date.month)) + "-"
                            + return2Char(String.valueOf(flightSegment.departureDateTime.date.day)) + " "
                            + return2Char(String.valueOf(flightSegment.departureDateTime.time.hour)) + ":"
                            + return2Char(String.valueOf(flightSegment.departureDateTime.time.minute)) + ":00";
                    entryTravelokaFlight.put(536870918, new Value(departureDateTime));

                    //date validation
                    if (!isValidDate(departureDateTime)) {
                        sscController.addWorkInfo(
                                "Booking " + bookingMitra.bookingId + " date is invalid",
                                "Booking " + bookingMitra.bookingId + " date is invalid, Please book again with valid travel date using same travel reff number",
                                bookingMitra.trNumber,
                                remedySession);
                        return new MitraResponse("FAILED", "A006", "Booking date is invalid");
                    }

                    //setting arrivalDateTime
                    String arrivalDateTime = String.valueOf(flightSegment.arrivalDateTime.date.year);
                    arrivalDateTime += "-" + String.valueOf(flightSegment.arrivalDateTime.date.month);
                    arrivalDateTime += "-" + String.valueOf(flightSegment.arrivalDateTime.date.day);
                    arrivalDateTime += " " + String.valueOf(flightSegment.arrivalDateTime.time.hour);
                    arrivalDateTime += ":" + String.valueOf(flightSegment.arrivalDateTime.time.minute);
                    entryTravelokaFlight.put(536870919, new Value(arrivalDateTime));

                    entryTravelokaFlight.put(536870920, new Value(flightSegment.airlineCode));
                    entryTravelokaFlight.put(536870921, new Value(flightSegment.seatClass));

                    bookingWorkInfo += "\nFrom " + flightSegment.sourceAirport + " to " + flightSegment.destinationAirport
                            + "\nDeparture Date=" + departureDateTime
                            + "\nArrival Date=" + arrivalDateTime
                            + "\nAirline Code=" + flightSegment.airlineCode
                            + "\nSeat Class=" + flightSegment.seatClass;

                    remedyAPI.insertNewRecord(remedySession, travelokaFlightForm, entryTravelokaFlight);
                }
            }

            Entry entryMitraPassenger = new Entry();
            String travelokaPassengerForm = "PTM:SSC:HR:Travel:INT:Mitra:flightPassenger";
            //getting adultPassenger
            if (bookingMitra.flightProductDetail.passengers.adults != null) {
                List<PassengerAdult> adultPasssengers = bookingMitra.flightProductDetail.passengers.adults;
                for (PassengerAdult passengerAdult : adultPasssengers) {
                    entryMitraPassenger.put(536870915, new Value(bookingMitra.bookingId));
                    entryMitraPassenger.put(536870917, new Value(passengerAdult.firstName));
                    entryMitraPassenger.put(536870918, new Value(passengerAdult.lastName));
                    entryMitraPassenger.put(536870919, new Value("Adult"));

                    //passengersName = passengerAdult.adults;
                    logger.info("passenger adult:" + passengerAdult.firstName + " + " + passengerAdult.lastName);
                    remedyAPI.insertNewRecord(remedySession, travelokaPassengerForm, entryMitraPassenger);
                    bookingWorkInfo += "\nPassenger:" + passengerAdult.firstName + " " + passengerAdult.lastName + " (Adult)";
                }
            }

            //getting childrenPassenger
            if (bookingMitra.flightProductDetail.passengers.children != null) {
                List<PassengerChildren> childrenPasssengers = bookingMitra.flightProductDetail.passengers.children;
                for (PassengerChildren passengerChildren : childrenPasssengers) {
                    entryMitraPassenger.put(536870915, new Value(bookingMitra.bookingId));
                    entryMitraPassenger.put(536870917, new Value(passengerChildren.firstName));
                    entryMitraPassenger.put(536870918, new Value(passengerChildren.lastName));
                    entryMitraPassenger.put(536870919, new Value("Children"));

                    logger.info("passenger children:" + passengerChildren.firstName + "+" + passengerChildren.lastName);
                    remedyAPI.insertNewRecord(remedySession, travelokaPassengerForm, entryMitraPassenger);
                    bookingWorkInfo += "\nPassenger:" + passengerChildren.firstName + " " + passengerChildren.lastName + " (Children)";
                }
            }

            //getting childrenPassenger
            if (bookingMitra.flightProductDetail.passengers.infants != null) {
                List<PassengerInfant> infantPassengers = bookingMitra.flightProductDetail.passengers.infants;
                for (PassengerInfant passengerInfant : infantPassengers) {
                    entryMitraPassenger.put(536870915, new Value(bookingMitra.bookingId));
                    entryMitraPassenger.put(536870917, new Value(passengerInfant.firstName));
                    entryMitraPassenger.put(536870918, new Value(passengerInfant.lastName));
                    entryMitraPassenger.put(536870919, new Value("Infant"));

                    logger.info("passenger infant:" + passengerInfant.firstName + "+" + passengerInfant.lastName);
                    remedyAPI.insertNewRecord(remedySession, travelokaPassengerForm, entryMitraPassenger);
                    bookingWorkInfo += "\nPassenger:" + passengerInfant.firstName + " " + passengerInfant.lastName + " (Infant)";
                }
            }
        }

        //insert train detail to BMC (if any)
        List<TrainSegment> trainList = new ArrayList<TrainSegment>();
        if (bookingMitra.trainProductDetail != null) {
            tarifPesawat += totalFare;
            String mitraTrainForm = "PTM:SSC:HR:Travel:INT:Mitra:trainProductDetail";
            Entry entryMitraTrain = new Entry();
            //getting flightDetail
            List<TrainTripSegment> tripSegments = new ArrayList<TrainTripSegment>();
            tripSegments = bookingMitra.trainProductDetail.journeys;
            int segmentNumber = 0;
            int trainNumber = 0;
            for (TrainTripSegment tripSegment : tripSegments) {
                segmentNumber++;
                trainList = tripSegment.segments;
                for (TrainSegment trainSegment : trainList) {
                    trainNumber++;
                    logger.info("segment:" + segmentNumber + "-trainNumber:" + trainNumber);
                    logger.info("get DepartureDate=" + trainSegment.departureDateTime.date.day);
                    entryMitraTrain.put(536870915, new Value(bookingMitra.bookingId));
                    entryMitraTrain.put(536870916, new Value(trainSegment.sourceStation));
                    entryMitraTrain.put(536870917, new Value(trainSegment.destinationStation));

                    //setting departureDateTime
                    String departureDateTime = String.valueOf(trainSegment.departureDateTime.date.year) + "-"
                            + return2Char(String.valueOf(trainSegment.departureDateTime.date.month)) + "-"
                            + return2Char(String.valueOf(trainSegment.departureDateTime.date.day)) + " "
                            + return2Char(String.valueOf(trainSegment.departureDateTime.time.hour)) + ":"
                            + return2Char(String.valueOf(trainSegment.departureDateTime.time.minute)) + ":00";
                    entryMitraTrain.put(536870918, new Value(departureDateTime));

                    //date validation
                    if (!isValidDate(departureDateTime)) {
                        sscController.addWorkInfo(
                                "Booking " + bookingMitra.bookingId + " date is invalid",
                                "Booking " + bookingMitra.bookingId + " date is invalid, Please book again with valid travel date using same travel reff number",
                                bookingMitra.trNumber,
                                remedySession);
                        return new MitraResponse("FAILED", "A006", "Booking date is invalid");
                    }

                    //setting arrivalDateTime
                    String arrivalDateTime = String.valueOf(trainSegment.arrivalDateTime.date.year);
                    arrivalDateTime += "-" + String.valueOf(trainSegment.arrivalDateTime.date.month);
                    arrivalDateTime += "-" + String.valueOf(trainSegment.arrivalDateTime.date.day);
                    arrivalDateTime += " " + String.valueOf(trainSegment.arrivalDateTime.time.hour);
                    arrivalDateTime += ":" + String.valueOf(trainSegment.arrivalDateTime.time.minute);
                    entryMitraTrain.put(536870919, new Value(arrivalDateTime));

                    entryMitraTrain.put(536870920, new Value(trainSegment.trainCode));
                    entryMitraTrain.put(536870921, new Value(trainSegment.seatClass));

                    bookingWorkInfo += "\nFrom " + trainSegment.sourceStation + " to " + trainSegment.destinationStation
                            + "\nDeparture Date=" + departureDateTime
                            + "\nArrival Date=" + arrivalDateTime
                            + "\nAirline Code=" + trainSegment.trainCode
                            + "\nSeat Class=" + trainSegment.seatClass;

                    remedyAPI.insertNewRecord(remedySession, mitraTrainForm, entryMitraTrain);
                }
            }

            Entry entryMitraPassenger = new Entry();
            String travelokaPassengerForm = "PTM:SSC:HR:Travel:INT:Mitra:trainPassenger";
            //getting adultPassenger
            if (bookingMitra.trainProductDetail.passengers.adults != null) {
                List<PassengerAdult> adultPasssengers = bookingMitra.trainProductDetail.passengers.adults;
                for (PassengerAdult passengerAdult : adultPasssengers) {
                    entryMitraPassenger.put(536870915, new Value(bookingMitra.bookingId));
                    entryMitraPassenger.put(536870917, new Value(passengerAdult.firstName));
                    entryMitraPassenger.put(536870918, new Value(passengerAdult.lastName));
                    entryMitraPassenger.put(536870919, new Value("Adult"));

                    //passengersName = passengerAdult.adults;
                    logger.info("passenger adult:" + passengerAdult.firstName + " + " + passengerAdult.lastName);
                    remedyAPI.insertNewRecord(remedySession, travelokaPassengerForm, entryMitraPassenger);
                    bookingWorkInfo += "\nPassenger:" + passengerAdult.firstName + " " + passengerAdult.lastName + " (Adult)";
                }
            }

            //getting childrenPassenger
            if (bookingMitra.trainProductDetail.passengers.children != null) {
                List<PassengerChildren> childrenPasssengers = bookingMitra.trainProductDetail.passengers.children;
                for (PassengerChildren passengerChildren : childrenPasssengers) {
                    entryMitraPassenger.put(536870915, new Value(bookingMitra.bookingId));
                    entryMitraPassenger.put(536870917, new Value(passengerChildren.firstName));
                    entryMitraPassenger.put(536870918, new Value(passengerChildren.lastName));
                    entryMitraPassenger.put(536870919, new Value("Children"));

                    logger.info("passenger children:" + passengerChildren.firstName + "+" + passengerChildren.lastName);
                    remedyAPI.insertNewRecord(remedySession, travelokaPassengerForm, entryMitraPassenger);
                    bookingWorkInfo += "\nPassenger:" + passengerChildren.firstName + " " + passengerChildren.lastName + " (Children)";
                }
            }

            //getting childrenPassenger
            if (bookingMitra.trainProductDetail.passengers.infants != null) {
                List<PassengerInfant> infantPassengers = bookingMitra.trainProductDetail.passengers.infants;
                for (PassengerInfant passengerInfant : infantPassengers) {
                    entryMitraPassenger.put(536870915, new Value(bookingMitra.bookingId));
                    entryMitraPassenger.put(536870917, new Value(passengerInfant.firstName));
                    entryMitraPassenger.put(536870918, new Value(passengerInfant.lastName));
                    entryMitraPassenger.put(536870919, new Value("Infant"));

                    logger.info("passenger infant:" + passengerInfant.firstName + "+" + passengerInfant.lastName);
                    remedyAPI.insertNewRecord(remedySession, travelokaPassengerForm, entryMitraPassenger);
                    bookingWorkInfo += "\nPassenger:" + passengerInfant.firstName + " " + passengerInfant.lastName + " (Infant)";
                }
            }
        }
        //getting hotel product
        if (bookingMitra.hotelProductDetail != null) {
            tarifHotel += totalFare;
            String hotelName = bookingMitra.hotelProductDetail.hotelName;

            if (!hotelName.isEmpty()) {
                String mitraHotelForm = "PTM:SSC:HR:Travel:INT:Mitra:hotelProductDetail";
                Entry entryMitraHotel = new Entry();

                logger.info("hotel name=" + hotelName);
                entryMitraHotel.put(536870915, new Value(bookingMitra.bookingId));
                entryMitraHotel.put(536870916, new Value(hotelName));
                entryMitraHotel.put(536870917, new Value(bookingMitra.hotelProductDetail.hotelCity));
                entryMitraHotel.put(536870918, new Value(bookingMitra.hotelProductDetail.hotelCountry));

                //setting checkin date
                String checkinDate = bookingMitra.hotelProductDetail.checkInDate.year + "-"
                        + return2Char(bookingMitra.hotelProductDetail.checkInDate.month) + "-"
                        + return2Char(bookingMitra.hotelProductDetail.checkInDate.day);

                //checkinDate += (bookingMitra.hotelProductDetail.checkInDate.day.length()<2) ? 
                //		"-0"+ bookingMitra.hotelProductDetail.checkInDate.day :
                //			"-"+bookingMitra.hotelProductDetail.checkInDate.day;
                if (!isValidDate(checkinDate + " 10:00:00")) {
                    sscController.addWorkInfo(
                            "Booking " + bookingMitra.bookingId + " date is invalid",
                            "Booking " + bookingMitra.bookingId + " date is invalid, Please book again with valid travel date using same travel reff number",
                            bookingMitra.trNumber,
                            remedySession);
                    return new MitraResponse("FAILED", "A006", "Booking date is invalid");
                }

                entryMitraHotel.put(536870919, new Value(checkinDate));
                entryMitraHotel.put(536870920, new Value(bookingMitra.hotelProductDetail.numOfNights));
                entryMitraHotel.put(536870921, new Value(bookingMitra.hotelProductDetail.numOfRooms));
                entryMitraHotel.put(536870928, new Value(bookingMitra.hotelProductDetail.guestName));
                entryMitraHotel.put(536870927, new Value(bookingMitra.hotelProductDetail.roomName));
                entryMitraHotel.put(536870925, new Value(bookingMitra.hotelProductDetail.pricePerRoomPerNight.currency
                        + " " + bookingMitra.hotelProductDetail.pricePerRoomPerNight.fare));
                entryMitraHotel.put(536870926, new Value(bookingMitra.hotelProductDetail.currency));
                entryMitraHotel.put(536870929, new Value(bookingMitra.hotelProductDetail.fare));
                remedyAPI.insertNewRecord(remedySession, mitraHotelForm, entryMitraHotel);

                bookingWorkInfo += "\nHotel Name=" + hotelName
                        + "\nCity=" + bookingMitra.hotelProductDetail.hotelCity
                        + "\nCountry=" + bookingMitra.hotelProductDetail.hotelCountry
                        + "\nNumber of night=" + bookingMitra.hotelProductDetail.numOfNights
                        + "\nNumber of Rooms=" + bookingMitra.hotelProductDetail.numOfRooms
                        + "\nGuest Name=" + bookingMitra.hotelProductDetail.guestName
                        + "\nRoom Name =" + bookingMitra.hotelProductDetail.roomName;
                hariMenginap += bookingMitra.hotelProductDetail.numOfNights;

                /*
				bookingWorkInfo += (bookingMitra.currency.equalsIgnoreCase("IDR")) ? 
						"\nTotal Fare="+kursIndonesia.format(Double.parseDouble(bookingMitra.totalFare)) : 
							"\nTotal Fare="+bookingMitra.currency+" "+bookingMitra.totalFare;*/
            }

        }

        logger.info("mitra:" + mitraMainForm);
        remedyAPI.insertNewRecord(remedySession, mitraMainForm, entryMitrakaMainForm);
        sscController.addWorkInfo(bookingInformation,
                bookingWorkInfo,
                bookingMitra.trNumber,
                remedySession);
        /*
		Entry entryTravelokaNotif = new Entry();
		entryTravelokaNotif.put(536870916, new Value(bookingWorkInfo));
		entryTravelokaNotif.put(536870915, new Value(bookingInformation));
		entryTravelokaNotif.put(8, new Value(bookingMitra.corporateReferenceId));
		remedyAPI.insertNewRecord(remedySession, formTravelokaNotification, entryTravelokaNotif);*/

        //update harga OTA di PTM:SSC:HR:SKPD
        List<EntryListInfo> eListHRSKPDs = remedyAPI.getRemedyRecordByQuery(remedySession, "PTM:SSC:HR:SKPD", "'SR-Request ID'=\"" + reqNumberTrip + "\" ");
        try {
            for (EntryListInfo eListHRSKPD : eListHRSKPDs) {
                Entry recordSKPD = remedySession.getEntry("PTM:SSC:HR:SKPD", eListHRSKPD.getEntryID(), null);
                hariMenginap += recordSKPD.get(536870935).getIntValue();
                tarifPesawat += recordSKPD.get(536871140).getIntValue();
                tarifHotel += recordSKPD.get(536871141).getIntValue();

                recordSKPD.put(536870935, new Value(hariMenginap)); //jumlah hari
                recordSKPD.put(536871140, new Value(tarifPesawat)); //penerbangan
                recordSKPD.put(536871141, new Value(tarifHotel)); //hotel

                remedySession.setEntry("PTM:SSC:HR:SKPD", recordSKPD.getEntryId(), recordSKPD, null, 0);
            }
        } catch (ARException e) {
            logger.info("ARException Error on OTA Calculation : " + e.toString());
        }

        Entry entryremedy = new Entry();
        entryremedy.put(536870913, new Value(bookingMitra.bookingId));
        entryremedy.put(536870915, new Value(bookingMitra.employeeId));
        entryremedy.put(536870916, new Value(bookingMitra.trNumber));
        entryremedy.put(536870917, new Value("SUCCESSFUL"));

        remedyAPI.insertNewRecord(remedySession, "PTM:SSC:HR:Travel:IssuanceTicketStatus", entryremedy);

        return new MitraResponse("SUCCESSFUL", "-", "-");
    }

    public MitraResponse isBookingMitraValid(
            String corporateRefId,
            ARServerUser remedySession,
            String bookingId) {

        boolean isValid = false;
        RemedyAPI remedyAPI = new RemedyAPI();
        SSCController sscController = new SSCController();

        try {
            List<EntryListInfo> eListValidSRMs = remedyAPI.getRemedyRecordByQuery(
                    remedySession,
                    "SRM:Request",
                    "'CorporateReferenceID__c'=\"" + corporateRefId + "\"");
            logger.info("corp=" + corporateRefId);
            logger.info("piro see" + eListValidSRMs.size());
            if (eListValidSRMs.size() < 1) {
                return new MitraResponse("FAILED", "A003", "Corporate Reference ID is invalid");
            } else {
                for (EntryListInfo eListValidSRM : eListValidSRMs) {
                    Entry srmRecord = remedySession.getEntry("SRM:Request", eListValidSRM.getEntryID(), null);
                    String reqNumber = srmRecord.get(1000000829).getValue().toString();
                    reqNumberTrip = reqNumber;

                    logger.info("tidak ada work info approved:" + reqNumber);

                    //check apakah diluar tanggal dinas?
                    List<EntryListInfo> eListSKPDs = remedyAPI.getRemedyRecordByQuery(
                            remedySession,
                            "PTM:SSC:HR:Travel:INT:SAP",
                            "'REQNUMBER__c'=\"" + reqNumber + "\" AND 'Status__c'=\"1\" ");

                    if (eListSKPDs.size() == 0) {
                        return new MitraResponse("FAILED", "A005", "Corporate Reference ID is invalid");
                    } else {
                        logger.info("testing skpd=" + eListSKPDs.size());
                        for (EntryListInfo eListSKPD : eListSKPDs) {
                            Entry skpdRecord = remedySession.getEntry("PTM:SSC:HR:Travel:INT:SAP", eListSKPD.getEntryID(), null);
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                            Timestamp tsBerangkatDinas = new Timestamp(
                                    df.parse(skpdRecord.get(536870919).getValue().toString()
                                            + " " + skpdRecord.get(536870920).getValue().toString()).getTime());
                            tanggalAwalDinas = tsBerangkatDinas.getTime();

                            Timestamp tsPulangDinas = new Timestamp(
                                    df.parse(skpdRecord.get(536870921).getValue().toString()
                                            + " " + skpdRecord.get(536870922).getValue().toString()).getTime());
                            tanggalAkhirDinas = tsPulangDinas.getTime();

                            logger.info("timestamp berangkat=" + tsBerangkatDinas.toString() + ":" + tanggalAwalDinas);
                            logger.info("ts pulang=" + tsPulangDinas.toString() + ":" + tanggalAkhirDinas);
                        }
                    }

                }
            }
        } catch (ARException e) {
            logger.info("ARException Error on generate SAPIntegrationMetadata: " + e.toString());
        } catch (ParseException e) {
            logger.info("ARException Error on parse datetime: " + e.toString());
        }

        return new MitraResponse("SUCCESSFUL", "-", "On progress checking");
    }

    public void addWorkInfo(
            String summary,
            String notes,
            String corpRefId,
            ARServerUser remedySession) {

        RemedyAPI remedyAPI = new RemedyAPI();
        Entry entryTravelokaNotif = new Entry();
        entryTravelokaNotif.put(536870916, new Value(notes));
        entryTravelokaNotif.put(536870915, new Value(summary));
        entryTravelokaNotif.put(8, new Value(corpRefId));
        String formTravelokaNotification = "PTM:SSC:HR:Travel:SendTravelokaBookingtoWorkInfo";

        logger.info("input in bmc:" + corpRefId);

        remedyAPI.insertNewRecord(remedySession, formTravelokaNotification, entryTravelokaNotif);
    }

}
