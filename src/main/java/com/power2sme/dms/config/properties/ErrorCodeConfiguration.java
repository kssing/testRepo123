package com.power2sme.dms.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix="dms.api.errorcode")
public class ErrorCodeConfiguration {
	private Integer db;
	private Integer internalserver;
	private Integer dataformaterror;
	private Integer norecordfound;
	private Integer inputerror;
	private Integer connectionerror;
	private Integer inputerrorlogin;
	

}
