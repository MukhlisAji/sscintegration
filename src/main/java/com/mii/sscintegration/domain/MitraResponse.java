package com.mii.sscintegration.domain;

public class MitraResponse {
	private String status, errorCode, errorMessage;
	
	public MitraResponse(String status, String errorCode, String errorMessage) {
		this.status = status;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
}
