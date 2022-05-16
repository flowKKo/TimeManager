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
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.haibin.TimeManager.Adapter.BaseAdapter;
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
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.touch.OnItemStateChangedListener;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

import org.litepal.LitePal;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class showDailyTodoActivity extends AppCompatActivity {

    protected SwipeRecyclerView mRecyclerView;
    protected LinearLayoutManager mLayoutManager=new LinearLayoutManager(this);
    protected RecyclerView.ItemDecoration mItemDecoration;
    protected List<Todo> mToDoList;
    protected DragTouchAdapter mAdapter;
    private showDailyTodoActivity.LocalReceiver localReceiver;   //本地广播接收者
    private LocalBroadcastManager localBroadcastManager;   //本地广播管理者   可以用来注册广播
    private IntentFilter intentFilter;
    private String curDate;

    private IntentFilter intentFilter1;
    private Timer timer;
    private TimerTask timerTask;
    cancel_delete_fragment cancelDeleteFragment;
    protected Todo tempTodo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_daily_todo);

        Intent intent = getIntent();
        String currentDate = intent.getStringExtra("extra_data");
        curDate = currentDate;
        String showDate = "日程安排  " + currentDate.substring(6, 7) + "月" + currentDate.substring(8, 10) + "日";
        this.setTitle(showDate);

        mRecyclerView=findViewById(R.id.recycler_view);
        mItemDecoration = new DefaultItemDecoration(ContextCompat.getColor(this, R.color.divider_color));
        mAdapter = new DragTouchAdapter(this,mRecyclerView);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(mItemDecoration);
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
        localReceiver=new showDailyTodoActivity.LocalReceiver();
        intentFilter=new IntentFilter("myaction6");
        intentFilter1=new IntentFilter("MyAction5");//cancel_delete
        localBroadcastManager.registerReceiver(localReceiver,intentFilter);
        localBroadcastManager.registerReceiver(localReceiver,intentFilter1);



        mToDoList= LitePal.where("is_delete = ? and date=?", "0",curDate).
                order("date desc").find(Todo.class);
        mAdapter.notifyDataSetChanged(mToDoList);
        LinearLayout noInfoContent = findViewById(R.id.noInfoContent);
        if(mToDoList.size() == 0){
            noInfoContent.setVisibility(View.VISIBLE);
        }else{
            noInfoContent.setVisibility(View.INVISIBLE);
        }


        ImageButton button_todo = (ImageButton)findViewById(R.id.button_todo);
        button_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(showDailyTodoActivity.this, showActivity.class);
                startActivity(intent);
            }
        });

        ImageButton button_calendar = (ImageButton)findViewById(R.id.button_calendar);
        button_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(showDailyTodoActivity.this, FullActivity.class);
                startActivity(intent);
            }
        });
        button_calendar.setBackgroundColor(Color.parseColor("#D7D7D7"));

        ImageButton button_clock = (ImageButton)findViewById(R.id.button_clock);
        button_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(showDailyTodoActivity.this, PomodoroActivity.class);
                startActivity(intent);
            }
        });

        ImageButton button_statistics = (ImageButton)findViewById(R.id.button_statistics);
        button_statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(showDailyTodoActivity.this, StatisticsActivity.class);
                startActivity(intent);
            }
        });
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
            if("myaction6".equals(action)){
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
                        ContextCompat.getColor(showDailyTodoActivity.this, R.color.white_pressed));
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_SWIPE) {
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
                // 在手松开的时候还原背景。
                ViewCompat.setBackground(viewHolder.itemView,
                        ContextCompat.getDrawable(showDailyTodoActivity.this, R.drawable.select_white));
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

                int year = Integer.valueOf(curDate.substring(0, 4));
                int month = Integer.valueOf(curDate.substring(6, 7));
                int day = Integer.valueOf(curDate.substring(8, 10));
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
        int year = Integer.valueOf(curDate.substring(0, 4));
        int month = Integer.valueOf(curDate.substring(6, 7));
        int day = Integer.valueOf(curDate.substring(8, 10));
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
