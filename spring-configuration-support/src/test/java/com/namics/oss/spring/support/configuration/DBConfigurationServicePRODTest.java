/*
 * Copyright 2000-2011 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.configuration;

import com.namics.oss.spring.support.configuration.config.SpringTestContextConfiguration;
import com.namics.oss.spring.support.configuration.model.ConfigurationValue;
import com.namics.oss.spring.support.configuration.service.ConfigurationValueService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringTestContextConfiguration.class})
@ActiveProfiles("PROD")
@DirtiesContext
public class DBConfigurationServicePRODTest {
	
	@Autowired(required = true)
	private ConfigurationValueService service;

	@Test
	public void testValueA() {
		Collection<ConfigurationValue> values = this.service.getValues();
		Assert.assertTrue(values.size() == 3);

	}

}
