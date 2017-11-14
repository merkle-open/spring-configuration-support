/*
 * Copyright 2000-2011 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.configuration;

import com.namics.oss.spring.support.configuration.config.SpringTestContextConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringTestContextConfiguration.class})
@ActiveProfiles("PROD")
@DirtiesContext
public class DBPropertiesResolverPRODTest {

	@Value("${tst.key.a}")
	private String testKeyA;

	@Value("${tst.key.b}")
	private String testKeyB;

	@Test
	public void testValueA() {
		assertThat(testKeyA, is("tst.key.a.value.prod"));
	}

	@Test
	public void testValueB() {
		assertThat(testKeyB, is("tst.key.b.value.common.prod"));
	}
}
