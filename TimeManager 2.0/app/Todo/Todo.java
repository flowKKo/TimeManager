package com.haibin.TimeManager.Todo;

//import org.litepal.crud.DataSupport;
import org.litepal.crud.LitePalSupport;

//class Date extends LitePalSupport{
//    int year;//年
//    int month;//月
//    int day;//日
//    Date(int year,int month,int day){
//        this.year=year;
//        this.month=month;
//        this.day=day;
//    }
//    void SetDate(int year,int month,int day) {
//        this.year=year;
//        this.month=month;
//        this.day=day;
//    }
//
//    public int getYear() {
//        return year;
//    }
//
//    public void setYear(int year) {
//        this.year = year;
//    }
//
//    public int getMonth() {
//        return month;
//    }
//
//    public void setMonth(int month) {
//        this.month = month;
//    }
//
//    public int getDay() {
//        return day;
//    }
//
//    public void setDay(int day) {
//        this.day = day;
//    }
//}
//
//class Time extends LitePalSupport{
//    int hour;//小时
//    int minute;//分钟
//
//    public int getHour() {
//        return hour;
//    }
//
//    public void setHour(int hour) {
//        this.hour = hour;
//    }
//
//    public int getMinute() {
//        return minute;
//    }
//
//    public void setMinute(int minute) {
//        this.minute = minute;
//    }
//}

//数据库部分
public class Todo extends LitePalSupport {
    private int id;//todo的id
    private String todo;//待办事项
    private String date;//待办事项时间
    private String create_date;//todo创建时间
    private boolean is_clock;//是否提醒
    private String time;//提醒时间
    private boolean is_done;//是否完成
    private String is_delete;//是否被删除

    public String isIs_delete() {
        return is_delete;
    }

    public void setIs_delete(String Is_delete) {

        this.is_delete = Is_delete;
    }

    public boolean getIs_done() {
        return is_done;
    }

    public void setIs_done(boolean is_done) {
        this.is_done = is_done;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public void setIs_clock(boolean is_clock) {
        this.is_clock = is_clock;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getTodo() {
        return todo;
    }

    public String getDate() {
        return date;
    }

    public String getCreate_date() {
        return create_date;
    }

    public boolean getIs_clock() {
        return is_clock;
    }

    public Todo(int id, String todo, String date, String create_date, boolean is_clock, String time, boolean is_done, String is_delete) {
        this.id = id;
        this.todo = todo;
        this.date = date;
        this.create_date = create_date;
        this.is_clock = is_clock;
        this.time = time;
        this.is_done = is_done;
        this.is_delete = is_delete;
    }
    public void set(Todo todo){
        this.id=todo.id;
        this.todo=todo.todo;
        this.date=todo.date;
        this.create_date=todo.create_date;
        this.is_clock=todo.is_clock;
        this.time=todo.time;
        this.is_done=todo.is_done;
        this.is_delete=todo.is_delete;
    }
    public String getTime() {
        return time;
    }
    public Todo(){}


}
