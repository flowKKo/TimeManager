package com.haibin.TimeManager.Pomodoro;

import org.litepal.crud.LitePalSupport;

public class Clock_Database extends LitePalSupport {
    private int id;
    private String date;
    private long time;
    private boolean state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean getState()
    {
        return state;
    }

    public void setState(boolean state)
    {
        this.state = state;
    }
}
