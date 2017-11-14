# Spring-Configuration-Support-Starter

The `Spring-Configuration-Support` module provides a Spring Boot Starter for Spring Boot projects.
An example, on how to integrate the starter into an existing project, is provided in the `Spring-Configuration-Support-Starter-Sample` project.

## Step 1: Add the required dependencies

Add the dependency for the starter which is responsible for the auto-configuration of the module.

    <dependency>
		<groupId>com.namics.oss.spring.support.configuration</groupId>
		<artifactId>spring-configuration-support-starter</artifactId>
		<version>1.0.0</version>
	</dependency>

If you do not want the auto-configuration to configure the default user-interface for configuration management, you can ommit the dependency for `Spring-Configuration-Support-Web` by excluding it:

    <dependency>
		<groupId>com.namics.oss.spring.support.configuration</groupId>
		<artifactId>spring-configuration-support-starter</artifactId>
		<version>1.0.0</version>
		<exclusions>
			<exclusion>
				<groupId>com.namics.oss.spring.support.configuration</groupId>
				<artifactId>spring-configuration-support-web</artifactId>
			</exclusion>
		</exclusions>
	</dependency>

## Step 2: Configure the properties of the starter

### Configuration via DataSource

If a DataSource Bean is provided, the starter tries to fetch the configured values from the specified DataSource.
Default values are applied for table- and column names.
The starter supports the configuration of the these default settings within the application.properties file of your Spring Boot project:

    # Optional properties for configuration using a data-source
    com.namics.oss.spring.support.configuration.dataSource.tableName=nmx_configuration
    com.namics.oss.spring.support.configuration.dataSource.keyColumnName=configuration_key
    com.namics.oss.spring.support.configuration.dataSource.valueColumnName=configuration_value
    com.namics.oss.spring.support.configuration.dataSource.environmentColumnName=configuration_env
    com.namics.oss.spring.support.configuration.dataSource.defaultEnvironment=DEV
    
You have to create the table yourself. With the following SQL Script, you create the default table used for properties:

	-- create table schema
	CREATE TABLE nmx_configuration (
    	configuration_env VARCHAR(32) NOT NULL,
    	configuration_key VARCHAR(250) NOT NULL,
    	configuration_value VARCHAR(250) NULL,
    	PRIMARY KEY (configuration_env, configuration_key)
    );

### Configuration of the Environment
Depending on your active profiles, multiple PropertySources are added to your environment. Differentiation between environments is managed via Spring profiles.

### Configuration of the user-interface
The starter allows you to override the default settings for servlet-name and servlet-mapping.

    # Optional properties for configuration-web
    com.namics.oss.spring.support.configuration.web.servletName=configurationServlet
    com.namics.oss.spring.support.configuration.web.servletMapping=/configuration/*
    
You could only administrate the properties for one environment over the configuration servlet. It's possible to set the profile, for which the properties are editable over the administration interface, with the following property:
	
	#Optional property for configruation admin service
	com.namics.oss.spring.support.configuration.defaultProfile=DEFAULT

You could also define your own Bean of type `ConfigurationEnvironment` to define the environment.

	@Bean
	public ConfigurationEnvironment configurationEnvironmentDefault() {
		return new ConfigurationEnvironment("YOUR-PROFILE-TO-ADMINISTRATE");
	}
	

## Step 3: Encryption with jasypt

If you like to encrypt your database properties, you could use jasypt. There is a spring boot starter available to use jasypt:

		<dependency>
			<groupId>com.github.ulisesbocchio</groupId>
			<artifactId>jasypt-spring-boot-starter</artifactId>
			<version>???</version>
		</dependency>

Provide a Bean of org.jasypt.encryption.StringEncryptor.class and save your properties with `ENC()` to the database. Those properties gets decrypted automatically.