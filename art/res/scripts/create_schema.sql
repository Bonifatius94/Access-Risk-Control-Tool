
-- ========================================
--        create database schema
-- ========================================

-- ========================================
--     create id pools (as sequences)
-- ========================================
CREATE SEQUENCE SYSTEM_SEQUENCE_9C7878CB_9D2D_4DE8_9798_72B896A64B63 CACHE 32;
CREATE SEQUENCE SYSTEM_SEQUENCE_72CA9091_BEC1_4DA7_A292_E5BD72B428B4 CACHE 32;
CREATE SEQUENCE SYSTEM_SEQUENCE_69456BBD_4DCC_403A_BEA0_CFA432C8BCCC CACHE 32;
CREATE SEQUENCE SYSTEM_SEQUENCE_A24A1562_DAA9_4DF5_9373_E976618BAC1C CACHE 32;
CREATE SEQUENCE SYSTEM_SEQUENCE_CEBD2006_1A6D_4CF2_ACF3_C10DF768FDD4 CACHE 32;
CREATE SEQUENCE SYSTEM_SEQUENCE_CF732AAA_AB2B_4AB6_8ECC_3B998D7E9759 CACHE 32;
CREATE SEQUENCE SYSTEM_SEQUENCE_E83BFF70_DAC8_4678_BB1B_689F6538BC84 CACHE 32;
CREATE SEQUENCE SYSTEM_SEQUENCE_F7AD21BC_5519_452C_BE75_01712363638B CACHE 32;
CREATE SEQUENCE SYSTEM_SEQUENCE_F73D0F72_FC43_49EF_BD47_92899F7E5B57 CACHE 32;

-- ========================================
--      create table access patterns
-- ========================================
CREATE CACHED TABLE ACCESSPATTERNS
(
   ID           INTEGER        DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_F73D0F72_FC43_49EF_BD47_92899F7E5B57) NOT NULL
                                       NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_F73D0F72_FC43_49EF_BD47_92899F7E5B57,
   CREATEDAT    TIMESTAMP      NOT NULL,
   CREATEDBY    VARCHAR(255)   NOT NULL,
   DESCRIPTION  CLOB,
   ISARCHIVED   BOOLEAN        NOT NULL,
   LINKAGE      VARCHAR(4),
   USECASEID    VARCHAR(255)   NOT NULL
);

ALTER TABLE ACCESSPATTERNS
   ADD PRIMARY KEY (ID);

-- ========================================
--     create table access conditions
-- ========================================
CREATE CACHED TABLE ACCESSCONDITIONS
(
   ID         INTEGER      DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_CEBD2006_1A6D_4CF2_ACF3_C10DF768FDD4) NOT NULL
                                   NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_CEBD2006_1A6D_4CF2_ACF3_C10DF768FDD4,
   TYPE       VARCHAR(7)   NOT NULL,
   PATTERNID  INTEGER
);

ALTER TABLE ACCESSCONDITIONS
  ADD PRIMARY KEY (ID);

ALTER TABLE ACCESSCONDITIONS
  ADD CONSTRAINT FKSQXNR3AI5QI69VJFS6UXY2L1A FOREIGN KEY (PATTERNID)
  REFERENCES ACCESSPATTERNS (ID)
  ON UPDATE RESTRICT
  ON DELETE RESTRICT;

--CREATE INDEX FKSQXNR3AI5QI69VJFS6UXY2L1A_INDEX_D
--   ON ACCESSCONDITIONS (PATTERNID ASC);

-- ========================================
--  create table access pattern conditions
-- ========================================
CREATE CACHED TABLE ACCESSPATTERNCONDITIONS
(
   CONDITION_ID  INTEGER   NOT NULL
);

ALTER TABLE ACCESSPATTERNCONDITIONS
   ADD PRIMARY KEY (CONDITION_ID);

ALTER TABLE ACCESSPATTERNCONDITIONS
   ADD CONSTRAINT FKT7TDFSEBQIOGQT16YMCD7K6B FOREIGN KEY (CONDITION_ID)
   REFERENCES ACCESSCONDITIONS (ID)
   ON UPDATE RESTRICT
   ON DELETE RESTRICT;

