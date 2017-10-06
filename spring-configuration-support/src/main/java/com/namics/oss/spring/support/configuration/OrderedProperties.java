package com.namics.oss.spring.support.configuration;

import org.springframework.core.env.PropertiesPropertySource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

/**
 * OrderedProperties holds multiple instances of {@link Properties} along with their key/identifier.
 * The constructor requires a parameter of type {@link LinkedHashMap}, since this type of HashMap maintains the insertion order.
 *
 * @author crfischer, Namics AG
 * @since 26.09.2017 14:56
 */
public class OrderedProperties {

	private LinkedHashMap<String,Properties> properties;

	/**
	 * Constructor accepting a {@link LinkedHashMap} instance which contains properties instances by insertion-order.
	 *
	 * @param properties the properties along with their key/identifier by insertion-order
	 */
	public OrderedProperties(LinkedHashMap<String,Properties> properties){
		this.properties = properties;
	}

	/**
	 * Get all properties along with their key/identifier
	 *
	 * @return the properties
	 */
	public LinkedHashMap<String,Properties> getProperties(){
		return this.properties;
	}

	/**
	 * Get all keys of the current properties map.
	 *
	 * @return the keys
	 */
	public List<String> getKeys(){
		if(this.properties != null) {
			return new ArrayList<>(this.properties.keySet());
		}
		return Collections.emptyList();
	}

	/**
	 * Creates an array of {@link PropertiesPropertySource} with respect to the insertion-order of the backing properties map.
	 * This allows to add the returned {@link PropertiesPropertySource} instances to your environment maintaining the correct hierarchy (e.g. most specific to general properties).
	 *
	 * @return an array of {@link PropertiesPropertySource}
	 */
	public PropertiesPropertySource[] toPropertiesPropertySources(){

		List<PropertiesPropertySource> propertiesPropertySources = new ArrayList<>();

		List<String> keySet = this.getKeys();
		for(String key: keySet){
			propertiesPropertySources.add(new PropertiesPropertySource(key, this.properties.get(key)));
		}

		return propertiesPropertySources.toArray(new PropertiesPropertySource[propertiesPropertySources.size()]);
	}
}
