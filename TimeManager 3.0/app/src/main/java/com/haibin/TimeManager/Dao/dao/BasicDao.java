package com.haibin.TimeManager.Dao.dao;


import com.haibin.TimeManager.Dao.Utils.JDBCUtils;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @Author:raozhao
 * @Date:2022/4/22-04-22-11:11
 * @Description:android.testConnection._dao.dao
 * @version:1.0
 */
public class BasicDao<T> {

    private static QueryRunner qr = new QueryRunner();

    // 开发通用的dml方法
    public static int update(String sql, Object... parameters) {
        Connection connection = null;
        try {
            connection = JDBCUtils.getConnection();
            int update = qr.update(connection, sql, parameters);
            return update;
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        } finally {
            JDBCUtils.close(null, null, connection);
        }
    }

    // 返回查询的多行结果
    public List<T> queryMulti(String sql, Class<T> clazz, Object... parameters) {
        Connection connection = null;
        try {
            connection = JDBCUtils.getConnection();
            return qr.query(connection, sql, new BeanListHandler<>(clazz), parameters);
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        } finally {
            JDBCUtils.close(null, null, connection);
        }
    }


    // 查询单行结果
    public T querySingle(String sql, Class<T> clazz, Object... parameters) {
        Connection connection = null;
        try {
            connection = JDBCUtils.getConnection();
            return qr.query(connection, sql, new BeanHandler<>(clazz), parameters);
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        } finally {
            JDBCUtils.close(null, null, connection);
        }
    }


    public Object queryScalar(String sql, Object... parameters) {
        Connection connection = null;
        try {
            connection = JDBCUtils.getConnection();
            return qr.query(connection, sql, new ScalarHandler(), parameters);
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        } finally {
            JDBCUtils.close(null, null, connection);
        }
    }


}
