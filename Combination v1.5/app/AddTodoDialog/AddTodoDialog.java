package com.haibin.TimeManager.AddTodoDialog;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.haibin.TimeManager.R;
import com.haibin.TimeManager.Todo.Date;
import com.haibin.TimeManager.Todo.Time;
import com.haibin.TimeManager.Todo.Todo;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.litepal.LitePal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class AddTodoDialog extends BottomSheetDialogFragment {
    private Calendar calendar= Calendar.getInstance(Locale.CHINA);//创建一个日历
    private Date select_date=new Date(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,
            calendar.get(Calendar.DAY_OF_MONTH));//选择的日期，默认今天
    private DatePickerDialog datePickerDialog=null;//日期选择对话框
    private boolean is_clock=false;//是否提醒
    private TimePickerDialog timePickerDialog=null;//时间选择对话框
    private Time select_time=new Time(12,0);//选择的提醒时间
    //重复任务设置
    private RepeatSet m_repeatSet=new RepeatSet(0, new Date(0, 0, 0),
            new Date(0, 0, 0), new LinkedList<>(),"", "");
    private boolean is_repeat=false;//是否重复任务
    private RepeatSetDialog repeatSetDialog=null;//重复任务设置对话框
    private boolean isIs_repeat=false;//不重复
    private OnTodoAddListener onTodoAddListener;//监听者

    // 构造方法
    public static AddTodoDialog newInstance(Long feedId) {
        Bundle args = new Bundle();
        args.putLong("FEED_ID", feedId);
        AddTodoDialog fragment = new AddTodoDialog();
        fragment.setArguments(args);
        return fragment;
    }

    // 创建View
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.add_todo, container);
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
        ((EditText)getView().findViewById(R.id.EditText_date)).setText(Integer.toString(calendar.get(Calendar.YEAR))+'/'+
                String.format("%02d",calendar.get(Calendar.MONTH)+1)+'/'+
                String.format("%02d",calendar.get(Calendar.DAY_OF_MONTH)));
        initDialog();//初始化各个对话框
        initOnClick();//初始化监听
    }

    public void initDialog(){
        //初始化各个对话框
        //创建一个日期选择对话框
        datePickerDialog = new DatePickerDialog(getContext(), 0,
                new DatePickerDialog.OnDateSetListener(){//日期选中监听
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day){
                        select_date.SetDate(year,month+1,day);
                        //显示选中的日期
                        ((EditText)getView().findViewById(R.id.EditText_date)).setText(select_date.tostring());
                    }
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        //创建一个时间选择对话框
        timePickerDialog=new TimePickerDialog(getContext(),0,
                new TimePickerDialog.OnTimeSetListener(){//日期选中监听
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute){
                        select_time.hour=hour;
                        select_time.minute=minute;
                        //按钮显示选中的时间
                        ((Button)getView().findViewById(R.id.button_clock)).setText(select_time.tostring());
                        is_clock=true;//设置提醒标志
                    }
                },12,0,true);
        //创建一个重复任务设置对话框
       /* repeatSetDialog=new RepeatSetDialog();
        repeatSetDialog.setOnRepeatSetListener(new OnRepeatSetListener() {//设置监听
            @Override
            public void onRepeatSet(RepeatSet repeatSet) {
                m_repeatSet=repeatSet;
                is_repeat=true;
                //按钮文本提示...
                ((Button)getView().findViewById(R.id.button_repeat)).setText("重复");
            }
        });*/
    }

    public void initOnClick(){
        //初始化响应事件
        //按钮信号槽连接
        Button btn_add_todo=(Button) getView().findViewById(R.id.button_addtodo);
        Button btn_select_date=(Button) getView().findViewById(R.id.button_select_date);
        Button btn_clock=(Button) getView().findViewById(R.id.button_clock);
        Button btn_repeat=(Button) getView().findViewById(R.id.button_repeat);
        btn_add_todo.setOnClickListener(this::onClick_Dialog);
        btn_select_date.setOnClickListener(this::onClick_Dialog);
        btn_clock.setOnClickListener(this::onClick_Dialog);
        btn_repeat.setOnClickListener(this::onClick_Dialog);
    }


    public void onClick_Dialog(View view){//监听事件
        switch(view.getId()){
            case R.id.button_addtodo:  //添加待办事项
                AddToSQL();//添加到数据库
                Toast.makeText(getContext(),"添加成功", Toast.LENGTH_SHORT).show();
                onTodoAddListener.onTodoAdd();//函数回调
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
                    ((Button)getView().findViewById(R.id.button_clock)).setText("不提醒");
                    is_clock=false;//取消提醒
                }
                break;
            case R.id.button_repeat:  //重复任务设置
                if(!is_repeat){
                    repeatSetDialog=new RepeatSetDialog();
                    repeatSetDialog.setOnRepeatSetListener(new OnRepeatSetListener() {//设置监听
                        @Override
                        public void onRepeatSet(RepeatSet repeatSet) {
                            m_repeatSet=repeatSet;
                            is_repeat=true;
                            //按钮文本提示...
                            ((Button)getView().findViewById(R.id.button_repeat)).setText("重复");
                        }
                    });
                    repeatSetDialog.show(getParentFragmentManager(),"This is repeatSetDialog");
                }
                else{//还原按钮
                    ((Button)getView().findViewById(R.id.button_repeat)).setText("不重复");
                    is_repeat=false;//取消重复
                }
                break;
            default: break;
        }


    }

    public void AddToSQL(){
        int max_id;
        if(LitePal.count("Todo")==0) max_id=0;
        else max_id=LitePal.max("Todo","id",int.class);//保证todo_id自增
        if(!is_repeat){//不重复
            Todo todo = new Todo();//设置要加入的todo
            todo.setTodo(((EditText)getView().findViewById(R.id.EditText_ToDo)).getText().toString());
            todo.setCreate_date(Integer.toString(calendar.get(Calendar.YEAR))+'/'+
                    String.format("%02d",calendar.get(Calendar.MONTH)+1)+'/'+
                    String.format("%02d",calendar.get(Calendar.DAY_OF_MONTH)));
            todo.setIs_clock(is_clock);
            todo.setTime(select_time.tostring());
            todo.setDate(select_date.tostring());
            todo.setId(max_id+1);
            todo.setIs_delete("false");
            todo.save();//插入数据
        }
        else{//重复添加
            String []week={"星期天","星期一","星期二","星期三","星期四","星期五","星期六",};
            SimpleDateFormat format= new SimpleDateFormat("yyyy/MM/dd");
            List<Todo> todos=new LinkedList<>();
            for(Date date = m_repeatSet.date_begin; date.LessEqual(m_repeatSet.date_end); date.increase()){
                switch (m_repeatSet.RepeatMode){
                    case 0://日重复
                        AddTodoToList(todos,max_id+1,date);
                        max_id++;
                        break;
                    case 1://周重复
                        java.util.Date date1= null;//通过Calendar推算星期
                        try {
                            date1 = format.parse(date.tostring());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Calendar calendar1=Calendar.getInstance(Locale.CHINA);
                        calendar1.setTime(date1);
                        int w=calendar1.get(Calendar.DAY_OF_WEEK)-1;
                        if(m_repeatSet.DaysOfWeek.contains(week[w])){//添加
                            AddTodoToList(todos,max_id+1,date);
                            max_id++;
                        }
                        break;
                    case 2://月重复
                        if(date.getDay()== Integer.valueOf
                                (m_repeatSet.DayOfMonth.substring(1,m_repeatSet.DayOfMonth.length()-1))){
                            //添加
                            AddTodoToList(todos,max_id+1,date);
                            max_id++;
                        }
                        break;
                    case 3://年重复
                        if(date.getMonth()==Integer.valueOf(m_repeatSet.DayOfYear.substring(0,2))&&
                        date.getDay()==Integer.valueOf(m_repeatSet.DayOfYear.substring(3,5))){
                            //添加
                            AddTodoToList(todos,max_id+1,date);
                            max_id++;
                        }
                        break;
                    default:break;
                }

            }
            LitePal.saveAll(todos);//保存
        }
    }

    public void AddTodoToList(List<Todo> todos, int id, Date date){//重复任务添加一个到list
        todos.add(new Todo());
        todos.get(todos.size()-1).setTodo(((EditText)getView().findViewById(R.id.EditText_ToDo)).getText().toString());
        todos.get(todos.size()-1).setCreate_date(Integer.toString(calendar.get(Calendar.YEAR))+'/'+
                String.format("%02d",calendar.get(Calendar.MONTH)+1)+'/'+
                String.format("%02d",calendar.get(Calendar.DAY_OF_MONTH)));
        todos.get(todos.size()-1).setIs_clock(is_clock);
        todos.get(todos.size()-1).setTime(select_time.tostring());
        todos.get(todos.size()-1).setDate(date.tostring());
        todos.get(todos.size()-1).setId(id);
    }

    public void setOnTodoAddListener(OnTodoAddListener onTodoAddListener){
        this.onTodoAddListener=onTodoAddListener;//回调函数定义
    }

}
