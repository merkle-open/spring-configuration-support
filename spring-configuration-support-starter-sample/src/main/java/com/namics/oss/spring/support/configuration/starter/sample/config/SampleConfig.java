package com.namics.oss.spring.support.configuration.starter.sample.config;

import com.namics.oss.spring.support.configuration.ConfigurationEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.H2)
				.generateUniqueName(true)
				.ignoreFailedDrops(true)
				.setScriptEncoding("UTF-8")
				.addScripts("classpath:/db/schema.sql", "classpath:/db/data.sql")
				.build();
	}

	// Optional:
	// set right environment to administrate over ConfigurationValueService (e.g. to persist/modify properties for a certain environment)
	@Bean
	public ConfigurationEnvironment configurationEnvironmentDefault() {
		return new ConfigurationEnvironment("DEV");
	}

}
