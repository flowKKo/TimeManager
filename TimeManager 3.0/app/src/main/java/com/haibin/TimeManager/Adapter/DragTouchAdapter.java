package com.haibin.TimeManager.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haibin.TimeManager.R;
import com.haibin.TimeManager.Todo.Todo;
import com.google.android.material.chip.Chip;
import com.haibin.TimeManager.menu.search_dustbin;
import com.haibin.TimeManager.menu.search_future;
import com.haibin.TimeManager.menu.search_history;
import com.haibin.TimeManager.showActivity;
import com.haibin.TimeManager.showDailyTodoActivity;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.List;

public class DragTouchAdapter extends BaseAdapter<DragTouchAdapter.ViewHolder> {

    private final SwipeRecyclerView mMenuRecyclerView;
    private List<Todo> mToDoList;
    // 存储勾选框状态的map集合
    public boolean[] flag = new boolean[100];//用来记录checkbutton是否被选中，否则在todo事件过多时会出现错乱问题
    private LocalBroadcastManager localBroadcastManager;

    Context m_context;

    public void updateItemsData(List<Todo> list){
        this.mToDoList = list;
        notifyDataSetChanged();
    }


    public DragTouchAdapter(Context context, SwipeRecyclerView menuRecyclerView) {
        super(context);
        this.mMenuRecyclerView = menuRecyclerView;
    }

    @Override
    public void notifyDataSetChanged(List<Todo> dataList) {
        this.mToDoList = dataList;
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mToDoList == null ? 0 : mToDoList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(getInflater().inflate(R.layout.task, parent, false));
        viewHolder.mMenuRecyclerView = mMenuRecyclerView;
        localBroadcastManager = LocalBroadcastManager.getInstance(parent.getContext());

        m_context = parent.getContext();

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Todo task= mToDoList.get(position);
        holder.task_name.setText(task.getTodo());
        holder.date.setText(task.getDate());
        holder.itemView.setTag(task.getId());//以id为唯一标识字段，且id不在UI界面中显示，故采用setTag方法,这个东西暂时没用到
        holder.setVisibility(true);
        holder.check_box.setOnCheckedChangeListener(null);//先设置一次CheckBox的选中监听器，传入参数null
        holder.check_box.setChecked(flag[position]);//用数组中的值设置CheckBox的选中状态



        //再设置一次CheckBox的选中监听器，当CheckBox的选中状态发生改变时，把改变后的状态储存在数组中
        holder.check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //从后台退出时，flag的内容是否会发生变化
                flag[position] = b;
                //更新task内容，同步更新activity的内容，在这里发送广播告诉该条信息已经被改变
                task.setIs_done(b);
                int n= task.getId();
                int test=holder.getAdapterPosition();
                String todo=task.getTodo();
                int id=task.getId();

                Intent intent;
                if(m_context instanceof showActivity){
                    intent =new Intent("myaction");
                }else if(m_context instanceof search_future){
                    intent =new Intent("myaction3");
                }else if(m_context instanceof search_history){
                    intent =new Intent("myaction4");
                }else if(m_context instanceof search_dustbin){
                    intent =new Intent("myaction5");
                }else if(m_context instanceof showDailyTodoActivity){
                    intent =new Intent("myaction6");
                }
                else{
                    intent =new Intent("myaction7");
                }

                intent.putExtra("todo_name",todo);
                intent.putExtra("todo_id",id);
                intent.putExtra("is_done",b);
                intent.putExtra("position",test);
                localBroadcastManager.sendBroadcast(intent);
            }
        });

    }
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

        public AppCompatTextView task_name;
        public CheckBox check_box;
        public Chip date;
        public Button tomato;
        SwipeRecyclerView mMenuRecyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            task_name = itemView.findViewById(R.id.todo_text);
            check_box=itemView.findViewById(R.id.checkbox);
            date=itemView.findViewById(R.id.todo_chip);
            itemView.findViewById(R.id.todo_text).setOnTouchListener(this);//为什么要额外设置一个部件来监听呢


        }
        public void setVisibility(boolean isVisible){
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)itemView.getLayoutParams();
            if (isVisible){
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
            }else{
                param.height = 0;
                param.width = 0;
            }
            itemView.setLayoutParams(param);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            /*int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    mMenuRecyclerView.startDrag(this);
                    break;
                }
            }*/
            return false;
        }

    }

}
