INSERT INTO nmx_configuration
(
	configuration_env,
  configuration_key,
  configuration_value
)
VALUES
('*','test.property.profile.default.db','correct'),
('*','test.property.profile.dev.db','wrong'),
('DEV','test.property.profile.dev.db','correct'),
('*','test.property.profile.prod.db','correct'),
('PROD','test.property.profile.prod.db','wrong'),
('MY-CUSTOM-ENVIRONMENT','test.property.profile.customEnvironment.db','OK')
;