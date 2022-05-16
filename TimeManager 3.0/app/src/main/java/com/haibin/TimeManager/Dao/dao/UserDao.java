package com.haibin.TimeManager.Dao.dao;

import com.haibin.TimeManager.Dao.Utils.JDBCUtils;
import com.haibin.TimeManager.Dao.domin.Android_todo;
import com.haibin.TimeManager.Dao.domin.Android_user;
import com.haibin.TimeManager.Todo.Local_user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao extends BasicDao<Local_user> {
    public static int check(String userName) {
        int a = 1;
        List<Android_user> android_users = queryAll();
        for (Android_user android_user : android_users) {
            String currName = android_user.getUserName();
            if (currName.equals(userName))
                return 0;
        }
        return a;
    }

    public static List<Android_user> queryAll() {
        List<Android_user> cloud_users = new ArrayList<>();
        String sql = "select * from admin";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet set = null;

        connection = JDBCUtils.getConnection();
        try {
            preparedStatement = connection.prepareStatement(sql);
            set = preparedStatement.executeQuery();
            while (set.next()) {
                Android_user android_user = new Android_user(set.getString(1), set.getString(2));
                //android_todo.setUserName(set.getString(1));
                cloud_users.add(android_user);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtils.close(set, preparedStatement, connection);
            return cloud_users;
        }
    }

    public static void add_user(String userName, String password) {
        String sql = "insert into admin values(?,?)";
        UserDao.update(sql, userName, password);
    }


}
