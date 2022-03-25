package com.example.todolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.annotation.NonNull;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.Locale;
//import androidx.fragment.app.Fragment;
//import androidx.navigation.fragment.NavHostFragment;




public class BottomSheet_Add extends BottomSheetDialog{
    //private int layoutId;//布局文件的id
    private Calendar calendar= Calendar.getInstance(Locale.CANADA);//创建一个日历
    private Date select_date=new Date(0,0,0);//选择的日期
    private DatePickerDialog datePickerDialog=null;//日期选择对话框
    private boolean is_clock=false;//是否提醒
    private TimePickerDialog timePickerDialog=null;//时间选择对话框
    private Time select_time=new Time();//选择的提醒时间

    public BottomSheet_Add(@NonNull Context context) {
        super(context,0);
        //this.layoutId=layoutId;
        //LitePal.deleteAll(ToDo.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_todo);//添加布局

        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //创建一个日期选择对话框
        datePickerDialog = new DatePickerDialog(getContext(), 0,
                new DatePickerDialog.OnDateSetListener(){//日期选中监听
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day){
                        select_date.year=year;
                        select_date.month=month+1;
                        select_date.day=day;
                        //显示选中的日期
                        ((EditText)findViewById(R.id.EditText_date)).setText(Integer.toString(select_date.year)+'/'
                                +Integer.toString(select_date.month)+'/'+Integer.toString(select_date.day));
                    }
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        //创建一个时间选择对话框
        timePickerDialog=new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener(){//日期选中监听
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute){
                        select_time.hour=hour;
                        select_time.minute=minute;
                        //按钮显示选中的时间
                        ((Button)findViewById(R.id.button_clock)).setText(Integer.toString(select_time.hour)+':'
                                +Integer.toString(select_time.minute));
                        is_clock=true;//设置提醒标志
                }
            },12,0,true);
        Button btn_add_todo=(Button) findViewById(R.id.button_addtodo);
        Button btn_select_date=(Button) findViewById(R.id.button_select_date);
        Button btn_clock=(Button) findViewById(R.id.button_clock);
        btn_add_todo.setOnClickListener(this::onClick_Dialog);
        btn_select_date.setOnClickListener(this::onClick_Dialog);
        btn_clock.setOnClickListener(this::onClick_Dialog);
    }

    public void onClick_Dialog(View view){//监听事件
        switch(view.getId()){
            case R.id.button_addtodo:  //添加待办事项
                //连接数据库，添加todo
                ToDo todo = new ToDo();
                //设置要加入的todo
                todo.setTodo(((EditText)findViewById(R.id.EditText_ToDo)).getContext().toString());
                todo.setCreate_date(new Date(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)));
                todo.setDate(select_date);
                int max_id= LitePal.max("ToDo","id",int.class);
                todo.setId(max_id+1);
                todo.setIs_clock(is_clock);
                todo.setTime(select_time);
                todo.save();//插入数据
                Toast.makeText(getContext(),"添加成功", Toast.LENGTH_SHORT).show();
                dismiss();//销毁添加待办事项对话框
                break;
            case R.id.button_select_date:  //选择日期
                datePickerDialog.show();
                break;
            case R.id.button_clock:  //设置提醒
                if(!is_clock){
                    //选择提醒时间
                    timePickerDialog.show();
                }
                else{
                    //还原按钮
                    ((Button)findViewById(R.id.button_clock)).setText("不提醒");
                    is_clock=false;//取消提醒
                }
                break;
            default: break;
        }


    }



}
