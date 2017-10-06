/*
 * Copyright 2000-2011 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.configuration;

/**
 * Interface as part of the extensible enum pattern.
 *
 * @author Sandro Ruch, namics AG
 * @since namics-configuration 1.0
 */
public interface Environment {
	/**
	 * Returns the value of the enum.
	 *
	 * @return enum value
	 */
	String getValue();

	String DEFAULT = "*";
}
