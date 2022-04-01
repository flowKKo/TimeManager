package com.example.todolist;

import org.litepal.LitePal;
import org.litepal.annotation.Column;
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
    void SetDate(int year,int month,int day) {
        this.year=year;
        this.month=month;
        this.day=day;
    }

    void increase(){//自增一天
        day++;
        if(month==2){//2月
            if(year%4==0){//闰年
                if(day==30){
                    month++;
                    day=1;
                }
            }
            else{//非闰年
                if(day==29){
                    month++;
                    day=1;
                }
            }
        }
        else if((month%2==0&&month<8)||(month%2==1&&month>=8)){//30天
            if(day==31){
                month++;
                day=1;
            }
        }
        else{//31天
            if(day==32){
                month++;
                day=1;
            }
        }
        if(month==13){
            month=1;
            year++;
        }
    }

    boolean LessEqual(Date rhs){//日期比较
        if(year<rhs.year) return true;
        else if(year> rhs.year) return false;
        if(month<rhs.month) return true;
        else if(month>rhs.month) return false;
        if(day<=rhs.day) return true;
        else return false;
    }

    public String tostring(){//转为字符串
        return Integer.toString(year)+'/'+
                String.format("%02d",month)+'/'+
                String.format("%02d",day);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}

class Time{
    int hour;//小时
    int minute;//分钟

    public String tostring(){
        return String.format("%02d",hour)+':'+
                String.format("%02d",minute);
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}


public class Todo extends LitePalSupport {
    private int id;//todo的id
    private String todo;//待办事项
    private String date;//待办事项时间
    private String create_date;//todo创建时间
    private boolean is_clock;//是否提醒
    private String time;//提醒时间

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

    public String getTime() {
        return time;
    }
}
