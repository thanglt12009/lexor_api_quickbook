package com.lexor.qbsa.repository;

import com.lexor.qbsa.beanhandler.CompanyConfigHandler;
import com.lexor.qbsa.domain.CompanyConfig;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

public class CompanyConfigRepository extends BaseRepository<CompanyConfig> {

    @Override
    public CompanyConfig get(Integer id) throws SQLException {
        QueryRunner queryRunner = new QueryRunner();
        ResultSetHandler<List<CompanyConfig>> resultHandler = new CompanyConfigHandler();

        List<CompanyConfig> empList = queryRunner.query(connection, "SELECT * FROM \"CompanyConfig\" WHERE \"id\" = ?", resultHandler, id);
        if (empList.size() > 0) {
            return empList.get(0);
        }
        return null;
    }

    public CompanyConfig findByRealmId(String realmId) throws SQLException {
        QueryRunner queryRunner = new QueryRunner();
        ResultSetHandler<List<CompanyConfig>> resultHandler = new CompanyConfigHandler();

        List<CompanyConfig> empList = queryRunner.query(connection, "SELECT * FROM \"CompanyConfig\" WHERE \"realmId\" = ?", resultHandler, realmId);
        if (empList.size() > 0) {
            return empList.get(0);
        }
        return null;
    }

    @Override
    public int persist(Object o) throws SQLException {
        CompanyConfig c = (CompanyConfig) o;
        QueryRunner runner = new QueryRunner();
        String insertSQL
                = "INSERT INTO \"CompanyConfig\" (\"realmId\", \"accessToken\", \"accessTokenSecret\", \"webhooksSubscribedEntites\", \"lastCdcTimestamp\", \"oauth2BearerToken\") VALUES (?, ?, ?, ?, ?, ?)";

        return runner.update(connection, insertSQL, c.getRealmId(), c.getAccessToken(), c.getAccessTokenSecret(), c.getWebhooksSubscribedEntites(), c.getLastCdcTimestamp(), c.getOauth2BearerToken());
    }

    @Override
    public int update(Integer id, Object o) throws SQLException {
        CompanyConfig c = (CompanyConfig) o;
        QueryRunner runner = new QueryRunner();
        String updateSQL
                = "UPDATE \"CompanyConfig\" "
                + " SET \"realmId\"=?, \"accessToken\"=?, \"accessTokenSecret\"=?, \"webhooksSubscribedEntites\"=?, \"lastCdcTimestamp\"=?, \"oauth2BearerToken\"=? "
                + " WHERE \"id\"=?;";

        return runner.update(connection, updateSQL, c.getRealmId(), c.getAccessToken(), c.getAccessTokenSecret(), c.getWebhooksSubscribedEntites(),c.getLastCdcTimestamp(), c.getOauth2BearerToken(), id);
    }

