package com.haibin.TimeManager.Pomodoro;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.haibin.TimeManager.R;
import com.haibin.TimeManager.Statistics.StatisticsActivity;
import com.haibin.TimeManager.calendar.full.FullActivity;
import com.haibin.TimeManager.showActivity;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PomodoroActivity extends AppCompatActivity {
    private static final String TAG = "";
    //    boolean isProcessRunning = false;
//    if (isProcessRunning == true) return;
    boolean isTimeRunning = false, isBreak = false;
    //    isProcessRunning = true;
    final static long DEFAULT_WORKING_TIME = 1500000, DEFAULT_BREAK_TIME = 300000;
    static long startTime, breakTime, millisLeft;
    ImageButton resumePauseButton, resetButton;
    Button skipButton;
    CountDownTimer timer;
    ProgressBar timerProgressBar;
    TextView timerText;
    EditText numCount;
    Vibrator vibrator;
    Ringtone ringtone;
    private ImageView star_working, star_breaking;
    boolean isdone = true; //记录当前番茄钟是否被用完，是则为true，反之为false
    int id = -1; //记录当前番茄钟的编号
    private Animation bigAnimation, smallAnimation;
    private boolean isSkipExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (isTimeRunning) return;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);
        LitePal.getDatabase();

        resumePauseButton = findViewById(R.id.resumePauseButton);
        resetButton = findViewById(R.id.resetButton);
        skipButton = findViewById(R.id.skip_btn);
        skipButton.setEnabled(false);
        timerProgressBar = findViewById(R.id.progressBar);
        timerText = findViewById(R.id.textView);
        numCount = findViewById(R.id.editTextNumberDecimal);
        star_working = (ImageView) findViewById(R.id.toWorkView);
        star_breaking = (ImageView) findViewById(R.id.toBreakView);
        readCount();
        numCount.setKeyListener(null);
        scaleAnimation(); //初始化动画参数

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        ringtone = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        startTime = DEFAULT_WORKING_TIME;
        breakTime = DEFAULT_BREAK_TIME;

        if (isBreak) {
            //star_control(star_working, false);
            star_control(star_breaking, true);
        } else {
            star_control(star_working, true);
            //star_control(star_breaking, false);
        }
        millisLeft = (isBreak) ? breakTime : startTime;
        onStart();



        ImageButton button_todo = (ImageButton)findViewById(R.id.button_todo);
        button_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PomodoroActivity.this, showActivity.class);
                startActivity(intent);
            }
        });

        ImageButton button_calendar = (ImageButton)findViewById(R.id.button_calendar);
        button_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PomodoroActivity.this, FullActivity.class);
                startActivity(intent);
            }
        });

        ImageButton button_clock = (ImageButton)findViewById(R.id.button_clock);
        button_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PomodoroActivity.this, PomodoroActivity.class);
                startActivity(intent);
            }
        });

        ImageButton button_statistics = (ImageButton)findViewById(R.id.button_statistics);
        button_statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PomodoroActivity.this, StatisticsActivity.class);
                startActivity(intent);
            }
        });
        button_clock.setBackgroundColor(Color.parseColor("#D7D7D7"));


    }

    private void readCount() {
        //获取当前番茄个数
        int cnt = LitePal.where("state = ?", "1").count(Clock_Database.class);
        numCount.setText(String.valueOf(cnt));
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
                if (isTimeRunning == true) {
                    Toast.makeText(this, "番茄钟正在进行，请勿修改时间", Toast.LENGTH_LONG).show();
                    return false;
                }
                Intent workIntent = new Intent(PomodoroActivity.this, SetTimeActivity.class);
                workIntent.putExtra("startTime", startTime);
                workIntent.putExtra("requestCode", 10);
                startActivityForResult(workIntent, 10);
                return true;
            case R.id.breakTimerOption:

                if (isTimeRunning == true) {
                    Toast.makeText(this, "番茄钟正在进行，请勿修改时间", Toast.LENGTH_LONG).show();
                    return false;
                }

                Intent breakIntent = new Intent(PomodoroActivity.this, SetTimeActivity.class);

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
            } else if (requestCode == 20) {
                breakTime = Objects.requireNonNull(data.getExtras()).getLong("breakTime");

                resetTimer();
                defineProgress();
            }
        }
    }

    private void startTimer() {
        isTimeRunning = true;
        if (isdone && !isBreak) {//如果当前番茄钟结束，且下一个仍为专注模式
            isdone = false;
            record_pomodoro(false); //记录番茄钟
        }
        if (isBreak && !isSkipExist)
        {
            //--------------------------------------------动画出现按钮----------------
            skipButton.setEnabled(true);
            skipButton.startAnimation(bigAnimation); //放大动画
            skipButton.setVisibility(View.VISIBLE); //显示按钮
            isSkipExist = true; //标记按钮存在
        }
        timer = new CountDownTimer(millisLeft, 100) {
            int countDown = 10000;
            @Override
            public void onTick(long millisUntilFinished) {
                millisLeft = millisUntilFinished;
                updateTimerProgress();
                if (isSkipExist && countDown == 0)
                {
                    skipButton.setVisibility(View.INVISIBLE);
                    skipButton.setText("跳过");
                    isSkipExist = false;
                    skipButton.setEnabled(false);
                }
                if (isSkipExist)
                {
                    skipButton.setText("跳过(" + countDown / 1000 + ")");
                    countDown = countDown - 100;
                }
            }

            @Override
            public void onFinish() {
                if (!isBreak) {//记录番茄钟的结束
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

    //状态更新为1
    private void update_pomodoro() {
        Clock_Database db1 = new Clock_Database();
        db1.setState(true);
        db1.setTime(startTime);
        db1.updateAll("id = ?", String.valueOf(id)); //更新该id的番茄钟
        isdone = true;
    }

    private void record_pomodoro(boolean isfinished) {
        Clock_Database db1 = new Clock_Database();
        ContentValues values = new ContentValues();
        Calendar calendar = Calendar.getInstance();
        //年
        int year = calendar.get(Calendar.YEAR);
        //月
        int month = calendar.get(Calendar.MONTH) + 1;
        //日
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //整合
        String cur_date = year + "/" + month + "/" + day;

        //获取当前番茄id
        id = LitePal.count(Clock_Database.class) + 1;

        db1.setId(1);
        db1.setDate(cur_date);
        db1.setTime(startTime);
        db1.setState(isfinished);
        db1.save();

        values.clear();
    }

    private void changeCount() {
        if (!isBreak) {
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
        if (!isBreak) {
            millisLeft = breakTime;
        } else {
            millisLeft = startTime;
        }
        isBreak = !isBreak;
        if (isBreak) {
            star_control(star_working, false);
            star_control(star_breaking, true);

            //--------------------------------------------动画出现按钮----------------
            skipButton.setEnabled(true);
            skipButton.startAnimation(bigAnimation); //放大动画
            skipButton.setVisibility(View.VISIBLE); //显示按钮
            isSkipExist = true; //标记按钮存在
            //点击事件：
            skipButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    skipBreaking();
                }
            });
            //--------------------------------------------动画出现按钮----------------

        } else {
            star_control(star_working, true);
            star_control(star_breaking, false);
        }
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
    public void finish() {
        if (isTimeRunning && !isBreak) {
            record_pomodoro(false);
        }
    }

    @Override
    protected void onDestroy() {
        int a = 1;
        //如果在番茄钟运行途中退出，则记录为专注失败
        if (isTimeRunning && !isBreak) {
            record_pomodoro(false);
        }
        super.onDestroy();
    }

    //缩放动画
    private void scaleAnimation() {
        //放大
        bigAnimation = AnimationUtils.loadAnimation(PomodoroActivity.this, R.anim.scale_big);
        //缩小
        smallAnimation = AnimationUtils.loadAnimation(PomodoroActivity.this, R.anim.scale_small);
    }

    public void star_control(ImageView image_id, boolean toShow) {
        if (toShow) {//如果是要显示该星星
            image_id.startAnimation(bigAnimation);
            image_id.setVisibility(View.VISIBLE);
        } else {//如果要隐藏该星星
            image_id.startAnimation(smallAnimation);

            smallAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    image_id.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

//    void countDownbtn() {
//        skipButton.startAnimation(bigAnimation); //放大动画
//        skipButton.setVisibility(View.VISIBLE); //显示按钮
//        //点击事件：
//        skipButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                skipBreaking();
//            }
//        });
//    }

    //跳过休息模式
    private void skipBreaking() {
        alertTimerFinish();
        changeCount();
        changeTimerType();
        defineProgress();
        //隐藏动画
//        smallAnimation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                skipButton.setVisibility(View.INVISIBLE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
        skipButton.setVisibility(View.INVISIBLE);
        skipButton.setEnabled(false);
        skipButton.setText("跳过");
        isSkipExist = false;
    }
}

