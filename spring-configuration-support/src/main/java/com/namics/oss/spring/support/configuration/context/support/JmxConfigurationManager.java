/*
 * Copyright 2000-2011 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.configuration.context.support;

import com.namics.oss.spring.support.configuration.model.ConfigurationValue;
import com.namics.oss.spring.support.configuration.service.ConfigurationValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

/**
 * Simple JMX component to manage the current configuration values.
 *
 * @author Sandro Ruch, namics ag
 * @since namics-configuration 2.2
 */
@Component("configurationManager")
@ManagedResource(description = "Manages the platform's configuration")
public class JmxConfigurationManager {
	private ConfigurationValueService configurationService;

	/**
	 * Displays a configuration value the application is configured with.
	 *
	 * @param configurationProperty the property name
	 * @return see description
	 */
	@ManagedOperation(description = "Display desired configuration property the application is configured with")
	@ManagedOperationParameters({ @ManagedOperationParameter(name = "configurationProperty", description = "The property key in the datastore") })
	public String displayApplicationValue(String configurationProperty) {
		return configurationService.getValueFor(configurationProperty) != null ? configurationService.getValueFor(configurationProperty) : "unknown";
	}

	/**
	 * Displays a configuration value in the datastore.
	 *
	 * @param configurationProperty the property name
	 * @return see description
	 */
	@ManagedOperation(description = "Retrieves desired configuration property from datastore")
	@ManagedOperationParameters({ @ManagedOperationParameter(name = "configurationProperty", description = "The property key in the datastore") })
	public String displayDatastoreValue(String configurationProperty) {
		ConfigurationValue configValue = configurationService.getValue(configurationProperty);
		return configValue != null ? configValue.getValue() : "unknown";
	}

	/**
	 * Changes the configuration value for a given property.
	 *
	 * @param configurationProperty property name
	 * @param configurationValue    the new value
	 */
	@ManagedOperation(description = "Changes the desired configuration property")
	@ManagedOperationParameters({ @ManagedOperationParameter(name = "configurationProperty", description = "The property key in the datastore"),
			@ManagedOperationParameter(name = "configurationValue", description = "The new value to set for the property key") })
	public void changeConfiguration(String configurationProperty,
	                                String configurationValue) {
		configurationService.setValueFor(configurationProperty, configurationValue);
	}

	// CHECKSTYLE:OFF
	@Autowired(required = true)
	public void setConfigurationService(ConfigurationValueService configurationService) {
		this.configurationService = configurationService;
	}
	// CHECKSTYLE:ON
}
