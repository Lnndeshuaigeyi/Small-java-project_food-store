package org.example.db;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
public class Db {
        public static Connection getConnection() {
            try {
                Properties props = new Properties();
                try (InputStream in = Db.class.getClassLoader().getResourceAsStream("db.properties")) {
                    if (in == null) {
                        throw new RuntimeException("Cannot find db.properties in src/main/resources");
                    }
                    props.load(in);
                }

                String url = props.getProperty("db.url");
                String user = props.getProperty("db.user");
                String password = props.getProperty("db.password");

                // Optional (but makes driver issues explicit)
                Class.forName("org.postgresql.Driver");

                return DriverManager.getConnection(url, user, password);
            } catch (Exception e) {
                throw new RuntimeException("Failed to get DB connection", e);
            }
        }
    }

