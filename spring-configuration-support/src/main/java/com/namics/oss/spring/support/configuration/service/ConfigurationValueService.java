/*
 * Copyright 2000-2011 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.configuration.service;

import com.namics.oss.spring.support.configuration.ConfigurationException;
import com.namics.oss.spring.support.configuration.Environment;
import com.namics.oss.spring.support.configuration.model.ConfigurationValue;

import java.util.Collection;

/**
 * Service to retrieve and set configuration values.
 *
 * @author Sandro Ruch, namics ag
 * @since namics-configuration 2.2
 */
public interface ConfigurationValueService {
	/**
	 * Returns the configured value for a specific key (not directly from the datastore but from the properties configurer.
	 *
	 * @param key the key of the configuration parameter
	 * @return the value or null if key doesn't exists
	 * @throws ConfigurationException if service call fails
	 */
	String getValueFor(String key) throws ConfigurationException;

	/**
	 * Returns the value directly from the datastore (may not be the same as in getValueFor).
	 *
	 * @param key the key of the configuration parameter
	 * @return the value or null if key doesn't exists
	 * @throws ConfigurationException if service call fails
	 */
	ConfigurationValue getValue(String key) throws ConfigurationException;

	/**
	 * Saves a new value for a specific configuration property.
	 *
	 * @param key   the configuration property
	 * @param value the configuration value
	 * @throws ConfigurationException if service call fails
	 */
	void setValueFor(String key,
	                 String value) throws ConfigurationException;

	/**
	 * Returns the current configuration.
	 *
	 * @return collection of configuration values
	 * @throws ConfigurationException if service call fails
	 */
	Collection<ConfigurationValue> getValues() throws ConfigurationException;

	/**
	 * Returns the current configuration.
	 *
	 * @return current config
	 * @throws ConfigurationException if service call fails
	 */
	Environment getEnv() throws ConfigurationException;

	/**
	 * Is inserting new values supported in current environment.
	 *
	 * @return true if insert of new values is possible.
	 */
	boolean isInsertSupported();

	/**
	 * Is deleting values supported in current environment.
	 *
	 * @return true if deleting values is possible.
	 */
	boolean isDeleteSupported();

	/**
	 * Delete value
	 *
	 * @param key key to delete
	 * @param env environemnt to delete
	 */
	void delete(String key, String env);
}
