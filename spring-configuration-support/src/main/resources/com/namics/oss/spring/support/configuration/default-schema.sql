CREATE TABLE nmx_configuration
(
	environment varchar(255) NOT NULL,
	property_key varchar(255) NOT NULL,
	property_value varchar(2000) NOT NULL,
	
	CONSTRAINT PK_nmx_configuration PRIMARY KEY (environment,property_key)
);


