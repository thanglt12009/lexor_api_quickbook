package com.lexor.qbsa.repository;

import java.sql.SQLException;
import java.util.List;

public interface Repository {
    public int persist(Object o) throws SQLException;

    public int update(Integer id, Object o) throws SQLException;

    public int remove(Object o) throws SQLException;
    
    public <T extends Object> T get(Integer id) throws SQLException;
    
    public <T> List<T> find(Object o, int[] range) throws SQLException;
    
    public <T> List<T> findPaging(int[] range) throws SQLException;
    
    public <T> List<T> findAll() throws SQLException;
    
    public long count(Object o) throws SQLException;
}
