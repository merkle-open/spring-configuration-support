package com.namics.oss.spring.support.configuration;

import com.namics.oss.spring.support.configuration.dao.ConfigurationDao;
import com.namics.oss.spring.support.configuration.dao.ConfigurationDaoImpl;
import com.namics.oss.spring.support.configuration.model.ConfigurationValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

/**
 * DaoConfigurationPropertiesTest.
 *
 * @author crfischer, Namics AG
 * @since 26.09.2017 16:17
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DaoConfigurationPropertiesTest.DaoConfiguration.class})
@ActiveProfiles("DEV")
@DirtiesContext
public class DaoConfigurationPropertiesTest {

	@Inject
	protected ConfigurationDao dao;

	@Value("${tst.key.c}")
	protected String testValueC;

	@Test
	public void testValueA() {

		assertThat(testValueC,is("^[A-Za-z0-9_$#%&!.?]{6,}$"));

		ConfigurationValue valueDev = dao.getConfigurationValue(new ConfigurationEnvironment("DEV"), Environment.DEFAULT, "tst.key.c");
		ConfigurationValue valueDefault = dao.getConfigurationValue(new ConfigurationEnvironment(Environment.DEFAULT),Environment.DEFAULT,"tst.key.c");

		assertEquals(testValueC,valueDev.getValue());
		assertNotEquals(testValueC, valueDefault.getValue());
	}

	@Configuration
	static class DaoConfiguration{

		@Bean
		@DependsOn("databaseConfigurationSources")
		public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer(){
			return new PropertySourcesPlaceholderConfigurer();
		}

		@Bean(name = "databaseConfigurationSources")
		public PropertiesPropertySource[] databaseConfigurationSources(
				@Named("daoConfigurationFactoryBean") OrderedProperties orderedProperties,
				ConfigurableEnvironment environment) throws Exception {

			PropertiesPropertySource[] propertiesPropertySources = orderedProperties.toPropertiesPropertySources();
			for(int i=(propertiesPropertySources.length-1);i>=0;i--){
				environment.getPropertySources().addAfter(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, propertiesPropertySources[i]);
			}
			return propertiesPropertySources;
		}

		@Bean("daoConfigurationFactoryBean")
		public DaoConfigurationPropertiesFactoryBean daoConfigurationPropertiesFactoryBean(org.springframework.core.env.Environment environment){
			return new DaoConfigurationPropertiesFactoryBean(configurationDao(), environment.getActiveProfiles());
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
	}
}
