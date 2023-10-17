
-- create the three roles
CREATE ROLE Admin;
CREATE ROLE Configurator;
CREATE ROLE Viewer;

-- this role is just a dummy that shows if the user has already logged in or not
CREATE ROLE FirstLogin;

-- grant privileges to admin role
GRANT ALL ON DbUsers TO Admin;

-- grant privileges to data analyst role
GRANT ALL ON CriticalAccessQueries TO Configurator;
GRANT ALL ON CriticalAccessEntries TO Configurator;
GRANT ALL ON Whitelists TO Configurator;
GRANT ALL ON WhitelistEntries TO Configurator;
GRANT ALL ON Configurations TO Configurator;
GRANT ALL ON nm_Configuration_AccessPattern TO Configurator;
GRANT ALL ON AccessPatterns TO Configurator;
GRANT ALL ON AccessConditions TO Configurator;
GRANT ALL ON AccessPatternConditions TO Configurator;
GRANT ALL ON AccessProfileConditions TO Configurator;
GRANT ALL ON AccessPatternConditionProperties TO Configurator;
GRANT ALL ON SapConfigurations TO Configurator;
GRANT ALL ON DbUsers TO Configurator;

-- grant privileges to viewer role
GRANT ALL ON CriticalAccessQueries TO Viewer;
GRANT ALL ON CriticalAccessEntries TO Viewer;
GRANT SELECT ON Whitelists TO Viewer;
GRANT SELECT ON WhitelistEntries TO Viewer;
GRANT SELECT ON Configurations TO Viewer;
GRANT SELECT ON nm_Configuration_AccessPattern TO Viewer;
GRANT SELECT ON AccessPatterns TO Viewer;
GRANT SELECT ON AccessConditions TO Viewer;
GRANT SELECT ON AccessPatternConditions TO Viewer;
GRANT SELECT ON AccessProfileConditions TO Viewer;
GRANT SELECT ON AccessPatternConditionProperties TO Viewer;
GRANT SELECT ON SapConfigurations TO Viewer;
GRANT ALL ON DbUsers TO Viewer;

-- remove privileges for public role (only registered users can access data)
REVOKE ALL ON CriticalAccessQueries FROM PUBLIC;
REVOKE ALL ON CriticalAccessEntries FROM PUBLIC;
REVOKE ALL ON Whitelists FROM PUBLIC;
REVOKE ALL ON WhitelistEntries FROM PUBLIC;
REVOKE ALL ON Configurations FROM PUBLIC;
REVOKE ALL ON nm_Configuration_AccessPattern FROM PUBLIC;
REVOKE ALL ON AccessPatterns FROM PUBLIC;
REVOKE ALL ON AccessConditions FROM PUBLIC;
REVOKE ALL ON AccessPatternConditions FROM PUBLIC;
REVOKE ALL ON AccessProfileConditions FROM PUBLIC;
REVOKE ALL ON AccessPatternConditionProperties FROM PUBLIC;
REVOKE ALL ON SapConfigurations FROM PUBLIC;
REVOKE ALL ON DbUsers FROM PUBLIC;