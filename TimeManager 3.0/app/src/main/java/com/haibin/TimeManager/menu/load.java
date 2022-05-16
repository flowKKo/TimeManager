package com.haibin.TimeManager.menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.haibin.TimeManager.Dao.Function.Cloud_function;
import com.haibin.TimeManager.Dao.dao.Local_userDao;
import com.haibin.TimeManager.Dao.domin.Android_user;
import com.haibin.TimeManager.Pomodoro.PomodoroActivity;
import com.haibin.TimeManager.R;
import com.haibin.TimeManager.Statistics.StatisticsActivity;
import com.haibin.TimeManager.calendar.full.FullActivity;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class load extends AppCompatActivity {
    protected Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        mToolbar = findViewById(R.id.toolbar_load);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView textView = findViewById(R.id.load_textView);
        String a = Local_userDao.getThisUserName();
        if (a.equals("")) {
            textView.setText("未登录");
        } else {
            textView.setText(a);
        }

        // 把密码设置为遮掩的。。
        TextView textView1=findViewById(R.id.password);
        textView1.setTransformationMethod(PasswordTransformationMethod.getInstance());

        Button buttonBackUp = findViewById(R.id.button_backup);
        buttonBackUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int a = 0;
                try {
                    a = Cloud_function.backUp();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (a == 0) {
                    Toast.makeText(load.this, "No one log in", Toast.LENGTH_SHORT).show();
                } else if (a == 1) {
                    Toast.makeText(load.this, "back success!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button buttonRecovery = findViewById(R.id.button_recovery);
        buttonRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int a = 0;
                try {
                    a = Cloud_function.recovery();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (a == 0) {
                    Toast.makeText(load.this, "No one log in", Toast.LENGTH_SHORT).show();
                } else if (a == 1) {
                    Toast.makeText(load.this, "recovery success!!", Toast.LENGTH_SHORT).show();

                }
            }
        });

        Button buttonLogin = findViewById(R.id.button_load);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText user_edit = findViewById(R.id.user_name);
                String userName = user_edit.getText().toString();
                EditText pass_edit = findViewById(R.id.password);
                String password = pass_edit.getText().toString();

                int a = 0;
                try {
                    a = Cloud_function.login(userName, password);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (a == 1) {
                    Toast.makeText(load.this, "Log in success!!", Toast.LENGTH_SHORT).show();
                    user_edit.setText("");
                    pass_edit.setText("");
                    TextView textView = findViewById(R.id.load_textView);
                    String curr = Local_userDao.getThisUserName();
                    if (curr.equals("")) {
                        textView.setText("未登录");
                    } else {
                        textView.setText(curr);
                    }
                } else if (a == -1) {
                    Toast.makeText(load.this, "The password is wrong!!", Toast.LENGTH_SHORT).show();
                    pass_edit.setText("");
                } else if (a == 0) {
                    Toast.makeText(load.this, "There is no such account!!", Toast.LENGTH_SHORT).show();
                    user_edit.setText("");
                    pass_edit.setText("");
                }
            }
        });

        Button buttonAdd = findViewById(R.id.button_adduser);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(load.this, add_user.class);
                startActivity(intent);
            }
        });

    }


    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(2, getIntent());
            finish();
        }
        return true;
    }


}