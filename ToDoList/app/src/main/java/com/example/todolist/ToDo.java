package com.example.todolist;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import org.litepal.crud.LitePalSupport;
import org.litepal.exceptions.DataSupportException;

class Date{
    int year;//年
    int month;//月
    int day;//日
    Date(int year,int month,int day){
        this.year=year;
        this.month=month;
        this.day=day;
    }

}

class Time{
    int hour;//小时
    int minute;//分钟
}


public class ToDo extends LitePalSupport {
    private int id;//todo的id
    private String todo;//待办事项
    private Date date;//待办事项时间
    private Date create_date;//todo创建时间
    private boolean is_clock;//是否提醒
    private Time time;//提醒时间

    public void setId(int id) {
        this.id = id;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public void setIs_clock(boolean is_clock) {
        this.is_clock = is_clock;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getTodo() {
        return todo;
    }

    public Date getDate() {
        return date;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public boolean getIs_clock() {
        return is_clock;
    }

    public Time getTime() {
        return time;
    }
}