-- ==================================================
--  create table access pattern condition properties
-- ==================================================
CREATE CACHED TABLE ACCESSPATTERNCONDITIONPROPERTIES
(
   ID                  INTEGER        DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_69456BBD_4DCC_403A_BEA0_CFA432C8BCCC) NOT NULL
                                               NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_69456BBD_4DCC_403A_BEA0_CFA432C8BCCC,
   AUTHOBJECT          VARCHAR(255)   NOT NULL,
   AUTHOBJECTPROPERTY  VARCHAR(255)   NOT NULL,
   VALUE1              VARCHAR(255)   NOT NULL,
   VALUE2              VARCHAR(255),
   VALUE3              VARCHAR(255),
   VALUE4              VARCHAR(255),
   CONDITIONID         INTEGER
);

ALTER TABLE ACCESSPATTERNCONDITIONPROPERTIES
   ADD PRIMARY KEY (ID);

ALTER TABLE ACCESSPATTERNCONDITIONPROPERTIES
   ADD CONSTRAINT FK2SSDCMKP9T5MTA8V27JKI5KJF FOREIGN KEY (CONDITIONID)
   REFERENCES ACCESSPATTERNCONDITIONS (CONDITION_ID)
   ON UPDATE RESTRICT
   ON DELETE RESTRICT;

--CREATE INDEX FK2SSDCMKP9T5MTA8V27JKI5KJF_INDEX_6
--   ON ACCESSPATTERNCONDITIONPROPERTIES (CONDITIONID ASC);

-- ========================================
--  create table access profile conditions
-- ========================================
CREATE CACHED TABLE ACCESSPROFILECONDITIONS
(
   PROFILE       VARCHAR(255)   NOT NULL,
   CONDITION_ID  INTEGER        NOT NULL
);

ALTER TABLE ACCESSPROFILECONDITIONS
   ADD PRIMARY KEY (CONDITION_ID);

ALTER TABLE ACCESSPROFILECONDITIONS
   ADD CONSTRAINT FKMJ2WA64RA3DTHE620OVGQV9JA FOREIGN KEY (CONDITION_ID)
   REFERENCES ACCESSCONDITIONS (ID)
   ON UPDATE RESTRICT
   ON DELETE RESTRICT;

-- ========================================
--         create table whitelists
-- ========================================
CREATE CACHED TABLE WHITELISTS
(
   ID           INTEGER        DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_F7AD21BC_5519_452C_BE75_01712363638B) NOT NULL
                                       NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_F7AD21BC_5519_452C_BE75_01712363638B,
   CREATEDAT    TIMESTAMP      NOT NULL,
   CREATEDBY    VARCHAR(255)   NOT NULL,
   DESCRIPTION  CLOB,
   ISARCHIVED   BOOLEAN        NOT NULL,
   NAME         VARCHAR(255)   NOT NULL
);

ALTER TABLE WHITELISTS
   ADD PRIMARY KEY (ID);

-- ========================================
--      create table whitelist entries
-- ========================================
CREATE CACHED TABLE WHITELISTENTRIES
(
   ID           INTEGER        DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_CF732AAA_AB2B_4AB6_8ECC_3B998D7E9759) NOT NULL
                                       NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_CF732AAA_AB2B_4AB6_8ECC_3B998D7E9759,
   USECASEID    VARCHAR(255)   NOT NULL,
   USERNAME     VARCHAR(255)   NOT NULL,
   WHITELISTID  INTEGER
);

ALTER TABLE WHITELISTENTRIES
   ADD PRIMARY KEY (ID);

ALTER TABLE WHITELISTENTRIES
   ADD CONSTRAINT FKD8SV321J1HDAW5RCJ0NN7110O FOREIGN KEY (WHITELISTID)
   REFERENCES WHITELISTS (ID)
   ON UPDATE RESTRICT
   ON DELETE RESTRICT;

