CREATE TABLE tbl_configuration
(
	configuration_env varchar(255) NOT NULL,
	configuration_key varchar(255) NOT NULL,
	configuration_value varchar(500) NOT NULL,
	
	CONSTRAINT PK_tbl_configuration PRIMARY KEY (configuration_env,configuration_key)
);
