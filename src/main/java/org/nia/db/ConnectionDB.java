package org.nia.db;

import org.nia.PropertiesLoader;
import org.telegram.telegrambots.logging.BotLogger;

import java.sql.*;

/**
 * @author IANazarov
 */
public class ConnectionDB {

    private static final String LINK_DB = PropertiesLoader.INSTANCE.getConnectionString();
    private static final String JDBC_DRIVER = PropertiesLoader.INSTANCE.getJDBCDriverClassName();
    private static final String LOGTAG = "CONNECTIONDB";
    private Connection currentConnection;

    public ConnectionDB() {
        this.currentConnection = openConnection();
    }

    private Connection openConnection() {
        Connection connection = null;
        try {
            Class.forName(JDBC_DRIVER).newInstance();
            connection = DriverManager.getConnection(LINK_DB);
        } catch (SQLException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            BotLogger.error(LOGTAG, e);
        }

        return connection;
    }

    public void closeConnection() {
        try {
            this.currentConnection.close();
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }

    }

    public ResultSet runSqlQuery(String query) throws SQLException {
        final Statement statement;
        statement = this.currentConnection.createStatement();
        return statement.executeQuery(query);
    }

    public Boolean executeQuery(String query) throws SQLException {
        final Statement statement = this.currentConnection.createStatement();
        return statement.execute(query);
    }

    public PreparedStatement getPreparedStatement(String query) throws SQLException {
        return this.currentConnection.prepareStatement(query);
    }

    /**
     * Initilize a transaction in database
     *
     * @throws SQLException If initialization fails
     */
    public void initTransaction() throws SQLException {
        this.currentConnection.setAutoCommit(true);
    }
}
