# Spring-Configuration-Support

System        | Status
--------------|------------------------------------------------        
CI master     | [![Build Status][travis-master]][travis-url]
CI develop    | [![Build Status][travis-develop]][travis-url]
Dependency    | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.namics.oss.spring.support.configuration/spring-configuration-support/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.namics.oss.spring.support.configuration/spring-configuration-support)

`Spring-Configuration-Support` allows to fetch environment-specific property values from a configured data-source.
Additionally, a default user-interface for management of these properties is provided as well.
The configuration of a PropertySourcesPlaceholderConfigurer, which depends on the provided source, allows to resolve the specified property placeholders in your application.

## Usage

You have the choice of either configuring the library yourself (via Java-Config) or use the provided Spring Boot starter dependency.

### Maven Dependency (Latest Version in `pom.xml`):

	<dependency>
		<groupId>com.namics.oss.spring.support.configuration</groupId>
		<artifactId>spring-configuration-support</artifactId>
		<version>1.0.0</version>
	</dependency>

### Spring Boot Starter (Latest Version in `pom.xml`):

    <dependency>
		<groupId>com.namics.oss.spring.support.configuration</groupId>
		<artifactId>spring-configuration-support-starter</artifactId>
		<version>1.0.0</version>
	</dependency>

## Requirements	
Java: JDK 8  

## Configuration

### Spring Boot Starter Auto-Configuration

Requirements:
Configured DataSource Bean.

The auto-configuration relies on a pre-configured DataSource Bean. If no different table or column names are specified, the following structure of the table named "nmx_configuration" is required:

    configuration_env | configuration_key | configuration_value
    ----------------------------------------------------------------------------
    DEV               | app.base.url      | http://localhost:8080/myapp
    PROD              | app.base.url      | http://prod.customer.com/myapp

The default environment is "*".
The table name, the column names and the default environment can be specified via the following properties in your application.properties file:

    com.namics.oss.spring.support.configuration.dataSource.tableName=my_configuration_table
    com.namics.oss.spring.support.configuration.dataSource.keyColumnName=my_configuration_key
    com.namics.oss.spring.support.configuration.dataSource.valueColumnName=my_configuration_value
    com.namics.oss.spring.support.configuration.dataSource.environmentColumnName=my_configuration_env
    com.namics.oss.spring.support.configuration.dataSource.defaultEnvironment=MY_DEFAULT_ENVIRONMENT

#### User Interface

The user-interface for managing the properties is located at the following url by default:
http://localhost:8080/configuration/properties.html (for localhost)

The servlet-mapping as well as the servlet-name can be specified in your application.properties file as well:

    com.namics.oss.spring.support.configuration.web.servletName=configurationServlet
    com.namics.oss.spring.support.configuration.web.servletMapping=/configuration/*

There is also the possibility to exclude the user-interface from the auto-configuration by excluding the dependency `spring-configuration-support-web`:
    
    <dependency>
		<groupId>com.namics.oss.spring.support.configuration</groupId>
		<artifactId>spring-configuration-support-starter</artifactId>
		<exclusions>
			<exclusion>
				<groupId>com.namics.oss.spring.support.configuration</groupId>
				<artifactId>spring-configuration-support-web</artifactId>
			</exclusion>
		</exclusions>
	</dependency>

### Configuration via Java-Config

The following example, based on an in-memory database, demonstrates the configuration of the module by using Spring Java-Config.
Depending on the active profiles in your environment, the module adds a PropertiesPropertySource for every profile to your environment.
The PropertiesPropertySource representing the default environment has the lowest priority.

	@Configuration
    public class PropertiesConfig {
        
    	@Bean(name = "dbSource")
    	public PropertiesPropertySource[] source(
    			@Named("dbProps") OrderedProperties orderedProperties,
    			ConfigurableEnvironment environment) throws Exception {
    
    		PropertiesPropertySource[] propertiesPropertySources = orderedProperties.toPropertiesPropertySources();
    		for(int i=(propertiesPropertySources.length-1);i>=0;i--){
    			environment.getPropertySources().addFirst(propertiesPropertySources[i]);
    		}
    		return propertiesPropertySources;
    	}
    
    	@Bean(name = "dbProps")
    	public DaoConfigurationPropertiesFactoryBean databaseConfigFactory(ConfigurationDao configurationDao, Environment environment) throws Exception {
    		return new DaoConfigurationPropertiesFactoryBean(configurationDao, environment.getActiveProfiles(), "*");
    	}
    
    	@Bean
    	public ConfigurationDaoJdbcImpl configurationDao(DataSource dataSource) {
    		return ConfigurationDaoJdbcImpl.forDataSource(dataSource)
    		                               .tableName("nmx_configuration")
    		                               .environmentColumn("environment")
    		                               .propertyKeyColumn("property_key")
    		                               .valueColumn("property_value")
    		                               .build();
    	}
    	
        @Bean
        public DataSource dataSource(){
            return new EmbeddedDatabaseBuilder()
                    .continueOnError(true)
                    .addScripts("classpath:/META-INF/db/schema.sql","classpath:/META-INF/db/data.sql")
                    .build();
        }
    }

You can also configure a Bean of type ConfigurationValueService to work with the configured properties.
Note that you have to set a specific environment to operate on. 

    @Bean
	public ConfigurationValueServiceImpl configurationValueService(ConfigurationDao configurationDao) {
		return new ConfigurationValueServiceImpl(configurationDao, new ConfigurationEnvironment("DEV"));
	}

Configure a PropertySourcesPlaceholderConfigurer, which depends on the loaded property sources, to resolve the placeholders (e.g. @Value(...)) in your application.

	@Bean
	@DependsOn("dbSource")
	public static PropertySourcesPlaceholderConfigurer databaseConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

### Default values

Certain values remain the same for multiple environments (e.g. maximum password length). 
In this case, we configure the length for the default environment "*".

Example:

	configuration_env | configuration_key | configuration_value
	-----------------------------------------------------------
	*                 | password.length   | 60
	PROD              | password.length   | 50

Using the above configuration the property password.length will always resolve to 60 unless the profile "PROD" is active.
If the profile "PROD" is active, all configuration values for the specified environment will be added as a PropertySource to the environment additionally.
The property password.length will then resolve to the value 50.

A SQL-Script creating the default schema is located at:
	`com/namics/oss/spring/support/configuration/default-schema.sql`
	
	
[travis-master]: https://travis-ci.org/namics/spring-configuration-support.svg?branch=master
[travis-develop]: https://travis-ci.org/namics/spring-configuration-support.svg?branch=develop
[travis-url]: https://travis-ci.org/namics/spring-configuration-support
