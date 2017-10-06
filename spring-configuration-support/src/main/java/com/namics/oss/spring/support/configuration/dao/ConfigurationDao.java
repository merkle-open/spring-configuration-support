/*
 * Copyright 2000-2011 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.configuration.dao;

import com.namics.oss.spring.support.configuration.Environment;
import com.namics.oss.spring.support.configuration.model.ConfigurationValue;
import org.springframework.dao.DataAccessException;

import java.util.Collection;

import static org.springframework.util.Assert.notNull;

/**
 * Interface to the configuration table.
 *
 * @author Sandro Ruch, namics ag
 * @since namics-configuration 2.2
 */
public interface ConfigurationDao {

	/**
	 * Insert is a new method contract, so not all implementations support insert.
	 * Implementations that support insert should implement isInsertSupported and return true.
	 *
	 * @return true if this dao supports insert
	 */
	default boolean isInsertSupported() {
		return false;
	}

	/**
	 * Insert a new configuration value to data store.
	 *
	 * @param env   environment to store property for
	 * @param key   key of property to be stored
	 * @param value value to be stored
	 */
	default void insertConfigurationValue(String env,
	                                      String key,
	                                      String value) {
		throw new UnsupportedOperationException("ConfigurationDao#insertConfigurationValue not avalilable in this implementation");
	}

	/**
	 * Insert a new configuration value to data store.
	 *
	 * @param env   environment to store property for
	 * @param key   key of property to be stored
	 * @param value value to be stored
	 */
	default void insertConfigurationValue(Environment env,
	                                      String key,
	                                      String value) {
		notNull(env);
		insertConfigurationValue(env.getValue(), key, value);
	}

	/**
	 * Updates the value for a specific configuration property.
	 *
	 * @param env           the desired environment
	 * @param defaultEnvKey the default environment key value (eg. *)
	 * @param key           configuration property
	 * @param value         the new value
	 * @throws DataAccessException if Dao call fails
	 */
	void updateConfigurationValue(Environment env,
	                              String defaultEnvKey,
	                              String key,
	                              String value) throws DataAccessException;


	/**
	 * Update or insert a config value
	 *
	 * @param env           the desired environment
	 * @param defaultEnvKey the default environment key value (eg. *)
	 * @param key           configuration property
	 * @param value         the new value
	 * @throws DataAccessException if Dao call fails
	 */
	default void save(Environment env,
	                  String defaultEnvKey,
	                  String key,
	                  String value) throws DataAccessException {
		if (isInsertSupported() && getConfigurationValue(env, defaultEnvKey, key) == null) {
			insertConfigurationValue(env, key, value);
		} else {
			updateConfigurationValue(env, defaultEnvKey, key, value);
		}
	}

	/**
	 * Returns all configuration values of a specific environment.
	 *
	 * @param env           then desired environment
	 * @param defaultEnvKey the default environment key value (eg. *)
	 * @return a collection of configuration values
	 * @throws DataAccessException if call fails
	 */
	Collection<ConfigurationValue> getConfiguration(Environment env,
	                                                String defaultEnvKey) throws DataAccessException;

	/**
	 * Returns a specific configuration value of a specific environment.
	 *
	 * @param env           then desired environment
	 * @param defaultEnvKey the default environment key value (eg. *)
	 * @param key           the key property to get the ConfigurationValue for
	 * @return a configuration values or null
	 * @throws DataAccessException if call fails
	 */
	ConfigurationValue getConfigurationValue(Environment env,
	                                         String defaultEnvKey,
	                                         String key) throws DataAccessException;

	/**
	 * Delete is a new method contract, so not all implementations support insert.
	 * Implementations that support delete should implement isDeleteSupported and return true.
	 *
	 * @return true if this dao supports delete
	 */
	default boolean isDeleteSupported(){
		return false;
	}


	/**
	 * Insert a new configuration value to data store.
	 *
	 * @param env   environment to store property for
	 * @param key   key of property to be stored
	 */
	default void deleteConfigurationValue(String env,
	                                      String key) {
		throw new UnsupportedOperationException("ConfigurationDao#deleteConfigurationValue not avalilable in this implementation");
	}
}
