

package com.todo;

import java.util.ArrayList;
import java.io.*; //BufferWriter、FileReaderやBufferedReaderのため
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

//複数データの管理
public class TodoManager {

    private final ArrayList<Todo> todos; //ArrayListそのものの再代入は禁止。中身の追加・削除は可能

    //コンストラクタ
    public TodoManager(){
        todos = new ArrayList<>();
    }

    //Todoを追加メソッド
    public void addTodo(Todo todo){
        if(todo == null){
            System.out.println("追加できません。");
            return;
        }
        todos.add(todo);
    }

    //Todo一覧を表示メソッド
    public void showTodos(){        
        if(todos.isEmpty()){
            System.out.println("タスクがありません。");
            return;
        }
        for (int i = 0; i < todos.size(); i++){
            Todo todo = todos.get(i);

            System.out.println("番号：" + (i + 1));
            System.out.println("タスク名：" + todo.getTaskName());
            System.out.println("状態：" + todo.isComplete());
            System.out.println("作成日時：" + todo.getCreatedAt());
            System.out.println("----------------------");
        }
    }

    //Todo削除メソッド
    public boolean deleteTodo(Todo todo){
        if(todo == null){
            System.out.println("タスクがありません。");
            return false;
        }
        return todos.remove(todo);
    }

    //Todo編集メソッド
    public boolean updateTodo(Todo todo, String newTaskName){
        if(newTaskName == null || todo == null || newTaskName.isBlank()){ //Stringクラスのメソッドなので文字列を持っている変数から呼び出す
            return false;
        }
        todo.setTaskName(newTaskName);
        return true;
    }

    //Todo完了状態を更新するメソッド
    public void toggleTodo(int index){
        if(index < 0 || index >= todos.size()){
            throw new IndexOutOfBoundsException("存在しません。");
        }
        Todo todo = todos.get(index); //Todo取得
        todo.toggleComplete(); ////取得したTodoのtoggleComplete()を呼ぶ
    }

    //件数取得メソッド
    public int getTodoCount(){
        return todos.size();
    }

    //指定したindexのTodoを取得するメソッド
    public Todo getTodo(int index){
        if(index < 0 || index >= todos.size()){ //範囲チェック
            return null;
        }
        return todos.get(index); //Todoを返す
    }

    //インデックス取得メソッド
    public int getTodoIndex(Todo todo){
        if(todo == null){
            return -1; //intなので数値返す
        }
        //return todos.get(todo); ❌　get(番号)：番号を指定してTodoを取得する
        return todos.indexOf(todo);
    }

    // Todoをファイルに保存する
    /*Java標準の FileWriter と BufferedReader。外部ライブラリは不要 */
    public void saveTodos() {

        try (BufferedWriter writer =
                new BufferedWriter(new FileWriter("todos.txt"))) {

            for (Todo todo : todos) {

                //タスク名に含まれるカンマを保存時に「、」へ置き換え　(区切り文字との混同を防ぐ)
                String taskName = todo.getTaskName().replace(",", "、");

                //CSV形式(Comma-Separated Values)
                writer.write( //文字列と数値やboolean型を+で連結すると、自動的に文字列へ変換される
                    taskName + "," + todo.isComplete() + "," + todo.getCreatedAt()
                );
                writer.newLine(); //ファイル内で改行するメソッド

            }

            System.out.println("保存しました！");

        } catch (IOException e) {
            System.out.println("保存に失敗しました。");
            e.printStackTrace();
        }
    }

    //Todoをファイルから読み込む処理
    public void loadTodos(){

        //読み込み開始前にリストを空にする
        todos.clear(); 

        File file = new File("todos.txt");

        //ファイルの存在確認
        if (!file.exists()){
            System.out.println("保存ファイルがありません。新規に開始します。");
            return;
        }

        try (BufferedReader reader =
                new BufferedReader(new FileReader("todos.txt"))) {
            
            while(true){

                //CSV方式にしたのでline
                String line = reader.readLine(); //readLine()：ファイルから1行読み込むメソッド

                if(line == null){ //ファイルの最後なら繰り返しを終了
                    break;
                }

                //分割してString配列で扱う
                /* split(",")：カンマで文字列を分割する
                    正の数：分割する回数を制限する
                    0：末尾の空データを削除する（省略時と同じ）
                    負の数：末尾の空データも残す */
                String[] data = line.split("," , -1);

                //データ数のチェック
                if(data.length != 3){
                    System.out.println("ファイルのデータ数が不正です。");
                    todos.clear(); //読み込みに失敗したときに、中途半端なデータをアプリに残さないための処理
                    break;
                }

                String taskName = data[0];
                if(taskName.isBlank()){
                    System.out.println("タスク名のデータが不正です。");
                    todos.clear();
                    break;
                }
                String completeText = data[1];
                String createdAtText = data[2];

                //完了状態の不正データのチェック (trueでなくabcとか)
                if(!completeText.equals("true") && !completeText.equals("false")){
                    System.out.println("完了状態のデータが不正です。");
                    todos.clear();
                    break;
                }
                //boolean型に変換
                boolean complete = Boolean.parseBoolean(completeText); // "true"→ trueに変換 //parseBoolean()：文字列をboolean型に変換するメソッド
                
                //日時の不正データのチェック,型変換
                LocalDateTime createdAt;
                try {
                    createdAt = LocalDateTime.parse(createdAtText); //LocalDateTime.parse()：文字列をLocalDateTime型に変換するメソッド
                } catch (DateTimeParseException e) {
                    System.out.println("作成日時のデータが不正です。");
                    todos.clear();
                    break;
                }
                
                //Todoを生成
                Todo todo = new Todo(taskName, complete, createdAt);
                //不正データの入力チェック
                try {
                    todo = new Todo(taskName, complete, createdAt);
                } catch (IllegalArgumentException e) {
                    System.out.println("タスクデータが不正です。");
                    todos.clear();
                    break;
                }
                //ArrayListに追加
                todos.add(todo);
            }

        } catch(IOException e) {
            System.out.println("読み込みに失敗しました。");
            todos.clear();
            e.printStackTrace();
        }
    }
}
