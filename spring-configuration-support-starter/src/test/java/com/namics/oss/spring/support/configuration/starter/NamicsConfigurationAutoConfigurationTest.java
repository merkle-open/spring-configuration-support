package com.namics.oss.spring.support.configuration.starter;

import org.junit.Test;

import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.junit.Assert.fail;

/**
 * NamicsConfigurationAutoconfigurationTest.
 *
 * @author crfischer, Namics AG
 * @since 25.09.2017 09:14
 */
public class NamicsConfigurationAutoConfigurationTest {

	@Test
	public void configurationPropertiesExist() {

		Set<String> configurablePropertyFields = stream(SpringConfigurationSupportAutoConfiguration.class.getDeclaredFields())
				.filter(field -> Modifier.isStatic(field.getModifiers()) && field.getName().startsWith("property"))
				.map(field -> {
					try {
						field.setAccessible(true);
						return (String) field.get(null);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					return null;
				})
				.collect(Collectors.toSet());

		// Verify these fields exist in the ConfigurationProperties class
		for (String field : configurablePropertyFields) {

			String[] packages = field.split("\\.");
			String fieldName = packages[packages.length-1];

			try {
				SpringConfigurationSupportProperties.DataSource.class.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				fail("Field=\"" + fieldName + "\" on class=\"" + SpringConfigurationSupportProperties.DataSource.class.getName() + "\" does not exist, make sure the properties declared in the AutoConfiguration class are configurable in the corresponding ConfigurationProperties class.");
			}
		}
	}
}
