package com.haibin.TimeManager.Dao.Function;

import com.haibin.TimeManager.Dao.dao.TodoDao;
import com.haibin.TimeManager.Dao.domin.Android_todo;
import com.haibin.TimeManager.Todo.Local_user;
import com.haibin.TimeManager.Todo.Todo;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class Recovery implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        int a = 0;
        String userName = "";
        List<Local_user> lists = LitePal.findAll(Local_user.class);
        for (Local_user local_user : lists) {
            if (local_user.isLogin() == true) {
                a = 1;
                userName = local_user.getUserName();
            }
        }
        if (a == 0)
            return a;

        LitePal.deleteAll(Todo.class);
        //List<Todo> todoList = LitePal.findAll(Todo.class);
        List<Android_todo> cloud_todos = TodoDao.queryByName(userName);
        if (cloud_todos == null) {
            return 1;
        }
        List<Todo> todoAdd = new ArrayList<>();
        for (int i = 0; i < cloud_todos.size(); i++) {
            Android_todo cloud_todo = cloud_todos.get(i);
            // set one todo
            Todo todo = new Todo();
            todo.setId(cloud_todo.getId());
            todo.setTodo(cloud_todo.getTodo());
            todo.setDate(cloud_todo.getDate());
            todo.setCreate_date(cloud_todo.getCreate_date());
            todo.setIs_clock(cloud_todo.getIs_clock() == 1);
            todo.setTime(cloud_todo.getTime());
            todo.setIs_done(cloud_todo.getIs_done() == 1);
            todo.setIs_delete(cloud_todo.getIs_delete() == 1);
            todo.setPos(cloud_todo.getPos());
            // todo.save();
            // add to todoAdd
            todoAdd.add(todo);
        }
        LitePal.saveAll(todoAdd);

        return a;
    }
}
