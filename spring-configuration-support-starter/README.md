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

### Configuration of the Environment
Depending on your active profiles, multiple PropertySources are added to your environment. Differentiation between environments is managed via Spring profiles.

### Configuration of the user-interface
The starter allows you to override the default settings for servlet-name and servlet-mapping.

    # Optional properties for configuration-web
    com.namics.oss.spring.support.configuration.web.servletName=configurationServlet
    com.namics.oss.spring.support.configuration.web.servletMapping=/configuration/*

### Resolve configured values

Configure a PropertySourcesPlaceholderConfigurer, if you want to resolve @Value annotations based on the current Spring Environment and its set of PropertySources.
Note the DependsOn-annotation, which makes sure that we include the properties fetched from the DataSource.

    @Bean
	@DependsOn("databaseConfigurationSource")
	public static PropertySourcesPlaceholderConfigurer databaseConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
