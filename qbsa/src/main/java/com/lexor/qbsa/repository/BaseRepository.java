package com.lexor.qbsa.repository;

import com.lexor.qbsa.util.DbConnectionHelper;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseRepository<T> implements Repository {

    DbConnectionHelper dbHelper;
    protected Connection connection;

    protected BaseRepository() {
        try {
            this.dbHelper = new DbConnectionHelper();
            this.connection = this.dbHelper.getConnection();
        } catch (Exception ex) {
            Logger.getLogger(BaseRepository.class.getName()).log(Level.SEVERE, "{0}", ex);
        }
    }
}
