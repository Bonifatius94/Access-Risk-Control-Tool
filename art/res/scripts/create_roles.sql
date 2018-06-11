
-- create the three roles
CREATE ROLE Admin;
CREATE ROLE DataAnalyst;
CREATE ROLE Viewer;

-- grant privileges to admin role
GRANT ALL ON DbUsers TO Admin;
REVOKE ALL ON DbUsers FROM PUBLIC;

-- the admin account should only manage database accounts, so he just needs to have the admin flag set
-- therefore no more privileges are required

-- grant privileges to data analyst role
GRANT ALL ON CriticalAccessQueries TO DataAnalyst;
GRANT ALL ON CriticalAccessEntries TO DataAnalyst;
GRANT ALL ON Whitelists TO DataAnalyst;
GRANT ALL ON WhitelistEntries TO DataAnalyst;
GRANT ALL ON Configurations TO DataAnalyst;
GRANT ALL ON nm_Configuration_AccessPattern TO DataAnalyst;
GRANT ALL ON AccessPatterns TO DataAnalyst;
GRANT ALL ON AccessConditions TO DataAnalyst;
GRANT ALL ON AccessPatternConditions TO DataAnalyst;
GRANT ALL ON AccessProfileConditions TO DataAnalyst;
GRANT ALL ON AccessPatternConditionProperties TO DataAnalyst;
GRANT ALL ON SapConfigurations TO DataAnalyst;

-- grant privileges to viewer role
GRANT SELECT ON CriticalAccessQueries TO Viewer;
GRANT SELECT ON CriticalAccessEntries TO Viewer;
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