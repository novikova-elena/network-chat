package ru.gb.chat.server.auth;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class AuthenticationService {

    private static Connection connection;
    private static Statement statement;
    private static final Logger logger = LogManager.getLogger(AuthenticationService.class);

    public static void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:maindb.db");
        statement = connection.createStatement();
        logger.info("Connection to DB open");
    }

    public static void disconnect() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
            logger.info("Connection to DB closed");
        } catch (SQLException e) {
            logger.throwing(Level.ERROR, e);
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static Statement getStatement() {
        return statement;
    }
}
