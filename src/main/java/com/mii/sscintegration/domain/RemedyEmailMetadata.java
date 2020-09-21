package com.mii.sscintegration.domain;

public class RemedyEmailMetadata {
	private String emailFrom,
	emailTo,
	subjectEmail,
	emailBody,
	namaFileAttachment;

	public String getEmailFrom() {
		return emailFrom;
	}

	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}

	public String getEmailTo() {
		return emailTo;
	}

	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}

	public String getSubjectEmail() {
		return subjectEmail;
	}

	public void setSubjectEmail(String subjectEmail) {
		this.subjectEmail = subjectEmail;
	}

	public String getEmailBody() {
		return emailBody;
	}

	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}

	public String getNamaFileAttachment() {
		return namaFileAttachment;
	}

	public void setNamaFileAttachment(String namaFileAttachment) {
		this.namaFileAttachment = namaFileAttachment;
	}
}
