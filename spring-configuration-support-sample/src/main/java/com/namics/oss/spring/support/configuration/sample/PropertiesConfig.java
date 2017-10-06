/*
 * Copyright 2000-2016 Namics AG. All rights reserved.
 */

package com.namics.oss.spring.support.configuration.sample;

import com.namics.oss.spring.support.configuration.ConfigurationEnvironment;
import com.namics.oss.spring.support.configuration.DaoConfigurationPropertiesFactoryBean;
import com.namics.oss.spring.support.configuration.OrderedProperties;
import com.namics.oss.spring.support.configuration.dao.ConfigurationDao;
import com.namics.oss.spring.support.configuration.dao.ConfigurationDaoJdbcImpl;
import com.namics.oss.spring.support.configuration.service.ConfigurationValueServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;

import javax.inject.Named;
import javax.sql.DataSource;

/**
 * PropertiesConfig.
 *
 * @author aschaefer, Namics AG
 * @since 10.02.16 16:22
 */
@Configuration
public class PropertiesConfig {

	@Bean(name = "dbSource")
	public PropertiesPropertySource[] source(
			@Named("dbProps") OrderedProperties orderedProperties,
			ConfigurableEnvironment environment) throws Exception {

		PropertiesPropertySource[] propertiesPropertySources = orderedProperties.toPropertiesPropertySources();
		for(int i=(propertiesPropertySources.length-1);i>=0;i--){
			environment.getPropertySources().addFirst(propertiesPropertySources[i]);
		}
		return propertiesPropertySources;
	}

	@Bean(name = "dbProps")
	public DaoConfigurationPropertiesFactoryBean databaseConfigFactory(ConfigurationDao configurationDao, Environment environment) throws Exception {
		return new DaoConfigurationPropertiesFactoryBean(configurationDao, environment.getActiveProfiles(), "*");
	}

	@Bean
	public ConfigurationDaoJdbcImpl configurationDao(DataSource dataSource) {
		return ConfigurationDaoJdbcImpl.forDataSource(dataSource)
		                               .tableName("nmx_configuration")
		                               .environmentColumn("environment")
		                               .propertyKeyColumn("property_key")
		                               .valueColumn("property_value")
		                               .build();
	}

	@Bean
	public ConfigurationValueServiceImpl configurationValueService(ConfigurationDao configurationDao) {
		return new ConfigurationValueServiceImpl(configurationDao, new ConfigurationEnvironment("DEV"));
	}
}
