package com.power2sme.dms.emails;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
@JacksonXmlRootElement(localName = "payload")
public class OnZippingCompletedEmailData {
	@JacksonXmlProperty(localName="object")
	private String object;
	
	@JacksonXmlProperty(localName="event")
	private String event;
	
	@JacksonXmlProperty(localName="mail_to")
	private EmailIdDto mailTo;
	
	@JacksonXmlProperty(localName="mail_cc")
	private EmailIdDto mailCC;
	
	@JacksonXmlProperty(localName="link")
	private String link;
	
	@JacksonXmlProperty(localName="sme_id")
	private String smeId;
}


