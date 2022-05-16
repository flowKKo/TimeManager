package com.haibin.TimeManager.menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.haibin.TimeManager.Pomodoro.PomodoroActivity;
import com.haibin.TimeManager.R;

import java.util.concurrent.ExecutionException;

public class add_user extends AppCompatActivity {
    protected Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        mToolbar = findViewById(R.id.toolbar_adduser);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView textView_password1 = findViewById(R.id.password_add);
        TextView textView_password2 = findViewById(R.id.password_add2);
        textView_password1.setTransformationMethod(PasswordTransformationMethod.getInstance());
        textView_password2.setTransformationMethod(PasswordTransformationMethod.getInstance());


        Button button_addUser = findViewById(R.id.button_add);
        button_addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText user_edit = findViewById(R.id.user_name_add);
                String userName = user_edit.getText().toString();
                EditText pass_edit = findViewById(R.id.password_add);
                String password = pass_edit.getText().toString();
                EditText pass_edit1 = findViewById(R.id.password_add2);
                String password1 = pass_edit1.getText().toString();
                //Local_userDao.delete_all_user();
                if (!password1.equals(password)) {
                    Toast.makeText(add_user.this, "not the same passwords!!", Toast.LENGTH_SHORT).show();
                    pass_edit.setText("");
                    pass_edit1.setText("");
                } else {
                    int a = 0;
                    try {
                        a = Cloud_function.add_a_user(userName, password);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (a == 0) {
                        Toast.makeText(add_user.this, "You input the same userName..", Toast.LENGTH_SHORT).show();
                        user_edit.setText("");
                    } else {
                        Toast.makeText(add_user.this, "Add user success!!", Toast.LENGTH_SHORT).show();
                        user_edit.setText("");
                        pass_edit.setText("");
                        pass_edit1.setText("");
                    }
                    Local_userDao.show_for_debug();
                }
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
