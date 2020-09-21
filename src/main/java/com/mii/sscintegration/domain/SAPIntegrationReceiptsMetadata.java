package com.mii.sscintegration.domain;

public class SAPIntegrationReceiptsMetadata {
	private String reqNumber,
	receiptNumber,
	expType,
	recAmount,
	recCurr,
	recDate;

	public String getReqNumber() {
		return reqNumber;
	}

	public void setReqNumber(String reqNumber) {
		this.reqNumber = reqNumber;
	}

	public String getReceiptNumber() {
		return receiptNumber;
	}

	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}

	public String getExpType() {
		return expType;
	}

	public void setExpType(String expType) {
		this.expType = expType;
	}

	public String getRecAmount() {
		return recAmount;
	}

	public void setRecAmount(String recAmount) {
		this.recAmount = recAmount;
	}

	public String getRecCurr() {
		return recCurr;
	}

	public void setRecCurr(String recCurr) {
		this.recCurr = recCurr;
	}

	public String getRecDate() {
		return recDate;
	}

	public void setRecDate(String recDate) {
		this.recDate = recDate;
	}
	
	
}
