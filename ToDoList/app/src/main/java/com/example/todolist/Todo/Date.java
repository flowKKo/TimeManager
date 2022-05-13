package com.haibin.TimeManager.Todo;

public class Date {
    public int year;//年
    public int month;//月
    public int day;//日

    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public void SetDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public void increase() {//自增一天
        day++;
        if (month == 2) {//2月
            if (year % 4 == 0) {//闰年
                if (day == 30) {
                    month++;
                    day = 1;
                }
            } else {//非闰年
                if (day == 29) {
                    month++;
                    day = 1;
                }
            }
        } else if ((month % 2 == 0 && month < 8) || (month % 2 == 1 && month >= 8)) {//30天
            if (day == 31) {
                month++;
                day = 1;
            }
        } else {//31天
            if (day == 32) {
                month++;
                day = 1;
            }
        }
        if (month == 13) {
            month = 1;
            year++;
        }
    }

    public boolean LessEqual(Date rhs) {//日期比较
        if (year < rhs.year) return true;
        else if (year > rhs.year) return false;
        if (month < rhs.month) return true;
        else if (month > rhs.month) return false;
        if (day <= rhs.day) return true;
        else return false;
    }

    public String tostring() {//转为字符串
        return Integer.toString(year) + '/' +
                String.format("%02d", month) + '/' +
                String.format("%02d", day);
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

