/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.configuration;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.DatabaseConfiguration;
import org.springframework.beans.factory.FactoryBean;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

/**
 * <code>DatabaseConfigurationPropertiesFactoryBean</code> creates an <code>OrderedProperties</code> instance which holds multiple <code>properties</code> along with their identifier/key.
 * Every properties instance held by <code>OrderedProperties</code> is associated with an environment (e.g. DEFAULT(*), DEV, QUAL, PROD, ...).
 * The field <code>environments</code>, which can be configured via the constructor of {@link DatabaseConfigurationPropertiesFactoryBean#DatabaseConfigurationPropertiesFactoryBean(DataSource, String, String, String, String, String[])} or via {@link DatabaseConfigurationPropertiesFactoryBean#setEnvironments(String[])}, allows to specify the environments for which the factory is going to fetch the properties.
 * An environment (e.g. DEV) instructs this FactoryBean to fetch properties for environment DEV and the default properties.
 *
 * The resulting {@link OrderedProperties} instance holds multiple {@link Properties} instances. The default properties instance is always the last item in within the {@link OrderedProperties}.
 *
 * @author Sandro Ruch, namics ag
 * @since namics-configuration 1.0
 */
public class DatabaseConfigurationPropertiesFactoryBean implements FactoryBean<OrderedProperties> {

	protected DataSource dataSource;
	protected String tableName = "nmx_configuration";
	protected String environmentColumn = "environment";
	protected String propertyKeyColumn = "property_key";
	protected String valueColumn = "property_value";
	protected String defaultEnvironment = Environment.DEFAULT;
	protected Set<String> environments;

	protected final static String PROPERTY_SOURCE_PREFIX = "dataSource";
	protected final static String PROPERTY_SOURCE_DEFAULT = "DEFAULT";

	protected OrderedProperties properties;


	/**
	 * Create Factory with datasource and defaults, use fluent setters to populate.
	 *
	 * @param dataSource datasource
	 */
	public DatabaseConfigurationPropertiesFactoryBean(DataSource dataSource) {

		this.dataSource = dataSource;
		this.environments = Collections.emptySet();
	}

	/**
	 * Only constructor.
	 *
	 * @param datasource  used data source
	 * @param table       the name of the table with the configuraton
	 * @param envColumn   the name of the column with the environment information
	 * @param keyColumn   the name of the column with the key information
	 * @param valueColumn the name of the comlum with the value information
	 * @param environments the environments to fetch properties for
	 */
	public DatabaseConfigurationPropertiesFactoryBean(DataSource datasource, final String table, final String envColumn, final String keyColumn,
	                                                  final String valueColumn, String[] environments) {

		this.dataSource = datasource;
		this.tableName = table;
		this.environmentColumn = envColumn;
		this.propertyKeyColumn = keyColumn;
		this.valueColumn = valueColumn;
		this.defaultEnvironment = Environment.DEFAULT;
		this.environments = environments == null ? Collections.emptySet() : stream(environments).collect(Collectors.toSet());
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

		// fetch properties for each specific environment
		environments.forEach(currentEnvironment -> {
			DatabaseConfiguration dbConfig = createDatabaseConfiguration(this.dataSource,this.tableName,this.environmentColumn,this.propertyKeyColumn,this.valueColumn,currentEnvironment);
			propertiesByEnvironment.put(PROPERTY_SOURCE_PREFIX + "-" + currentEnvironment, createPropertiesForConfiguration(dbConfig));
		});

		// fetch default properties
		DatabaseConfiguration dbConfigDefault = createDatabaseConfiguration(this.dataSource,this.tableName,this.environmentColumn,this.propertyKeyColumn,this.valueColumn,this.defaultEnvironment);
		propertiesByEnvironment.put(PROPERTY_SOURCE_PREFIX + "-" + PROPERTY_SOURCE_DEFAULT, createPropertiesForConfiguration(dbConfigDefault));

		this.properties = new OrderedProperties(propertiesByEnvironment);
	}

