package org.nia.db;

/**
 * @author IANazarov
 */
class DBStrings {
    static final String createUserTable = "if not exists (select * from sysobjects where name='cwt_User' and xtype='U')\n" +
            "create table cwt_User (\n" +
            "UserID INTEGER NOT NULL PRIMARY KEY, \n" +
            "Name VARCHAR(200) NOT NULL, \n" +
            "nick VARCHAR(200) NOT NULL, \n" +
            "lastDrinkTime DATETIME, \n" +
            "isBarmen  bit NOT NULL DEFAULT 0, \n" +
            "isAdmin  bit NOT NULL DEFAULT 0, \n" + //TODO
            "alkoCount INTEGER NOT NULL, \n" +
            "drinkType varchar(50), \n" +
            "wanted varchar(50), \n" +
            "gold integer NOT NULL DEFAULT 0, \n" + //TODO
            "visitTavern DATETIME, \n" + //TODO
            "location varchar(50) NOT NULL, \n" + //TODO
            "locationReturnTime DATETIME, \n" + //TODO
            "drinkedTotal INTEGER NOT NULL)";
    static final String createUserPrefTable = "if not exists (select * from sysobjects where name='cwt_DrinkPrefs' and xtype='U')\n" +
            "create table cwt_DrinkPrefs (\n" +
            "PublicID INTEGER NOT NULL IDENTITY(1,1) PRIMARY KEY, \n" +
            "UserID INTEGER NOT NULL, \n" +
            "drinkType varchar(50), \n" +
            "toDrinkCount INTEGER, \n" +
            "toThrowCount INTEGER, \n" +
            "toBeThrownCount INTEGER)";
    static final String createTournamentTable = "if not exists (select * from sysobjects where name='cwt_Tournament' and xtype='U')\n" +
            "create table cwt_Tournament (\n" +
            "PublicID INTEGER NOT NULL IDENTITY(1,1) PRIMARY KEY, \n" +
            "RegistrationDateTime DATETIME NOT NULL, \n" +
            "TournamentType varchar(50), \n" +
            "TournamentState varchar(50), \n" +
            "MaxUsers INTEGER NOT NULL, \n" +
            "Winner INTEGER, \n" +
            "Round INTEGER DEFAULT 0)";
    static final String createTournamentUsersTable = "if not exists (select * from sysobjects where name='cwt_TournamentUsers' and xtype='U')\n" +
            "create table cwt_TournamentUsers (\n" +
            "PublicID INTEGER NOT NULL IDENTITY(1,1) PRIMARY KEY, \n" +
            "TournamentID INTEGER NOT NULL, \n" +
            "UserID INTEGER NOT NULL, \n" +
            "Position INTEGER NOT NULL, \n" +
            "Round INTEGER DEFAULT 1, \n" +
            "Score INTEGER DEFAULT 0, \n" +
            "InFight bit DEFAULT 0, \n" +
            "lose bit DEFAULT 0)";
}
