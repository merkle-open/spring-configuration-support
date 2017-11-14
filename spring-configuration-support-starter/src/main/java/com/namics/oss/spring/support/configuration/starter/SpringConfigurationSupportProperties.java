package com.namics.oss.spring.support.configuration.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SpringConfigurationSupportProperties.
 *
 * @author crfischer, Namics AG
 * @since 02.08.2017 11:00
 */
@ConfigurationProperties(prefix = SpringConfigurationSupportProperties.NAMICS_CONFIGURATION_PROPERTIES_PREFIX)
public class SpringConfigurationSupportProperties {

	public static final String NAMICS_CONFIGURATION_PROPERTIES_PREFIX = "com.namics.oss.spring.support.configuration";

	private DataSource dataSource = new DataSource();
	private Web web = new Web();
	private String defaultProfile = "DEFAULT";

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Web getWeb() {
		return web;
	}

	public void setWeb(Web web) {
		this.web = web;
	}

	public String getDefaultProfile() {
		return defaultProfile;
	}

	public void setDefaultProfile(String defaultProfile) {
		this.defaultProfile = defaultProfile;
	}


	public static class Web {

		public static final String NAMICS_CONFIGURATION_DATA_SOURCE_PROPERTIES_PREFIX = "dataSource";

		public static final String DEFAULT_SERVLET_MAPPING = "/configuration/*";
		public static final String DEFAULT_SERVLET_NAME = "configurationServlet";

		/**
		 * the servlet-name
		 */
		private String servletName = DEFAULT_SERVLET_NAME;

		/**
		 * the  mapping
		 */
		private String servletMapping = DEFAULT_SERVLET_MAPPING;

		public String getServletName() {
			return servletName;
		}

		public void setServletName(String servletName) {
			this.servletName = servletName;
		}

		public String getServletMapping() {
			return servletMapping;
		}

		public void setServletMapping(String servletMapping) {
			this.servletMapping = servletMapping;
		}
	}


	public static class DataSource {

		public static final String NAMICS_CONFIGURATION_DATA_SOURCE_PROPERTIES_PREFIX = "dataSource";

		public static final String DEFAULT_TABLE_NAME = "nmx_configuration";
		public static final String DEFAULT_COLUMN_KEY = "configuration_key";
		public static final String DEFAULT_COLUMN_VALUE = "configuration_value";
		public static final String DEFAULT_COLUMN_ENVIRONMENT = "configuration_env";
		public static final String DEFAULT_ENVIRONMENT = "*";

		/**
		 * default environment (e.g. *)
		 */
		private String defaultEnvironment = DEFAULT_ENVIRONMENT;

		/**
		 * table name
		 */
		private String tableName = DEFAULT_TABLE_NAME;

		/**
		 * environmentColumnName
		 */
		private String environmentColumnName = DEFAULT_COLUMN_ENVIRONMENT;

		/**
		 * keyColumnName
		 */
		private String keyColumnName = DEFAULT_COLUMN_KEY;

		/**
		 * valueColumnName
		 */
		private String valueColumnName = DEFAULT_COLUMN_VALUE;

		public String getTableName() {
			return tableName;
		}

		public void setTableName(String tableName) {
			this.tableName = tableName;
		}

		public String getEnvironmentColumnName() {
			return environmentColumnName;
		}

		public void setEnvironmentColumnName(String environmentColumnName) {
			this.environmentColumnName = environmentColumnName;
		}

		public String getKeyColumnName() {
			return keyColumnName;
		}

		public void setKeyColumnName(String keyColumnName) {
			this.keyColumnName = keyColumnName;
		}

		public String getValueColumnName() {
			return valueColumnName;
		}

		public void setValueColumnName(String valueColumnName) {
			this.valueColumnName = valueColumnName;
		}

		public String getDefaultEnvironment() {
			return defaultEnvironment;
		}

		public void setDefaultEnvironment(String defaultEnvironment) {
			this.defaultEnvironment = defaultEnvironment;
		}
	}
}
