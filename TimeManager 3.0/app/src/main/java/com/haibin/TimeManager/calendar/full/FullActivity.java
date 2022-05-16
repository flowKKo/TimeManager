package com.haibin.TimeManager.calendar.full;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.haibin.TimeManager.Pomodoro.PomodoroActivity;
import com.haibin.TimeManager.Statistics.StatisticsActivity;
import com.haibin.TimeManager.Todo.Todo;
import com.haibin.TimeManager.showActivity;
import com.haibin.TimeManager.showDailyTodoActivity;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.haibin.TimeManager.R;
import com.haibin.TimeManager.calendar.base.activity.BaseActivity;

import org.litepal.LitePal;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FullActivity extends BaseActivity implements
        CalendarView.OnCalendarSelectListener{

    TextView mTextMonthDay;
    TextView mTextYear;
    TextView mTextLunar;
    TextView mTextCurrentDay;
    private int mYear;
    CalendarView mCalendarView;
    Calendar current_calendar;

    public static void show(Context context) {
        context.startActivity(new Intent(context, FullActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_full;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView() {

        setStatusBarDarkMode();
        mTextMonthDay = findViewById(R.id.tv_month_day);
        mTextYear = findViewById(R.id.tv_year);
        mTextLunar = findViewById(R.id.tv_lunar);
        mTextCurrentDay = findViewById(R.id.tv_current_day);
        mCalendarView = findViewById(R.id.calendarView);
        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.showYearSelectLayout(mYear);
                mTextLunar.setVisibility(View.GONE);
                mTextYear.setVisibility(View.GONE);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });
        findViewById(R.id.fl_current).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.scrollToCurrent();
            }
        });

        mCalendarView.setOnCalendarSelectListener(this);
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
        mTextLunar.setText("今日");
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));


        ImageButton button_todo = (ImageButton)findViewById(R.id.button_todo);
        button_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FullActivity.this, showActivity.class);
                startActivity(intent);
            }
        });

        ImageButton button_calendar = (ImageButton)findViewById(R.id.button_calendar);
        button_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FullActivity.this, FullActivity.class);
                startActivity(intent);
            }
        });
        button_calendar.setBackgroundColor(Color.parseColor("#D7D7D7"));

        ImageButton button_clock = (ImageButton)findViewById(R.id.button_clock);
        button_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FullActivity.this, PomodoroActivity.class);
                startActivity(intent);
            }
        });

        ImageButton button_statistics = (ImageButton)findViewById(R.id.button_statistics);
        button_statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FullActivity.this, StatisticsActivity.class);
                startActivity(intent);
            }
        });

        current_calendar = mCalendarView.getSelectedCalendar();

        mCalendarView.setRange(1949,10,1,2099,12,31);

    }

    @Override
    protected void initData() {
        int year = mCalendarView.getCurYear();
        int month = mCalendarView.getCurMonth();
        monthDataInit(year, month);
    }

    private void monthDataInit(int year, int month) {
        Map<String, Calendar> map = new HashMap<>();

        // 遍历本月寻找所有存在日程的日期

        java.util.Calendar cld= java.util.Calendar.getInstance(Locale.CHINA);
        cld.set(java.util.Calendar.YEAR, year);
        cld.set(java.util.Calendar.MONTH, month - 1);
        cld.set(java.util.Calendar.DATE, 1);
        cld.roll(java.util.Calendar.DATE, -1);
        int maxDate = cld.get(java.util.Calendar.DATE);
        cld.set(java.util.Calendar.DAY_OF_MONTH, 1);// 从一号开始

        List<Todo> mToDoList;
        String str;
        for(int i = 0 ; i < maxDate ; i++){
            String curDate = Integer.toString(cld.get(java.util.Calendar.YEAR))+'/'+
                    String.format("%02d",cld.get(java.util.Calendar.MONTH)+1)+'/'+
                    String.format("%02d",cld.get(java.util.Calendar.DAY_OF_MONTH));

            str = "";
            mToDoList= LitePal.where("is_done = ? and is_delete = ? and date=? ", "0", "0",curDate).find(Todo.class);
            for(int j = 0 ; j < (Math.min(mToDoList.size() , 3 )); j++){
                if(j == 0){
                    str += mToDoList.get(j).getTodo();
                }else{
                    str += "\n" + mToDoList.get(j).getTodo();
                }
            }

            if(str != ""){
                map.put(getSchemeCalendar(year, month, i+1, 0xFF40db25, str).toString(),
                        getSchemeCalendar(year, month, i+1, 0xFF40db25, str));
            }
            cld.add(java.util.Calendar.DAY_OF_MONTH,1);
        }

        //此方法在巨大的数据量上不影响遍历性能，推荐使用
        mCalendarView.setSchemeDate(map);
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        return calendar;
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();

        if(current_calendar.getMonth() != calendar.getMonth()){
            monthDataInit(calendar.getYear(), calendar.getMonth());
        }
        current_calendar = calendar;

        if(isClick){
            int year = calendar.getYear();
            int month = calendar.getMonth();
            int day = calendar.getDay();
            String currentDate = String.valueOf(year) + "/" + String.format("%02d", month) + "/" +String.format("%02d", day);
            Intent intent = new Intent(FullActivity.this, showDailyTodoActivity.class);
            intent.putExtra("extra_data", currentDate);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        monthDataInit(current_calendar.getYear(), current_calendar.getMonth());
    }
}

