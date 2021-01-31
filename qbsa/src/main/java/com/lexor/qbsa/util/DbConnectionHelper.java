package com.lexor.qbsa.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import org.jvnet.hk2.annotations.Service;

@Singleton
@Service
public class DbConnectionHelper {

    ConfigHelper config_helper;

    private static Connection connection = null;

    @PostConstruct
    public void init() {
        try {
            this.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(DbConnectionHelper.class.getName()).log(Level.SEVERE, "{0}", ex);
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            config_helper = new ConfigHelper();
            try {
                Class.forName(this.config_helper.getProperties().getProperty("com.lexor.qbsa.database.driver"));
            } catch (ClassNotFoundException err) {
                err.printStackTrace();
            }
            try {
                String url = this.config_helper.getProperties().getProperty("com.lexor.qbsa.database.url");
                Properties props = new Properties();
                props.setProperty("user", this.config_helper.getProperties().getProperty("com.lexor.qbsa.database.user"));
                props.setProperty("password", this.config_helper.getProperties().getProperty("com.lexor.qbsa.database.password"));
                props.setProperty("ssl", this.config_helper.getProperties().getProperty("com.lexor.qbsa.database.ssl"));
                connection = DriverManager.getConnection(url, props);
            } catch (SQLException ex) {
                Logger.getLogger(DbConnectionHelper.class.getName()).log(Level.SEVERE, "{0}", ex);
                connection = null;
            } catch (Exception ex) {
                connection = null;
            }
        }
        return connection;
    }
}
