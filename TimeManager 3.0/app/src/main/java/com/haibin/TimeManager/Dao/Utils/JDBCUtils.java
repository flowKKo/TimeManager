package com.haibin.TimeManager.Dao.Utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * @Author:raozhao
 * @Date:2022/4/22-04-22-11:12
 * @Description:android.testConnection._dao.Utils
 * @version:1.0
 */
public class JDBCUtils {
    private static String user;
    private static String password;
    private static String url;
    private static String driver;

    // 在static代码块去初始化
    static {
        try {
            user = "crz";
            password = "crz";
            url = "jdbc:mysql://124.220.28.218:3306/connection_for_android?rewriteBatchedStatements=true";
            driver = "com.mysql.jdbc.Driver";
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            // 可以选择处理改异常，也可以不管
            throw new RuntimeException(e);
        }
    }

    // connect to the database
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
    }

    public static void close(ResultSet set, Statement statement, Connection connection) {
        try {
            if (set != null) set.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
