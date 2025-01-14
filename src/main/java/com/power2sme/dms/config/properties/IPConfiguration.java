package com.power2sme.dms.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "dms.ip")
public class IPConfiguration {
	private String blacklist;
	private String nonajaxallowed;

}
