/*
 * Copyright 2000-2016 Namics AG. All rights reserved.
 */

package com.namics.oss.spring.support.configuration.model;

import com.namics.oss.spring.support.configuration.ConfigurationEnvironment;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * ConfigurationValueTest.
 *
 * @author aschaefer, Namics AG
 * @since 22.01.16 09:54
 */
public class ConfigurationValueTest {

	@Test
	public void testEqualsNull() throws Exception {
		assertFalse(new ConfigurationValue(null, null, null).equals(null));
		assertFalse(new ConfigurationValue(null, null, null).equals(new Object()));
		assertTrue(new ConfigurationValue(null, null, null).equals(new ConfigurationValue(null, null, null)));
	}

	@Test
	public void testEqualsTrue() throws Exception {
		assertTrue(new ConfigurationValue(new ConfigurationEnvironment("LIVE"), "key", "value").equals(new ConfigurationValue(new ConfigurationEnvironment("LIVE"), "key", "value")));
	}
	@Test
	public void testEqualsKeyFalse() throws Exception {
		assertFalse(new ConfigurationValue(new ConfigurationEnvironment("LIVE"), "key", "value").equals(new ConfigurationValue(new ConfigurationEnvironment("LIVE"), "different", "value")));
	}
	@Test
	public void testEqualsEnvironmentFalse() throws Exception {
		assertFalse(new ConfigurationValue(new ConfigurationEnvironment("LIVE"), "key", "value").equals(new ConfigurationValue(new ConfigurationEnvironment("STA"), "different", "value")));
	}
}