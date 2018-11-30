package com.namics.oss.spring.support.configuration.starter;

import com.namics.oss.spring.support.configuration.DaoConfigurationPropertiesFactoryBean;
import com.namics.oss.spring.support.configuration.dao.ConfigurationDao;
import com.namics.oss.spring.support.configuration.dao.ConfigurationDaoJdbcImpl;
import com.namics.oss.spring.support.configuration.starter.initializer.DataSourcePropertiesInitializer;
import com.namics.oss.spring.support.configuration.starter.initializer.EncryptableDataSourcePropertiesInitializer;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyFilter;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;
import com.ulisesbocchio.jasyptspringboot.filter.DefaultPropertyFilter;
import com.ulisesbocchio.jasyptspringboot.resolver.DefaultPropertyResolver;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.data.jpa.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.inject.Named;
import javax.sql.DataSource;

import static com.namics.oss.spring.support.configuration.starter.SpringConfigurationSupportProperties.DataSource.NAMICS_CONFIGURATION_DATA_SOURCE_PROPERTIES_PREFIX;
import static com.namics.oss.spring.support.configuration.starter.SpringConfigurationSupportProperties.NAMICS_CONFIGURATION_PROPERTIES_PREFIX;

/**
 * SpringConfigurationSupportAutoConfiguration.
 *
 * @author crfischer, Namics AG
 * @since 02.08.2017 09:24
 */
@Configuration
//fixme add condition @ConditionalOnBean({ DataSource.class })
@ConditionalOnClass({ ConfigurationDaoJdbcImpl.class })
@EnableConfigurationProperties(SpringConfigurationSupportProperties.class)
@AutoConfigureAfter({ DataSourceAutoConfiguration.class })
public class SpringConfigurationSupportAutoConfiguration implements EnvironmentAware {

	private static Logger LOG = LoggerFactory.getLogger(SpringConfigurationSupportAutoConfiguration.class);

	private static final String PROPERTY_TABLE_NAME = NAMICS_CONFIGURATION_PROPERTIES_PREFIX + "." + NAMICS_CONFIGURATION_DATA_SOURCE_PROPERTIES_PREFIX + "." + "tableName";
	private static final String PROPERTY_COLUMN_KEY = NAMICS_CONFIGURATION_PROPERTIES_PREFIX + "." + NAMICS_CONFIGURATION_DATA_SOURCE_PROPERTIES_PREFIX + "." + "keyColumnName";
	private static final String PROPERTY_COLUMN_VALUE = NAMICS_CONFIGURATION_PROPERTIES_PREFIX + "." + NAMICS_CONFIGURATION_DATA_SOURCE_PROPERTIES_PREFIX + "." + "valueColumnName";
	private static final String PROPERTY_COLUMN_ENVIRONMENT = NAMICS_CONFIGURATION_PROPERTIES_PREFIX + "." + NAMICS_CONFIGURATION_DATA_SOURCE_PROPERTIES_PREFIX + "." + "environmentColumnName";
	private static final String PROPERTY_DEFAULT_ENVIRONMENT = NAMICS_CONFIGURATION_PROPERTIES_PREFIX + "." + NAMICS_CONFIGURATION_DATA_SOURCE_PROPERTIES_PREFIX + "." + "defaultEnvironment";

	protected org.springframework.core.env.Environment environment;

	@Override
	public void setEnvironment(final org.springframework.core.env.Environment environment) {
		this.environment = environment;
	}

	@Configuration
	protected static class DataSourcePropertiesInitializerInitializerJpaDependencyConfiguration extends EntityManagerFactoryDependsOnPostProcessor {
		public DataSourcePropertiesInitializerInitializerJpaDependencyConfiguration() {
			super("dataSourcePropertiesInitializer");
		}
	}

	// Jasypt classes are present and String-Encryptor initialized (custom or default by Jasypt-Starter)
	@Configuration
	@ConditionalOnClass(name = {
			"org.jasypt.encryption.StringEncryptor",
			"com.ulisesbocchio.jasyptspringboot.wrapper.EncryptablePropertySourceWrapper",
	})
	@ConditionalOnBean(value = {StringEncryptor.class})
	public static class EncryptableConfiguration {