--CREATE INDEX FKD8SV321J1HDAW5RCJ0NN7110O_INDEX_7
--   ON WHITELISTENTRIES (WHITELISTID ASC);

-- ========================================
--       create table configurations
-- ========================================
CREATE CACHED TABLE CONFIGURATIONS
(
   ID           INTEGER        DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_9C7878CB_9D2D_4DE8_9798_72B896A64B63) NOT NULL
                                       NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_9C7878CB_9D2D_4DE8_9798_72B896A64B63,
   CREATEDAT    TIMESTAMP      NOT NULL,
   CREATEDBY    VARCHAR(255)   NOT NULL,
   DESCRIPTION  CLOB,
   ISARCHIVED   BOOLEAN        NOT NULL,
   NAME         VARCHAR(255)   NOT NULL,
   WHITELISTID  INTEGER
);

ALTER TABLE CONFIGURATIONS
   ADD PRIMARY KEY (ID);

ALTER TABLE CONFIGURATIONS
   ADD CONSTRAINT FKRNF2FEWI8OD07IJYMP0T179MX FOREIGN KEY (WHITELISTID)
   REFERENCES WHITELISTS (ID)
   ON UPDATE RESTRICT
   ON DELETE RESTRICT;

--CREATE INDEX FKRNF2FEWI8OD07IJYMP0T179MX_INDEX_7
--   ON CONFIGURATIONS (WHITELISTID ASC);

-- ========================================
--       create config X pattern map
-- ========================================
CREATE CACHED TABLE NM_CONFIGURATION_ACCESSPATTERN
(
   CONFIGID         INTEGER   NOT NULL,
   ACCESSPATTERNID  INTEGER   NOT NULL
);

ALTER TABLE NM_CONFIGURATION_ACCESSPATTERN
   ADD PRIMARY KEY (CONFIGID, ACCESSPATTERNID);

ALTER TABLE NM_CONFIGURATION_ACCESSPATTERN
   ADD CONSTRAINT FK143MUV8ELA76XO2SGHAQQV69L FOREIGN KEY (ACCESSPATTERNID)
   REFERENCES ACCESSPATTERNS (ID)
   ON UPDATE RESTRICT
   ON DELETE RESTRICT;

ALTER TABLE NM_CONFIGURATION_ACCESSPATTERN
   ADD CONSTRAINT FKIOGTGKM7R9B182IDBW8VO222N FOREIGN KEY (CONFIGID)
   REFERENCES CONFIGURATIONS (ID)
   ON UPDATE RESTRICT
   ON DELETE RESTRICT;

--CREATE INDEX FK143MUV8ELA76XO2SGHAQQV69L_INDEX_3
--   ON NM_CONFIGURATION_ACCESSPATTERN (ACCESSPATTERNID ASC);

--CREATE INDEX FKIOGTGKM7R9B182IDBW8VO222N_INDEX_3
--   ON NM_CONFIGURATION_ACCESSPATTERN (CONFIGID ASC);

-- ========================================
--     create table sap configurations
-- ========================================
CREATE CACHED TABLE SAPCONFIGURATIONS
(
   ID                 INTEGER        DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_E83BFF70_DAC8_4678_BB1B_689F6538BC84) NOT NULL
                                             NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_E83BFF70_DAC8_4678_BB1B_689F6538BC84,
   CLIENT             VARCHAR(255)   NOT NULL,
   CREATEDAT          TIMESTAMP      NOT NULL,
   CREATEDBY          VARCHAR(255)   NOT NULL,
   DESCRIPTION        CLOB,
   ISARCHIVED         BOOLEAN        NOT NULL,
   LANGUAGE           VARCHAR(255)   NOT NULL,
   POOLCAPACITY       VARCHAR(255)   NOT NULL,
   SERVERDESTINATION  VARCHAR(255)   NOT NULL,
   SYSNR              VARCHAR(255)   NOT NULL
);

ALTER TABLE SAPCONFIGURATIONS
   ADD PRIMARY KEY (ID);

