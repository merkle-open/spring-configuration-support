/*
 * Copyright 2000-2011 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.configuration;

import com.namics.oss.spring.support.configuration.config.SpringTestContextConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringTestContextConfiguration.class})
@ActiveProfiles("DEV")
@DirtiesContext
public class DBPropertiesResolverDEVTest {

	@Value("${tst.key.a}")
	private String testKeyA;

	@Value("${tst.key.b}")
	private String testKeyB;

	@Test
	public void testValueA() {
		Assert.assertTrue("Not the same!!", testKeyA.equals("tst.key.a.value.dev"));
	}

	@Test
	public void testValueB() {
		Assert.assertTrue("Not the same!!", testKeyB.equals("tst.key.b.value.common"));
	}
}
