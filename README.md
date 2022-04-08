你可以类似如下使用AddTodoDialog

addTodoDialog=new AddTodoDialog();//创建对话框
addTodoDialog.setOnTodoAddListener(new OnTodoAddListener() {//设置监听添加响应事件
        @Override
         public void onTodoAdd() {
                  //Todo...
                  
         }
});
addTodoDialog.show(getSupportFragmentManager(),"This is addTodoDialog");//显示对话框