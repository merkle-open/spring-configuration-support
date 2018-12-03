package com.namics.oss.spring.support.configuration.starter;

import com.namics.oss.spring.support.configuration.DaoConfigurationPropertiesFactoryBean;
import com.namics.oss.spring.support.configuration.dao.ConfigurationDao;
import com.namics.oss.spring.support.configuration.dao.ConfigurationDaoJdbcImpl;
import com.namics.oss.spring.support.configuration.starter.initializer.DataSourcePropertiesInitializer;
import com.namics.oss.spring.support.configuration.starter.initializer.EncryptableDataSourcePropertiesInitializer;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyFilter;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;
import com.ulisesbocchio.jasyptspringboot.JasyptSpringBootAutoConfiguration;
import com.ulisesbocchio.jasyptspringboot.filter.DefaultPropertyFilter;
import com.ulisesbocchio.jasyptspringboot.resolver.DefaultPropertyResolver;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
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
import java.util.Optional;

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
@ConditionalOnClass({ConfigurationDaoJdbcImpl.class})
@EnableConfigurationProperties(SpringConfigurationSupportProperties.class)
@AutoConfigureAfter(value = {DataSourceAutoConfiguration.class, JasyptSpringBootAutoConfiguration.class})
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

    // DataSource properties with encryption support if:
    // - Jasypt classes are present
    // - At least a {@link org.jasypt.encryption.StringEncryptor} is provided
    @Configuration
    @ConditionalOnClass(name = {
            "org.jasypt.encryption.StringEncryptor",
            "com.ulisesbocchio.jasyptspringboot.wrapper.EncryptablePropertySourceWrapper"

    })
    public static class EncryptableConfiguration {

        @Bean(name = "dataSourcePropertiesInitializer")
        public DataSourcePropertiesInitializer encryptableDataSourcePropertiesInitializer(@Named("databaseConfiguration") DaoConfigurationPropertiesFactoryBean databaseConfigFactory,
                                                                                          Optional<StringEncryptor> stringEncryptor,
                                                                                          Optional<EncryptablePropertyResolver> encryptablePropertyResolver,
                                                                                          Optional<EncryptablePropertyFilter> encryptablePropertyFilter) {

            if (stringEncryptor.isPresent()) {

                EncryptablePropertyResolver propertyResolver = encryptablePropertyResolver.isPresent() ? encryptablePropertyResolver.get() : encryptablePropertyResolver(stringEncryptor.get());
                EncryptablePropertyFilter propertyFilter = encryptablePropertyFilter.isPresent() ? encryptablePropertyFilter.get() : encryptablePropertyFilter();

                LOG.debug("Initialize support for encrypted DataSource properties");
                return new EncryptableDataSourcePropertiesInitializer(databaseConfigFactory, propertyResolver, propertyFilter);
            }

            LOG.debug("Initialize default support for DataSource properties");
            return new DataSourcePropertiesInitializer(databaseConfigFactory);
        }

        private EncryptablePropertyResolver encryptablePropertyResolver(StringEncryptor stringEncryptor) {
            return new DefaultPropertyResolver(stringEncryptor);
        }

        private EncryptablePropertyFilter encryptablePropertyFilter() {
            return new DefaultPropertyFilter();
        }
    }

    // Jasypt classes are not present
    @Bean(name = "dataSourcePropertiesInitializer")
    @ConditionalOnMissingClass({
            "com.ulisesbocchio.jasyptspringboot.wrapper.EncryptablePropertySourceWrapper"

    })
    @ConditionalOnMissingBean
    public DataSourcePropertiesInitializer dataSourcePropertiesInitializer(@Named("databaseConfiguration") DaoConfigurationPropertiesFactoryBean databaseConfigFactory) {
        LOG.debug("Initialize default support for DataSource properties");
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
