package com.haibin.TimeManager.Statistics;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.haibin.TimeManager.Pomodoro.Clock_Database;
import com.haibin.TimeManager.Pomodoro.PomodoroActivity;
import com.haibin.TimeManager.R;
import com.haibin.TimeManager.Todo.Todo;
import com.haibin.TimeManager.calendar.full.FullActivity;
import com.haibin.TimeManager.showActivity;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        ImageButton button_todo = (ImageButton)findViewById(R.id.button_todo);
        button_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity.this, showActivity.class);
                startActivity(intent);
            }
        });

        ImageButton button_calendar = (ImageButton)findViewById(R.id.button_calendar);
        button_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity.this, FullActivity.class);
                startActivity(intent);
            }
        });

        ImageButton button_clock = (ImageButton)findViewById(R.id.button_clock);
        button_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity.this, PomodoroActivity.class);
                startActivity(intent);
            }
        });

        ImageButton button_statistics = (ImageButton)findViewById(R.id.button_statistics);
        button_statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity.this, StatisticsActivity.class);
                startActivity(intent);
            }
        });
        button_statistics.setBackgroundColor(Color.parseColor("#D7D7D7"));



        java.util.Calendar cld= java.util.Calendar.getInstance(Locale.CHINA);//创建一个日历
        List<Todo> mToDoList;
        int sevenDayTodoNum = 0;
        for(int i = 6 ; i >= 0 ; i--){
            String curDate = Integer.toString(cld.get(java.util.Calendar.YEAR))+'/'+
                    String.format("%02d",cld.get(java.util.Calendar.MONTH)+1)+'/'+
                    String.format("%02d",cld.get(java.util.Calendar.DAY_OF_MONTH));


            mToDoList= LitePal.where("is_delete = ? and date=? and is_done = ?", "0",curDate, "1").find(Todo.class);
            sevenDayTodoNum += mToDoList.size();
            cld.add(Calendar.DAY_OF_MONTH,-1);
        }

        TextView sevenDaysTodo = findViewById(R.id.sevenDayTodo);
        sevenDaysTodo.setText(String.valueOf(sevenDayTodoNum));

        mToDoList= LitePal.where("is_delete = ? and is_done = ?", "0", "1").find(Todo.class);
        TextView totalTodo = findViewById(R.id.TotalTodo);
        totalTodo.setText(String.valueOf(mToDoList.size()));


        cld= java.util.Calendar.getInstance(Locale.CHINA);//创建一个日历
        List<Clock_Database> mTomatoList;
        long mils = 0;
        for(int i = 6 ; i >= 0 ; i--){
            String curDate = Integer.toString(cld.get(java.util.Calendar.YEAR))+'/'+
                    String.valueOf(cld.get(java.util.Calendar.MONTH)+1)+'/'+
                    String.valueOf(cld.get(java.util.Calendar.DAY_OF_MONTH));

            mTomatoList= LitePal.where("state = ? and date=?", "1",curDate).find(Clock_Database.class);
            for(int j = 0 ; j < mTomatoList.size(); j++){
                mils += mTomatoList.get(j).getTime();
            }
            cld.add(Calendar.DAY_OF_MONTH,-1);
        }

        TextView sevenDaysTomato = findViewById(R.id.sevenDayTomato);
        if(mils < 216000000){
            sevenDaysTomato.setText(String.valueOf(mils / 60000) + "min");
        }else{
            sevenDaysTomato.setText(String.valueOf(mils * 1.0 / 216000000) + "h");
        }

        mTomatoList = LitePal.where("state = ? ", "1").find(Clock_Database.class);
        mils = 0;
        for(int i = 0 ; i < mTomatoList.size(); i++){
            mils += mTomatoList.get(i).getTime();
        }
        TextView totalTomato = findViewById(R.id.TotalTomato);

        if(mils < 216000000){
            totalTomato.setText(String.valueOf(mils / 60000) + "min");
        }else{
            totalTomato.setText(String.format("%.1f",mils * 1.0 / 216000000) + "h");
        }
    }
}