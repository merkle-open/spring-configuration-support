INSERT INTO nmx_configuration (environment, property_key, property_value) VALUES ('*', 'property.a', 'a.*');
INSERT INTO nmx_configuration (environment, property_key, property_value) VALUES ('DEV', 'property.a', 'a.DEV');
INSERT INTO nmx_configuration (environment, property_key, property_value) VALUES ('STA', 'property.a', 'a.STA');
INSERT INTO nmx_configuration (environment, property_key, property_value) VALUES ('LIVE', 'property.a', 'a.LIVE');

INSERT INTO nmx_configuration (environment, property_key, property_value) VALUES ('*', 'property.b', 'b.*');

INSERT INTO nmx_configuration (environment, property_key, property_value) VALUES ('*', 'property.c', 'c.*');
INSERT INTO nmx_configuration (environment, property_key, property_value) VALUES ('LIVE', 'property.c', 'c.LIVE');
INSERT INTO nmx_configuration (environment, property_key, property_value) VALUES ('*', 'some.very.very.extra.un.normal.long.property.like.stupid.long.hystrix.command.timeOutInMilliSeconds', 'Wih a very long value that may cause table to extra wide.');
INSERT INTO nmx_configuration (environment, property_key, property_value) VALUES ('*', 'hystrix.command.topicPilotDeleteUser.execution.isolation.thread.timeoutInMilliseconds', 'Wih a very long value that may cause table to extra wide.');

INSERT INTO nmx_configuration (environment, property_key, property_value) VALUES ('*', 'secret.property', 'SHOULD BE HIDDEN');
INSERT INTO nmx_configuration (environment, property_key, property_value) VALUES ('*', 'hidden.passwordValue', 'SHOULD BE HIDDEN');
INSERT INTO nmx_configuration (environment, property_key, property_value) VALUES ('*', 'hidden.password', 'SHOULD BE HIDDEN');