		@Bean(name = "dataSourcePropertiesInitializer")
		public EncryptableDataSourcePropertiesInitializer encryptableDataSourcePropertiesInitializer(@Named("databaseConfiguration") DaoConfigurationPropertiesFactoryBean databaseConfigFactory, EncryptablePropertyResolver encryptablePropertyResolver, EncryptablePropertyFilter encryptablePropertyFilter) {
			LOG.debug("Initialize support for DataSource properties with support for encryption");
			return new EncryptableDataSourcePropertiesInitializer(databaseConfigFactory, encryptablePropertyResolver,encryptablePropertyFilter);
		}

		@Bean
		@ConditionalOnMissingBean
		public EncryptablePropertyFilter defaultPropertyFilter(){
			return new DefaultPropertyFilter();
		}

		@Bean
		@ConditionalOnMissingBean
		public EncryptablePropertyResolver defaultPropertyResolver(StringEncryptor stringEncryptor){
			return new DefaultPropertyResolver(stringEncryptor);
		}
	}

	// Jasypt classes are present but no String-Encryptor initialized
	@Configuration
	@ConditionalOnClass(name = {
			"org.jasypt.encryption.StringEncryptor",
			"com.ulisesbocchio.jasyptspringboot.wrapper.EncryptablePropertySourceWrapper",
	})
	@ConditionalOnMissingBean(value = {StringEncryptor.class})
	public static class DefaultConfiguration {

		@Bean(name = "dataSourcePropertiesInitializer")
		public DataSourcePropertiesInitializer encryptableDataSourcePropertiesInitializer(@Named("databaseConfiguration") DaoConfigurationPropertiesFactoryBean databaseConfigFactory) {
			LOG.debug("Initialize support for DataSource properties without support for encryption (no Bean of type org.jasypt.encryption.StringEncryptor found)");
			return new DataSourcePropertiesInitializer(databaseConfigFactory);
		}
	}

	// Jasypt classes are not present
	@Bean(name = "dataSourcePropertiesInitializer")
	@ConditionalOnMissingClass({
			"org.jasypt.encryption.StringEncryptor",
			"com.ulisesbocchio.jasyptspringboot.wrapper.EncryptablePropertySourceWrapper",
	})
	@ConditionalOnMissingBean
	public DataSourcePropertiesInitializer dataSourcePropertiesInitializer(@Named("databaseConfiguration") DaoConfigurationPropertiesFactoryBean databaseConfigFactory) {
		LOG.debug("Initialize support for DataSource properties without support for encryption");
		return new DataSourcePropertiesInitializer(databaseConfigFactory);
	}

	@Bean(name = "databaseConfiguration")
	public DaoConfigurationPropertiesFactoryBean databaseConfigFactory(ConfigurationDao configurationDao, Environment environment) throws Exception {
		return new DaoConfigurationPropertiesFactoryBean(configurationDao, environment.getActiveProfiles());
	}

	@Bean
	public ConfigurationDao configurationDao(DataSource dataSource) {
		return ConfigurationDaoJdbcImpl.forDataSource(dataSource)
				.tableName(getTableName())
				.environmentColumn(getEnvironmentColumnName())
				.propertyKeyColumn(getKeyColumnName())
				.valueColumn(getValueColumnName())
				.build();
	}

	protected String getTableName() {
		return environment.getProperty(PROPERTY_TABLE_NAME, SpringConfigurationSupportProperties.DataSource.DEFAULT_TABLE_NAME);
	}

	protected String getKeyColumnName() {
		return environment.getProperty(PROPERTY_COLUMN_KEY, SpringConfigurationSupportProperties.DataSource.DEFAULT_COLUMN_KEY);
	}

	protected String getValueColumnName() {
		return environment.getProperty(PROPERTY_COLUMN_VALUE, SpringConfigurationSupportProperties.DataSource.DEFAULT_COLUMN_VALUE);
	}

	protected String getEnvironmentColumnName() {
		return environment.getProperty(PROPERTY_COLUMN_ENVIRONMENT, SpringConfigurationSupportProperties.DataSource.DEFAULT_COLUMN_ENVIRONMENT);
	}

	protected String getDefaultEnvironment() {
		return environment.getProperty(PROPERTY_DEFAULT_ENVIRONMENT, SpringConfigurationSupportProperties.DataSource.DEFAULT_ENVIRONMENT);
	}
}
