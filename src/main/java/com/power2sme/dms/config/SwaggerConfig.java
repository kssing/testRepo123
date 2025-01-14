package com.power2sme.dms.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	private static final String COM_POWER2SME_DMS = "com.power2sme.dms";
	private static final String JWT_AUTHORIZATION_HEADER = "JWT Authorization Header";
	private static final String HEADER = "header";
	private static final String STRING = "string";
	private static final String X_AUTH_POWER2SMETOKEN = "X-Auth-power2smetoken";
	
	@Autowired
	ServletContext servletContext;
	
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.power2sme.dms"))
				.paths(PathSelectors.regex("/api/(?!v2).*")).build()
				.pathProvider(new RelativePathProvider(servletContext))
				.apiInfo(apiInfo());
	}

	@Bean
	public Docket api2() {
		ParameterBuilder aParameterBuilder = new ParameterBuilder();
		aParameterBuilder.name(X_AUTH_POWER2SMETOKEN).modelRef(new ModelRef(STRING)).parameterType(HEADER)
				.description(JWT_AUTHORIZATION_HEADER).required(true).build();
		List<Parameter> aParameters = new ArrayList<>();
		aParameters.add((Parameter) aParameterBuilder.build());
		return new Docket(DocumentationType.SWAGGER_2).groupName("api-2.0").select()
				.apis(RequestHandlerSelectors.basePackage(COM_POWER2SME_DMS)).paths(PathSelectors.regex("/api/v2/.*")).build()
				.apiInfo(apiInfo()).globalOperationParameters(aParameters);
	}
	
	private ApiInfo apiInfo() {
		Contact contact = new Contact("Power2SME", "rahul.chaurasia@power2sme.com", "https://www.power2sme.com/");
		return new ApiInfo("DMS API DOCUMENTATION", "Documentation of DMS API is listed here", "1.0",
				"https://www.power2sme.com/termsandconditions", contact, "Power2sme Pvt. Limited",
				"https://www.power2sme.com/", new ArrayList<>());
	}

}
