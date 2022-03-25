package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.util.Calendar;
import java.util.Locale;



public class MainActivity extends AppCompatActivity {
    private BottomSheet_Add bottomSheet_add=null;//添加待办事项对话框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LitePal.getDatabase();//创建数据库
    }

    public void onClick(View view){//监听函数
        switch (view.getId()){
            case R.id.button_add://添加待办事项按钮
                //BottomSheetDialog dialog=new BottomSheetDialog();
                bottomSheet_add=new BottomSheet_Add(this);
                //bottomSheet_add.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
                //bottomSheet_add.getWindow().getDecorView().setBackground(null);
                bottomSheet_add.show();
                //bottomSheet_add.getWindow().getDecorView().setBackground(null); // 移除dialog的decorview背景色
                break;
            default:
            break;
        }
    }


}