    @Override
    public int remove(Object o) throws SQLException {
        CompanyConfig c = (CompanyConfig) o;
        QueryRunner runner = new QueryRunner();
        String deleteSQL = "DELETE FROM \"CompanyConfig\" WHERE id = ?;";
        return runner.execute(connection, deleteSQL, c.getId());
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
        ResultSetHandler<List<CompanyConfig>> resultHandler = new CompanyConfigHandler();

        StringBuilder sqlSelect = new StringBuilder("SELECT * FROM \"CompanyConfig\" ");
        StringBuilder sqlWhere = new StringBuilder("");
        StringBuilder sqlLimit = new StringBuilder(" LIMIT ? OFFSET ?");
        List<Object> params = new ArrayList<>();
        if (o != null) {
            CompanyConfig c = (CompanyConfig) o;
            String realmId = c.getRealmId();
            if (realmId != null && realmId.trim().length() > 0) {
                if (realmId.contains("%")) {
                    sqlWhere.append(" LOWER(\"realmId\") LIKE ? ");
                } else {
                    sqlWhere.append(" LOWER(\"realmId\") = ? ");
                }
                params.add(realmId.trim().toLowerCase());
            }
            String accessToken = c.getAccessToken();
            if (accessToken != null && accessToken.trim().length() > 0) {
                if (accessToken.contains("%")) {
                    sqlWhere.append(" LOWER(\"accessToken\") LIKE ? ");
                } else {
                    sqlWhere.append(" LOWER(\"accessToken\") = ? ");
                }
                params.add(accessToken.trim().toLowerCase());
            }
            String accessTokenSecret = c.getAccessTokenSecret();
            if (accessTokenSecret != null && accessTokenSecret.trim().length() > 0) {
                if (accessTokenSecret.contains("%")) {
                    sqlWhere.append(" LOWER(\"accessTokenSecret\") LIKE ? ");
                } else {
                    sqlWhere.append(" LOWER(\"accessTokenSecret\") = ? ");
                }
                params.add(accessTokenSecret.trim().toLowerCase());
            }
            String webhooksSubscribedEntites = c.getWebhooksSubscribedEntites();
            if (webhooksSubscribedEntites != null && webhooksSubscribedEntites.trim().length() > 0) {
                if (webhooksSubscribedEntites.contains("%")) {
                    sqlWhere.append(" LOWER(\"webhooksSubscribedEntites\") LIKE ? ");
                } else {
                    sqlWhere.append(" LOWER(\"webhooksSubscribedEntites\") = ? ");
                }
                params.add(webhooksSubscribedEntites.trim().toLowerCase());
            }
            String lastCdcTimestamp = c.getLastCdcTimestamp();
            if (lastCdcTimestamp != null && lastCdcTimestamp.trim().length() > 0) {
                if (lastCdcTimestamp.contains("%")) {
                    sqlWhere.append(" LOWER(\"lastCdcTimestamp\") LIKE ? ");
                } else {
                    sqlWhere.append(" LOWER(\"lastCdcTimestamp\") = ? ");
                }
                params.add(lastCdcTimestamp.trim().toLowerCase());
            }
            String oauth2BearerToken = c.getOauth2BearerToken();
            if (oauth2BearerToken != null && oauth2BearerToken.trim().length() > 0) {
                if (oauth2BearerToken.contains("%")) {
                    sqlWhere.append(" LOWER(\"oauth2BearerToken\") LIKE ? ");
                } else {
                    sqlWhere.append(" LOWER(\"oauth2BearerToken\") = ? ");
                }
                params.add(oauth2BearerToken.trim().toLowerCase());
            }
        }

        if (params.size() > 0) {
            sqlSelect.append("WHERE");
            sqlSelect.append(sqlWhere);
        }
        sqlSelect.append(sqlLimit);
        params.add(pagesize);
        params.add(start_index);

        List<CompanyConfig> empList = queryRunner.query(connection, sqlSelect.toString(), resultHandler, params.toArray());
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
        StringBuilder sqlSelect = new StringBuilder("SELECT COUNT(0) FROM \"CompanyConfig\" ");
        long count = 0;
        if (o != null) {
            CompanyConfig c = (CompanyConfig) o;
            StringBuilder sqlWhere = new StringBuilder("");
            List<Object> params = new ArrayList<>();
            String realmId = c.getRealmId();
            if (realmId != null && realmId.trim().length() > 0) {
                if (realmId.contains("%")) {
                    sqlWhere.append(" LOWER(\"realmId\") LIKE ? ");
                } else {
                    sqlWhere.append(" LOWER(\"realmId\") = ? ");
                }
                params.add(realmId.trim().toLowerCase());
            }
            String accessToken = c.getAccessToken();
            if (accessToken != null && accessToken.trim().length() > 0) {
                if (accessToken.contains("%")) {
                    sqlWhere.append(" LOWER(\"accessToken\") LIKE ? ");
                } else {
                    sqlWhere.append(" LOWER(\"accessToken\") = ? ");
                }
                params.add(accessToken.trim().toLowerCase());
            }
            String accessTokenSecret = c.getAccessTokenSecret();
            if (accessTokenSecret != null && accessTokenSecret.trim().length() > 0) {
                if (accessTokenSecret.contains("%")) {
                    sqlWhere.append(" LOWER(\"accessTokenSecret\") LIKE ? ");
                } else {
                    sqlWhere.append(" LOWER(\"accessTokenSecret\") = ? ");
                }
                params.add(accessTokenSecret.trim().toLowerCase());
            }
            String webhooksSubscribedEntites = c.getWebhooksSubscribedEntites();
            if (webhooksSubscribedEntites != null && webhooksSubscribedEntites.trim().length() > 0) {
                if (webhooksSubscribedEntites.contains("%")) {
                    sqlWhere.append(" LOWER(\"webhooksSubscribedEntites\") LIKE ? ");
                } else {
                    sqlWhere.append(" LOWER(\"webhooksSubscribedEntites\") = ? ");
                }
                params.add(webhooksSubscribedEntites.trim().toLowerCase());
            }
            String lastCdcTimestamp = c.getLastCdcTimestamp();
            if (lastCdcTimestamp != null && lastCdcTimestamp.trim().length() > 0) {
                if (lastCdcTimestamp.contains("%")) {
                    sqlWhere.append(" LOWER(\"lastCdcTimestamp\") LIKE ? ");
                } else {
                    sqlWhere.append(" LOWER(\"lastCdcTimestamp\") = ? ");
                }
                params.add(lastCdcTimestamp.trim().toLowerCase());
            }
            String oauth2BearerToken = c.getOauth2BearerToken();
            if (oauth2BearerToken != null && oauth2BearerToken.trim().length() > 0) {
                if (oauth2BearerToken.contains("%")) {
                    sqlWhere.append(" LOWER(\"oauth2BearerToken\") LIKE ? ");
                } else {
                    sqlWhere.append(" LOWER(\"oauth2BearerToken\") = ? ");
                }
                params.add(oauth2BearerToken.trim().toLowerCase());
            }

            if (params.size() > 0) {
                sqlSelect.append("WHERE");
                sqlSelect.append(sqlWhere);
            }
            count = runner.query(connection, sqlSelect.toString(), scalarHandler, params.toArray());
        } else {
            count = runner.query(connection, sqlSelect.toString(), scalarHandler);
        }
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
        ResultSetHandler<List<CompanyConfig>> resultHandler = new CompanyConfigHandler();

        List<CompanyConfig> empList = queryRunner.query(connection, "SELECT * FROM \"CompanyConfig\" LIMIT ? OFFSET ?", resultHandler, pagesize, start_index);
        empList.forEach(comp -> {
            list.add((T) comp);
        });
        return list;
    }

    @Override
    public <T> List<T> findAll() throws SQLException {
        List<T> list = new ArrayList<>();
        QueryRunner queryRunner = new QueryRunner();
        ResultSetHandler<List<CompanyConfig>> resultHandler = new CompanyConfigHandler();

        List<CompanyConfig> empList = queryRunner.query(connection, "SELECT * FROM \"CompanyConfig\"", resultHandler);
        empList.forEach(comp -> {
            list.add((T) comp);
        });
        return list;
    }

}
