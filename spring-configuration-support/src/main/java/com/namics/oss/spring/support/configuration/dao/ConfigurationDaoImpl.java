/*
 * Copyright 2000-2011 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.configuration.dao;

import com.namics.oss.spring.support.configuration.ConfigurationEnvironment;
import com.namics.oss.spring.support.configuration.Environment;
import com.namics.oss.spring.support.configuration.model.ConfigurationValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Jdbc implementation of the <code>ConfigurationDao</code> interface.
 *
 * @author Sandro Ruch, namics ag
 * @since namics-configuration 2.2
 * @deprecated use ConfigurationDaoJdbcImpl instead!
 */
@Deprecated
public final class ConfigurationDaoImpl extends JdbcDaoSupport implements ConfigurationDao {

	private static final Logger LOG = LoggerFactory.getLogger(ConfigurationDaoImpl.class);

	private String updateQuery;
	private String listQuery;
	private String getQuery;

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private String tableName;
	private String tableKeyColumn;
	private String tableValueColumn;
	private String tableEnvColumn;

	@Override
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
	public Collection<ConfigurationValue> getConfiguration(Environment env,
	                                                       String defaultEnvKey) throws DataAccessException {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("config_env", env.getValue());
		paramMap.put("default_env_key", defaultEnvKey);
		return namedParameterJdbcTemplate.query(this.listQuery, paramMap, new ConfigurationValueRowMapper());
	}

	@Override
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

	/**
	 * Init dao.
	 */
	protected void initDao() throws Exception {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());

		this.updateQuery = "UPDATE " + this.tableName
		                   + " SET " + this.tableValueColumn + " = :config_value "
		                   + " WHERE " + this.tableKeyColumn + " = :config_key "
		                   + " AND " + this.tableEnvColumn + " = :config_env";

		this.listQuery = "SELECT "
		                 + this.tableEnvColumn + ", "
		                 + this.tableKeyColumn + ", "
		                 + this.tableValueColumn
		                 + " FROM " + this.tableName
		                 + " WHERE " + this.tableEnvColumn + " = :config_env"
		                 + " OR " + this.tableEnvColumn + " = :default_env_key";

		this.getQuery = "SELECT "
		                + this.tableEnvColumn + ", "
		                + this.tableKeyColumn + ", "
		                + this.tableValueColumn
		                + " FROM " + this.tableName
		                + " WHERE " + this.tableKeyColumn + " = :config_key"
		                + " AND " + this.tableEnvColumn + " = :config_env";

	}

	private static class ConfigurationValueRowMapper implements RowMapper<ConfigurationValue> {

		@Override
		public ConfigurationValue mapRow(ResultSet rs,
		                                 int rowNum) throws SQLException {
			return new ConfigurationValue(new ConfigurationEnvironment(rs.getString(1)), rs.getString(2), rs.getString(3));
		}
	}

	// CHECKSTYLE:OFF
	@Required
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Required
	public void setTableKeyColumn(String tableKeyColumn) {
		this.tableKeyColumn = tableKeyColumn;
	}

	@Required
	public void setTableValueColumn(String tableValueColumn) {
		this.tableValueColumn = tableValueColumn;
	}

	@Required
	public void setTableEnvColumn(String tableEnvColumn) {
		this.tableEnvColumn = tableEnvColumn;
	}
	// CHECKSTYLE:ON

}
