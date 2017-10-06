package com.namics.oss.spring.support.configuration.starter.sample.config;

import com.namics.oss.spring.support.configuration.ConfigurationEnvironment;
import com.namics.oss.spring.support.configuration.dao.ConfigurationDao;
import com.namics.oss.spring.support.configuration.service.ConfigurationValueService;
import com.namics.oss.spring.support.configuration.service.ConfigurationValueServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

/**
 * SampleConfig.
 *
 * @author crfischer, Namics AG
 * @since 03.08.2017 09:26
 */
@Configuration
public class SampleConfig {

	@Bean
	@DependsOn("databaseConfigurationSource")
	public static PropertySourcesPlaceholderConfigurer databaseConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public DataSource dataSource(){
		return new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.H2)
				.generateUniqueName(true)
				.ignoreFailedDrops(true)
				.setScriptEncoding("UTF-8")
				.addScripts("classpath:/db/schema.sql","classpath:/db/data.sql")
				.build();
	}

	// Optional:
	// Create ConfigurationValueService-Bean if required (e.g. to persist/modify properties for a certain environment)
	@Bean
	public ConfigurationValueService configurationValueService(ConfigurationDao configurationDao){
		return new ConfigurationValueServiceImpl(configurationDao, new ConfigurationEnvironment("DEV"));
	}
}
