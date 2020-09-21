package com.mii.sscintegration.domain;

import javax.xml.soap.SOAPMessage;

public class SoapResponse {
	private SOAPMessage soapResponse;
	private String connectionNotes, status, errorMessage, tripNo;
	
	public SoapResponse(SOAPMessage soapResponse, String connectionNotes) {
		this.soapResponse = soapResponse;
		this.connectionNotes = connectionNotes;
	}
	
	public SOAPMessage getSoapResponse() {
		return soapResponse;
	}
	public void setSoapResponse(SOAPMessage soapResponse) {
		this.soapResponse = soapResponse;
	}
	public String getConnectionNotes() {
		return connectionNotes;
	}
	public void setConnectionNotes(String connectionNotes) {
		this.connectionNotes = connectionNotes;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getTripNo() {
		return tripNo;
	}

	public void setTripNo(String tripNo) {
		this.tripNo = tripNo;
	}
	
}
