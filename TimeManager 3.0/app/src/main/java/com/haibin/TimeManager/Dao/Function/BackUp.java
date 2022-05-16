package com.haibin.TimeManager.Dao.Function;

import com.haibin.TimeManager.Dao.dao.Local_userDao;
import com.haibin.TimeManager.Dao.dao.TodoDao;
import com.haibin.TimeManager.Dao.dao.UserDao;
import com.haibin.TimeManager.Dao.domin.Android_todo;
import com.haibin.TimeManager.Dao.domin.Android_user;
import com.haibin.TimeManager.Todo.Local_user;
import com.haibin.TimeManager.Todo.Todo;

import org.litepal.LitePal;

import java.util.List;
import java.util.concurrent.Callable;

public class BackUp implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        List<Local_user> local_users = LitePal.findAll(Local_user.class);
        String userName = "";
        int a = 0;
        // 先查本地表，如果本地表有，就改个人名就可以了
        for (Local_user local_user : local_users) {
            boolean isLog = local_user.isLogin();
            if (isLog == true) {
                a = 1;
                userName = local_user.getUserName();
            }
        }
        if (a == 0) {
            return 0;
        }
        TodoDao.deleteByUserName(userName);
        List<Todo> todoList = LitePal.findAll(Todo.class);
        for (int i = 0; i < todoList.size(); i++) {
            Todo currTodo = todoList.get(i);
            Android_todo android_todo = new Android_todo();
            // set UserName
            android_todo.setUserName(userName);
            // set things
            android_todo.setId(currTodo.getId());
            android_todo.setTodo(currTodo.getTodo());
            android_todo.setDate(currTodo.getDate());
            android_todo.setCreate_date(currTodo.getCreate_date());
            android_todo.setIs_clock(currTodo.getIs_clock() == true ? 1 : 0);
            android_todo.setTime(currTodo.getTime());
            android_todo.setIs_clock(currTodo.getIs_done() == true ? 1 : 0);
            android_todo.setIs_delete(currTodo.isIs_delete()== true ? 1 : 0);
            android_todo.setIs_delete(currTodo.getPos());
            TodoDao.todo_add(android_todo);
        }
        return a;
    }
}
