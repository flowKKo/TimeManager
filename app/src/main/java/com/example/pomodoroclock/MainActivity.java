package com.example.pomodoroclock;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.CircularArray;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "";
    //    boolean isProcessRunning = false;
//    if (isProcessRunning == true) return;
    boolean isTimeRunning = false, isBreak = false;
//    isProcessRunning = true;
    final static long DEFAULT_WORKING_TIME = 1500000, DEFAULT_BREAK_TIME = 300000;
    static long startTime, breakTime, millisLeft;
    ImageButton resumePauseButton, resetButton;
    CountDownTimer timer;
    ProgressBar timerProgressBar;
    TextView timerText;
    EditText numCount;
    Vibrator vibrator;
    Ringtone ringtone;
    clockDatabaseHelper dbHelper;
    boolean isdone = true; //记录当前番茄钟是否被用完，是则为true，反之为false
    int id = -1; //记录当前番茄钟的编号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (isTimeRunning) return;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new clockDatabaseHelper(this, "Pomodoro_records", null, 1);
        resumePauseButton = findViewById(R.id.resumePauseButton);
        resetButton = findViewById(R.id.resetButton);
        timerProgressBar = findViewById(R.id.progressBar);
        timerText = findViewById(R.id.textView);
        numCount = findViewById(R.id.editTextNumberDecimal);
        readCount();
        numCount.setKeyListener(null);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        ringtone = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        startTime = DEFAULT_WORKING_TIME;
        breakTime = DEFAULT_BREAK_TIME;

        millisLeft = (isBreak) ? breakTime : startTime;
        onStart();
    }

    private void readCount() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //获取当前番茄id
        Cursor cursor = db.rawQuery("select count(id) from Clock where state = 1",null);
        cursor.moveToFirst();
        numCount.setText(String.valueOf(cursor.getInt(0)));
    }

    @Override
    protected void onStart() {
        super.onStart();

        defineProgress();
        updateTimerProgress();

        resumePauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTimeRunning)
                    pauseTimer();
                else
                    startTimer();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.workingTimerOption:
                if (isTimeRunning == true)
                {
                    Toast.makeText(this, "番茄钟正在进行，请勿修改时间", Toast.LENGTH_LONG).show();
                    return false;
                }
                Intent workIntent = new Intent(MainActivity.this, SetTimeActivity.class);
                workIntent.putExtra("startTime", startTime);
                workIntent.putExtra("requestCode", 10);
                startActivityForResult(workIntent, 10);

                return true;
            case R.id.breakTimerOption:

                if (isTimeRunning == true)
                {
                    Toast.makeText(this, "番茄钟正在进行，请勿修改时间", Toast.LENGTH_LONG).show();
                    return false;
                }

                Intent breakIntent = new Intent(MainActivity.this, SetTimeActivity.class);

                breakIntent.putExtra("breakTime", breakTime);
                breakIntent.putExtra("requestCode", 20);
                startActivityForResult(breakIntent, 20);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("isTimeRunning", isTimeRunning);
        outState.putLong("millisLeft", millisLeft);
        outState.putLong("startTime", startTime);
        outState.putLong("breakTime", breakTime);
        outState.putBoolean("isBreak", isBreak);

