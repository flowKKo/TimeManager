package com.haibin.TimeManager.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haibin.TimeManager.Adapter.DragTouchAdapter;
import com.haibin.TimeManager.R;
import com.haibin.TimeManager.Todo.Todo;
import com.haibin.TimeManager.menu.search_dustbin;
import com.haibin.TimeManager.menu.search_history;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.touch.OnItemStateChangedListener;

import org.litepal.LitePal;

import java.util.Collections;
import java.util.List;

//import org.litepal.crud.DataSupport;


public class MainActivity extends BaseActivity {
    private LocalReceiver localReceiver;    //本地广播接收者
    private LocalBroadcastManager localBroadcastManager;   //本地广播管理者   可以用来注册广播
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecyclerView.setAdapter(mAdapter);
        //在checkbox选中时，mTodolist也跟着发生了变化，然后发布广播消息，更新数据库的内容
        mAdapter.notifyDataSetChanged(mToDoList);

        mRecyclerView.setLongPressDragEnabled(true); // 长按拖拽，默认关闭。
        mRecyclerView.setItemViewSwipeEnabled(true); // 滑动删除，默认关闭。
        mRecyclerView.setOnItemStateChangedListener(mOnItemStateChangedListener); // 监听Item的手指状态，拖拽、侧滑、松开。
        mRecyclerView.setOnItemMoveListener(getItemMoveListener());// 监听拖拽和侧滑删除，更新UI和数据源。

        FloatingActionButton adddata=findViewById(R.id.fab);
        adddata.setOnClickListener(this::onClick_Dialog);//监听事件

        //注册广播接收器
        localBroadcastManager= LocalBroadcastManager.getInstance(this);
        localReceiver=new LocalReceiver();
        intentFilter=new IntentFilter("myaction");
        localBroadcastManager.registerReceiver(localReceiver,intentFilter);
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
        mToDoList= LitePal.where("is_delete = ?","false").
                order("date desc").find(Todo.class);
        mAdapter.notifyDataSetChanged(mToDoList);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.history_activity:
                Log.w("TAG4","entry history");
                Intent intent_history = new Intent(MainActivity.this, search_history.class);
                startActivityForResult(intent_history,1);
                break;
            case R.id.help:
                Toast.makeText(this, "You clicked activity", Toast.LENGTH_LONG).show();
                break;
            case R.id.dustbin_activity:
                Log.w("TAG5","entry dustbin");
                Intent intent_dustbin = new Intent(MainActivity.this, search_dustbin.class);
                startActivityForResult(intent_dustbin,2);
                break;
            default:
        }
        return true;
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
                        ContextCompat.getColor(MainActivity.this, R.color.white_pressed));
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_SWIPE) {
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
                // 在手松开的时候还原背景。
                ViewCompat.setBackground(viewHolder.itemView,
                        ContextCompat.getDrawable(MainActivity.this, R.drawable.select_white));
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
            String  todoname=mToDoList.get(position).getTodo();
            //在这里弹出一个对话框
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("确定要删除该事件？"+todoname)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "你点击了确定按钮~", Toast.LENGTH_SHORT).show();
                            Todo updatetodo=new Todo();
                            updatetodo.setIs_delete("true");
                            updatetodo.updateAll("todo=?",todoname);

                            mToDoList.remove(position);
                            //LitePal.deleteAll(Todo.class,"todo=?",todo);
                            mAdapter.notifyItemRemoved(position);

                            Toast.makeText(MainActivity.this, "现在的第" + position + "条被删除。", Toast.LENGTH_SHORT).show();
                            Log.w("TAG0",""+mToDoList.size());
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ((DragTouchAdapter)mAdapter).updateItemsData(mToDoList);//取消删除
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

    public void onClick_Dialog(View view){//监听事件

        switch(view.getId()){
            case R.id.fab:
                AddTodoDialog addTodoDialog = new AddTodoDialog();
                //设置添加后监听函数，刷新recyclerView
                addTodoDialog.setOnTodoAddListener(new OnTodoAddListener() {
                    @Override
                    public void onTodoAdd() {
                        mToDoList= LitePal.where("is_delete = ?","false").
                                order("date desc").find(Todo.class);
                        mAdapter.notifyDataSetChanged(mToDoList);
                    }
                } );
                addTodoDialog.show(getSupportFragmentManager(),"tag");//显示对话框

                break;
            default: break;
        }
    }
}