	/**
	 * Creates a properties instance with the key/value pairs specified within the passed configuration object.
	 *
	 * @param configuration the configuration
	 * @return the populated properties instance
	 */
	protected Properties createPropertiesForConfiguration(Configuration configuration){

		Properties properties = new Properties();

		final Iterator keys = configuration.getKeys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			List list = configuration.getList(key);
			properties.setProperty(key, (String) list.get(0));
		}

		return properties;
	}

	/**
	 * Creates a DatabaseConfiguration instance with the specified parameters.
	 *
	 * @param dataSource the data-source
	 * @param tableName the table name
	 * @param environmentColumn the environment column
	 * @param keyColumn the key column
	 * @param valueColumn the value column
	 * @param environment the environment
	 * @return the DatabaseConfiguration instance
	 */
	protected DatabaseConfiguration createDatabaseConfiguration(DataSource dataSource, String tableName, String environmentColumn, String keyColumn, String valueColumn, String environment){
		DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration(dataSource,tableName,environmentColumn,keyColumn,valueColumn,environment);
		databaseConfiguration.setDelimiterParsingDisabled(true);
		return databaseConfiguration;
	}

	@Override
	public Class<? extends OrderedProperties> getObjectType() {
		return OrderedProperties.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * set datasource.
	 *
	 * @param dataSource source to set
	 * @return this for fluent config
	 */
	public DatabaseConfigurationPropertiesFactoryBean dataSource(DataSource dataSource) {
		setDataSource(dataSource);
		return this;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * set name of config table.
	 *
	 * @param tableName to set
	 * @return this for fluent config
	 */
	public DatabaseConfigurationPropertiesFactoryBean tableName(String tableName) {
		setTableName(tableName);
		return this;
	}

	public String getEnvironmentColumn() {
		return environmentColumn;
	}

	public void setEnvironmentColumn(String environmentColumn) {
		this.environmentColumn = environmentColumn;
	}

	/**
	 * set name of column for environment.
	 *
	 * @param environmentColumn to set
	 * @return this for fluent config
	 */
	public DatabaseConfigurationPropertiesFactoryBean environmentColumn(String environmentColumn) {
		setEnvironmentColumn(environmentColumn);
		return this;
	}

	public String getPropertyKeyColumn() {
		return propertyKeyColumn;
	}

	public void setPropertyKeyColumn(String propertyKeyColumn) {
		this.propertyKeyColumn = propertyKeyColumn;
	}

	/**
	 * set name of column for properties name.
	 *
	 * @param propertyKeyColumn to set
	 * @return this for fluent config
	 */
	public DatabaseConfigurationPropertiesFactoryBean propertyKeyColumn(String propertyKeyColumn) {
		setPropertyKeyColumn(propertyKeyColumn);
		return this;
	}

	public String getValueColumn() {
		return valueColumn;
	}

	public void setValueColumn(String valueColumn) {
		this.valueColumn = valueColumn;
	}

	/**
	 * set name of column for properties value.
	 *
	 * @param valueColumn to set
	 * @return this for fluent config
	 */
	public DatabaseConfigurationPropertiesFactoryBean valueColumn(String valueColumn) {
		setValueColumn(valueColumn);
		return this;
	}

	public String getDefaultEnvironment() {
		return defaultEnvironment;
	}

	public void setDefaultEnvironment(String defaultEnvironment) {
		this.defaultEnvironment = defaultEnvironment;
	}

	/**
	 * set designator for default environment (*).
	 *
	 * @param defaultEnvironment to set
	 * @return this for fluent config
	 */
	public DatabaseConfigurationPropertiesFactoryBean defaultEnvironment(String defaultEnvironment) {
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
	public DatabaseConfigurationPropertiesFactoryBean environments(String[] environments) {
		setEnvironments(environments);
		return this;
	}
}
