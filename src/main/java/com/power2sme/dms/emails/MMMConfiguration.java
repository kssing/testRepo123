package com.power2sme.dms.emails;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix="finansme.mmm.api")
@Data
public class MMMConfiguration {
	private String baseUrl;
}
