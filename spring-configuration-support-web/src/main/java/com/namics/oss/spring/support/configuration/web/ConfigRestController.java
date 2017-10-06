/*
 * Copyright 2000-2016 Namics AG. All rights reserved.
 */

package com.namics.oss.spring.support.configuration.web;

import com.namics.oss.spring.support.configuration.model.ConfigurationValue;
import com.namics.oss.spring.support.configuration.service.ConfigurationValueService;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toCollection;

/**
 * ConfigRestController.
 *
 * @author aschaefer, Namics AG
 * @since 10.02.16 11:21
 */
@RestController
@RequestMapping(ConfigRestController.MAPPING)
public class ConfigRestController {

	public static final String MAPPING = "/value";
	protected final ConfigurationValueService service;
	protected Pattern hidePattern;

	public ConfigRestController(ConfigurationValueService service, Environment environment) {
		this.service = service;
		String regex = "(.*password.*)|(.*secret.*)";
		regex = environment.getProperty("namics.commons.config.web.hide.pattern", regex);
		this.hidePattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
	}

	@RequestMapping(method = RequestMethod.GET)
	public Map<String, ?> all() {
		HashMap<String, Object> result = new HashMap<>();
		result.put("insertSupported", service.isInsertSupported());
		result.put("deleteSupported", service.isDeleteSupported());
		result.put("properties", service.getValues().stream()
		                                .map(this::mapBean)
		                                .collect(toCollection(TreeSet::new)));
		return result;
	}


	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void save(@RequestParam("key") String key, @RequestParam String value) {
		service.setValueFor(key, value);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void delete(@RequestParam("key") String key, @RequestParam("env") String env) {
		service.delete(key, env);
	}

	protected ConfigBean mapBean(ConfigurationValue item) {
		String key = item.getKey();
		return new ConfigBean(key, item.getValue(), item.getEnv().getValue(), hidePattern.matcher(key).matches());
	}

	protected static class ConfigBean implements Comparable<ConfigBean> {

		private final String key;
		private final String value;
		private final String env;
		private final boolean secret;

		public ConfigBean(String key, String value, String env, boolean secret) {
			this.key = key;
			this.value = secret ? "********" : value;
			this.env = env;
			this.secret = secret;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

		public String getEnv() {
			return env;
		}

		public boolean isSecret() {
			return secret;
		}

		@Override
		public int compareTo(ConfigBean o) {
			if (o == null) {
				return Integer.MAX_VALUE;
			}
			if (getKey() == null) {
				return -1;
			}
			if (o.getKey() == null) {
				return -1;
			}
			return key.compareTo(o.getKey());
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			ConfigBean that = (ConfigBean) o;

			if (key != null ? !key.equals(that.key) : that.key != null) {
				return false;
			}
			return env != null ? env.equals(that.env) : that.env == null;

		}

		@Override
		public int hashCode() {
			int result = key != null ? key.hashCode() : 0;
			result = 31 * result + (env != null ? env.hashCode() : 0);
			return result;
		}
	}


}
