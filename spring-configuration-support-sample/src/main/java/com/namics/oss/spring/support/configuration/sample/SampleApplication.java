/*
 * Copyright 2000-2016 Namics AG. All rights reserved.
 */

package com.namics.oss.spring.support.configuration.sample;

import com.namics.oss.spring.support.configuration.web.ConfigServletConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * SampleApplication.
 *
 * @author aschaefer, Namics AG
 * @since 10.02.16 15:46
 */
@SpringBootApplication
public class SampleApplication {

	@Bean(name = "configServlet")
	public ServletRegistrationBean configServlet() {
		DispatcherServlet dispatcherServlet = new DispatcherServlet();
		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(ConfigServletConfig.class);
		dispatcherServlet.setApplicationContext(applicationContext);
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(dispatcherServlet, "/config/*");
		registrationBean.setLoadOnStartup(1);
		return registrationBean;
	}

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(SampleApplication.class);
		springApplication.setAdditionalProfiles("DEV");
		springApplication.run(args);
	}

}
