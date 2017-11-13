package com.namics.oss.spring.support.configuration.starter;

import com.namics.oss.spring.support.configuration.ConfigurationEnvironment;
import com.namics.oss.spring.support.configuration.dao.ConfigurationDao;
import com.namics.oss.spring.support.configuration.service.ConfigurationValueService;
import com.namics.oss.spring.support.configuration.service.ConfigurationValueServiceImpl;
import com.namics.oss.spring.support.configuration.web.ConfigServletConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.inject.Inject;

/**
 * SpringConfigurationSupportWebAutoConfiguration.
 *
 * @author crfischer, Namics AG
 * @since 07.08.2017 08:50
 */
@Configuration
@ConditionalOnClass(ConfigServletConfig.class)
@EnableConfigurationProperties(SpringConfigurationSupportProperties.class)
public class SpringConfigurationSupportWebAutoConfiguration {

	@Inject
	protected SpringConfigurationSupportProperties namicsConfigurationProperties;

	@Bean
	public ServletRegistrationBean configurationServlet() {

		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(ConfigServletConfig.class);

		DispatcherServlet dispatcherServlet = new DispatcherServlet();
		dispatcherServlet.setApplicationContext(applicationContext);

		ServletRegistrationBean registrationBean = new ServletRegistrationBean(dispatcherServlet, namicsConfigurationProperties.getWeb()
		                                                                                                                       .getServletMapping());
		registrationBean.setName(namicsConfigurationProperties.getWeb().getServletName());
		registrationBean.setLoadOnStartup(1);
		return registrationBean;
	}

	@Bean
	@ConditionalOnMissingBean
	public ConfigurationValueService configurationValueService(ConfigurationDao configurationDao, ConfigurationEnvironment configurationEnvironment) {
		return new ConfigurationValueServiceImpl(configurationDao, configurationEnvironment);
	}

	@Bean
	@ConditionalOnMissingBean
	public ConfigurationEnvironment configurationEnvironmentDefault() {
		return new ConfigurationEnvironment(namicsConfigurationProperties.getDefaultProfile());
	}
}
