package com.power2sme.dms.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix="dms.api.fieldname")
public class ApiConfiguration {

	private String errorcode;
	private String message;
	private String status;
	private String totalrecords;
	private String data;

}
