package com.haibin.TimeManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.haibin.TimeManager.Adapter.DragTouchAdapter;
import com.haibin.TimeManager.AddTodoDialog.AddTodoDialog;
import com.haibin.TimeManager.AddTodoDialog.OnTodoAddListener;
import com.haibin.TimeManager.EditTodoDialog.EditTodoDialog;
import com.haibin.TimeManager.EditTodoDialog.OnTodoEditListener;
import com.haibin.TimeManager.Pomodoro.PomodoroActivity;
import com.haibin.TimeManager.Statistics.StatisticsActivity;
import com.haibin.TimeManager.Todo.Date;
import com.haibin.TimeManager.Todo.Todo;
import com.haibin.TimeManager.calendar.full.FullActivity;
import com.haibin.TimeManager.menu.help_for_user;
import com.haibin.TimeManager.menu.search_dustbin;
import com.haibin.TimeManager.menu.search_history;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.haibin.TimeManager.calendar.base.activity.BaseActivity;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.touch.OnItemStateChangedListener;

import org.litepal.LitePal;

import java.util.Collections;
import java.util.List;

public class showActivity extends BaseActivity implements
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnCalendarLongClickListener,
        CalendarView.OnMonthChangeListener,
        CalendarView.OnYearChangeListener,
        CalendarView.OnWeekChangeListener,
        CalendarView.OnViewChangeListener,
        CalendarView.OnCalendarInterceptListener,
        CalendarView.OnYearViewChangeListener,
        DialogInterface.OnClickListener,
        View.OnClickListener {

    TextView mTextMonthDay;
    TextView mTextYear;
    TextView mTextLunar;
    TextView mTextCurrentDay;
    CalendarView mCalendarView;
    RelativeLayout mRelativeTool;
    private int mYear;
    CalendarLayout mCalendarLayout;
    Calendar current_calendar;


    protected SwipeRecyclerView mRecyclerView;
    protected LinearLayoutManager mLayoutManager=new LinearLayoutManager(this);
    protected List<Todo> mToDoList;
    protected DragTouchAdapter mAdapter;
    private LocalReceiver localReceiver;   //本地广播接收者
    private LocalBroadcastManager localBroadcastManager;   //本地广播管理者   可以用来注册广播
    private IntentFilter intentFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecyclerView=findViewById(R.id.recycler_view);
        mAdapter = new DragTouchAdapter(this,mRecyclerView);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mRecyclerView.setOnItemClickListener(this::onItemClick);
        mRecyclerView.setAdapter(mAdapter);
        //在checkbox选中时，mTodolist也跟着发生了变化，然后发布广播消息，更新数据库的内容

        mRecyclerView.setLongPressDragEnabled(true); // 长按拖拽，默认关闭。
        mRecyclerView.setItemViewSwipeEnabled(true); // 滑动删除，默认关闭。
        mRecyclerView.setOnItemStateChangedListener(mOnItemStateChangedListener); // 监听Item的手指状态，拖拽、侧滑、松开。
        mRecyclerView.setOnItemMoveListener(getItemMoveListener());// 监听拖拽和侧滑删除，更新UI和数据源。

        FloatingActionButton adddata=findViewById(R.id.fab);
        adddata.setOnClickListener(this::onClick_Dialog);//监听事件

        //注册广播接收器
        localBroadcastManager= LocalBroadcastManager.getInstance(this);
        localReceiver=new showActivity.LocalReceiver();
        intentFilter=new IntentFilter("myaction");
        localBroadcastManager.registerReceiver(localReceiver,intentFilter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_show;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView() {
        setStatusBarDarkMode();
        mTextMonthDay = findViewById(R.id.tv_month_day);
        mTextYear = findViewById(R.id.tv_year);
        mTextLunar = findViewById(R.id.tv_lunar);
        mRelativeTool = findViewById(R.id.rl_tool);
        mCalendarView = findViewById(R.id.calendarView);
        mTextCurrentDay = findViewById(R.id.tv_current_day);
        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCalendarLayout.isExpand()) {
                    mCalendarLayout.expand();
                    return;
                }
                mCalendarView.showYearSelectLayout(mYear);
                mTextLunar.setVisibility(View.GONE);
                mTextYear.setVisibility(View.GONE);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });

        mCalendarLayout = findViewById(R.id.calendarLayout);
        mCalendarView.setOnYearChangeListener(this);
        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnMonthChangeListener(this);
        mCalendarView.setOnCalendarLongClickListener(this, true);
        mCalendarView.setOnWeekChangeListener(this);
        mCalendarView.setOnYearViewChangeListener(this);

        //设置日期拦截事件，仅适用单选模式，当前无效
        mCalendarView.setOnCalendarInterceptListener(this);

        mCalendarView.setOnViewChangeListener(this);
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
        mTextLunar.setText("今日");
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));

        ImageButton button_todo = (ImageButton)findViewById(R.id.button_todo);
        button_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(showActivity.this, showActivity.class);
                startActivity(intent);
            }
        });
        button_todo.setBackgroundColor(Color.parseColor("#D7D7D7"));

        ImageButton button_calendar = (ImageButton)findViewById(R.id.button_calendar);
        button_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(showActivity.this, FullActivity.class);
                startActivity(intent);
            }
        });

        ImageButton button_clock = (ImageButton)findViewById(R.id.button_clock);
        button_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(showActivity.this, PomodoroActivity.class);
                startActivity(intent);
            }
        });

        ImageButton button_statistics = (ImageButton)findViewById(R.id.button_statistics);
        button_statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(showActivity.this, StatisticsActivity.class);
                startActivity(intent);
            }
        });

        current_calendar = mCalendarView.getSelectedCalendar();
        findViewById(R.id.fl_current).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.scrollToCurrent();
            }
        });
    }

    @SuppressWarnings("unused")
    @Override
    protected void initData() {
        final int year = mCalendarView.getCurYear();
        final int month = mCalendarView.getCurMonth();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    @Override
    public void onClick(View v){

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
        //Toast.makeText(this, String.format("%s : OutOfRange", calendar), Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        //Log.e("onDateSelected", "  -- " + calendar.getYear() + "  --  " + calendar.getMonth() + "  -- " + calendar.getDay());
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();

        current_calendar = calendar;
        // 此处处理视图更新逻辑
        onResume();
    }

    @Override
    public void onCalendarLongClickOutOfRange(Calendar calendar) {
        //Toast.makeText(this, String.format("%s : LongClickOutOfRange", calendar), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCalendarLongClick(Calendar calendar) {
        //Toast.makeText(this, "长按不选择日期\n" + getCalendarText(calendar), Toast.LENGTH_SHORT).show();
    }

    private static String getCalendarText(Calendar calendar) {
        return String.format("新历%s \n 农历%s \n 公历节日：%s \n 农历节日：%s \n 节气：%s \n 是否闰月：%s",
                calendar.getMonth() + "月" + calendar.getDay() + "日",
                calendar.getLunarCalendar().getMonth() + "月" + calendar.getLunarCalendar().getDay() + "日",
                TextUtils.isEmpty(calendar.getGregorianFestival()) ? "无" : calendar.getGregorianFestival(),
                TextUtils.isEmpty(calendar.getTraditionFestival()) ? "无" : calendar.getTraditionFestival(),
                TextUtils.isEmpty(calendar.getSolarTerm()) ? "无" : calendar.getSolarTerm(),
                calendar.getLeapMonth() == 0 ? "否" : String.format("闰%s月", calendar.getLeapMonth()));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onMonthChange(int year, int month) {
        Log.e("onMonthChange", "  -- " + year + "  --  " + month);
        Calendar calendar = mCalendarView.getSelectedCalendar();
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();
    }

    @Override
    public void onViewChange(boolean isMonthView) {
        Log.e("onViewChange", "  ---  " + (isMonthView ? "月视图" : "周视图"));
    }


    @Override
    public void onWeekChange(List<Calendar> weekCalendars) {
        for (Calendar calendar : weekCalendars) {
            Log.e("onWeekChange", calendar.toString());
        }
    }

    @Override
    public void onYearViewChange(boolean isClose) {
        Log.e("onYearViewChange", "年视图 -- " + (isClose ? "关闭" : "打开"));
    }

    /**
     * 屏蔽某些不可点击的日期，可根据自己的业务自行修改
     *
     * @param calendar calendar
     * @return 是否屏蔽某些不可点击的日期，MonthView和WeekView有类似的API可调用
     */
    @Override
    public boolean onCalendarIntercept(Calendar calendar) {
        Log.e("onCalendarIntercept", calendar.toString());
        int day = calendar.getDay();
        return day == 1 || day == 3 || day == 6 || day == 11 || day == 12 || day == 15 || day == 20 || day == 26;
    }

    @Override
    public void onCalendarInterceptClick(Calendar calendar, boolean isClick) {
        //Toast.makeText(this, calendar.toString() + "拦截不可点击", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onYearChange(int year) {
        mTextMonthDay.setText(String.valueOf(year));
        //Log.e("onYearChange", " 年份变化 " + year);
    }






    //侧边栏
    public boolean onCreateOptionsMenu(Menu menu) {//得到MenuInflater对象并调用inflate方法创建菜单
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data)  {
        super.onActivityResult(requestCode, resultCode,  data);
        //resultCode就是在B页面中返回时传的parama，可以根据需求做相应的处理
//        mToDoList= LitePal.where("is_delete = ?","false").
//                order("date desc").find(Todo.class);
//        mAdapter.notifyDataSetChanged(mToDoList);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.history_activity:
                Log.w("TAG4","entry history");
                Intent intent_history = new Intent(showActivity.this, search_history.class);
                startActivityForResult(intent_history,1);
                break;
            case R.id.help:
                Log.w("TAG_New","entry help");
                Intent intent_help = new Intent(showActivity.this, help_for_user.class);
                Log.w("TAG_New","start help");
                startActivity(intent_help);
                break;
            case R.id.dustbin_activity:
                Log.w("TAG5","entry dustbin");
                Intent intent_dustbin = new Intent(showActivity.this, search_dustbin.class);
                startActivityForResult(intent_dustbin,2);
                break;
            default:
        }
        return true;
    }

    //这个函数会让已经完成的事件标记为已完成
    public void init_is_done(){
        for(int i=0;i<mToDoList.size();i++){
            //已完成
            mAdapter.flag[i]=false;//先初始化为全未完成

        }
        for(int i=0;i<mToDoList.size();i++){
            if(mToDoList.get(i).getIs_done()){//已完成
                mAdapter.flag[i]=true;
            }
        }


    }
    public void swap_position(){
        int k=mToDoList.size();
        for(int i=0;i<k;){
            if(mToDoList.get(i).getIs_done()){
                k--;
                for (int j = i; j <mToDoList.size()-1; j++) {
                    Collections.swap(mToDoList, j, j + 1);//交换数据源两个数据的位置
                    //swap(mAdapter.flag,j,j+1);
                    //mAdapter.notifyItemMoved(j, j + 1);

                }
            }
            else i++;
        }
    }
    private class LocalReceiver extends BroadcastReceiver {//消息从adapter收到了
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if("myaction".equals(action)){
                Log.d( "消息：" + intent.getStringExtra( "data" )  , "线程： " + Thread.currentThread().getName() ) ;
            }
            String todo_name=intent.getStringExtra("to_doname");
            int todo_id=intent.getIntExtra("todo_id",0);
            boolean is_done=intent.getBooleanExtra("is_done",false);
            //在这里出了问题（已改正），position应该是由getAdapterPosition函数得到的
            int fromPosition=intent.getIntExtra("position",0);

            Todo UpdateTodo=new Todo();
            if(is_done) UpdateTodo.setIs_done(is_done);
            else UpdateTodo.setToDefault("is_done");//当set为false时应调用该函数
            UpdateTodo.updateAll("id = ?",String.valueOf(todo_id));


            //flag也跟着变化
            List<Todo> test=LitePal.findAll(Todo.class);

            //接下来要做的是将这个todo的position移到最下面去
            //分为两种情况，当is_done变为true时和当is_done变为false时
            if(is_done) {
                int toPosition = mToDoList.size() - 1;
                if (fromPosition < toPosition) {
                    //从上往下拖动，每滑动一个item，都将list中的item向下交换，向上滑同理。
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(mToDoList, i, i + 1);//交换数据源两个数据的位置
                        mAdapter.notifyItemMoved(i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(mToDoList, i, i - 1);//交换数据源两个数据的位置
                        mAdapter.notifyItemMoved(i, i - 1);
                    }
                }
            }
            else{
                int toPosition = 0;
                if (fromPosition < toPosition) {
                    //从上往下拖动，每滑动一个item，都将list中的item向下交换，向上滑同理。
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(mToDoList, i, i + 1);//交换数据源两个数据的位置
                        mAdapter.notifyItemMoved(i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(mToDoList, i, i - 1);//交换数据源两个数据的位置
                        mAdapter.notifyItemMoved(i, i - 1);
                    }
                }
            }
        }
    }

    //取消广播的注册
    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }
    //编辑信息
    public void onItemClick(View itemView, int position) {
        //Toast.makeText(this, "第" + position + "个", Toast.LENGTH_SHORT).show();
        //在这里调用修改信息
        EditTodoDialog editTodoDialog = new EditTodoDialog(mToDoList.get(position).getId());
        editTodoDialog.setOnTodoEditListener(new OnTodoEditListener() {
            @Override
            public void onTodoEdit() {//刷新界面
                onResume();
            }
        });
        editTodoDialog.show(getSupportFragmentManager(),"EditDialog");
    }
    protected OnItemMoveListener getItemMoveListener() {
        return onItemMoveListener;
    }

    //Item的拖拽/侧滑删除时，手指状态发生变化监听。
    //用来设置Item在拖拽/删除时背景变化的函数
    private final OnItemStateChangedListener mOnItemStateChangedListener = new OnItemStateChangedListener() {
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState == OnItemStateChangedListener.ACTION_STATE_DRAG) {
                // 拖拽的时候背景就透明了，这里我们可以添加一个特殊背景。
                viewHolder.itemView.setBackgroundColor(
                        ContextCompat.getColor(showActivity.this, R.color.white_pressed));
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_SWIPE) {
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
                // 在手松开的时候还原背景。
                ViewCompat.setBackground(viewHolder.itemView,
                        ContextCompat.getDrawable(showActivity.this, R.drawable.select_white));
            }
        }
    };

    //监听拖拽和侧滑删除，更新UI和数据源
    private final OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
            // 不同的ViewType不能拖拽换位置。
            if (srcHolder.getItemViewType() != targetHolder.getItemViewType()) return false;

            int fromPosition = srcHolder.getAdapterPosition();
            int toPosition = targetHolder.getAdapterPosition();
            //这里的position也跟着一起变化了
            Collections.swap(mToDoList, fromPosition, toPosition);
            mAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;// 返回true表示处理了并可以换位置，返回false表示你没有处理并不能换位置。
        }

        //对Item的删除
        @Override
        public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
            int position = srcHolder.getAdapterPosition();
            String  Todo_name=mToDoList.get(position).getTodo();
            int Todo_id=mToDoList.get(position).getId();
            //Toast.makeText(showActivity.this, "你点击了确定按钮~", Toast.LENGTH_SHORT).show();
            Todo updatetodo=new Todo();
            updatetodo.setIs_delete(true);
            updatetodo.updateAll("id = ?",String.valueOf(Todo_id));
            List<Todo> test=LitePal.findAll(Todo.class);

            mToDoList.remove(position);
            mAdapter.notifyItemRemoved(position);



            int year = current_calendar.getYear();
            int month = current_calendar.getMonth();
            int day = current_calendar.getDay();
            String currentDate = String.valueOf(year) + "/" + String.format("%02d", month) + "/" +String.format("%02d", day);
            mToDoList= LitePal.where("is_delete = ? and date=?", "0",currentDate).
                    order("date desc").find(Todo.class);
            LinearLayout noInfoContent = findViewById(R.id.noInfoContent);
            if(mToDoList.size() == 0){
                noInfoContent.setVisibility(View.VISIBLE);
            }else{
                noInfoContent.setVisibility(View.INVISIBLE);
            }


        }
    };
    public void onClick_Dialog(View view){//监听事件
        switch(view.getId()){
            case R.id.fab:

                int year = current_calendar.getYear();
                int month = current_calendar.getMonth();
                int day = current_calendar.getDay();
                Date currentDate = new Date(year, month, day);

                AddTodoDialog addTodoDialog = new AddTodoDialog(currentDate);
                //设置添加后监听函数，刷新recyclerView
                addTodoDialog.setOnTodoAddListener(new OnTodoAddListener() {
                    @Override
                    public void onTodoAdd() {
                        onResume();
                    }
                } );
                addTodoDialog.show(getSupportFragmentManager(),"tag");//显示对话框

                //在这里顺便监听一下数据库的内容
                List<Todo> testlist;
                testlist=LitePal.findAll(Todo.class);
                for(Todo todo:mToDoList){
                    Log.d("MainActivity","taskname is"+todo.getTodo());
                    Log.d("MainActivity","这件事情是否已完成"+todo.getIs_done());
                }
                break;
            default: break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int year = current_calendar.getYear();
        int month = current_calendar.getMonth();
        int day = current_calendar.getDay();
        String currentDate = String.valueOf(year) + "/" + String.format("%02d", month) + "/" +String.format("%02d", day);


        mToDoList= LitePal.where("is_delete = ? and date=?", "0",currentDate).
                order("date desc").find(Todo.class);
        swap_position();
        init_is_done();//已完成的打钩
        mAdapter.notifyDataSetChanged(mToDoList);

        LinearLayout noInfoContent = findViewById(R.id.noInfoContent);
        if(mToDoList.size() == 0){
            noInfoContent.setVisibility(View.VISIBLE);
        }else{
            noInfoContent.setVisibility(View.INVISIBLE);
        }
    }
}


