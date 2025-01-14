package com.power2sme.dms.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "dms.file.extension")
public class FileExtensionConfiguration {

	private String allowed;
}
