package com.lexor.qbsa.repository;

import com.lexor.qbsa.beanhandler.PayloadQueueHandler;
import com.lexor.qbsa.domain.PayloadQueue;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

public class PayloadQueueRepository extends BaseRepository<PayloadQueueHandler> {

    @Override
    public PayloadQueue get(Integer id) throws SQLException {
        QueryRunner queryRunner = new QueryRunner();
        ResultSetHandler<List<PayloadQueue>> resultHandler = new PayloadQueueHandler();

        List<PayloadQueue> empList = queryRunner.query(connection, "SELECT * FROM \"PayloadQueue\" WHERE \"id\" = ?", resultHandler, id);
        if (empList.size() > 0) {
            return empList.get(0);
        }
        return null;
    }

    @Override
    public int persist(Object o) throws SQLException {
        PayloadQueue c = (PayloadQueue) o;
        QueryRunner runner = new QueryRunner();
        String insertSQL
                = "INSERT INTO \"PayloadQueue\" (\"source\", \"payload\", \"status\") VALUES (?, ?, ?)";
        connection.setAutoCommit(false);
        int result = runner.update(connection, insertSQL, c.getSource(), c.getPayload(), c.getStatus());
        connection.commit();
        return result;
    }

    @Override
    public int update(Integer id, Object o) throws SQLException {
        PayloadQueue c = (PayloadQueue) o;
        QueryRunner runner = new QueryRunner();
        String updateSQL
                = "UPDATE \"PayloadQueue\" "
                + " SET \"source\"=?, \"payload\"=?, \"status\"=? "
                + " WHERE \"id\"=?;";
        connection.setAutoCommit(false);
        int result = runner.update(connection, updateSQL, c.getSource(), c.getPayload(), c.getStatus(), id);
        connection.commit();
        return result;
    }

    public int updateStatus(Object o) throws SQLException {
        PayloadQueue c = (PayloadQueue) o;
        QueryRunner runner = new QueryRunner();
        String updateSQL
                = "UPDATE \"PayloadQueue\" "
                + " SET \"status\"=? "
                + " WHERE \"id\"=?;";
        connection.setAutoCommit(false);
        int result = runner.update(connection, updateSQL, c.getStatus(), c.getId());
        connection.commit();
        return result;
    }

    @Override
    public int remove(Object o) throws SQLException {
        PayloadQueue c = (PayloadQueue) o;
        QueryRunner runner = new QueryRunner();
        String deleteSQL = "DELETE FROM \"PayloadQueue\" WHERE id = ?;";
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
        ResultSetHandler<List<PayloadQueue>> resultHandler = new PayloadQueueHandler();

        StringBuilder sqlSelect = new StringBuilder("SELECT * FROM \"PayloadQueue\" ");
        StringBuilder sqlWhere = new StringBuilder("");
        StringBuilder sqlLimit = new StringBuilder(" LIMIT ? OFFSET ?");
        List<Object> params = new ArrayList<>();
        if (o != null) {
            PayloadQueue c = (PayloadQueue) o;
            String source = c.getSource();
            if (source != null && source.trim().length() > 0) {
                if (source.contains("%")) {
                    sqlWhere.append(" LOWER(\"source\") LIKE ? ");
                } else {
                    sqlWhere.append(" LOWER(\"source\") = ? ");
                }
                params.add(source.trim().toLowerCase());
            }
            Integer status = c.getStatus();
            if (status != null) {
                sqlWhere.append(" \"status\" = ? ");
                params.add(status);
            }
        }

        if (params.size() > 0) {
            sqlSelect.append("WHERE");
            sqlSelect.append(sqlWhere);
        }
        sqlSelect.append(sqlLimit);
        params.add(pagesize);
        params.add(start_index);

        List<PayloadQueue> empList = queryRunner.query(connection, sqlSelect.toString(), resultHandler, params.toArray());
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
        StringBuilder sqlSelect = new StringBuilder("SELECT COUNT(0) FROM \"PayloadQueue\" ");
        StringBuilder sqlWhere = new StringBuilder("");
        List<Object> params = new ArrayList<>();
        if (o != null) {
            PayloadQueue c = (PayloadQueue) o;
            String source = c.getSource();
            if (source != null && source.trim().length() > 0) {
                if (source.contains("%")) {
                    sqlWhere.append(" LOWER(\"source\") LIKE ? ");
                } else {
                    sqlWhere.append(" LOWER(\"source\") = ? ");
                }
                params.add(source.trim().toLowerCase());
            }
            Integer status = c.getStatus();
            if (status != null) {
                sqlWhere.append(" \"status\" = ? ");
                params.add(status);
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
        ResultSetHandler<List<PayloadQueue>> resultHandler = new PayloadQueueHandler();

        List<PayloadQueue> empList = queryRunner.query(connection, "SELECT * FROM \"PayloadQueue\" LIMIT ? OFFSET ?", resultHandler, pagesize, start_index);
        empList.forEach(comp -> {
            list.add((T) comp);
        });
        return list;
    }

    @Override
    public <T> List<T> findAll() throws SQLException {
        List<T> list = new ArrayList<>();
        QueryRunner queryRunner = new QueryRunner();
        ResultSetHandler<List<PayloadQueue>> resultHandler = new PayloadQueueHandler();

        List<PayloadQueue> empList = queryRunner.query(connection, "SELECT * FROM \"PayloadQueue\"", resultHandler);
        empList.forEach(comp -> {
            list.add((T) comp);
        });
        return list;
    }

}
