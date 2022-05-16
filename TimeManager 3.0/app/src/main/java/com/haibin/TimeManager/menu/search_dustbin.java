package com.haibin.TimeManager.menu;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import com.haibin.TimeManager.showActivity;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.touch.OnItemStateChangedListener;

import org.litepal.LitePal;

import java.util.Collections;
import java.util.List;

public class search_dustbin extends AppCompatActivity {
private Toolbar mToolbar;
    protected SearchView mSearchView = null;
    String search_str=null;
    protected List<Todo> HistoryList;
    protected SwipeRecyclerView mRecyclerView;
    protected LinearLayoutManager mLayoutManager=new LinearLayoutManager(this);
    protected RecyclerView.ItemDecoration mItemDecoration;
    protected DragTouchAdapter mAdapter;
    private LocalReceiver localReceiver;    //本地广播接收者
    private LocalBroadcastManager localBroadcastManager;   //本地广播管理者   可以用来注册广播
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_dustbin);
        mToolbar=findViewById(R.id.toolbar_dustbin);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSearchView = (SearchView) findViewById(R.id.searchView);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchHistory();
                Log.w("TAG8","search history change");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    search_str=newText;
                }else{
                    HistoryList= LitePal.where("is_delete = ?", "1").
                            order("date desc").find(Todo.class);
                    mAdapter.notifyDataSetChanged(HistoryList);
                }
                return true;
            }
        });
        mRecyclerView=findViewById(R.id.recycler_view_dustbin);
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

        //注册广播接收器
        localBroadcastManager= LocalBroadcastManager.getInstance(this);
        localReceiver=new LocalReceiver();
        intentFilter=new IntentFilter("myaction5");
        localBroadcastManager.registerReceiver(localReceiver,intentFilter);

        ImageButton button_clean = (ImageButton)findViewById(R.id.imageButton2);
        button_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(search_dustbin.this)
                        .setMessage("确定彻底清空垃圾箱？")
                        .setNegativeButton("确定清空", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LitePal.deleteAll(Todo.class,"is_delete = ?","1");
                                HistoryList.clear();
                                mAdapter.notifyDataSetChanged(HistoryList);
                                Toast.makeText(search_dustbin.this, "现在垃圾站被清空。", Toast.LENGTH_SHORT).show();
                                LinearLayout noInfoContent = findViewById(R.id.noInfoContent_dustbin);
                                if(HistoryList.size() == 0){
                                    noInfoContent.setVisibility(View.VISIBLE);
                                }else{
                                    noInfoContent.setVisibility(View.INVISIBLE);
                                }
                            }
                        })
                        .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ((DragTouchAdapter)mAdapter).updateItemsData(HistoryList);//取消
                            }
                        })
                        .show();
            }
        });

        ImageButton button_todo = (ImageButton)findViewById(R.id.button_todo);
        button_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(search_dustbin.this, showActivity.class);
                startActivity(intent);
            }
        });

        ImageButton button_calendar = (ImageButton)findViewById(R.id.button_calendar);
        button_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(search_dustbin.this, FullActivity.class);
                startActivity(intent);
            }
        });

        ImageButton button_clock = (ImageButton)findViewById(R.id.button_clock);
        button_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(search_dustbin.this, PomodoroActivity.class);
                startActivity(intent);
            }
        });

        ImageButton button_statistics = (ImageButton)findViewById(R.id.button_statistics);
        button_statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(search_dustbin.this, StatisticsActivity.class);
                startActivity(intent);
            }
        });



    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(2,getIntent());
            finish();
        }
        return true;
    }

    public void SearchHistory()
    {

        Log.w("TAG6","search history begin"+search_str);
        HistoryList= LitePal.where("is_delete = ? and todo like ?", "1","%"+search_str+"%").
                order("date desc").find(Todo.class);
//        HistoryList=LitePal.findAll(Todo.class);
        Log.w("TAG7","search history over"+HistoryList.size());
        mAdapter.notifyDataSetChanged(HistoryList);

    }

        //对Item的删除



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
        if("myaction5".equals(action)){
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
                        ContextCompat.getColor(search_dustbin.this, R.color.white_pressed));
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_SWIPE) {
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
                // 在手松开的时候还原背景。
                ViewCompat.setBackground(viewHolder.itemView,
                        ContextCompat.getDrawable(search_dustbin.this, R.drawable.select_white));
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

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
            int position = srcHolder.getAdapterPosition();
            String todoname = HistoryList.get(position).getTodo();
            int id = HistoryList.get(position).getId();
            //在这里弹出一个对话框
            new AlertDialog.Builder(search_dustbin.this)
                    .setMessage("确定要恢复或彻底清除该事件？" + todoname)
                    .setPositiveButton("确定恢复", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Todo updatetodo = new Todo();
                            updatetodo.setToDefault("is_delete");
                            updatetodo.updateAll("id = ?", String.valueOf(id));
                            HistoryList.remove(position);
                            //LitePal.deleteAll(Todo.class,"todo=?",todo);
                            mAdapter.notifyItemRemoved(position);
                            //Toast.makeText(search_dustbin.this, "现在的第" + position + "条被恢复。", Toast.LENGTH_SHORT).show();
                            LinearLayout noInfoContent = findViewById(R.id.noInfoContent_dustbin);
                            if(HistoryList.size() == 0){
                                noInfoContent.setVisibility(View.VISIBLE);
                            }else{
                                noInfoContent.setVisibility(View.INVISIBLE);
                            }
                        }
                    })
                    .setNegativeButton("确定清除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LitePal.deleteAll(Todo.class, "id=?", String.valueOf(id));
                            HistoryList.remove(position);
                            mAdapter.notifyItemRemoved(position);
                            //Toast.makeText(search_dustbin.this, "现在的第" + position + "条被清除。", Toast.LENGTH_SHORT).show();
                            LinearLayout noInfoContent = findViewById(R.id.noInfoContent_dustbin);
                            if(HistoryList.size() == 0){
                                noInfoContent.setVisibility(View.VISIBLE);
                            }else{
                                noInfoContent.setVisibility(View.INVISIBLE);
                            }
                        }
                    })
                    .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ((DragTouchAdapter) mAdapter).updateItemsData(HistoryList);//取消
                        }
                    })
                    .show();

        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        HistoryList= LitePal.where("is_delete = ? ", "1").
                order("date desc").find(Todo.class);
        swap_position();
        init_is_done();//已完成的打钩
        mAdapter.notifyDataSetChanged(HistoryList);


        LinearLayout noInfoContent = findViewById(R.id.noInfoContent_dustbin);
        if(HistoryList.size() == 0){
            noInfoContent.setVisibility(View.VISIBLE);
        }else{
            noInfoContent.setVisibility(View.INVISIBLE);
        }
    }
}