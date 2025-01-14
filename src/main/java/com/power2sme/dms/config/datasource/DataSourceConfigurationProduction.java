package com.power2sme.dms.config.datasource;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

@Configuration
@Profile("!local")
public class DataSourceConfigurationProduction {

	 @Value("${spring.datasource.jndi-name}")
     private String jndiName;


	@Primary
	@Bean(value = "dmsDataSource")
	public DataSource dmsDataSource()
	{
		 JndiDataSourceLookup jndiDataSourceLookup = new JndiDataSourceLookup();
         return jndiDataSourceLookup.getDataSource(jndiName);

	}

}
