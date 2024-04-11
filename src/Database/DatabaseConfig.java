package Database;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class DatabaseConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("Database/config.properties"))
        {
            if (input == null) {
                throw new IOException();
            }

            properties.load(input);
        } catch (IOException e) {
            System.out.println("Error loading DB properties");
        }
    }

    static String getDbUrl() {

        return properties.getProperty("db.url");
    }

    static String getDbUsername() {
        return properties.getProperty("db.username");
    }

    static String getDbPassword() {
        return properties.getProperty("db.password");
    }
}
