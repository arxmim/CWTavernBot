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
            "lastEatTime DATETIME, \n" +
            "isBarmen  bit NOT NULL DEFAULT 0, \n" +
            "isAdmin  bit NOT NULL DEFAULT 0, \n" +
            "alkoCount INTEGER NOT NULL, \n" +
            "drinkType varchar(50), \n" +
            "wanted varchar(50), \n" +
            "food varchar(50), \n" +
            "foodCount INTEGER NOT NULL, \n" +
            "eatTotal INTEGER NOT NULL, \n" +
            "fightClubWins INTEGER, \n" +
            "brewCount INTEGER, \n" +
            "wantedFood varchar(50), \n" +
            "gold integer NOT NULL DEFAULT 0, \n" +
            "visitTavern DATETIME, \n" +
            "location varchar(50) NOT NULL, \n" +
            //"locationNextEventTime DATETIME, \n" + // TODO
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
    static final String createTournamentBetTable = "if not exists (select * from sysobjects where name='cwt_TournamentBet' and xtype='U')\n" +
            "create table cwt_TournamentBet (\n" +
            "PublicID INTEGER NOT NULL IDENTITY(1,1) PRIMARY KEY, \n" +
            "TournamentID INTEGER NOT NULL, \n" +
            "fromID INTEGER NOT NULL, \n" +
            "toID INTEGER NOT NULL, \n" +
            "sum INTEGER)";
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
    static final String createQuestTable = "if not exists (select * from sysobjects where name='cwt_Quest' and xtype='U')\n" +
            "create table cwt_Quest (\n" +
            "PublicID INTEGER NOT NULL IDENTITY(1,1) PRIMARY KEY, \n" +
            "UserID INTEGER NOT NULL, \n" +
            "QuestName varchar(50), \n" +
            "StartTime DATETIME NOT NULL, \n" +
            "EventTime DATETIME, \n" +
            "ReturnTime DATETIME, \n" +
            "goldEarned INTEGER DEFAULT 0)";
    static final String createQuestEventTable = "if not exists (select * from sysobjects where name='cwt_QuestEvent' and xtype='U')\n" +
            "create table cwt_QuestEvent (\n" +
            "PublicID INTEGER NOT NULL IDENTITY(1,1) PRIMARY KEY, \n" +
            "QuestID INTEGER NOT NULL, \n" +
            "WinChance INTEGER DEFAULT 80, \n" +
            "LinkedQuestEventID INTEGER, \n" +
            "eventName varchar(50), \n" +
            "eventTime DATETIME, \n" +
            "eventStep varchar(50), \n" +
            "win bit)";
    static final String createQuestItemTable = "if not exists (select * from sysobjects where name='cwt_QuestItem' and xtype='U')\n" +
            "create table cwt_QuestItem (\n" +
            "PublicID INTEGER NOT NULL IDENTITY(1,1) PRIMARY KEY, \n" +
            "QuestID INTEGER NOT NULL, \n" +
            "QuestItem varchar(50), \n" +
            "ItemCount INTEGER)";
    static final String createQuestFactTable = "if not exists (select * from sysobjects where name='cwt_QuestFact' and xtype='U')\n" +
            "create table cwt_QuestFact (\n" +
            "PublicID INTEGER NOT NULL IDENTITY(1,1) PRIMARY KEY, \n" +
            "QuestID INTEGER NOT NULL, \n" +
            "QuestFact varchar(50))";
}
