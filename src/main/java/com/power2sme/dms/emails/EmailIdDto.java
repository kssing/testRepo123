package com.power2sme.dms.emails;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EmailIdDto {
	@JacksonXmlProperty(localName="email_id")
	private String emailId;
}
