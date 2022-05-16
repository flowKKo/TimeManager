package com.haibin.TimeManager.Dao.dao;

import android.content.ContentValues;

import com.haibin.TimeManager.Todo.Local_user;

import org.litepal.LitePal;

import java.util.List;

public class Local_userDao {
    public static void set_true(String userName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("isLogin", false);
        LitePal.updateAll(Local_user.class, contentValues);

        contentValues.put("isLogin", true);
        LitePal.updateAll(Local_user.class, contentValues, "userName=?", userName);
    }

    public static int check_userName(String userName) {
        int a = 1;
        List<Local_user> all = LitePal.findAll(Local_user.class);
        for (Local_user local_user : all) {
            String _userName = local_user.getUserName();
            if (_userName.equals(userName))
                a = 0;
        }
        return a;
    }

    public static int Add_a_user(String userName, String password) {
        int a = check_userName(userName);
        if (a == 0)
            return 0;
        Local_user local_user = new Local_user(userName, password, false);
        local_user.save();
        set_true(userName);
        return a;
    }


    public static void show_for_debug() {
        List<Local_user> local_users = LitePal.findAll(Local_user.class);
        for (int i = 0; i < local_users.size(); i++) {
            System.out.println(local_users);
        }
    }

    public static void delete_all_user() {
        LitePal.deleteAll(Local_user.class);
    }

    public static String getThisUserName() {
        String userName = "";
        List<Local_user> all = LitePal.findAll(Local_user.class);
        for (Local_user local_user : all) {
            if (local_user.isLogin() == true) {
                return local_user.getUserName();
            }
        }
        return userName;
    }


}
