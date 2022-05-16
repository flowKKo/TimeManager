package com.haibin.TimeManager.Dao.Function;

import com.haibin.TimeManager.Dao.dao.Local_userDao;
import com.haibin.TimeManager.Dao.dao.UserDao;
import com.haibin.TimeManager.Dao.domin.Android_user;

import java.util.concurrent.Callable;

public class Add_a_user implements Callable<Integer> {
    public String userName = null;
    public String password = null;

    public Add_a_user(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    @Override
    public Integer call() throws Exception {
        // 1.先看这个名字有没有在云端的表上重复
        int a = UserDao.check(userName);
        // 2. 没有的话在云端和本地都添加一条user和password的信息
        if (a == 0) {
            return 0;
        } else {
            // 本地加一张表
            Local_userDao.Add_a_user(userName, password);

            // 云端加一张表
            UserDao.add_user(userName, password);

        }
        return 1;
    }
}
