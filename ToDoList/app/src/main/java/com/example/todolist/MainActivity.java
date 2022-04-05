package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import org.litepal.crud.LitePalSupport;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private BottomSheet_Add bottomSheet_add=null;//添加待办事项对话框
    private AddTodoDialog addTodoDialog=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LitePal.getDatabase();//创建/连接数据库
        //LitePal.deleteAll("Todo");
    }

    public void onClick(View view){//监听函数
        switch (view.getId()){
            case R.id.button_add://添加待办事项按钮
                /*bottomSheet_add=new BottomSheet_Add(this);
                bottomSheet_add.show();*/

                addTodoDialog=new AddTodoDialog();
                addTodoDialog.show(getSupportFragmentManager(),"This is addTodoDialog");


//                RepeatSetDialog dialog=new RepeatSetDialog();
//                dialog.show(getSupportFragmentManager(),"this is tag");

               /* DialogFragment dialog=new DialogFragment(R.layout.repeat_set);
                dialog.show(getSupportFragmentManager(),"this is tag");*/
                break;
            default:
                List<Todo> list= LitePal.findAll(Todo.class);
                Toast.makeText(this,"共有"+Integer.toString(list.size())+"条数据",Toast.LENGTH_LONG).show();
            break;
        }
    }


}