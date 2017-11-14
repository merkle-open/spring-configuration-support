/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.oss.spring.support.configuration.web;

import com.namics.oss.spring.support.configuration.service.ConfigurationValueService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * WebApiConfig.
 *
 * @author lboesch, Namics AG
 * @since 20.06.14 13:28
 */
@Configuration
@EnableAsync
public class ConfigServletConfig extends WebMvcConfigurationSupport {

	@Override
	protected void addViewControllers(ViewControllerRegistry registry) {
		super.addViewControllers(registry);
		registry.addViewController("/").setViewName("redirect:properties.html");
	}

	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		super.addResourceHandlers(registry);
		registry.addResourceHandler("/*.html").addResourceLocations("classpath:/META-INF/namics-configuration/terrific/assets/");
		registry.addResourceHandler("/fonts/**").addResourceLocations("classpath:/META-INF/namics-configuration/terrific/assets/font/");
		registry.addResourceHandler("/**/*.html").addResourceLocations("classpath:/META-INF/namics-configuration/terrific/");
		registry.addResourceHandler("/**/*.css", "/**/*.js").addResourceLocations("classpath:/META-INF/namics-configuration/terrific/");
	}

	@Override
	protected void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}


	@Bean
	public ConfigRestController configRestController(ConfigurationValueService service, Environment environment) {
		return new ConfigRestController(service, environment);
	}


}
