INSERT INTO nmx_configuration
(
	configuration_env,
  configuration_key,
  configuration_value
)
VALUES
('*','test.property.profile.default.db','correct'),
('*','test.property.profile.dev.db','wrong'),
('*','test.encrypted.db.property','ENC(nrdDE/a2fVTeWMOceKesa5g1+6yKTat9z6bwwACJVXB/fcA75FaTsA==)'),
('DEV','test.property.profile.dev.db','correct'),
('*','test.property.profile.prod.db','correct'),
('PROD','test.property.profile.prod.db','wrong'),
('MY-CUSTOM-ENVIRONMENT','test.property.profile.customEnvironment.db','OK')
;