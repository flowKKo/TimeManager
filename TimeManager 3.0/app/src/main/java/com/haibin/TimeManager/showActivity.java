package com.haibin.TimeManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.haibin.TimeManager.Adapter.DragTouchAdapter;
import com.haibin.TimeManager.AddTodoDialog.AddTodoDialog;
import com.haibin.TimeManager.AddTodoDialog.OnTodoAddListener;
import com.haibin.TimeManager.Dao.dao.Local_userDao;
import com.haibin.TimeManager.EditTodoDialog.EditTodoDialog;
import com.haibin.TimeManager.EditTodoDialog.OnTodoEditListener;
import com.haibin.TimeManager.Pomodoro.PomodoroActivity;
import com.haibin.TimeManager.Statistics.StatisticsActivity;
import com.haibin.TimeManager.Todo.Date;
import com.haibin.TimeManager.Todo.Todo;
import com.haibin.TimeManager.calendar.full.FullActivity;
import com.haibin.TimeManager.menu.help_for_user;
import com.haibin.TimeManager.menu.load;
import com.haibin.TimeManager.menu.search_dustbin;
import com.haibin.TimeManager.menu.search_future;
import com.haibin.TimeManager.menu.search_history;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.haibin.TimeManager.calendar.base.activity.BaseActivity;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.touch.OnItemStateChangedListener;

