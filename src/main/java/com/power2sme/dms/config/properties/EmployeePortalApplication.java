package com.power2sme.dms.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix="application.homepage")
public class EmployeePortalApplication {
	String url;
    String oauthUrl;
    String loginUrl;
    String logoUrl;
    String documentationUrl;
}
