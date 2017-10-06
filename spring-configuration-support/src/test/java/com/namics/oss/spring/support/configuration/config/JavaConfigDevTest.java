/*
 * Copyright 2000-2015 Namics AG. All rights reserved.
 */

package com.namics.oss.spring.support.configuration.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * JavaConfigTest.
 *
 * @author aschaefer, Namics AG
 * @since 06.02.15 15:42
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
@DirtiesContext
@ActiveProfiles("DEV")
public class JavaConfigDevTest {

	@Autowired
	TestBean devBean;

	@Autowired
	ConfigurableApplicationContext context;

	@Test
	public void testDev() throws Exception {
		assertEquals("tst.key.a.value.dev", devBean.getTestValue());
	}

}
