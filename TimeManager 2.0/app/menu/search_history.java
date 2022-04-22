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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haibin.TimeManager.Adapter.BaseAdapter;
import com.haibin.TimeManager.Adapter.DragTouchAdapter;
import com.haibin.TimeManager.R;
import com.haibin.TimeManager.Todo.Todo;
import com.haibin.TimeManager.activity.MainActivity;
import com.haibin.TimeManager.menu.search_dustbin;
import com.haibin.TimeManager.menu.search_history;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.touch.OnItemStateChangedListener;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

//import org.litepal.crud.DataSupport;


public class search_history extends AppCompatActivity {

    protected Toolbar mToolbar;
    protected SearchView mSearchView = null;
    String search_str=null;
    protected List<Todo> HistoryList;
    protected SwipeRecyclerView mRecyclerView;
    protected LinearLayoutManager mLayoutManager=new LinearLayoutManager(this);
    protected RecyclerView.ItemDecoration mItemDecoration;
    protected BaseAdapter mAdapter;

    private Calendar calendar= Calendar.getInstance(Locale.CHINA);//创建一个日历
    private String mdate;
    private LocalReceiver localReceiver;    //本地广播接收者
    private LocalBroadcastManager localBroadcastManager;   //本地广播管理者   可以用来注册广播
    private IntentFilter intentFilter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_history);
        mToolbar=findViewById(R.id.toolbar_history);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSearchView = (SearchView) findViewById(R.id.searchView_history);

        mdate = Integer.toString(calendar.get(Calendar.YEAR))+'/'+
                String.format("%02d",calendar.get(Calendar.MONTH)+1)+'/'+
                String.format("%02d",calendar.get(Calendar.DAY_OF_MONTH));

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
                    HistoryList= LitePal.where("is_delete = ? and date < ?","false",mdate).
                            order("date desc").find(Todo.class);
                    mAdapter.notifyDataSetChanged(HistoryList);
                }
                return true;
            }
        });
        mItemDecoration = createItemDecoration();
        mAdapter = createAdapter();

        mRecyclerView=findViewById(R.id.recycler_view_history);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(mItemDecoration);
        mRecyclerView.setOnItemClickListener(this::onItemClick);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLongPressDragEnabled(true); // 长按拖拽，默认关闭。
        mRecyclerView.setItemViewSwipeEnabled(true); // 滑动删除，默认关闭。
        mRecyclerView.setOnItemStateChangedListener(mOnItemStateChangedListener); // 监听Item的手指状态，拖拽、侧滑、松开。
        mRecyclerView.setOnItemMoveListener(getItemMoveListener());// 监听拖拽和侧滑删除，更新UI和数据源。
        //注册广播接收器
        localBroadcastManager= LocalBroadcastManager.getInstance(this);
        localReceiver=new search_history.LocalReceiver();
        intentFilter=new IntentFilter("myaction");
        localBroadcastManager.registerReceiver(localReceiver,intentFilter);
        HistoryList= LitePal.where("is_delete = ? and date<?","false",mdate).
                order("date desc").find(Todo.class);
        mAdapter.notifyDataSetChanged(HistoryList);
    }
    protected BaseAdapter createAdapter() {
        return new DragTouchAdapter(this,mRecyclerView);
    }
    protected int getContentView() {
        return R.layout.activity_main_temp;
    }
    protected RecyclerView.ItemDecoration createItemDecoration() {
        return new DefaultItemDecoration(ContextCompat.getColor(this, R.color.divider_color));
    }
    public void onItemClick(View itemView, int position) {
        Toast.makeText(this, "第" + position + "个", Toast.LENGTH_SHORT).show();
        //在这里调用修改信息
        EditTodoDialog editTodoDialog = new EditTodoDialog(HistoryList.get(position).getId());
        editTodoDialog.setOnTodoEditListener(new OnTodoEditListener() {
            @Override
            public void onTodoEdit() {//刷新界面
                HistoryList= LitePal.where("todo like ? and is_delete = ? and date < ?","%"+search_str+"%","false",mdate).
                        order("date desc").find(Todo.class);
                mAdapter.notifyDataSetChanged(HistoryList);
            }
        });
        editTodoDialog.show(getSupportFragmentManager(),"EditDialog");
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(1,getIntent());
            finish();
        }
        return true;
    }

    public void SearchHistory()
    {
        Log.w("TAG6","search history begin"+search_str);
        HistoryList= LitePal.where("todo like ? and is_delete = ? and date<?","%"+search_str+"%","false",mdate).
                order("date desc").find(Todo.class);
//        HistoryList=LitePal.findAll(Todo.class);
        Log.w("TAG7","search history over"+HistoryList.size());
        mAdapter.notifyDataSetChanged(HistoryList);

    }
    private class LocalReceiver extends BroadcastReceiver {//消息从adapter收到了
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if("myaction".equals(action)){
                Log.d( "消息：" + intent.getStringExtra( "data" )  , "线程： " + Thread.currentThread().getName() ) ;
            }
            String todoname=intent.getStringExtra("todoname");
            boolean isdone=intent.getBooleanExtra("is_done",false);

            //在这里出了问题（已改正），position应该是由getadapterposition函数得到的
            int fromPosition=intent.getIntExtra("position",0);

            //成功更新数据库is_done
            Todo updatetodo=new Todo();
            if(isdone) updatetodo.setIs_done(isdone);
            else updatetodo.setToDefault("is_done");//当set为false时应调用该函数
            updatetodo.updateAll("todo=?",todoname);

            //接下来要做的是将这个todo的position移到最下面去
            //分为两种情况，当is_done变为true时和当is_done变为false时
            if(isdone) {
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



    //取消广播的注册
    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);
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
                        ContextCompat.getColor(search_history.this, R.color.white_pressed));
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_SWIPE) {
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
                // 在手松开的时候还原背景。
                ViewCompat.setBackground(viewHolder.itemView,
                        ContextCompat.getDrawable(search_history.this, R.drawable.select_white));
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
            String  todoname=HistoryList.get(position).getTodo();
            //在这里弹出一个对话框
            new AlertDialog.Builder(search_history.this)
                    .setMessage("确定要删除该事件？"+todoname)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(search_history.this, "你点击了确定按钮~", Toast.LENGTH_SHORT).show();
                            Todo updatetodo=new Todo();
                            updatetodo.setIs_delete("true");
                            updatetodo.updateAll("todo=?",todoname);

                            HistoryList.remove(position);
                            //LitePal.deleteAll(Todo.class,"todo=?",todo);
                            mAdapter.notifyItemRemoved(position);

                            Toast.makeText(search_history.this, "现在的第" + position + "条被删除。", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ((DragTouchAdapter)mAdapter).updateItemsData(HistoryList);//取消删除
                        }
                    })//即使点了取消之后事件依然被删除啊啊，要不然试试页面刷新
                    .show();
