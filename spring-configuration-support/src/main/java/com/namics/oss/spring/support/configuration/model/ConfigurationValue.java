/*
 * Copyright 2000-2011 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.configuration.model;

import com.namics.oss.spring.support.configuration.Environment;

import java.io.Serializable;

/**
 * Represents a configuration value.
 *
 * @author Sandro Ruch, namics ag
 * @since namics-configuration 2.2
 */
public class ConfigurationValue implements Serializable {
	private static final long serialVersionUID = -5472586633561237500L;

	private Environment env;
	private String key;
	private String value;

	/**
	 * Default constructor.
	 *
	 * @param env   the environment
	 * @param key   the key
	 * @param value the value
	 */
	public ConfigurationValue(Environment env, String key, String value) {
		super();
		this.env = env;
		this.key = key;
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ConfigurationValue that = (ConfigurationValue) o;

		if (env != null ? !env.equals(that.env) : that.env != null) {
			return false;
		}
		return key != null ? key.equals(that.key) : that.key == null;

	}

	@Override
	public int hashCode() {
		int result = env != null ? env.hashCode() : 0;
		result = 31 * result + (key != null ? key.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "ConfigurationValue{" +
		       "key='" + key + '\'' +
		       ", env=" + env +
		       ", value='" + value + '\'' +
		       '}';
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public Environment getEnv() {
		return env;
	}

	public void setEnv(Environment env) {
		this.env = env;
	}

}
