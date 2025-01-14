/**
 * @author himanshu{@link himanshushekhar002@gmail.com}}
 * @Date 29th April, 2016
 * 
 * This class act as a starting point to make Spring Context up with
 * all configurations loaded. 
 * It provides location for scanning different packages and location
 * for other configuration class.
 * This class itself provide custom configuration for
 * ViewResolver, MultipartResolver, & extension for HttpMessageConverters  
 * This class is close for extension as it is project specific Configuration
 */
package com.power2sme.dms.config;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.power2sme.dms.config.properties.FilePathConfiguration;
import com.power2sme.dms.service.DocumentService;
import com.power2sme.metalogging.config.EnableP2SApiLog;

@EnableP2SApiLog
@EnableWebMvc
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Autowired
	private FilePathConfiguration filePathConfiguration;

	@Autowired
	private DocumentService docService;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		System.out.println("himanshu true2");
		this.applicationContext = applicationContext;
	}

	@Bean
	public MultipartResolver multipartResolver() {
		return new CommonsMultipartResolver();
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
		registry.addResourceHandler("/scripts/**").addResourceLocations("/scripts/");
		registry.addResourceHandler("/css/**").addResourceLocations("/css/");
		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

		try {
			registry.addResourceHandler("/files/**")
					.addResourceLocations(new File(filePathConfiguration.getFile()).toURI().toString(),
							new URL("https://s3-ap-southeast-1.amazonaws.com/p2sdms/").toURI().toString())
					.setCachePeriod(300);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		List<String> allowedOrigins = docService.getCorsAllowedOrigin();
		registry.addMapping("/**").allowedOrigins(allowedOrigins.toArray(new String[allowedOrigins.size()]));
	}

	@Bean
	public ViewResolver viewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setSuffix(".html");
		resolver.setPrefix("/WEB-INF/views/");
		resolver.setApplicationContext(applicationContext);
		return resolver;
	}
	
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.defaultContentType(MediaType.APPLICATION_JSON);
	}
}
