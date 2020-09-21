package com.mii.sscintegration.domain;

import java.util.List;

import com.mii.sscintegration.domain.traveloka.TravelFlightPrint;
import com.mii.sscintegration.domain.traveloka.TravelHotelPrint;

public class TravelokaPDF {
	public String noTrip, jenisPerjalanan, pekerja, sifatPerjalanan,
	tanggalDinas, alokasiCostCenter, pulangDinas, estimatedCost, alasanPerjalanan,
	totalBiayaOTA, kotaAsal, kotatujuan1, tgltujuan1, kotatujuan2, tgltujuan2, kotatujuan3, tgltujuan3, approvalTimeLimit, requstNumber, srInstanceId,
	companyCode, nomorPekerja;
	
	public List <TravelHotelPrint> listHotel;
	public List <TravelFlightPrint> listFlight;
	public String namafile;
}