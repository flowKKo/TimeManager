package com.haibin.TimeManager.Dao.Function;

import com.haibin.TimeManager.Dao.dao.Local_userDao;
import com.haibin.TimeManager.Dao.dao.UserDao;
import com.haibin.TimeManager.Dao.domin.Android_user;
import com.haibin.TimeManager.Todo.Local_user;

import org.litepal.LitePal;

import java.util.List;
import java.util.concurrent.Callable;

public class Login_or_exchange implements Callable<Integer> {
    public String userName = null;
    public String password = null;

    public Login_or_exchange(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    @Override
    public Integer call() throws Exception {
        // 准备工作，把两张表都拿到。
        int a = 0;
        List<Android_user> android_users = UserDao.queryAll();
        List<Local_user> local_users = LitePal.findAll(Local_user.class);
        String target_password = "";

        // 先查本地表，如果本地表有，就改个人名就可以了
        for (Local_user local_user : local_users) {
            String currName = local_user.getUserName();
            if (currName.equals(userName)) {
                Local_userDao.set_true(userName);
                return 1;
            }
        }
        // 如果本地表没有就去云端找，如果云端没有。。
        for (Android_user android_user : android_users) {
            String currName = android_user.getUserName();
            if (currName.equals(userName)) {
                a = 1;
                String password1 = android_user.getPassword();
                target_password = password1;
            }
        }
        if (a == 0) {
            return a;
        } else {
            // 如果云端有，比较记录,如果是对的就把这记录加到本地表中，并 setTure
            if (target_password.equals(password)) {
                Local_userDao.Add_a_user(userName, password);
                return a;
            } else {
                return -1;
            }
        }
    }
}
