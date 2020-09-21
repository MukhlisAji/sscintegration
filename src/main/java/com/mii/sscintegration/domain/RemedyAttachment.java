package com.mii.sscintegration.domain;

public class RemedyAttachment {
	private String filename, content64;
	private byte[] attachedFile;
	private String urutan;
	
	public RemedyAttachment(String filename, String content64, byte[] attachedFile) {
		this.filename = filename;
		this.content64 = content64;
		this.attachedFile = attachedFile;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getContent64() {
		return content64;
	}

	public void setContent64(String content64) {
		this.content64 = content64;
	}

	public byte[] getAttachedFile() {
		return attachedFile;
	}

	public void setAttachedFile(byte[] attachedFile) {
		this.attachedFile = attachedFile;
	}

	public String getUrutan() {
		return urutan;
	}

	public void setUrutan(String urutan) {
		this.urutan = urutan;
	}
}
