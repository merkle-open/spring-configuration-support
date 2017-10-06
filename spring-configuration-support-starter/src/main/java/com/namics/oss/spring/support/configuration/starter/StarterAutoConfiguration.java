package com.namics.oss.spring.support.configuration.starter;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * StarterAutoConfiguration.
 *
 * @author crfischer, Namics AG
 * @since 02.08.2017 08:55
 */
@Configuration
@Import({
		SpringConfigurationSupportAutoConfiguration.class,
		SpringConfigurationSupportWebAutoConfiguration.class
})
public class StarterAutoConfiguration {

}
