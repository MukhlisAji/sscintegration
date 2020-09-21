package com.mii.sscintegration.domain;

public class TestingDomain {
	private long testingId;
	private String content;
	
	public TestingDomain(long testingId, String content) {
		this.content = content;
		this.testingId = testingId;
	}
	
	public long getTestingId() {
		return testingId;
	}
	public void setTestingId(long testingId) {
		this.testingId = testingId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	
}
