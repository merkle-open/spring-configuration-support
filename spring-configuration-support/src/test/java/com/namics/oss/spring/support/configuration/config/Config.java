/*
 * Copyright 2000-2015 Namics AG. All rights reserved.
 */

package com.namics.oss.spring.support.configuration.config;

import com.namics.oss.spring.support.configuration.DatabaseConfigurationPropertiesFactoryBean;
import com.namics.oss.spring.support.configuration.OrderedProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import javax.sql.DataSource;

/**
 * Config.
 *
 * @author aschaefer, Namics AG
 * @since 06.02.15 16:43
 */
@Configuration
public class Config {

	@Bean
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()
				.continueOnError(true)
				.addScripts("classpath:/META-INF/db/schema.sql", "classpath:/META-INF/db/test-data.sql")
				.build();
	}

	@Bean
	@DependsOn("source")
	public PropertySourcesPlaceholderConfigurer databaseConfigurer() throws Exception {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean(name = "source")
	public PropertiesPropertySource[] source(
			@Qualifier("props") OrderedProperties orderedProperties,
			ConfigurableEnvironment environment) throws Exception {

		PropertiesPropertySource[] propertiesPropertySources = orderedProperties.toPropertiesPropertySources();
		for(PropertiesPropertySource propertiesPropertySource: propertiesPropertySources){
			environment.getPropertySources().addFirst(propertiesPropertySource);
		}
		return propertiesPropertySources;
	}

	@Bean(name = "props")
	@Profile("!PROD")
	public OrderedProperties databaseConfigurationDev(Environment environment) throws Exception {
		return new DatabaseConfigurationPropertiesFactoryBean(dataSource())
				.tableName("tbl_configuration")
				.environmentColumn("configuration_env")
				.propertyKeyColumn("configuration_key")
				.valueColumn("configuration_value")
				.environments(environment.getActiveProfiles())
				.getObject();
	}

	@Bean(name = "props")
	@Profile("PROD")
	public OrderedProperties databaseConfigurationLive(Environment environment) throws Exception {
		return new DatabaseConfigurationPropertiesFactoryBean(dataSource())
				.tableName("tbl_configuration")
				.environmentColumn("configuration_env")
				.propertyKeyColumn("configuration_key")
				.valueColumn("configuration_value")
				.environments(environment.getActiveProfiles())
				.getObject();
	}

	@Bean
	public TestBean testBean(org.springframework.core.env.Environment env) {
		return new TestBean().testValue(env.getProperty("tst.key.a"));
	}
}
