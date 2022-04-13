package com.haibin.TimeManager;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class showDailyTodoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_daily_todo);
        this.setTitle("此处根据传入内容显示具体日期");
    }
}

