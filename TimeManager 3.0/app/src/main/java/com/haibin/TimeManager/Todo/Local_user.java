package com.haibin.TimeManager.Todo;

import org.litepal.crud.LitePalSupport;

public class Local_user extends LitePalSupport {
    private String userName;
    private String passWord;
    private boolean isLogin;

    public Local_user(String userName, String passWord, boolean isLogin) {
        this.userName = userName;
        this.passWord = passWord;
        this.isLogin = isLogin;
    }

    public Local_user() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

}
