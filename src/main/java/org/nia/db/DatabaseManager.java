package org.nia.db;

import org.telegram.telegrambots.logging.BotLogger;

import java.sql.SQLException;

/**
 * @author IANazarov
 */
public class DatabaseManager {
    private static final String LOGTAG = "DATABASEMANAGER";

    private static volatile DatabaseManager instance;
    private volatile ConnectionDB connectionDB;

    public ConnectionDB getConnectionDB() {
        return connectionDB;
    }

    /**
     * Private constructor (due to Singleton)
     */
    private DatabaseManager() {
        connectionDB = new ConnectionDB();
        recreateTable();
    }

    /**
     * Get Singleton instance
     *
     * @return instance of the class
     */
    public static DatabaseManager getInstance() {
        final DatabaseManager currentInstance;
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
                currentInstance = instance;
            }
        } else {
            currentInstance = instance;
        }
        return currentInstance;
    }

    /**
     * Recreates the DB
     */
    private void recreateTable() {
        try {
            connectionDB.initTransaction();
            createNewTables();
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
    }

    private void createNewTables() throws SQLException {
        connectionDB.executeQuery(DBStrings.createProfileTable);
        connectionDB.executeQuery(DBStrings.createEquipmentTable);
        connectionDB.executeQuery(DBStrings.createBattleStatTable);
        connectionDB.executeQuery(DBStrings.createDonatorTable);
    }
}
