package org.nia;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author IANazarov
 */
public class PropertiesLoader {
    Properties properties;
    public static PropertiesLoader INSTANCE = new PropertiesLoader();
    private PropertiesLoader() {
        properties = System.getProperties();
    }

    public String getBotToken() {
        return System.getenv("botToken");
    }

    public String getConnectionString() {
        return System.getenv("HEROKU_POSTGRESQL_ONYX_JDBC_URL");
    }
}
