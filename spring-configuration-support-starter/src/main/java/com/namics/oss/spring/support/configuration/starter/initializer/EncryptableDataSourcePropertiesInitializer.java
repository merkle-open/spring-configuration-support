package com.namics.oss.spring.support.configuration.starter.initializer;

import com.namics.oss.spring.support.configuration.DaoConfigurationPropertiesFactoryBean;
import com.ulisesbocchio.jasyptspringboot.resolver.DefaultPropertyResolver;
import com.ulisesbocchio.jasyptspringboot.wrapper.EncryptablePropertySourceWrapper;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.util.Map;

/**
 * EncryptableDataSourcePropertiesInitializer.
 * ensures that encryption is processed if a stringEncryptor is available.
 *
 * @author lboesch, Namics AG
 * @since 06.11.17 08:25
 */
public class EncryptableDataSourcePropertiesInitializer extends DataSourcePropertiesInitializer {

	protected StringEncryptor stringEncryptor;

	public EncryptableDataSourcePropertiesInitializer(DaoConfigurationPropertiesFactoryBean databaseConfigFactory, StringEncryptor stringEncryptor) {
		super(databaseConfigFactory);
		this.stringEncryptor = stringEncryptor;
	}

	@Override
	protected PropertySource<Map<String, Object>> getPropertySource(PropertySource<Map<String, Object>> propertiesPropertySource) {
		if (stringEncryptor != null) {
			return new EncryptablePropertySourceWrapper<>(propertiesPropertySource, new DefaultPropertyResolver(stringEncryptor));
		}
		return propertiesPropertySource;
	}

}
