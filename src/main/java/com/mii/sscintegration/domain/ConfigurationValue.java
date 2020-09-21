package com.mii.sscintegration.domain;


//@Configuration
//@ComponentScan(basePackages = { "com.mii.sscintegration.*" })
//@PropertySource("classpath:sscconfig.properties")
public class ConfigurationValue {
	
	private String remedyServer;
	private String remedyUsername;
	private String remedyPassword;
	private String remedyPort;
	private String filenetUrl; 
	private String filenetPassword; 
	private String filenetUser;
	private String filenetObjectStore; 
	private String filenetMiddleForm;
	private String remedyTravelSettlement;
	private String remedyTravelAdvance;
	private String travelokaPayment;
	private String sapTripWs;
	private String sapCredential;
	private String remedyPullData;
	private String pertaminaProxy;
	
	
	
	
	public ConfigurationValue(
			String remedyServer, 
			String remedyUsername,
			String remedyPassword,  
			String remedyPort,
			String filenetUrl,
			String filenetPassword, 
			String filenetUser,
			String filenetObjectStore,
			String filenetMiddleForm,
			String remedyTravelSettlement,
			String remedyTravelAdvance,
			String travelokaPayment,
			String sapTripWs,
			String sapCredential,
			String remedyPullData,
			String pertaminaProxy) {
		this.remedyServer = remedyServer;
		this.remedyPassword = remedyPassword;
		this.remedyUsername = remedyUsername;
		this.remedyPort = remedyPort;
		this.filenetUrl = filenetUrl;
		this.filenetUser = filenetUser;
		this.filenetPassword = filenetPassword;
		this.filenetObjectStore = filenetObjectStore;
		this.filenetMiddleForm = filenetMiddleForm;
		this.remedyTravelSettlement = remedyTravelSettlement;
		this.remedyTravelAdvance = remedyTravelAdvance;
		this.travelokaPayment = travelokaPayment;
		this.sapTripWs = sapTripWs;
		this.sapCredential = sapCredential;
		this.remedyPullData = remedyPullData;
		this.pertaminaProxy = pertaminaProxy;
	}
	
	public ConfigurationValue() {
		
	}


	public String getRemedyServer() {
		return remedyServer;
	}


	public void setRemedyServer(String remedyServer) {
		this.remedyServer = remedyServer;
	}


	public String getRemedyUsername() {
		return remedyUsername;
	}


	public void setRemedyUsername(String remedyUsername) {
		this.remedyUsername = remedyUsername;
	}


	public String getRemedyPassword() {
		return remedyPassword;
	}


	public void setRemedyPassword(String remedyPassword) {
		this.remedyPassword = remedyPassword;
	}


	public String getRemedyPort() {
		return remedyPort;
	}


	public void setRemedyPort(String remedyPort) {
		this.remedyPort = remedyPort;
	}


	public String getFilenetUrl() {
		return filenetUrl;
	}


	public void setFilenetUrl(String filenetUrl) {
		this.filenetUrl = filenetUrl;
	}


	public String getFilenetPassword() {
		return filenetPassword;
	}


	public void setFilenetPassword(String filenetPassword) {
		this.filenetPassword = filenetPassword;
	}


	public String getFilenetUser() {
		return filenetUser;
	}


	public void setFilenetUser(String filenetUser) {
		this.filenetUser = filenetUser;
	}


	public String getFilenetObjectStore() {
		return filenetObjectStore;
	}


	public void setFilenetObjectStore(String filenetObjectStore) {
		this.filenetObjectStore = filenetObjectStore;
	}


	public String getFilenetMiddleForm() {
		return filenetMiddleForm;
	}


	public void setFilenetMiddleForm(String filenetMiddleForm) {
		this.filenetMiddleForm = filenetMiddleForm;
	}


	public String getRemedyTravelSettlement() {
		return remedyTravelSettlement;
	}


	public void setRemedyTravelSettlement(String remedyTravelSettlement) {
		this.remedyTravelSettlement = remedyTravelSettlement;
	}


	public String getRemedyTravelAdvance() {
		return remedyTravelAdvance;
	}


	public void setRemedyTravelAdvance(String remedyTravelAdvance) {
		this.remedyTravelAdvance = remedyTravelAdvance;
	}

	public String getTravelokaPayment() {
		return travelokaPayment;
	}

	public void setTravelokaPayment(String travelokaPayment) {
		this.travelokaPayment = travelokaPayment;
	}

	public String getSapTripWs() {
		return sapTripWs;
	}

	public void setSapTripWs(String sapTripWs) {
		this.sapTripWs = sapTripWs;
	}

	public String getSapCredential() {
		return sapCredential;
	}

	public void setSapCredential(String sapCredential) {
		this.sapCredential = sapCredential;
	}

	public String getRemedyPullData() {
		return remedyPullData;
	}

	public void setRemedyPullData(String remedyPullData) {
		this.remedyPullData = remedyPullData;
	}

	public String getPertaminaProxy() {
		return pertaminaProxy;
	}

	public void setPertaminaProxy(String pertaminaProxy) {
		this.pertaminaProxy = pertaminaProxy;
	}
}
