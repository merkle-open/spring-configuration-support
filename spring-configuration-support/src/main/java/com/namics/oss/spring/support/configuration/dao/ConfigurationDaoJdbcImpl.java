/*
 * Copyright 2000-2011 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.configuration.dao;

import com.namics.oss.spring.support.configuration.ConfigurationEnvironment;
import com.namics.oss.spring.support.configuration.Environment;
import com.namics.oss.spring.support.configuration.model.ConfigurationValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;

/**
 * Jdbc implementation of the <code>ConfigurationDao</code> interface.
 *
 * @author Sandro Ruch, namics ag
 * @since namics-configuration 2.2
 */
public class ConfigurationDaoJdbcImpl implements ConfigurationDao {

	private static final Logger LOG = LoggerFactory.getLogger(ConfigurationDaoJdbcImpl.class);

	private final String insertQuery;
	private final String updateQuery;
	private final String listQuery;
	private final String getQuery;
	private final String deleteQuery;

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public ConfigurationDaoJdbcImpl(DataSource dataSource, String tableName, String propertyKeyColumn, String valueColumn, String environmentColumn) {
		notNull(dataSource, "dataSource must be set");
		hasText(tableName, "tableName must be set");
		hasText(propertyKeyColumn, "propertyKeyColumn must be set");
		hasText(valueColumn, "valueColumn must be set");
		hasText(environmentColumn, "environmentColumn must be set");

		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		this.updateQuery = "UPDATE " + tableName
		                   + " SET " + valueColumn + " = :config_value "
		                   + " WHERE " + propertyKeyColumn + " = :config_key "
		                   + " AND " + environmentColumn + " = :config_env";

		this.insertQuery = "INSERT INTO " + tableName
		                   + " ( "
		                   + valueColumn + ", "
		                   + propertyKeyColumn + ", "
		                   + environmentColumn
		                   + " ) VALUES ( :config_value,  :config_key,  :config_env)";

		this.listQuery = "SELECT "
		                 + environmentColumn + ", "
		                 + propertyKeyColumn + ", "
		                 + valueColumn
		                 + " FROM " + tableName
		                 + " WHERE " + environmentColumn + " = :config_env"
		                 + " OR " + environmentColumn + " = :default_env_key";

		this.getQuery = "SELECT "
		                + environmentColumn + ", "
		                + propertyKeyColumn + ", "
		                + valueColumn
		                + " FROM " + tableName
		                + " WHERE " + propertyKeyColumn + " = :config_key"
		                + " AND " + environmentColumn + " = :config_env";

		this.deleteQuery = "DELETE FROM " + tableName
		                   + " WHERE " + propertyKeyColumn + " = :config_key "
		                   + " AND " + environmentColumn + " = :config_env";

	}

	@Override
	@Transactional(readOnly = false)
	public void updateConfigurationValue(Environment env,
	                                     String defaultEnvKey,
	                                     String key,
	                                     String value) throws DataAccessException {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("config_key", key);
		paramMap.put("config_value", value);
		try {
			// check, whether there is a concrete config value
			paramMap.put("config_env", env.getValue());
			namedParameterJdbcTemplate.queryForObject(this.getQuery, paramMap, new ConfigurationValueRowMapper());
		} catch (EmptyResultDataAccessException e) {
			// no... set the default to update
			LOG.trace("no concrete config value, set the default to update", e);
			paramMap.put("config_env", defaultEnvKey);
		}

		namedParameterJdbcTemplate.update(this.updateQuery, paramMap);
	}

	@Override
	public boolean isInsertSupported() {
		return true;
	}

	@Override
	@Transactional(readOnly = false)
	public void insertConfigurationValue(String env, String key, String value) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("config_key", key);
		paramMap.put("config_value", value);
		paramMap.put("config_env", env);
		namedParameterJdbcTemplate.update(this.insertQuery, paramMap);
	}

	@Override
	public boolean isDeleteSupported() {
		return true;
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteConfigurationValue(String env, String key) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("config_key", key);
		paramMap.put("config_env", env);
		namedParameterJdbcTemplate.update(this.deleteQuery, paramMap);
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<ConfigurationValue> getConfiguration(Environment env,
	                                                       String defaultEnvKey) throws DataAccessException {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("config_env", env.getValue());
		paramMap.put("default_env_key", defaultEnvKey);
		return namedParameterJdbcTemplate.query(this.listQuery, paramMap, new ConfigurationValueRowMapper());
	}

	@Override
	@Transactional(readOnly = true)
	public ConfigurationValue getConfigurationValue(Environment env,
	                                                String defaultEnvKey,
	                                                String key) throws DataAccessException {
		Map<String, Object> paramMap = new HashMap<>();
		ConfigurationValue configValue = null;
		try {
			// first try the most specific one
			paramMap.put("config_env", env.getValue());
			paramMap.put("config_key", key);
			configValue = namedParameterJdbcTemplate.queryForObject(this.getQuery, paramMap, new ConfigurationValueRowMapper());

		} catch (EmptyResultDataAccessException e) {
			// not found
			LOG.trace("no specific value found", e);
		}

		if (configValue == null) {
			try {
				// ok... then try the defaultEnvKey
				paramMap.put("config_env", defaultEnvKey);
				configValue = namedParameterJdbcTemplate.queryForObject(this.getQuery, paramMap, new ConfigurationValueRowMapper());
			} catch (EmptyResultDataAccessException e) {
				// not found
				LOG.trace("no default value found", e);
			}
		}
		// check whether the env is default...
		if (configValue != null) {
			if (configValue.getEnv().getValue().equals(defaultEnvKey)) {
				// in this case overwrite it with the requested
				configValue.setEnv(env);
			}
		}
		return configValue;
	}


	private static class ConfigurationValueRowMapper implements RowMapper<ConfigurationValue> {
		@Override
		public ConfigurationValue mapRow(ResultSet rs,
		                                 int rowNum) throws SQLException {
			return new ConfigurationValue(new ConfigurationEnvironment(rs.getString(1)), rs.getString(2), rs.getString(3));
		}
	}

	public static class Builder {
		private DataSource dataSource;
		private String tableName;
		private String propertyKeyColumn = "configuration_key";
		private String valueColumn = "configuration_value";
		private String environmentColumn = "configuration_env";

		public Builder dataSource(DataSource dataSource) {
			this.dataSource = dataSource;
			return this;
		}

		public Builder tableName(String tableName) {
			this.tableName = tableName;
			return this;
		}

		public Builder propertyKeyColumn(String propertyKeyColumn) {
			this.propertyKeyColumn = propertyKeyColumn;
			return this;
		}


		public Builder valueColumn(String valueColumn) {
			this.valueColumn = valueColumn;
			return this;
		}

		public Builder environmentColumn(String environmentColumn) {
			this.environmentColumn = environmentColumn;
			return this;
		}

		public ConfigurationDaoJdbcImpl build() {
			return new ConfigurationDaoJdbcImpl(dataSource, tableName, propertyKeyColumn, valueColumn, environmentColumn);
		}
	}

	public static Builder forDataSource(DataSource dataSource) {
		return new Builder().dataSource(dataSource);
	}

}
