package org.nia.db;

/**
 * @author IANazarov
 */
class DBStrings {
    static final String createProfileTable = "if not exists (select * from sysobjects where name='cwo_Profile' and xtype='U')\n" +
            "create table cwo_Profile (\n" +
            "PublicID bigint NOT NULL IDENTITY(1,1) PRIMARY KEY, \n" +
            "UserID INTEGER NOT NULL, \n" +
            "Name VARCHAR(1000) NOT NULL, \n" +
            "Castle  VARCHAR(100) NOT NULL, \n" +
            "Lvl INTEGER NOT NULL, \n" +
            "Exp INTEGER, \n" +
            "Team VARCHAR(1000), \n" +
            "Atk INTEGER NOT NULL, \n" +
            "Def INTEGER NOT NULL, \n" +
            "Gold INTEGER, \n" +
            "Crystalls INTEGER, \n" +
            "Stamina INTEGER, \n" +
            "Bag INTEGER)";
    static final String createEquipmentTable = "if not exists (select * from sysobjects where name='cwo_Equipment' and xtype='U')\n" +
            "create table cwo_Equipment (\n" +
            "PublicID bigint NOT NULL IDENTITY(1,1) PRIMARY KEY, \n" +
            "ProfileName VARCHAR(1000) NOT NULL, \n" +
            "Name varchar(200) NOT NULL, \n" +
            "Atk INTEGER NOT NULL, \n" +
            "Def INTEGER NOT NULL)";
    static final String createBattleStatTable = "if not exists (select * from sysobjects where name='cwo_BattleStat' and xtype='U')\n" +
            "create table cwo_BattleStat (\n" +
            "PublicID bigint NOT NULL IDENTITY(1,1) PRIMARY KEY, \n" +
            "UserID INTEGER NOT NULL, \n" +
            "Name VARCHAR(1000) NOT NULL, \n" +
            "Castle  VARCHAR(100) NOT NULL, \n" +
            "ReportDate datetime NOT NULL, \n" +
            "BattleDate datetime NOT NULL, \n" +
            "Target varchar(100), \n" +
            "atk INTEGER NOT NULL, \n" +
            "def INTEGER NOT NULL, \n" +
            "lvl INTEGER NOT NULL, \n" +
            "exp INTEGER, \n" +
            "gold INTEGER, \n" +
            "equip varchar(200))";
    static final String createDonatorTable = "if not exists (select * from sysobjects where name='cwo_Donator' and xtype='U')\n" +
            "create table cwo_Donator (\n" +
            "PublicID bigint NOT NULL IDENTITY(1,1) PRIMARY KEY, \n" +
            "Name VARCHAR(1000) NOT NULL, \n" +
            "Resource varchar(100) NOT NULL, \n" +
            "Count INTEGER NOT NULL)";
}
