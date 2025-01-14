package com.power2sme.dms.aws.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix="dms.aws")
public class AWSBucketConfiguration {

	private String bucketname;
	private String clientregion;
	private String filepath;
}
