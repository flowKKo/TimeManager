package com.haibin.TimeManager.Dao.domin;

/**
 * @Author:raozhao
 * @Date:2022/4/22-04-22-14:56
 * @Description:android.testConnection._dao.domin
 * @version:1.0
 */
public class Android_todo {
    private String userName;// 用户名
    private int id;//todo的id
    private String todo;//待办事项
    private String date;//待办事项时间
    private String create_date;//todo创建时间
    private int is_clock;//是否提醒
    private String time;//提醒时间
    private int is_done;//是否完成
    private int is_delete;//是否被删除
    private int pos;


    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }


    public int getIs_clock() {
        return is_clock;
    }

    public void setIs_clock(int is_clock) {
        this.is_clock = is_clock;
    }

    public int getIs_done() {
        return is_done;
    }

    public void setIs_done(int is_done) {
        this.is_done = is_done;
    }

    public int getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(int is_delete) {
        this.is_delete = is_delete;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    @Override
    public String toString() {
        return "Android_todo{" +
                "userName='" + userName + '\'' +
                ", id=" + id +
                ", todo='" + todo + '\'' +
                ", date='" + date + '\'' +
                ", create_date='" + create_date + '\'' +
                ", is_clock=" + is_clock +
                ", time='" + time + '\'' +
                ", is_done=" + is_done +
                ", is_delete=" + is_delete +
                '}';
    }
}