import org.litepal.LitePal;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class showActivity extends BaseActivity implements
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnCalendarInterceptListener,
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
    private IntentFilter intentFilter1;
    private Timer timer;
    private TimerTask timerTask;
    cancel_delete_fragment cancelDeleteFragment;
    protected Todo tempTodo;

    private NavigationView navView;
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
        intentFilter1=new IntentFilter("MyAction1");//cancel_delete
        localBroadcastManager.registerReceiver(localReceiver,intentFilter);
        localBroadcastManager.registerReceiver(localReceiver,intentFilter1);


        if(!XXPermissions.isGranted(this, Permission.Group.CALENDAR)){
            //还没有授权，请求授权
            XXPermissions.with(this)
                    .permission(Permission.Group.CALENDAR)
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            if(all){//所有权限获取
                                Toast.makeText(getApplicationContext(),"授权成功",Toast.LENGTH_SHORT).show();
                            }
                            else{//部分权限获取
                                Toast.makeText(getApplicationContext(),"部分授权成功",Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onDenied(List<String> permissions, boolean never){
                            if(never){//被永久拒绝授权
                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                Toast.makeText(getApplicationContext(),"请授予权限，否则程序无法正常运行",Toast.LENGTH_LONG).show();
                                XXPermissions.startPermissionActivity(getApplicationContext(),permissions);
                            }
                            else{//获取权限失败
                                Toast.makeText(getApplicationContext(),"授权失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        navView = findViewById(R.id.nav_view);
        View headerView = navView.getHeaderView(0);
        ImageButton slide_open=(ImageButton)findViewById(R.id.slide_menu);
        slide_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout mDrawerLayout=(DrawerLayout)findViewById((R.id.drawer_layout));
                mDrawerLayout.openDrawer(navView);
            }
        });
        ImageButton user_head=(ImageButton)headerView.findViewById(R.id.user_enter);

        user_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_load = new Intent(showActivity.this, load.class);
                startActivity(intent_load);
            }
        });
        navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.history_activity:
                    Log.w("TAG4","entry history");
                    Intent intent_history = new Intent(showActivity.this, search_history.class);
                    startActivityForResult(intent_history,1);
                    break;
                case R.id.future_activity:
                    Log.w("TAG4","entry history");
                    Intent intent_future = new Intent(showActivity.this, search_future.class);
                    startActivityForResult(intent_future,3);
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
                    break;
            }
            return true;
        });

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
        mCalendarView.setOnCalendarSelectListener(this);

        //设置日期拦截事件，仅适用单选模式，当前无效
        mCalendarView.setOnCalendarInterceptListener(this);

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
    public void onClick(View v){

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
    public boolean onCalendarIntercept(Calendar calendar) {
        Log.e("onCalendarIntercept", calendar.toString());
        int day = calendar.getDay();
        return day == 1 || day == 3 || day == 6 || day == 11 || day == 12 || day == 15 || day == 20 || day == 26;
    }
    @Override
    public void onCalendarInterceptClick(Calendar calendar, boolean isClick) {
        //Toast.makeText(this, calendar.toString() + "拦截不可点击", Toast.LENGTH_SHORT).show();
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

        //从系统权限设置页返回判断
        if (requestCode == XXPermissions.REQUEST_CODE) {
            if (XXPermissions.isGranted(this, Permission.Group.CALENDAR)) {
                //用户已授予权限
                Toast.makeText(this,"授权成功",Toast.LENGTH_SHORT).show();
            } else {
                //用户没有授予权限
                Toast.makeText(this,"授权失败，请进入设置手动授权，否则程序无法正常运行",Toast.LENGTH_LONG).show();
            }
        }
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
        // 分两段对列表进行排序  好像不能部分排序 那就全排好序再把完成的放到最下面？
        Collections.sort(mToDoList);
        // 将已完成的事项放在列表最末端
        int k=mToDoList.size();
        for(int i=0;i<k;){
            if(mToDoList.get(i).getIs_done()){
                k--;
                for (int j = i; j <mToDoList.size()-1; j++) {
                    Collections.swap(mToDoList, j, j + 1);//交换数据源两个数据的位置
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
                    onResume();
                }
                else{
                    onResume();
                }
            }

            if("MyAction1".equals(action)){
                //Toast.makeText(showActivity.this, "点击了撤销按钮", Toast.LENGTH_LONG).show();
                //本来在数据库中设置为已删除，现在恢复为未删除，
                List<Todo> TestList1=LitePal.findAll(Todo.class);

                int Todo_id=tempTodo.getId();
                Todo UpdateTodo=new Todo();
                UpdateTodo.setToDefault("is_delete");
                UpdateTodo.updateAll("id = ?",String.valueOf(Todo_id));
                List<Todo> TestList=LitePal.findAll(Todo.class);
                //放入原来的位置
                onResume();
                removeFragment2();
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
            int fix = 0; //对于已完成的事件，他们在列表里并不处于最底端
            for (int i = 0; i < mToDoList.size(); i++) {
                if (mToDoList.get(i).getIs_done() == false) {
                    fix++;
                }
            }
            if (fromPosition >= fix || toPosition >= fix) return false;
            else {
                //这里的position也跟着一起变化
                //交换pos位置
                int from = mToDoList.get(fromPosition).getPos();
                int to=mToDoList.get(toPosition).getPos();
                Todo UpdateTodo_from = new Todo();
                if(to==0) UpdateTodo_from.setToDefault("pos");
                else UpdateTodo_from.setPos(to);
                UpdateTodo_from.updateAll("id=?", String.valueOf(mToDoList.get(fromPosition).getId()));
                Todo UpdateTodo_to = new Todo();
                if(to==0) UpdateTodo_to.setToDefault("pos");
                else UpdateTodo_to.setPos(from);
                UpdateTodo_to.updateAll("id=?", String.valueOf(mToDoList.get(toPosition).getId()));
                Collections.swap(mToDoList, fromPosition, toPosition);
                mAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;// 返回true表示处理了并可以换位置，返回false表示你没有处理并不能换位置。
            }
        }

        //对Item的删除
        @Override
        public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {

            if(timer!=null) timer.cancel();
            if(timerTask!=null) timerTask.cancel();
            if(cancelDeleteFragment!=null)deleteFragment(cancelDeleteFragment);


            int position = srcHolder.getAdapterPosition();
            String  Todo_name=mToDoList.get(position).getTodo();
            tempTodo=mToDoList.get(position);
            int Todo_id=mToDoList.get(position).getId();
            Todo Update_Todo=new Todo();
            Update_Todo.setIs_delete(true);
            Update_Todo.updateAll("id = ?",String.valueOf(Todo_id));
            List<Todo> test=LitePal.findAll(Todo.class);

            mToDoList.remove(position);
            mAdapter.notifyItemRemoved(position);

            LinearLayout noInfoContent = findViewById(R.id.noInfoContent);
            if(mToDoList.size() == 0){
                noInfoContent.setVisibility(View.VISIBLE);
            }else{
                noInfoContent.setVisibility(View.INVISIBLE);
            }


            //传入当前todo内容及position,当按钮点击确定
            cancelDeleteFragment = new cancel_delete_fragment();
            addFragment(cancelDeleteFragment, "fragment1");
            //设置定时，如10s后cancel_delete Fragment从界面上取消
            timerTask =new TimerTask() {
                @Override
                public void run() {
                    Message msg=new Message();
                    msg.what=0;
                    handler.sendMessage(msg);
                }
            };
            timer=new Timer();
            timer.schedule(timerTask,5000);

        }
    };


    //这是接收回来处理的消息
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler()

    {
        public void handleMessage(Message msg) {
            deleteFragment(cancelDeleteFragment);
        }

    };
    //添加和删除cancel_delete Fragment
    private void addFragment(Fragment fragment, String tag) {
        androidx.fragment.app.FragmentManager manager=getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction transaction=manager.beginTransaction();
        transaction.add(R.id.fragment_container, fragment, tag);
        transaction.commitAllowingStateLoss();
    }
    private void deleteFragment(Fragment fragment){
        androidx.fragment.app.FragmentManager manager=getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction transaction=manager.beginTransaction();
        transaction.remove(fragment);
        transaction.commitAllowingStateLoss();
    }
    private void removeFragment2() {
        androidx.fragment.app.FragmentManager manager=getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag("fragment1");
        androidx.fragment.app.FragmentTransaction transaction=manager.beginTransaction();
        transaction.remove(fragment);
        transaction.commitAllowingStateLoss();
    }
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

        //刷新登录状态
        navView = findViewById(R.id.nav_view);
        View headerView = navView.getHeaderView(0);
        TextView textView = (TextView)headerView.findViewById(R.id.bar_user_name);
        String str = Local_userDao.getThisUserName();
        if (str.equals("")) {
            textView.setText("未登录");
        }
        else {
            textView.setText("  " + str);
        }
        //刷新登录状态

    }
}


