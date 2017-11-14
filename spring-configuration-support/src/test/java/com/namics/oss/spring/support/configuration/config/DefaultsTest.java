/*
 * Copyright 2000-2015 Namics AG. All rights reserved.
 */

package com.namics.oss.spring.support.configuration.config;

import com.namics.oss.spring.support.configuration.DatabaseConfigurationPropertiesFactoryBean;
import com.namics.oss.spring.support.configuration.OrderedProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;

/**
 * DefaultsTest.
 *
 * @author aschaefer, Namics AG
 * @since 26.02.15 13:01
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("DEV")
@ContextConfiguration(classes = DefaultsTest.Config.class)
public class DefaultsTest {

	@Autowired
	TestBean devBean;

	@Test
	public void testDev() throws Exception {
		assertEquals("DEFAULT", devBean.getTestValue());
	}

	@Configuration
	public static class Config {
		@Bean
		public DataSource dataSource() {
			return new EmbeddedDatabaseBuilder()
					.continueOnError(true)
					.addScripts("classpath:/com/namics/oss/spring/support/configuration/default-schema.sql",
					            "classpath:/META-INF/db/namics-configuration-default-data.sql")
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
				environment.getPropertySources().addAfter(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, propertiesPropertySource);
			}
			return propertiesPropertySources;
		}

		@Bean(name = "props")
		public OrderedProperties databaseConfigurationDev() throws Exception {
			return new DatabaseConfigurationPropertiesFactoryBean(dataSource()).getObject();
		}

		@Bean
		public TestBean testBean(org.springframework.core.env.Environment env) {
			return new TestBean().testValue(env.getProperty("tst.key.a"));
		}

	}
}
