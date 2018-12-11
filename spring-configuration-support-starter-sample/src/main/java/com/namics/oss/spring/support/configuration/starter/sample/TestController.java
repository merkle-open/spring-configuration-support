package com.namics.oss.spring.support.configuration.starter.sample;

import com.namics.oss.spring.support.configuration.dao.ConfigurationDao;
import com.namics.oss.spring.support.configuration.model.ConfigurationValue;
import com.namics.oss.spring.support.configuration.service.ConfigurationValueService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * TestController.
 *
 * @author crfischer, Namics AG
 * @since 03.08.2017 09:05
 */
@Controller
public class TestController {

	private ConfigurationValueService configurationValueService;

	@Value("${test.property.profile.default:wrong}")
	protected String property1;
	@Value("${test.property.profile.dev:wrong}")
	protected String property2;
	@Value("${test.property.profile.default.db:wrong}")
	protected String property3;
	@Value("${test.property.profile.dev.db:wrong}")
	protected String property4;
	@Value("${test.encrypted.file.property:wrong}")
	protected String decryptedProperty;
	@Value("${test.encrypted.db.property:wrong}")
	protected String decryptedDbProperty;

	@Inject
	public TestController(ConfigurationValueService configurationValueService, ConfigurationDao configurationDao){
		this.configurationValueService = configurationValueService;
	}

	@RequestMapping("/properties")
	@ResponseBody
	public Collection<String> resolvedProperties(){
		return asList(property1,property2,property3,property4,"File-Property:" + decryptedProperty, "Db-Property:" + decryptedDbProperty);
	}

	@RequestMapping("/db")
	@ResponseBody
	public Collection<ConfigurationValue> db(){
		return configurationValueService.getValues();
	}

	@RequestMapping("/file")
	@ResponseBody
	public Map<String,String> file(){

		Map<String,String> mappings = new LinkedHashMap<>();
		mappings.put("test.property.profile.default",property1);
		mappings.put("test.property.profile.dev",property2);
		mappings.put("test.encrypted.file.property",decryptedProperty);
		return mappings;
	}

}
