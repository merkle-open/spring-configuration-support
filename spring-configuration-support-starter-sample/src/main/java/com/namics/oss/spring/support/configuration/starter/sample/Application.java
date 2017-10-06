package com.namics.oss.spring.support.configuration.starter.sample;

import com.namics.oss.spring.support.configuration.starter.sample.config.SampleConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * Application.
 *
 * @author crfischer, Namics AG
 * @since 03.08.2017 08:39
 */
@SpringBootApplication
@Import({ SampleConfig.class})
public class Application {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Application.class);
		app.setAdditionalProfiles("DEV");
		app.run(args);
	}
}
