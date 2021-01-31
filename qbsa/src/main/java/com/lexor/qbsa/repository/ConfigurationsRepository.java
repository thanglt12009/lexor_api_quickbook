package com.lexor.qbsa.repository;

import com.lexor.qbsa.beanhandler.ConfigurationsHandler;
import com.lexor.qbsa.domain.Configurations;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

public class ConfigurationsRepository extends BaseRepository<ConfigurationsHandler> {

    @Override
    public Configurations get(Integer id) throws SQLException {
        QueryRunner queryRunner = new QueryRunner();
        ResultSetHandler<List<Configurations>> resultHandler = new ConfigurationsHandler();

        List<Configurations> empList = queryRunner.query(connection, "SELECT * FROM \"Configurations\" WHERE \"id\" = ?", resultHandler, id);
        if (empList.size() > 0) {
            return empList.get(0);
        }
        return null;
    }
    
    public Configurations getByKey(String key) throws SQLException {
        QueryRunner queryRunner = new QueryRunner();
        ResultSetHandler<List<Configurations>> resultHandler = new ConfigurationsHandler();

        List<Configurations> empList = queryRunner.query(connection, "SELECT * FROM \"Configurations\" WHERE \"key\" = ?", resultHandler, key);
        if (empList.size() > 0) {
            return empList.get(0);
        }
        return null;
    }

    @Override
    public int persist(Object o) throws SQLException {
        Configurations c = (Configurations) o;
        QueryRunner runner = new QueryRunner();
        String insertSQL
                = "INSERT INTO \"Configurations\" (\"key\", \"value\") VALUES (?, ?)";
        connection.setAutoCommit(false);
        int result = runner.update(connection, insertSQL, c.getKey(), c.getValue());
        connection.commit();
        return result;
    }

    @Override
    public int update(Integer id, Object o) throws SQLException {
        Configurations c = (Configurations) o;
        QueryRunner runner = new QueryRunner();
        String updateSQL
                = "UPDATE \"Configurations\" "
                + " SET \"key\"=?, \"value\"=? "
                + " WHERE \"id\"=?;";
        connection.setAutoCommit(false);
        int result = runner.update(connection, updateSQL, c.getKey(), c.getValue(), id);
        connection.commit();
        return result;
    }

    @Override
    public int remove(Object o) throws SQLException {
        Configurations c = (Configurations) o;
        QueryRunner runner = new QueryRunner();
        String deleteSQL = "DELETE FROM \"Configurations\" WHERE id = ?;";
        connection.setAutoCommit(false);
        int result = runner.execute(connection, deleteSQL, c.getId());
        connection.commit();
        return result;
    }

    @Override
    public <T> List<T> find(Object o, int[] range) throws SQLException {
        Integer pagesize = range[0];
        Integer pagenum = range[1];
        Integer start_index = 0;
        if (pagenum >= 1) {
            start_index = (pagenum - 1) * pagesize;
        }
        QueryRunner queryRunner = new QueryRunner();
        ResultSetHandler<List<Configurations>> resultHandler = new ConfigurationsHandler();

        StringBuilder sqlSelect = new StringBuilder("SELECT * FROM \"Configurations\" ");
        StringBuilder sqlWhere = new StringBuilder("");
        StringBuilder sqlLimit = new StringBuilder(" LIMIT ? OFFSET ?");
        List<Object> params = new ArrayList<>();
        if (o != null) {
            Configurations c = (Configurations) o;
            String key = c.getKey();
            if (key != null && key.trim().length() > 0) {
                if (key.contains("%")) {
                    sqlWhere.append(" LOWER(\"key\") LIKE ? ");
                } else {
                    sqlWhere.append(" LOWER(\"key\") = ? ");
                }
                params.add(key.trim().toLowerCase());
            }
        }

        if (params.size() > 0) {
            sqlSelect.append("WHERE");
            sqlSelect.append(sqlWhere);
        }
        sqlSelect.append(sqlLimit);
        params.add(pagesize);
        params.add(start_index);

        List<Configurations> empList = queryRunner.query(connection, sqlSelect.toString(), resultHandler, params.toArray());
        List<T> list = new ArrayList<>();
        empList.forEach(comp -> {
            list.add((T) comp);
        });
        return list;
    }

    @Override
    public long count(Object o) throws SQLException {
        ScalarHandler<Long> scalarHandler = new ScalarHandler<>();

        QueryRunner runner = new QueryRunner();
        StringBuilder sqlSelect = new StringBuilder("SELECT COUNT(0) FROM \"Configurations\" ");
        StringBuilder sqlWhere = new StringBuilder("");
        List<Object> params = new ArrayList<>();
        if (o != null) {
            Configurations c = (Configurations) o;
            String key = c.getKey();
            if (key != null && key.trim().length() > 0) {
                if (key.contains("%")) {
                    sqlWhere.append(" LOWER(\"key\") LIKE ? ");
                } else {
                    sqlWhere.append(" LOWER(\"key\") = ? ");
                }
                params.add(key.trim().toLowerCase());
            }
        }
        if (params.size() > 0) {
            sqlSelect.append("WHERE");
            sqlSelect.append(sqlWhere);
        }
        long count = runner.query(connection, sqlSelect.toString(), scalarHandler, params.toArray());
        return count;
    }

    @Override
    public <T> List<T> findPaging(int[] range) throws SQLException {
        Integer pagesize = range[0];
        Integer pagenum = range[1];
        Integer start_index = 0;
        if (pagenum >= 1) {
            start_index = (pagenum - 1) * pagesize;
        }
        List<T> list = new ArrayList<>();
        QueryRunner queryRunner = new QueryRunner();
        ResultSetHandler<List<Configurations>> resultHandler = new ConfigurationsHandler();

        List<Configurations> empList = queryRunner.query(connection, "SELECT * FROM \"Configurations\" LIMIT ? OFFSET ?", resultHandler, pagesize, start_index);
        empList.forEach(comp -> {
            list.add((T) comp);
        });
        return list;
    }

    @Override
    public <T> List<T> findAll() throws SQLException {
        List<T> list = new ArrayList<>();
        QueryRunner queryRunner = new QueryRunner();
        ResultSetHandler<List<Configurations>> resultHandler = new ConfigurationsHandler();

        List<Configurations> empList = queryRunner.query(connection, "SELECT * FROM \"Configurations\"", resultHandler);
        empList.forEach(comp -> {
            list.add((T) comp);
        });
        return list;
    }

}
