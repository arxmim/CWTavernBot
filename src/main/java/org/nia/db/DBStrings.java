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
            "isAdmin  bit NOT NULL, \n" +
            "alkoCount INTEGER NOT NULL, \n" +
            "drinkType varchar(50), \n" +
            "wanted varchar(50), \n" +
            "drinkedToday INTEGER NOT NULL)";
}
