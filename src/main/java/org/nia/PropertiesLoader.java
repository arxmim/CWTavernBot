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
        properties = new Properties();
        try {
            properties.load(new FileInputStream("target/classes/credentials.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            properties = System.getProperties();
        }
    }

    public String getBotToken() {
        return properties.getProperty("token");
    }

    public String getJDBCDriverClassName() {
        return properties.getProperty("jdbcDriver");
    }
    public String getConnectionString() {
        System.out.println("env=" + System.getenv("JDBC_DATABASE_URL"));
        return System.getenv("JDBC_DATABASE_URL");
    }
}
