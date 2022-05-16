package com.haibin.TimeManager.Dao.Function;

import android.util.Log;
import android.view.View;

import com.haibin.TimeManager.Dao.dao.Local_userDao;
import com.haibin.TimeManager.Dao.dao.TodoDao;
import com.haibin.TimeManager.Dao.domin.Android_todo;
import com.haibin.TimeManager.Todo.Local_user;
import com.haibin.TimeManager.Todo.Todo;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Cloud_function {
    public static int backUp() throws ExecutionException, InterruptedException {
        int a = 0;
        ExecutorService executor = Executors.newFixedThreadPool(2);
        BackUp backUp = new BackUp();
        Future<Integer> future = executor.submit(backUp);
        a = (int) future.get();
        return a;
    }

    public static int recovery() throws ExecutionException, InterruptedException {
        int a = 0;
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Recovery recovery = new Recovery();
        Future<Integer> future = executor.submit(recovery);
        a = (int) future.get();
        return a;
    }

    public static int add_a_user(String userName, String password) throws ExecutionException, InterruptedException {
        int a = 0;
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Add_a_user add_a_user = new Add_a_user(userName, password);
        Future<Integer> future = executor.submit(add_a_user);
        a = (int) future.get();
        return a;
    }

    public static int login(String userName, String password) throws ExecutionException, InterruptedException {
        int a = 0;
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Login_or_exchange login_or_exchange = new Login_or_exchange(userName, password);
        Future<Integer> future = executor.submit(login_or_exchange);
        a = (int) future.get();
        return a;
    }

    public static void clear(){



    }

}
