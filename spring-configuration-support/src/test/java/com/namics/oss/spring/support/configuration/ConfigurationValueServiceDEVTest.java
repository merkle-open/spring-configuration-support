/*
 * Copyright 2000-2011 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.configuration;

import com.namics.oss.spring.support.configuration.config.SpringTestContextConfiguration;
import com.namics.oss.spring.support.configuration.model.ConfigurationValue;
import com.namics.oss.spring.support.configuration.service.ConfigurationValueService;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringTestContextConfiguration.class})
@ActiveProfiles("DEV")
@DirtiesContext
public class ConfigurationValueServiceDEVTest {

	@Autowired
	@Qualifier("configurationService")
	private ConfigurationValueService service;

	@Value("${tst.key.a}")
	private String testKeyA;

	@Test
	public void testConfigReading() {
		String value = this.service.getValueFor("tst.key.a");
		Assert.assertEquals(testKeyA, value);
	}

	@Test
	public void testConfigUpdate() {
		this.service.setValueFor("tst.key.a", "tst.key.a.newValue");
		Assert.assertNotSame(testKeyA, "tst.key.a.newValue");
		ConfigurationValue value = this.service.getValue("tst.key.a");
		Assert.assertSame(value.getValue(), "tst.key.a.newValue");
	}
}