-- ========================================
--   create table critical access queries
-- ========================================
CREATE CACHED TABLE CRITICALACCESSQUERIES
(
   ID           INTEGER        DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_A24A1562_DAA9_4DF5_9373_E976618BAC1C) NOT NULL
                                       NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_A24A1562_DAA9_4DF5_9373_E976618BAC1C,
   CREATEDAT    TIMESTAMP      NOT NULL,
   CREATEDBY    VARCHAR(255)   NOT NULL,
   ISARCHIVED   BOOLEAN        NOT NULL,
   CONFIGID     INTEGER,
   SAPCONFIGID  INTEGER
);

ALTER TABLE CRITICALACCESSQUERIES
   ADD PRIMARY KEY (ID);

ALTER TABLE CRITICALACCESSQUERIES
   ADD CONSTRAINT FKGIX36HMWVCPNFMASB77E7QSL5 FOREIGN KEY (CONFIGID)
   REFERENCES CONFIGURATIONS (ID)
   ON UPDATE RESTRICT
   ON DELETE RESTRICT;

ALTER TABLE CRITICALACCESSQUERIES
   ADD CONSTRAINT FK70MIUNC1DRWFONXN9J0E9MK71 FOREIGN KEY (SAPCONFIGID)
   REFERENCES SAPCONFIGURATIONS (ID)
   ON UPDATE RESTRICT
   ON DELETE RESTRICT;

--CREATE INDEX FKGIX36HMWVCPNFMASB77E7QSL5_INDEX_F
--   ON CRITICALACCESSQUERIES (CONFIGID ASC);

--CREATE INDEX FK70MIUNC1DRWFONXN9J0E9MK71_INDEX_F
--   ON CRITICALACCESSQUERIES (SAPCONFIGID ASC);

-- ========================================
--   create table critical access entries
-- ========================================
CREATE CACHED TABLE CRITICALACCESSENTRIES
(
   ID                 INTEGER        DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_72CA9091_BEC1_4DA7_A292_E5BD72B428B4) NOT NULL
                                             NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_72CA9091_BEC1_4DA7_A292_E5BD72B428B4,
   USERNAME           VARCHAR(255)   NOT NULL,
   VIOLATEDPATTERNID  INTEGER,
   QUERYID            INTEGER
);

ALTER TABLE CRITICALACCESSENTRIES
   ADD PRIMARY KEY (ID);

ALTER TABLE CRITICALACCESSENTRIES
   ADD CONSTRAINT FKN96PH81YROXJ5OSSLRD9JYM57 FOREIGN KEY (VIOLATEDPATTERNID)
   REFERENCES ACCESSPATTERNS (ID)
   ON UPDATE RESTRICT
   ON DELETE RESTRICT;

ALTER TABLE CRITICALACCESSENTRIES
   ADD CONSTRAINT FK6JIC06LJ159QSMBFMHH5DAPJL FOREIGN KEY (QUERYID)
   REFERENCES CRITICALACCESSQUERIES (ID)
   ON UPDATE RESTRICT
   ON DELETE RESTRICT;

--CREATE INDEX FKN96PH81YROXJ5OSSLRD9JYM57_INDEX_7
--   ON CRITICALACCESSENTRIES (VIOLATEDPATTERNID ASC);

--CREATE INDEX FK6JIC06LJ159QSMBFMHH5DAPJL_INDEX_7
--   ON CRITICALACCESSENTRIES (QUERYID ASC);

-- ========================================
--          create table DbUsers
-- ========================================
CREATE TABLE DbUsers
(
   USERNAME         VARCHAR(2147483647)     NOT NULL,
   ISADMIN          BIT                     NOT NULL,
   ISDATAANALYST    BIT                     NOT NULL,
   ISVIEWER         BIT                     NOT NULL,
   ISFIRSTLOGIN     BIT                     NOT NULL,

   PRIMARY KEY (USERNAME)
);

-- ========================================
--        Marco Tröster, 21.06.2018
-- ========================================