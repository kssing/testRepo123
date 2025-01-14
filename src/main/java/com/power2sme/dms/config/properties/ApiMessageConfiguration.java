package com.power2sme.dms.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix="dms.api.message")
public class ApiMessageConfiguration {

	private String errorcode;
	private String success;
	private String norecfound;
	private String dbexception;
	private String failed;
}
