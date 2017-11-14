package com.namics.oss.spring.support.configuration;

import com.namics.oss.spring.support.configuration.dao.ConfigurationDao;
import com.namics.oss.spring.support.configuration.service.ConfigurationValueService;

/**
 * ConfigurationEnvironment represents a simple implementation of the {@link Environment} interface.
 * This class is primarily used for interacting with instances of {@link ConfigurationDao} or {@link ConfigurationValueService} to set a concrete {@link Environment} (e.g. DEV, QUAL, PROD, ...).
 *
 * @author crfischer, Namics AG
 * @since 25.09.2017 17:24
 */
public class ConfigurationEnvironment implements Environment {

	private String environment;

	public ConfigurationEnvironment(String profile){
		this.environment = profile;
	}

	@Override
	public String getValue() {
		return this.environment;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ConfigurationEnvironment that = (ConfigurationEnvironment) o;

		return environment != null ? environment.equals(that.environment) : that.environment == null;

	}

	@Override
	public int hashCode() {
		return environment != null ? environment.hashCode() : 0;
	}
}
