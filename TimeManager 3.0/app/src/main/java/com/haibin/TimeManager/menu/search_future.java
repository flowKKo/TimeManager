package com.haibin.TimeManager.menu;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haibin.TimeManager.Adapter.DragTouchAdapter;
import com.haibin.TimeManager.EditTodoDialog.EditTodoDialog;
import com.haibin.TimeManager.EditTodoDialog.OnTodoEditListener;
import com.haibin.TimeManager.Pomodoro.PomodoroActivity;
import com.haibin.TimeManager.R;
import com.haibin.TimeManager.Statistics.StatisticsActivity;
import com.haibin.TimeManager.Todo.Todo;
import com.haibin.TimeManager.calendar.full.FullActivity;
import com.haibin.TimeManager.cancel_delete_fragment;
import com.haibin.TimeManager.showActivity;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.touch.OnItemStateChangedListener;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

//import org.litepal.crud.DataSupport;


public class search_future extends AppCompatActivity {

    protected Toolbar mToolbar;
    protected SearchView mSearchView = null;
    String search_str=null;
    protected List<Todo> HistoryList;
    protected SwipeRecyclerView mRecyclerView;
    protected LinearLayoutManager mLayoutManager=new LinearLayoutManager(this);
    protected DragTouchAdapter mAdapter;

