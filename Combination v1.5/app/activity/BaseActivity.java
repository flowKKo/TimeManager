package com.haibin.TimeManager.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haibin.TimeManager.Adapter.BaseAdapter;
import com.haibin.TimeManager.Adapter.DragTouchAdapter;
import com.haibin.TimeManager.R;
import com.haibin.TimeManager.Todo.Todo;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;


import org.litepal.LitePal;

import java.util.List;

public abstract class BaseActivity extends AppCompatActivity   {
    protected SwipeRecyclerView mRecyclerView;
    protected LinearLayoutManager mLayoutManager=new LinearLayoutManager(this);
    protected RecyclerView.ItemDecoration mItemDecoration;

    protected BaseAdapter mAdapter;
//implements OnItemClickListener
protected List<Todo> mToDoList;
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(getContentView());
        mRecyclerView=findViewById(R.id.recycler_view);
        mItemDecoration = createItemDecoration();
        initTask();
        mToDoList= LitePal.findAll(Todo.class);
        mAdapter = createAdapter();

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(mItemDecoration);
        mRecyclerView.setOnItemClickListener(this::onItemClick);


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

    //点击todo时弹出对话框编辑
//    @Override
    public void onItemClick(View itemView, int position) {
        Toast.makeText(this, "第" + position + "个", Toast.LENGTH_SHORT).show();
        //在这里调用修改信息
        EditTodoDialog editTodoDialog = new EditTodoDialog(mToDoList.get(position).getId());
        editTodoDialog.setOnTodoEditListener(new OnTodoEditListener() {
            @Override
            public void onTodoEdit() {//刷新界面
                mToDoList= LitePal.where("is_delete = ?","false").
                        order("date desc").find(Todo.class);
                mAdapter.notifyDataSetChanged(mToDoList);
            }
        });
        editTodoDialog.show(getSupportFragmentManager(),"EditDialog");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
    //初始化一些任务放进数据库
    public void initTask(){
        LitePal.deleteAll(Todo.class);
        Todo task1 = new Todo(1,"吃饭", "2033/3/31", "2033/3/31", false, "16:49", false,"false");
        task1.save();
        Todo task2 = new Todo(2,"睡觉", "2033/3/31", "2033/3/31", false, "16:49", false,"false");
        task2.save();
        Todo task3 = new Todo(3,"1", "2033/3/31", "2033/3/31", false, "16:49", false,"false");
        task3.save();
        Todo task4 = new Todo(4,"2", "2033/3/31", "2033/3/31", false, "16:49", false,"false");
        task4.save();
        Todo task5 = new Todo(5,"3", "2033/3/31", "2033/3/31", false, "16:49", false,"false");
        task5.save();
        int count = LitePal.count(Todo.class);
        Log.d("MainAvtivity", "---count=" + count);
//        for(int i=6;i<50;i++){
//            Todo task = new Todo(i,String.valueOf(i), "2033/3/31", "2033/3/31", false, "16:49", false);
//            task.save();
        //}
    }
}