//            //依然是根据todo的名字更新数据库
//
//            Todo updatetodo=new Todo();
//            updatetodo.setIs_delete(true);
//            updatetodo.updateAll("todo=?",todoname);
//
//            mToDoList.remove(position);
//            //LitePal.deleteAll(Todo.class,"todo=?",todo);
//            mAdapter.notifyItemRemoved(position);
//
//            Toast.makeText(MainActivity.this, "现在的第" + position + "条被删除。", Toast.LENGTH_SHORT).show();
        }

    };
//    public void refresh(){  //刷新recyclerView
//        mToDoList=LitePal.findAll(Todo.class);
    //recyclerView=findViewById(R.id.recycler_view);
//        TaskAdapter adapter=new TaskAdapter(toDoList);//通过adapter显示主界面的recyclerview
//        recyclerView.setAdapter(adapter);

    //    }
    public void onClick_Dialog(View view){//监听事件

        switch(view.getId()){
            case R.id.fab:
                AddTodoDialog addTodoDialog = new AddTodoDialog();
                //设置添加后监听函数，刷新recyclerView
                addTodoDialog.setOnTodoAddListener(new OnTodoAddListener() {
                    @Override
                    public void onTodoAdd() {
                        HistoryList=LitePal.findAll(Todo.class);
                        mAdapter.notifyDataSetChanged(HistoryList);
                    }
                } );
                addTodoDialog.show(getSupportFragmentManager(),"tag");//显示对话框

                break;
            default: break;
        }
    }
}