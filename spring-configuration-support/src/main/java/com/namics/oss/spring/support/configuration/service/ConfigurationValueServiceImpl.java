/*
 * Copyright 2000-2011 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.configuration.service;

import com.namics.oss.spring.support.configuration.ConfigurationException;
import com.namics.oss.spring.support.configuration.Environment;
import com.namics.oss.spring.support.configuration.dao.ConfigurationDao;
import com.namics.oss.spring.support.configuration.model.ConfigurationValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service to manipulate the configuration table.
 *
 * @author Sandro Ruch, namics ag
 * @since namics-configuration 2.2
 */
public class ConfigurationValueServiceImpl implements ConfigurationValueService {
	private static final Logger LOG = LoggerFactory.getLogger(ConfigurationValueServiceImpl.class);


	protected final ConfigurationDao configurationDao;

	protected final Environment environment;
	protected final String defaultEnvironment;

	public ConfigurationValueServiceImpl(ConfigurationDao configurationDao,
	                                     Environment environment,
	                                     String defaultEnvironment
	) {
		this.configurationDao = configurationDao;
		this.environment = environment;
		this.defaultEnvironment = defaultEnvironment;
	}

	public ConfigurationValueServiceImpl(ConfigurationDao configurationDao,
	                                     Environment environment
	) {
		this.configurationDao = configurationDao;
		this.environment = environment;
		this.defaultEnvironment = Environment.DEFAULT;
	}

	@Override
	@Transactional(readOnly = true)
	public String getValueFor(String key) {
		ConfigurationValue configurationValue = configurationDao.getConfigurationValue(environment, defaultEnvironment, key);
		String configValue = configurationValue.getValue();
		LOG.info("Getting value for configuration property [" + key + "], value is [" + configValue + "]");
		return configValue;
	}

	@Override
	@Transactional(readOnly = false)
	public void setValueFor(String key,
	                        String value) {
		LOG.info("Setting value for configuration property [" + key + "], value is [" + value + "]");
		configurationDao.save(environment, defaultEnvironment, key, value);
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<ConfigurationValue> getValues() {
		Collection<ConfigurationValue> values = this.configurationDao.getConfiguration(environment, defaultEnvironment);
		Map<String, ConfigurationValue> specificValues = new HashMap<>();
		Map<String, ConfigurationValue> genericValues = new HashMap<>();

		// loop over all values and separate specific defined and general one (double)
		values.forEach(value -> {
			if (value.getEnv().getValue().equalsIgnoreCase(environment.getValue())) {
				specificValues.put(value.getKey(), value);
			} else {
				genericValues.put(value.getKey(), value);
			}
		});

		List<ConfigurationValue> mergedValues = new ArrayList<>();

		specificValues.forEach((key,value) -> {
			mergedValues.add(value);
			// remove this one if generic is present
			genericValues.remove(key);
		});

		genericValues.forEach((key,value) -> mergedValues.add(value));

		return mergedValues;
	}

	@Override
	public Environment getEnv() {
		return this.environment;
	}

	@Override
	public boolean isInsertSupported() {
		return configurationDao.isInsertSupported();
	}

	@Override
	public boolean isDeleteSupported() {
		return this.configurationDao.isDeleteSupported();
	}

	@Override
	@Transactional(readOnly = false)
	public void delete(String key, String env) {
		configurationDao.deleteConfigurationValue(env, key);
	}

	@Override
	@Transactional(readOnly = true)
	public ConfigurationValue getValue(String key) throws ConfigurationException {
		return this.configurationDao.getConfigurationValue(environment, defaultEnvironment, key);
	}

}
