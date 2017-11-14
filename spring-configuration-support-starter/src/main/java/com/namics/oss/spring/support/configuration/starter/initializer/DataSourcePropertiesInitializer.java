package com.namics.oss.spring.support.configuration.starter.initializer;

import com.namics.oss.spring.support.configuration.DaoConfigurationPropertiesFactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.env.*;

import java.util.Map;

/**
 * DataSourcePropertiesInitializer.
 *
 * @author lboesch, Namics AG
 * @since 06.11.17 08:25
 */
public class DataSourcePropertiesInitializer implements Ordered, InitializingBean, EnvironmentAware {

	protected Environment environment;


	protected DaoConfigurationPropertiesFactoryBean databaseConfigFactory;

	public DataSourcePropertiesInitializer(DaoConfigurationPropertiesFactoryBean databaseConfigFactory) {
		this.databaseConfigFactory = databaseConfigFactory;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		ConfigurableEnvironment environment = (ConfigurableEnvironment) this.environment;
		PropertiesPropertySource[] propertiesPropertySources = databaseConfigFactory.getObject().toPropertiesPropertySources();
		for (int i = (propertiesPropertySources.length - 1); i >= 0; i--) {
			environment.getPropertySources()
			           .addAfter(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, getPropertySource(propertiesPropertySources[i]));
		}
	}

	/**
	 * get's property source.
	 *
	 * @param propertiesPropertySource .
	 * @return property source to add to env.
	 */
	protected PropertySource<Map<String, Object>> getPropertySource(PropertySource<Map<String, Object>> propertiesPropertySource) {
		return propertiesPropertySource;
	}

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
}
