
-- ==============================
--             users
-- ==============================

CREATE USER TestAdmin PASSWORD 'foobar' ADMIN;
CREATE USER TestDataAnalyst PASSWORD 'foobar';
CREATE USER TestViewer PASSWORD 'foobar';

GRANT Admin TO TestAdmin;
GRANT DataAnalyst TO TestDataAnalyst;
GRANT Viewer TO TestViewer;

-- ==============================
--          whitelists
-- ==============================

-- active whitelist
INSERT INTO Whitelists (id, description, createdBy, createdAt, isArchived) VALUES (1, 'test description', 'test', '2018-06-08T15:09:15', 0)
INSERT INTO WhitelistEntries (id, whitelistId, username, usecaseId) VALUES (1, 1, '2.A', 'ZT2111_P');
INSERT INTO WhitelistEntries (id, whitelistId, username, usecaseId) VALUES (2, 1, '2.A', 'ZT2112_F');
INSERT INTO WhitelistEntries (id, whitelistId, username, usecaseId) VALUES (3, 1, '1.A', 'ZT2111_P');
INSERT INTO WhitelistEntries (id, whitelistId, username, usecaseId) VALUES (4, 1, '2.B', 'ZT2111_P');
INSERT INTO WhitelistEntries (id, whitelistId, username, usecaseId) VALUES (5, 1, '2.C', 'ZT2111_P');
INSERT INTO WhitelistEntries (id, whitelistId, username, usecaseId) VALUES (6, 1, '3.A', 'ZT2111_P');
INSERT INTO WhitelistEntries (id, whitelistId, username, usecaseId) VALUES (7, 1, '3.B', 'ZT2111_P');

-- archived whitelist
INSERT INTO Whitelists (id, description, createdBy, createdAt, isArchived) VALUES (2, 'test description', 'test', '2018-06-08T15:09:15', 1);
INSERT INTO WhitelistEntries (id, whitelistId, username, usecaseId) VALUES ( 8, 2, '2.A', 'ZT2111_P');
INSERT INTO WhitelistEntries (id, whitelistId, username, usecaseId) VALUES ( 9, 2, '2.A', 'ZT2112_F');
INSERT INTO WhitelistEntries (id, whitelistId, username, usecaseId) VALUES (10, 2, '1.A', 'ZT2111_P');
INSERT INTO WhitelistEntries (id, whitelistId, username, usecaseId) VALUES (11, 2, '2.B', 'ZT2111_P');
INSERT INTO WhitelistEntries (id, whitelistId, username, usecaseId) VALUES (12, 2, '2.C', 'ZT2111_P');
INSERT INTO WhitelistEntries (id, whitelistId, username, usecaseId) VALUES (13, 2, '3.A', 'ZT2111_P');
INSERT INTO WhitelistEntries (id, whitelistId, username, usecaseId) VALUES (14, 2, '3.B', 'ZT2111_P');

-- ==============================
--      use cases (active)
-- ==============================

-- use case with profile condition (NONE linkage, use case 3.A from examples)
INSERT INTO AccessPatterns (id, isArchived, createdAt, createdBy, usecaseId, description, linkage) VALUES (1, 0, '2018-06-08T15:09:15', 'test', '3.A', 'test description', 'None');
INSERT INTO AccessProfileConditions (id, profile) VALUES (1, 'SAP_ALL');
INSERT INTO AccessConditions (id, type, patternId, patternConditionId, ProfileConditionId) VALUES (1, 'Profile', 1, NULL, 1);

