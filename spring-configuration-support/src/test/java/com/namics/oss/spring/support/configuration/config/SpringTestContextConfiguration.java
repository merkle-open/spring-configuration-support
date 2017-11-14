package com.namics.oss.spring.support.configuration.config;

import com.namics.oss.spring.support.configuration.ConfigurationEnvironment;
import com.namics.oss.spring.support.configuration.DatabaseConfigurationPropertiesFactoryBean;
import com.namics.oss.spring.support.configuration.OrderedProperties;
import com.namics.oss.spring.support.configuration.dao.ConfigurationDao;
import com.namics.oss.spring.support.configuration.dao.ConfigurationDaoImpl;
import com.namics.oss.spring.support.configuration.service.ConfigurationValueService;
import com.namics.oss.spring.support.configuration.service.ConfigurationValueServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import javax.inject.Named;
import javax.sql.DataSource;

/**
 * ContextConfiguration.
 *
 * @author crfischer, Namics AG
 * @since 26.09.2017 10:11
 */
@Configuration
public class SpringTestContextConfiguration {

	@Bean
	@DependsOn("databaseConfigurationSources")
	public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer(){
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean(name = "databaseConfigurationSources")
	public PropertiesPropertySource[] databaseConfigurationSource(
			@Named("databaseConfigurationFactoryBean") OrderedProperties orderedProperties,
			ConfigurableEnvironment environment) throws Exception {

		PropertiesPropertySource[] propertiesPropertySources = orderedProperties.toPropertiesPropertySources();
		for(int i=(propertiesPropertySources.length-1);i>=0;i--){
			environment.getPropertySources().addAfter(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, propertiesPropertySources[i]);
		}
		return propertiesPropertySources;
	}

	@Bean("databaseConfigurationFactoryBean")
	public DatabaseConfigurationPropertiesFactoryBean databaseConfigurationPropertiesFactoryBean(Environment environment){
		return new DatabaseConfigurationPropertiesFactoryBean(dataSource(),
				"tbl_configuration",
				"configuration_env",
				"configuration_key",
				"configuration_value",
				environment.getActiveProfiles());
	}

	@Bean
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()
				.continueOnError(true)
				.addScripts("classpath:/META-INF/db/schema.sql", "classpath:/META-INF/db/test-data.sql")
				.build();
	}

	@Bean("configurationDao")
	public ConfigurationDao configurationDao(){
		ConfigurationDaoImpl configurationDao = new ConfigurationDaoImpl();
		configurationDao.setDataSource(dataSource());
		configurationDao.setTableName("tbl_configuration");
		configurationDao.setTableKeyColumn("configuration_key");
		configurationDao.setTableEnvColumn("configuration_env");
		configurationDao.setTableValueColumn("configuration_value");
		return configurationDao;
	}

	@Bean("configurationService")
	public ConfigurationValueService configurationValueService(Environment environment){
		return new ConfigurationValueServiceImpl(configurationDao(), getEnvironment(environment));
	}

	protected com.namics.oss.spring.support.configuration.Environment getEnvironment(Environment environment){

		if(environment.acceptsProfiles("DEV")){
			return new ConfigurationEnvironment("DEV");
		}
		if(environment.acceptsProfiles("QUAL")){
			return new ConfigurationEnvironment("QUAL");
		}
		if(environment.acceptsProfiles("PROD")){
			return new ConfigurationEnvironment("PROD");
		}
		return new ConfigurationEnvironment("DEV");
	}
}
