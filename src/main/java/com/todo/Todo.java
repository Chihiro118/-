package com.todo;
import java.time.LocalDateTime;

//一つのタスクの管理
public class Todo {

    //フィールド　カプセル化　getterメソでの読み取り専用にする
    private String taskName; //コンストラクタで値を受け取るので、初期値を指定しなくてOK。
    private boolean complete; //初期値falseなので代入いらない
    private final LocalDateTime createdAt;

    //コンストラクタ
    public Todo(String taskName){
        if(taskName == null || taskName.isBlank()){
            throw new IllegalArgumentException("タスク名を入力してください。");
        }
        this.taskName = taskName;
        this.createdAt = LocalDateTime.now();
    }

    //保存データからTodoを復元するコンストラクタ(コンストラクタのオーバーロード)
    public Todo(String taskName, boolean complete, LocalDateTime createdAt){

        if(taskName == null || taskName.isBlank()){
            throw new IllegalArgumentException("タスク名を入力してください。");
        }
        this.taskName = taskName;
        this.complete = complete;
        this.createdAt = createdAt;
    }   
    
    //getterメソッド
    public String getTaskName(){
        return taskName;
    }
    public boolean isComplete(){
        return complete;
    }
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    //setterメソッド
    /*isEmpty()：空文字 ""か確認
      isBlank()：空文字または空白だけ "   " か確認　*/

    public void setTaskName(String taskName){
        //空白なら変更しない
        if(taskName == null || taskName.isBlank()){
            return; //メソッドを終了させる命令。 void なので値を返さずに終了できる
        }
        this.taskName = taskName; 
    }
    
    public void setComplete(boolean complete){
        this.complete = complete;
    }
    public void toggleComplete(){ //完了状態というデータを持っているのはTodo自身なので、自分で変更する
        this.complete = !this.complete;
    }
}