-- use case with pattern condition (AND linkage, use case 1.A from examples)
INSERT INTO AccessPatterns (id, isArchived, createdAt, createdBy, usecaseId, description, linkage) VALUES (2, 0, '2018-06-08T15:09:15', 'test', '1.A', 'Unexpected users are authorized to copy a client (local copy wo user/profiles)', 'And');
INSERT INTO AccessPatternConditions (id) VALUES (1);
INSERT INTO AccessConditions (id, type, patternId, patternConditionId, ProfileConditionId) VALUES (2, 'Pattern', 2, 1, NULL);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (1, 'S_TCODE',    'TCD',        'SCCL', 'SCC9', NULL, NULL, 1);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (2, 'S_ADMI_FCD', 'S_ADMI_FCD', 'T000',  NULL,  NULL, NULL, 1);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (3, 'S_TABU_DIS', 'ACTVT',      '02',    NULL,  NULL, NULL, 1);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (4, 'S_TABU_DIS', 'DICBERCLS',  '"*"',   NULL,  NULL, NULL, 1);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (5, 'S_TABU_CLI', 'CLIIDMAINT', 'X',     NULL,  NULL, NULL, 1);
INSERT INTO AccessPatternConditions (id) VALUES (2);
INSERT INTO AccessConditions (id, type, patternId, patternConditionId, ProfileConditionId) VALUES (3, 'Pattern', 2, 2, NULL);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (6,  'S_TCODE',    'TCD',      'SCCL', 'SCC9', NULL, NULL, 2);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (7,  'S_DATASET',  'PROGRAM',  '"*"',   NULL,  NULL, NULL, 2);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (8,  'S_DATASET',  'ACTVT',    '"*"',   NULL,  NULL, NULL, 2);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (9,  'S_DATASET',  'FILENAME', '"*"',   NULL,  NULL, NULL, 2);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (10, 'S_CLNT_IMP', 'ACTVT',    '60',    NULL,  NULL, NULL, 2);

-- use case with pattern condition (OR linkage, use case 2.B from examples)
INSERT INTO AccessPatterns (id, isArchived, createdAt, createdBy, usecaseId, description, linkage) VALUES (3, 0, '2018-06-08T15:09:15', 'test', '2.B', 'Unexpected users with unrestricted access to workbench components', 'Or');
INSERT INTO AccessPatternConditions (id) VALUES (3);
INSERT INTO AccessConditions (id, type, patternId, patternConditionId, ProfileConditionId) VALUES (4, 'Pattern', 3, 3, NULL);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (11, 'S_DEVELOP', 'DEVCLASS', '"*"', NULL, NULL, NULL, 3);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (12, 'S_DEVELOP', 'OBJTYPE',  '"*"', NULL, NULL, NULL, 3);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (13, 'S_DEVELOP', 'OBJNAME',  '"*"', NULL, NULL, NULL, 3);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (14, 'S_DEVELOP', 'P_GROUP',  '"*"', NULL, NULL, NULL, 3);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (15, 'S_DEVELOP', 'ACTVT',    '"*"', NULL, NULL, NULL, 3);
INSERT INTO AccessPatternConditions (id) VALUES (4);
INSERT INTO AccessConditions (id, type, patternId, patternConditionId, ProfileConditionId) VALUES (5, 'Pattern', 3, 4, NULL);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (16, 'S_DEVELOP', 'DEVCLASS', '*',     NULL,   NULL, NULL, 4);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (17, 'S_DEVELOP', 'OBJTYPE',  'DEBUG', 'PROG', NULL, NULL, 4);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (18, 'S_DEVELOP', 'OBJNAME',  '*',     NULL,   NULL, NULL, 4);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (19, 'S_DEVELOP', 'P_GROUP',  '*',     NULL,   NULL, NULL, 4);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (20, 'S_DEVELOP', 'ACTVT',    '01',    '02',   NULL, NULL, 4);

-- ==============================
--      use cases (archived)
-- ==============================

-- use case with profile condition (NONE linkage, use case 3.A from examples)
INSERT INTO AccessPatterns (id, isArchived, createdAt, createdBy, usecaseId, description, linkage) VALUES (4, 1, '2018-06-08T15:09:15', 'test', '3.A', 'test description', 'None');
INSERT INTO AccessProfileConditions (id, profile) VALUES (2, 'SAP_ALL');
INSERT INTO AccessConditions (id, type, patternId, patternConditionId, ProfileConditionId) VALUES (6, 'Profile', 4, NULL, 2);

