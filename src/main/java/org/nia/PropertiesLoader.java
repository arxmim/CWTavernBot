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
        return "340515593:AAGnZloyTSkKs90p8_FPdYoL-wVOFA41SvA";
//        return System.getenv("botToken");
    }

    public String getConnectionString() {
        return "jdbc:postgresql://ec2-54-247-99-159.eu-west-1.compute.amazonaws.com:5432/" +
                        "dc2mdupck03c34?user=hltzjztvfqatid&password=" +
                        "763351c1f0d1758070a3a9fcee498ddd61cf1e10c8826a0f49ad8f4066f18f25&sslmode=require&PORT=32766";
//        return System.getenv("JDBC_DATABASE_URL");
    }
}
