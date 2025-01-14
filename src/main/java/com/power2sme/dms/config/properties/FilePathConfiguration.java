package com.power2sme.dms.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "dms.path")
public class FilePathConfiguration {

	private String file;
	private String folder;

}
