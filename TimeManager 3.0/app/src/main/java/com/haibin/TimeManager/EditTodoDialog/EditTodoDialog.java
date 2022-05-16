package com.haibin.TimeManager.EditTodoDialog;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.haibin.TimeManager.Todo.Todo;

import java.util.Calendar;
import java.util.Locale;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;


import com.haibin.TimeManager.AddTodoDialog.OnTodoAddListener;
import com.haibin.TimeManager.R;
import com.haibin.TimeManager.Todo.Date;
import com.haibin.TimeManager.Todo.Time;
import com.haibin.TimeManager.SysCalendar.SysCalendar;


import org.litepal.LitePal;


public class EditTodoDialog extends BottomSheetDialogFragment {
    private static Object LayoutInflater;
    private int id;//要进行编辑的todo的id
    private Todo todo=new Todo();//要进行编辑的todo
    //private Todo edit_todo;//临时todo
    Calendar calendar=Calendar.getInstance(Locale.CHINA);//日历
    private DatePickerDialog datePickerDialog;//日期选择对话框
    private TimePickerDialog timePickerDialog;//时间选择对话框
    private OnTodoEditListener onTodoEditListener;//监听者

    // 构造方法
    public static EditTodoDialog newInstance(Long feedId,int id) {
        Bundle args = new Bundle();
        args.putLong("FEED_ID", feedId);
        EditTodoDialog fragment = new EditTodoDialog(id);
        fragment.setArguments(args);
        return fragment;
    }

    //构造函数
    public EditTodoDialog(int id){//需要传入todo的id
        this.id=id;
        todo.set(LitePal.find(Todo.class,id));//待编辑的todo初始成原todo
        //todo=LitePal.find(Todo.class,id);
        //edit_todo=todo;
    }

    // 创建View
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.edit_todo, container);
        return inflate;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*
        此方法在视图View已经创建后返回的，但是这个view 还没有添加到父级中。
        我们在这里可以重新设定view的各个数据，但是不能修改对话框最外层的ViewGroup的布局参数。
        因为这里的view还没添加到父级中，我们需要在下面onStart生命周期里修改对话框尺寸参数
         */
        //设置父背景为透明
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        initViewText();//根据原有的todo设置view初始文本
        initOnClick();//初始化信号连接
        initPickerDialog();//初始化选择对话框

    }

    private void initOnClick(){//初始化信号连接
        ((Button)getView().findViewById(R.id.button_save)).setOnClickListener(this::onClick);
        ((ImageButton)getView().findViewById(R.id.imageButton_editClose)).setOnClickListener(this::onClick);
        ((Button)getView().findViewById(R.id.button_edit_select_date)).setOnClickListener(this::onClick);
        ((Button)getView().findViewById(R.id.button_editClock)).setOnClickListener(this::onClick);
    }

    private void initViewText(){//根据原有的todo设置view初始文本
        //Todo todo=LitePal.find(Todo.class,id);//原有的todo
        ((EditText)getView().findViewById(R.id.editText_editTodo)).setText(todo.getTodo());
        ((EditText)getView().findViewById(R.id.editText_editDate)).setText(todo.getDate());
        if(todo.getIs_clock()){
            ((Button)getView().findViewById(R.id.button_editClock)).setText(todo.getTime());
            ((Button)getView().findViewById(R.id.button_editClock)).setSelected(true);//按钮标记为选中
        }
        else{
            ((Button)getView().findViewById(R.id.button_editClock)).setText("不提醒");
            ((Button)getView().findViewById(R.id.button_editClock)).setSelected(false);//按钮标记为未选中
        }
    }

    private void initPickerDialog(){
        datePickerDialog=new DatePickerDialog(getContext(), 0, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                todo.setDate(new Date(year,month+1,day).tostring());//设置日期
                ((EditText)getView().findViewById(R.id.editText_editDate)).setText(todo.getDate());
            }
        },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        timePickerDialog=new TimePickerDialog(getContext(), 0, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                todo.setTime(new Time(hour,minute).tostring());//设置提醒时间
                todo.setIs_clock(true);
                ((Button)getView().findViewById(R.id.button_editClock)).setText(todo.getTime());
                ((Button)getView().findViewById(R.id.button_editClock)).setSelected(true);//按钮标记为选中
            }
        },12,0,true);
    }

    //处理用户点击控件后的事件
    private void onClick(View view) {
        switch(view.getId()){
            case R.id.button_save:
                if(((EditText)getView().findViewById(R.id.editText_editTodo)).getText().length()==0){
                    //未输入内容
                    Toast.makeText(getContext(),"请填写内容", Toast.LENGTH_SHORT).show();
                }
                else{//保存
                    todo.setTodo(((EditText)getView().findViewById(R.id.editText_editTodo)).getText().toString());
                    todo.update(id);//更新数据库
                    if(todo.getIs_clock()){//更新系统日历event
                        //新的日期
                        Date date=new Date(Integer.valueOf(todo.getDate().substring(0,4)),
                                Integer.valueOf(todo.getDate().substring(5,7)),
                                Integer.valueOf(todo.getDate().substring(8,10)));
                        //新的提醒时间
                        Time time=new Time(Integer.valueOf(todo.getTime().substring(0,2)),
                                Integer.valueOf(todo.getTime().substring(3,5)));
                        SysCalendar.updateCalendarEvent(getContext(),todo.getId(),todo.getTodo(),"",date,time);//更新系统日历的event
                    }
                    else{//删除系统日历event
                        SysCalendar.deleteCalendarEvent(getContext(),todo.getId());
                    }
                    Toast.makeText(getContext(),"保存成功",Toast.LENGTH_LONG).show();
                    onTodoEditListener.onTodoEdit();//函数回调
                    dismiss();
                }
                break;
            case R.id.imageButton_editClose:
                dismiss();//直接关闭对话框
                break;
            case R.id.button_edit_select_date:
                datePickerDialog.show();
                break;
            case R.id.button_editClock:
                if(!todo.getIs_clock()){
                    timePickerDialog.show();
                }
                else{
                    todo.setIs_clock(false);
                    todo.setToDefault("is_clock");//bool类型不支持直接set
                    ((Button)getView().findViewById(R.id.button_editClock)).setText("不提醒");
                    ((Button)getView().findViewById(R.id.button_editClock)).setSelected(false);//按钮标记为未选中
                }
                break;
            default: break;
        }

    }
    //设置监听者，方便使用者监听“保存编辑的待办事项”事件的发生
    public void setOnTodoEditListener(OnTodoEditListener onTodoEditListener){
        this.onTodoEditListener=onTodoEditListener;//回调函数定义
    }


}
