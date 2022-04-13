package com.haibin.TimeManager.Todo;

public class Time {
    public int hour;//小时
    public int minute;//分钟

    public Time(int hour,int minute){
        this.hour=hour;
        this.minute=minute;
    }

    public String tostring() {
        return String.format("%02d", hour) + ':' +
                String.format("%02d", minute);
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
