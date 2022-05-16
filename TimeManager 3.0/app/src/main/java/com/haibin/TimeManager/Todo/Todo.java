package com.haibin.TimeManager.Todo;

//import org.litepal.crud.DataSupport;
import org.litepal.crud.LitePalSupport;

//数据库部分
public class Todo extends LitePalSupport implements Comparable<Todo>{
    private int id;//todo的id
    private String todo;//待办事项
    private String date;//待办事项时间
    private String create_date;//todo创建时间
    private boolean is_clock;//是否提醒
    private String time;//提醒时间
    private boolean is_done;//是否完成
    private boolean is_delete;//是否被删除
    private int pos;//用于todo排序

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public boolean isIs_delete() {
        return is_delete;
    }

    public void setIs_delete(boolean Is_delete) {

        this.is_delete = Is_delete;
    }

    public boolean getIs_done() {
        return is_done;
    }

    public void setIs_done(boolean is_done) {
        this.is_done = is_done;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public void setIs_clock(boolean is_clock) {
        this.is_clock = is_clock;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getTodo() {
        return todo;
    }

    public String getDate() {
        return date;
    }

    public String getCreate_date() {
        return create_date;
    }

    public boolean getIs_clock() {
        return is_clock;
    }



    public Todo(int id, String todo, String date, String create_date, boolean is_clock, String time, boolean is_done, boolean is_delete, int pos) {
        this.id = id;
        this.todo = todo;
        this.date = date;
        this.create_date = create_date;
        this.is_clock = is_clock;
        this.time = time;
        this.is_done = is_done;
        this.is_delete = is_delete;
        this.pos = pos;
    }

    public void set(Todo todo){
        this.id=todo.id;
        this.todo=todo.todo;
        this.date=todo.date;
        this.create_date=todo.create_date;
        this.is_clock=todo.is_clock;
        this.time=todo.time;
        this.is_done=todo.is_done;
        this.is_delete=todo.is_delete;
        this.pos=todo.pos;
    }
    public String getTime() {
        return time;
    }
    public Todo(){}


    @Override
    public int compareTo(Todo t) {
        //自定义比较方法，如果认为此实体本身大则返回1，否则返回-1
        if (this.pos >= t.getPos()) {
            return 1;
        }
        return -1;
    }

}