//        if (isTimeRunning)
//            destroyTimer();
        updateResumePauseButton();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        isTimeRunning = savedInstanceState.getBoolean("isTimeRunning");
        millisLeft = savedInstanceState.getLong("millisLeft");
        isBreak = savedInstanceState.getBoolean("isBreak");

        defineProgress();
        updateTimerProgress();

        if (millisLeft != startTime)
            updateResumePauseButton();

        if (isTimeRunning)
            startTimer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 10) {
                startTime = Objects.requireNonNull(data.getExtras()).getLong("startTime");

                resetTimer();
                defineProgress();
            }
            else if (requestCode == 20) {
                breakTime = Objects.requireNonNull(data.getExtras()).getLong("breakTime");

                resetTimer();
                defineProgress();
            }
        }
    }

    private void startTimer() {
        isTimeRunning = true;
        if (isdone && !isBreak)
        {//如果当前番茄钟结束，且下一个仍为专注模式
            isdone = false;
            record_pomodoro(false); //记录番茄钟

        }
        timer = new CountDownTimer(millisLeft, 100) {

            @Override
            public void onTick(long millisUntilFinished) {
                millisLeft = millisUntilFinished;

                updateTimerProgress();
            }

            @Override
            public void onFinish() {
                if (!isBreak)
                {//记录番茄钟的结束
                    update_pomodoro();
                }
                alertTimerFinish();
                changeCount();
                changeTimerType();
                defineProgress();
            }
        }.start();

        updateResumePauseButton();
    }

    private void update_pomodoro()
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("state", true);
        db.update("Clock", values, "id = ?",
                new String[] {String.valueOf(id)}); //更新番茄状态
        isdone = true;
    }

    private void record_pomodoro(boolean isfinished) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        Calendar calendar = Calendar.getInstance();
        //年
        int year = calendar.get(Calendar.YEAR);
        //月
        int month = calendar.get(Calendar.MONTH)+1;
        //日
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //整合
        String cur_date = year + "/" + month + "/" + day;

        //获取当前番茄id
        Cursor cursor = db.rawQuery("select count(id) from Clock",null);
        cursor.moveToFirst();
        id = cursor.getInt(0) + 1;

        //装载数据
        values.put("date", cur_date); //执行日期
        values.put("time", startTime); //专注时长
        values.put("state", isfinished); //完成状态
        db.insert("Clock", null, values);
        values.clear();
    }

    private void changeCount() {
        if (!isBreak)
        {
            int curCount = Integer.parseInt(numCount.getText().toString());
            curCount += 1;
            numCount.setText(String.valueOf(curCount));
        }
    }

    public void defineProgress() {
        timerProgressBar.setMax((int) TimeUnit.MILLISECONDS.toSeconds((isBreak) ? breakTime : startTime));
        timerProgressBar.setProgress(timerProgressBar.getMax());
    }

    private void alertTimerFinish() {
        vibrator.vibrate(1000);
        ringtone.play();
    }

    private void changeTimerType() {
        if (!isBreak)
        {
            millisLeft = breakTime;
        }
        else
        {
            millisLeft = startTime;
        }
        isBreak = !isBreak;
        resetTimer();

        NotificationManager manager = (NotificationManager) getSystemService
                (NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("专注时间到啦")
                .setContentText("休息一会，闭目养神~")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .build();
        manager.notify(1, notification);
    }

    private void destroyTimer() {
        timer.cancel();

        isTimeRunning = false;
    }

    private void pauseTimer() {
        destroyTimer();
        updateResumePauseButton();
    }

    private void resetTimer() {
        if (isTimeRunning)
            destroyTimer();

        millisLeft = (!isBreak) ? startTime : breakTime;

        updateTimerProgress();
        updateResumePauseButton();
    }

    private void updateTimerProgress() {
        String second = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(millisLeft) % 60);
        String minute = String.valueOf(TimeUnit.MILLISECONDS.toMinutes(millisLeft) % 60);
        String hour = String.valueOf(TimeUnit.MILLISECONDS.toHours(millisLeft));

        int hourInt = Integer.parseInt(hour);

        if (Integer.parseInt(minute) < 10 && hourInt > 0)
            minute = "0" + minute;
        if (Integer.parseInt(second) < 10)
            second = "0" + second;

        if (hourInt > 0)
            timerText.setText(getString(R.string.hour_time, hour, minute, second));
        else
            timerText.setText(getString(R.string.time, minute, second));

        timerProgressBar.setProgress((int) TimeUnit.MILLISECONDS.toSeconds(millisLeft));
    }

    private void updateResumePauseButton() {
        resumePauseButton.setImageResource(isTimeRunning ? R.drawable.baseline_pause_24 : R.drawable.baseline_play_arrow_24);
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void finish()
    {
        if (isTimeRunning && !isBreak)
        {
            record_pomodoro(false);
        }
    }

    @Override
    protected void onDestroy() {
        int a = 1;
        //如果在番茄钟运行途中退出，则记录为专注失败
        if (isTimeRunning && !isBreak)
        {
            record_pomodoro(false);
        }
        super.onDestroy();
    }

}