    private Calendar calendar= Calendar.getInstance(Locale.CHINA);//创建一个日历
    private String mdate;
    private LocalReceiver localReceiver;   //本地广播接收者
    private LocalBroadcastManager localBroadcastManager;   //本地广播管理者   可以用来注册广播
    private IntentFilter intentFilter;
    private IntentFilter intentFilter1;
    private Timer timer;
    private TimerTask timerTask;
    cancel_delete_fragment cancelDeleteFragment;
    protected Todo tempTodo;
    boolean fragment_flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w("LAG_FU","0");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_future);
        mToolbar=findViewById(R.id.toolbar_future);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSearchView = (SearchView) findViewById(R.id.searchView_future);

        mdate = Integer.toString(calendar.get(Calendar.YEAR))+'/'+
                String.format("%02d",calendar.get(Calendar.MONTH)+1)+'/'+
                String.format("%02d",calendar.get(Calendar.DAY_OF_MONTH));

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchHistory();
                Log.w("TAG8","search future change");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    search_str=newText;
                }else{
                    HistoryList= LitePal.where("is_delete = ? and date > ?", "0", mdate).
                            order("date desc").find(Todo.class);
                    mAdapter.notifyDataSetChanged(HistoryList);
                }
                return true;
            }
        });
        mRecyclerView=findViewById(R.id.recycler_view_future);
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
  mRecyclerView.setOnItemMoveListener(getItemMoveListener());// 监听拖拽和侧滑删除，更新UI和数据源。
        //注册广播接收器
        localBroadcastManager= LocalBroadcastManager.getInstance(this);
        localReceiver=new LocalReceiver();
        intentFilter=new IntentFilter("myaction3");
        localBroadcastManager.registerReceiver(localReceiver,intentFilter);
        intentFilter1=new IntentFilter("MyAction3");//cancel_delete
        localBroadcastManager.registerReceiver(localReceiver,intentFilter1);



        ImageButton button_todo = (ImageButton)findViewById(R.id.button_todo);
        button_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(search_future.this, showActivity.class);
                startActivity(intent);
            }
        });

        ImageButton button_calendar = (ImageButton)findViewById(R.id.button_calendar);
        button_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(search_future.this, FullActivity.class);
                startActivity(intent);
            }
        });

        ImageButton button_clock = (ImageButton)findViewById(R.id.button_clock);
        button_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(search_future.this, PomodoroActivity.class);
                startActivity(intent);
            }
        });

        ImageButton button_statistics = (ImageButton)findViewById(R.id.button_statistics);
        button_statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(search_future.this, StatisticsActivity.class);
                startActivity(intent);
            }
        });

        fragment_flag=false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(1,getIntent());
            if(fragment_flag)
            {
                timerTask.cancel();
                removeFragment2();
            }
            finish();
        }
        return true;
    }
    public void SearchHistory()
    {
        Log.w("TAG6","search future begin"+search_str);
        HistoryList= LitePal.where("todo like ? and is_delete = ? and date>?","%"+search_str+"%", "0",mdate).
                order("date desc").find(Todo.class);
//        HistoryList=LitePal.findAll(Todo.class);
        Log.w("TAG7","search future over"+HistoryList.size());
        mAdapter.notifyDataSetChanged(HistoryList);

    }

    //这个函数会让已经完成的事件标记为已完成
    public void init_is_done(){
        for(int i=0;i<HistoryList.size();i++){
            //已完成
            mAdapter.flag[i]=false;//先初始化为全未完成

        }
        for(int i=0;i<HistoryList.size();i++){
            if(HistoryList.get(i).getIs_done()){//已完成
                mAdapter.flag[i]=true;
            }
        }


    }
    public void swap_position(){
        int k=HistoryList.size();
        for(int i=0;i<k;){
            if(HistoryList.get(i).getIs_done()){
                k--;
                for (int j = i; j <HistoryList.size()-1; j++) {
                    Collections.swap(HistoryList, j, j + 1);//交换数据源两个数据的位置
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
            if("myaction3".equals(action)){
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
                    int toPosition = HistoryList.size() - 1;
                    if (fromPosition < toPosition) {
                        //从上往下拖动，每滑动一个item，都将list中的item向下交换，向上滑同理。
                        for (int i = fromPosition; i < toPosition; i++) {
                            Collections.swap(HistoryList, i, i + 1);//交换数据源两个数据的位置
                            mAdapter.notifyItemMoved(i, i + 1);
                        }
                    } else {
                        for (int i = fromPosition; i > toPosition; i--) {
                            Collections.swap(HistoryList, i, i - 1);//交换数据源两个数据的位置
                            mAdapter.notifyItemMoved(i, i - 1);
                        }
                    }
                }
                else{
                    int toPosition = 0;
                    if (fromPosition < toPosition) {
                        //从上往下拖动，每滑动一个item，都将list中的item向下交换，向上滑同理。
                        for (int i = fromPosition; i < toPosition; i++) {
                            Collections.swap(HistoryList, i, i + 1);//交换数据源两个数据的位置
                            mAdapter.notifyItemMoved(i, i + 1);
                        }
                    } else {
                        for (int i = fromPosition; i > toPosition; i--) {
                            Collections.swap(HistoryList, i, i - 1);//交换数据源两个数据的位置
                            mAdapter.notifyItemMoved(i, i - 1);
                        }
                    }
                }
            }
            if("MyAction3".equals(action)){
                //Toast.makeText(search_future.this, "点击了撤销按钮", Toast.LENGTH_LONG).show();
                //本来在数据库中设置为已删除，现在恢复为未删除，
                int Todo_id=tempTodo.getId();
                Todo UpdateTodo=new Todo();
                UpdateTodo.setToDefault("is_delete");
                UpdateTodo.updateAll("id = ?",String.valueOf(Todo_id));
                //放入原来的位置
                onResume();
                removeFragment2();
                LinearLayout noInfoContent = findViewById(R.id.noInfoContent_future);
                if(HistoryList.size() == 0){
                    noInfoContent.setVisibility(View.VISIBLE);
                }else{
                    noInfoContent.setVisibility(View.INVISIBLE);
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
        EditTodoDialog editTodoDialog = new EditTodoDialog(HistoryList.get(position).getId());
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
                        ContextCompat.getColor(search_future.this, R.color.white_pressed));
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_SWIPE) {
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
                // 在手松开的时候还原背景。
                ViewCompat.setBackground(viewHolder.itemView,
                        ContextCompat.getDrawable(search_future.this, R.drawable.select_white));
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
            Collections.swap(HistoryList, fromPosition, toPosition);
            mAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;// 返回true表示处理了并可以换位置，返回false表示你没有处理并不能换位置。
        }

        //对Item的删除
        @Override
        public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
            int position = srcHolder.getAdapterPosition();
            String  Todo_name=HistoryList.get(position).getTodo();
            tempTodo=HistoryList.get(position);
            int Todo_id=HistoryList.get(position).getId();
            Todo Update_Todo=new Todo();
            Update_Todo.setIs_delete(true);
            Update_Todo.updateAll("id = ?",String.valueOf(Todo_id));
            List<Todo> test=LitePal.findAll(Todo.class);

            HistoryList.remove(position);
            mAdapter.notifyItemRemoved(position);
            //传入当前todo内容及position,当按钮点击确定
            if(fragment_flag)
            {
                timerTask.cancel();
                removeFragment2();
            }
            cancelDeleteFragment = new cancel_delete_fragment();
            addFragment(cancelDeleteFragment, "fragment2");
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

            LinearLayout noInfoContent = findViewById(R.id.noInfoContent_future);
            if(HistoryList.size() == 0){
                noInfoContent.setVisibility(View.VISIBLE);
            }else{
                noInfoContent.setVisibility(View.INVISIBLE);
            }
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
        fragment_flag=true;
        androidx.fragment.app.FragmentManager manager=getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction transaction=manager.beginTransaction();
        transaction.add(R.id.fragment_container_future, fragment, tag);
        transaction.commit();
    }

    private void deleteFragment(Fragment fragment){
        fragment_flag=false;
        androidx.fragment.app.FragmentManager manager=getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction transaction=manager.beginTransaction();
        transaction.remove(fragment);
        transaction.commit();
    }

    private void removeFragment2() {
        fragment_flag=false;
        androidx.fragment.app.FragmentManager manager=getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag("fragment2");
        androidx.fragment.app.FragmentTransaction transaction=manager.beginTransaction();
        transaction.remove(fragment);
        transaction.commit();
    }



    @Override
    protected void onResume() {
        super.onResume();
        HistoryList= LitePal.where("is_delete = ? and date>?", "0",mdate).
                order("date desc").find(Todo.class);
        swap_position();
        init_is_done();//已完成的打钩
        mAdapter.notifyDataSetChanged(HistoryList);


        LinearLayout noInfoContent = findViewById(R.id.noInfoContent_future);
        if(HistoryList.size() == 0){
            noInfoContent.setVisibility(View.VISIBLE);
        }else{
            noInfoContent.setVisibility(View.INVISIBLE);
        }
    }
}