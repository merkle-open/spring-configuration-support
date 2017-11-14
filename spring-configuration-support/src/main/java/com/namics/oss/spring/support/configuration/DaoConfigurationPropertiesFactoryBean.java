/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.configuration;

import com.namics.oss.spring.support.configuration.dao.ConfigurationDao;
import com.namics.oss.spring.support.configuration.model.ConfigurationValue;
import org.springframework.beans.factory.FactoryBean;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

/**
 * <code>DatabaseConfigurationPropertiesFactoryBean</code> creates an <code>OrderedProperties</code> instance which holds multiple <code>properties</code> along with their identifier/key.
 * The properties are fetched via the configured {@link ConfigurationDao} instance.
 * Every properties instance held by <code>OrderedProperties</code> is associated with an environment (e.g. DEFAULT(*), DEV, QUAL, PROD, ...).
 * The field <code>environments</code>, which can be configured via the constructor of {@link DaoConfigurationPropertiesFactoryBean#DaoConfigurationPropertiesFactoryBean(ConfigurationDao, String[])} or via {@link DaoConfigurationPropertiesFactoryBean#setEnvironments(String[])}, allows to specify the environments for which the factory is going to fetch the properties.
 * An environment (e.g. DEV) instructs this FactoryBean to fetch properties for environment DEV and the default properties.
 *
 * The resulting {@link OrderedProperties} instance holds multiple {@link Properties} instances. The default properties instance is always the last item within the {@link OrderedProperties}.
 *
 * @author aschaefer, Namics AG
 * @since namics-configuration 3.0.5
 */
public class DaoConfigurationPropertiesFactoryBean implements FactoryBean<OrderedProperties> {

	protected final ConfigurationDao dao;

	protected OrderedProperties properties;
	protected Set<String> environments;
	protected String defaultEnvironment;

	protected final static String PROPERTY_SOURCE_PREFIX = "dataSource";
	protected final static String PROPERTY_SOURCE_DEFAULT = "DEFAULT";

	public DaoConfigurationPropertiesFactoryBean(ConfigurationDao dao, String[] environments, String defaultEnvironment) {
		this.dao = dao;
		this.defaultEnvironment = defaultEnvironment == null ? Environment.DEFAULT : defaultEnvironment;
		this.environments = environments == null ? Collections.emptySet() : stream(environments).collect(Collectors.toSet());
	}

	public DaoConfigurationPropertiesFactoryBean(ConfigurationDao dao, String[] environments) {
		this(dao, environments, null);
	}

	public DaoConfigurationPropertiesFactoryBean(ConfigurationDao dao) {
		this(dao,null,null);
	}

	@Override
	public OrderedProperties getObject() throws Exception {
		if (properties == null) {
			init();
		}
		return properties;
	}

	protected void init() {

		LinkedHashMap<String,Properties> propertiesByEnvironment = new LinkedHashMap<>();

		// Fetch and create configured properties for specific environments
		environments.forEach(currentEnvironment -> {
			Properties currentEnvironmentProperties = createPropertiesForEnvironment(currentEnvironment,this.defaultEnvironment);
			propertiesByEnvironment.put(PROPERTY_SOURCE_PREFIX + "-" + currentEnvironment, currentEnvironmentProperties);
		});

		// Fetch and create configured default properties
		Properties defaultProperties = createPropertiesForEnvironment(this.defaultEnvironment,this.defaultEnvironment);
		propertiesByEnvironment.put(PROPERTY_SOURCE_PREFIX + "-" + PROPERTY_SOURCE_DEFAULT, defaultProperties);

		this.properties = new OrderedProperties(propertiesByEnvironment);
	}

	/**
	 * Fetches the properties for the specified environment via the configured {@link ConfigurationDao} instance and returns a {@link Properties} instance which contains all the fetched properties.
	 *
	 * @param environment the environment to fetch the properties for
	 * @param defaultEnvironment the default environment
	 *
	 * @return a {@link Properties} instance containing all the configured properties for the specified environment
	 */
	protected Properties createPropertiesForEnvironment(String environment, String defaultEnvironment){
		Properties properties = new Properties();

		Environment configurationEnvironment = new ConfigurationEnvironment(environment);
		Collection<ConfigurationValue> values = dao.getConfiguration(configurationEnvironment, defaultEnvironment);

		values.forEach(value -> {
			if (environment.equals(value.getEnv().getValue())) {
				properties.setProperty(value.getKey(), value.getValue());
			}
		});

		return properties;
	}


	@Override
	public Class<? extends OrderedProperties> getObjectType() {
		return OrderedProperties.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}


	public void setDefaultEnvironment(String defaultEnvironment) {
		this.defaultEnvironment = defaultEnvironment;
	}

	public DaoConfigurationPropertiesFactoryBean defaultEnvironment(String defaultEnvironment) {
		setDefaultEnvironment(defaultEnvironment);
		return this;
	}

	public String[] getEnvironments() {
		return environments.toArray(new String[environments.size()]);
	}

	public void setEnvironments(String[] environments) {
		this.environments = stream(environments).collect(Collectors.toSet());
	}

	/**
	 * set the environments to fetch properties for.
	 *
	 * @param environments to set
	 * @return this for fluent config
	 */
	public DaoConfigurationPropertiesFactoryBean environments(String[] environments) {
		setEnvironments(environments);
		return this;
	}
}
