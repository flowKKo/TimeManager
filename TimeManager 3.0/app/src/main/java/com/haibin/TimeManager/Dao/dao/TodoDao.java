package com.haibin.TimeManager.Dao.dao;

import com.haibin.TimeManager.Dao.Utils.JDBCUtils;
import com.haibin.TimeManager.Dao.domin.Android_todo;
import com.haibin.TimeManager.Todo.Todo;

import org.apache.commons.dbutils.QueryRunner;
import org.litepal.LitePal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class TodoDao extends BasicDao<Android_todo> {

    public static void createTable() throws SQLException {
        Connection connection = JDBCUtils.getConnection();
        QueryRunner queryRunner = new QueryRunner();
        String sql = "create table todo_table(userName varchar(32),id int," +
                "todo varchar(128),date varchar(32),create_date varchar(32) ,is_clocked int," +
                "time varchar(32),is_done int,is_delete int,pos int)";
        queryRunner.update(connection, sql);
        JDBCUtils.close(null, null, connection);
    }

    public static int deleteByUserName(String userName) {
        TodoDao todoDao = new TodoDao();
        String delete_sql = "delete from todo_table where userName=?";
        int i = todoDao.update(delete_sql, userName);
        return i;
    }

    public static void todo_add(Android_todo todo) {
        int _id = todo.getId();
        String _userName = todo.getUserName();
        String _todo = todo.getTodo();
        String _date = todo.getDate();
        String _create_date = todo.getCreate_date();
        int _is_clock = todo.getIs_clock();
        String _time = todo.getTime();
        int _is_done = todo.getIs_done();
        int _is_delete = todo.getIs_delete();
        int _pos = todo.getPos();
        String sql = "insert into todo_table values(?,?,?,?,?,?,?,?,?,?)";
        update(sql, _userName, _id, _todo, _date, _create_date, _is_clock, _time, _is_done, _is_delete, _pos);
    }

    public static int deleteById(int target_id) {
        TodoDao todaoDao = new TodoDao();
        String sql = "delete from todo_table where id=?";
        int i = TodoDao.update(sql, target_id);
        return i;
    }

    public static List<Android_todo> return_byUserName(String userName) {
        TodoDao todoDao = new TodoDao();
        String sql = "select * from todo_table where userName=?";
        return todoDao.queryMulti(sql, Android_todo.class, userName);
    }

    public static List<Android_todo> queryByName(String userName) {
        List<Android_todo> cloud_todos = new ArrayList<>();
        String sql = "select * from todo_table where userName=?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet set = null;

        connection = JDBCUtils.getConnection();
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            set = preparedStatement.executeQuery();
            while (set.next()) {
                Android_todo android_todo = new Android_todo();
                android_todo.setUserName(set.getString(1));
                android_todo.setId(set.getInt(2));
                android_todo.setTodo(set.getString(3));
                android_todo.setDate(set.getString(4));
                android_todo.setCreate_date(set.getString(5));
                android_todo.setIs_clock(set.getInt(6));
                android_todo.setTime(set.getString(7));
                android_todo.setIs_done(set.getInt(8));
                android_todo.setIs_delete(set.getInt(9));
                android_todo.setPos(set.getInt(10));
                cloud_todos.add(android_todo);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtils.close(set, preparedStatement, connection);
            return cloud_todos;
        }
    }

    public static void add_a_cloud_uer() {


    }

}