-- use case with pattern condition (AND linkage, use case 1.A from examples)
INSERT INTO AccessPatterns (id, isArchived, createdAt, createdBy, usecaseId, description, linkage) VALUES (5, 1, '2018-06-08T15:09:15', 'test', '1.A', 'Unexpected users are authorized to copy a client (local copy wo user/profiles)', 'And');
INSERT INTO AccessPatternConditions (id) VALUES (5);
INSERT INTO AccessConditions (id, type, patternId, patternConditionId, ProfileConditionId) VALUES (7, 'Pattern', 5, 5, NULL);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (21, 'S_TCODE',    'TCD',        'SCCL', 'SCC9', NULL, NULL, 5);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (22, 'S_ADMI_FCD', 'S_ADMI_FCD', 'T000',  NULL,  NULL, NULL, 5);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (23, 'S_TABU_DIS', 'ACTVT',      '02',    NULL,  NULL, NULL, 5);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (24, 'S_TABU_DIS', 'DICBERCLS',  '"*"',   NULL,  NULL, NULL, 5);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (25, 'S_TABU_CLI', 'CLIIDMAINT', 'X',     NULL,  NULL, NULL, 5);
INSERT INTO AccessPatternConditions (id) VALUES (6);
INSERT INTO AccessConditions (id, type, patternId, patternConditionId, ProfileConditionId) VALUES (8, 'Pattern', 5, 6, NULL);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (26,  'S_TCODE',    'TCD',      'SCCL', 'SCC9', NULL, NULL, 6);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (27,  'S_DATASET',  'PROGRAM',  '"*"',   NULL,  NULL, NULL, 6);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (28,  'S_DATASET',  'ACTVT',    '"*"',   NULL,  NULL, NULL, 6);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (29,  'S_DATASET',  'FILENAME', '"*"',   NULL,  NULL, NULL, 6);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (30,  'S_CLNT_IMP', 'ACTVT',    '60',    NULL,  NULL, NULL, 6);

-- use case with pattern condition (OR linkage, use case 2.B from examples)
INSERT INTO AccessPatterns (id, isArchived, createdAt, createdBy, usecaseId, description, linkage) VALUES (6, 1, '2018-06-08T15:09:15', 'test', '2.B', 'Unexpected users with unrestricted access to workbench components', 'Or');
INSERT INTO AccessPatternConditions (id) VALUES (7);
INSERT INTO AccessConditions (id, type, patternId, patternConditionId, ProfileConditionId) VALUES (9, 'Pattern', 6, 7, NULL);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (31, 'S_DEVELOP', 'DEVCLASS', '"*"', NULL, NULL, NULL, 7);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (32, 'S_DEVELOP', 'OBJTYPE',  '"*"', NULL, NULL, NULL, 7);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (33, 'S_DEVELOP', 'OBJNAME',  '"*"', NULL, NULL, NULL, 7);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (34, 'S_DEVELOP', 'P_GROUP',  '"*"', NULL, NULL, NULL, 7);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (35, 'S_DEVELOP', 'ACTVT',    '"*"', NULL, NULL, NULL, 7);
INSERT INTO AccessPatternConditions (id) VALUES (8);
INSERT INTO AccessConditions (id, type, patternId, patternConditionId, ProfileConditionId) VALUES (10, 'Pattern', 6, 8, NULL);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (36, 'S_DEVELOP', 'DEVCLASS', '*',     NULL,   NULL, NULL, 8);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (37, 'S_DEVELOP', 'OBJTYPE',  'DEBUG', 'PROG', NULL, NULL, 8);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (38, 'S_DEVELOP', 'OBJNAME',  '*',     NULL,   NULL, NULL, 8);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (39, 'S_DEVELOP', 'P_GROUP',  '*',     NULL,   NULL, NULL, 8);
INSERT INTO AccessPatternConditionProperties (id, authObject, authObjectProperty, value1, value2, value3, value4, conditionId) VALUES (40, 'S_DEVELOP', 'ACTVT',    '01',    '02',   NULL, NULL, 8);
