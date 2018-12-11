package com.namics.oss.spring.support.configuration.starter.initializer;

import com.namics.oss.spring.support.configuration.DaoConfigurationPropertiesFactoryBean;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyFilter;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;
import com.ulisesbocchio.jasyptspringboot.wrapper.EncryptablePropertySourceWrapper;
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

	EncryptablePropertyResolver encryptablePropertyResolver;
	EncryptablePropertyFilter encryptablePropertyFilter;

	public EncryptableDataSourcePropertiesInitializer(DaoConfigurationPropertiesFactoryBean databaseConfigFactory, EncryptablePropertyResolver encryptablePropertyResolver, EncryptablePropertyFilter encryptablePropertyFilter) {
		super(databaseConfigFactory);
		this.encryptablePropertyResolver = encryptablePropertyResolver;
		this.encryptablePropertyFilter =  encryptablePropertyFilter;
	}

	@Override
	protected PropertySource<Map<String, Object>> getPropertySource(PropertySource<Map<String, Object>> propertiesPropertySource) {
		return new EncryptablePropertySourceWrapper<>(propertiesPropertySource,encryptablePropertyResolver,encryptablePropertyFilter);
	}

}
