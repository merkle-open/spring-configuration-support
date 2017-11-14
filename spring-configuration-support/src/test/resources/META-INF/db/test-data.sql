INSERT INTO tbl_configuration 
(
	configuration_env, 
  configuration_key,
  configuration_value
)
VALUES 
                                  
(
	'DEV', 
	'tst.key.a', 
	'tst.key.a.value.dev'
);
	
INSERT INTO tbl_configuration 
(
	configuration_env, 
  configuration_key,
  configuration_value
)
VALUES 
                                  
(
	'QUAL', 
	'tst.key.a', 
	'tst.key.a.value.qual'
);

INSERT INTO tbl_configuration 
(
	configuration_env, 
  configuration_key,
  configuration_value
)
VALUES
(
	'PROD', 
	'tst.key.a', 
	'tst.key.a.value.prod'
);

INSERT INTO tbl_configuration 
(
	configuration_env, 
  configuration_key,
  configuration_value
)
VALUES
(
	'*', 
	'tst.key.b', 
	'tst.key.b.value.common'
);

INSERT INTO tbl_configuration 
(
	configuration_env, 
  configuration_key,
  configuration_value
)
VALUES
(
	'PROD', 
	'tst.key.b', 
	'tst.key.b.value.common.prod'
);

INSERT INTO tbl_configuration 
(
	configuration_env, 
  configuration_key,
  configuration_value
)
VALUES
(
	'*', 
	'tst.key.c', 
	'^[A-Za-z0-9_$#%&!.?]{7,}$'
);

INSERT INTO tbl_configuration 
(
	configuration_env, 
  configuration_key,
  configuration_value
)
VALUES
(
	'DEV', 
	'tst.key.c', 
	'^[A-Za-z0-9_$#%&!.?]{6,}$'
);
