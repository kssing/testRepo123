package com.power2sme.dms.config;

import java.lang.reflect.Field;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.power2sme.dms.aws.configuration.AWSBucketConfiguration;
import com.power2sme.dms.config.properties.ApiConfiguration;
import com.power2sme.dms.config.properties.ApiMessageConfiguration;
import com.power2sme.dms.config.properties.DmsDbConfiguration;
import com.power2sme.dms.config.properties.EmployeePortalApplication;
import com.power2sme.dms.config.properties.ErrorCodeConfiguration;
import com.power2sme.dms.config.properties.FileExtensionConfiguration;
import com.power2sme.dms.config.properties.FilePathConfiguration;
import com.power2sme.dms.config.properties.IPConfiguration;
import com.power2sme.dms.emails.MMMConfiguration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


@EnableConfigurationProperties({
	ApiConfiguration.class,
	ApiMessageConfiguration.class,
	ErrorCodeConfiguration.class,
	DmsDbConfiguration.class,
	FilePathConfiguration.class,
	IPConfiguration.class,
	FileExtensionConfiguration.class,
	AWSBucketConfiguration.class,
	EmployeePortalApplication.class,
	MMMConfiguration.class
	
})
@Configuration
@Slf4j
@Data
public class ApplicationConfiguration {

	private static final String DASHES = "----------------------------------";
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	@Autowired
	private ApiConfiguration apiConfiguration;

	@Autowired
	private ApiMessageConfiguration apiMessageConfiguration;

	@Autowired
	private ErrorCodeConfiguration errorCodeConfiguration;
	
	@Autowired
	private DmsDbConfiguration dmsDbConfiguration;
	
	@Autowired
	private FilePathConfiguration filePathConfiguration;
	
	@Autowired
	private IPConfiguration ipConfiguration;
	
	@Autowired
	private FileExtensionConfiguration fileExtensionConfiguration;
	
	@Autowired
	private AWSBucketConfiguration aWSBucketConfiguration;
	
	@Autowired
	private EmployeePortalApplication employeePortalApplication;
	
	private Gson gson;

	@PostConstruct
	public void init() throws IllegalAccessException {
		if (log.isInfoEnabled()) {
			GsonBuilder builder = new GsonBuilder();
			builder.setPrettyPrinting();
			gson = builder.create();
			printConfigurations();
		}
	}
	
	private void printConfigurations() throws IllegalAccessException {
		if (log.isInfoEnabled()) {
			for (Field f : ApplicationConfiguration.class.getDeclaredFields()) {
				Autowired autowiredField = f.getAnnotation(Autowired.class);
				if (autowiredField != null) {					
					//log.logAtInfo(log,LINE_SEPARATOR+DASHES+f.getType().getSimpleName()+DASHES+LINE_SEPARATOR+gson.toJson(f.get(this))+LINE_SEPARATOR+DASHES+DASHES+DASHES);
					log.info(LINE_SEPARATOR+DASHES+f.getType().getSimpleName()+DASHES+LINE_SEPARATOR+gson.toJson(f.get(this))+LINE_SEPARATOR+DASHES+DASHES+DASHES);
				}
			}
		}
	}
}
