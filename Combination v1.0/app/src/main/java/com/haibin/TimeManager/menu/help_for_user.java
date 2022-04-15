package com.haibin.TimeManager.menu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haibin.TimeManager.Adapter.RecyclerViewAdapter;
import com.haibin.TimeManager.Pomodoro.PomodoroActivity;
import com.haibin.TimeManager.R;
import com.haibin.TimeManager.Statistics.StatisticsActivity;
import com.haibin.TimeManager.calendar.full.FullActivity;
import com.haibin.TimeManager.showActivity;

import java.util.ArrayList;
import java.util.List;

public class help_for_user extends AppCompatActivity {
    protected Toolbar mToolbar;
    private List<Img> ImgList = new ArrayList<>();
    RecyclerViewAdapter adapter;
    LinearLayoutManager manager;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w("TAG_New","enter help -1");
        super.onCreate(savedInstanceState);
        Log.w("TAG_New","enter help 0");
        setContentView(R.layout.activity_help_for_user);
        Log.w("TAG_New","enter help 1");
        mToolbar=findViewById(R.id.toolbar_help);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.w("TAG_New","enter help 2");
        InitImg();
        recyclerView = findViewById(R.id.recyclerview_help);
        Log.w("TAG_New","enter help 3");
        manager = new LinearLayoutManager(help_for_user.this);
        recyclerView.setLayoutManager(manager);
        Log.w("TAG_New","enter help 4");
        adapter = new RecyclerViewAdapter(ImgList);
        recyclerView.setAdapter(adapter);
        Log.w("TAG_New","enter help 5");
        adapter.notifyDataSetChanged(ImgList);
        Log.w("TAG_New","enter help over");


        ImageButton button_todo = (ImageButton)findViewById(R.id.button_todo);
        button_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(help_for_user.this, showActivity.class);
                startActivity(intent);
            }
        });

        ImageButton button_calendar = (ImageButton)findViewById(R.id.button_calendar);
        button_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(help_for_user.this, FullActivity.class);
                startActivity(intent);
            }
        });

        ImageButton button_clock = (ImageButton)findViewById(R.id.button_clock);
        button_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(help_for_user.this, PomodoroActivity.class);
                startActivity(intent);
            }
        });

        ImageButton button_statistics = (ImageButton)findViewById(R.id.button_statistics);
        button_statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(help_for_user.this, StatisticsActivity.class);
                startActivity(intent);
            }
        });



    }
    private void InitImg() {
        Img pic_01=new Img(R.drawable.help_1);
        ImgList.add(pic_01);
        Img pic_02=new Img(R.drawable.help_2);
        ImgList.add(pic_02);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

}