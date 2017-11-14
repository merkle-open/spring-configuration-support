/*
 * Copyright 2000-2015 Namics AG. All rights reserved.
 */

package com.namics.oss.spring.support.configuration.config;

/**
* TestBean.
*
* @author aschaefer, Namics AG
* @since 06.02.15 16:43
*/
public class TestBean {
	String testValue;

	public String getTestValue() {
		return testValue;
	}

	public void setTestValue(String testValue) {
		this.testValue = testValue;
	}

	public TestBean testValue(String testValue) {
		setTestValue(testValue);
		return this;
	}
}
