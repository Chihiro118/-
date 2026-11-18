package com.todo.console;

import com.todo.Todo;
import com.todo.TodoManager;

import java.util.Scanner;
import java.util.InputMismatchException;

public class Main {
    public static void main(String[] args){

        //タイトル表示
        System.out.println("======Todoアプリ======");

        //スキャナーインスタンス生成
        Scanner scanner = new Scanner(System.in);

        TodoManager manager = new TodoManager();

        while(true){
            //メニュー表示
            System.out.println("""
                    1. タスク一覧
                    2. タスク追加
                    3. 完了・未完了の切り替え
                    4. タスク削除
                    5. タスク編集
                    0. 終了
                    """);

            //番号選択
            int choice;
            try{
                choice = scanner.nextInt(); //\n は読み残される
                scanner.nextLine(); // 残った改行を読み取る→次の入力へ
            }catch(InputMismatchException e){
                System.out.println("⚠️ 数字を入力してください");
                scanner.nextLine();
                continue;
            }

            //それぞれ処理
            switch(choice){
                case 0:{
                    System.out.println("アプリを終了します");
                    scanner.close();
                    return;
                }

                case 1:{
                    manager.showTodos();
                }
                    break;

                case 2:{                   
                    System.out.println("タスク名を入力してください：");
                    String input = scanner.nextLine(); //タスク追加

                    //空白入力チェック
                    if(input.isBlank()){
                        System.out.println("タスク名を入力してください！");
                        break;
                    }else{                   
                        Todo todo = new Todo(input); // Todo生成                   
                        manager.addTodo(todo); //Todo追加
                        System.out.println("タスク名：" + input + " を追加しました！");
                    }
                }
                    break;

                case 3:{   //{ } で囲むことで、それぞれのcaseの中だけで変数を使えるようになる    
                    int index = selectTodoIndex(scanner, manager); //入力処理
                    if(index == -1){
                        break;
                    }
                    manager.toggleTodo(index); //完了状態の切り替え
                    manager.showTodos(); //一覧表示
                }
                    break;

                case 4:{
                    int index = selectTodoIndex(scanner, manager); //入力処理
                    if(index == -1){
                        break;
                    } 
                    Todo todo = manager.getTodo(index); //削除前に対象のTodoを取得しておく
                     
                    boolean result = manager.deleteTodo(todo);
                    if(result){
                        System.out.println("タスク「" + todo.getTaskName() + "」を削除しました！");//削除成功
                    }else{
                        System.out.println("削除できませんでした。");//削除失敗
                    }
                }
                    break;
                
                case 5:{
                    int index = selectTodoIndex(scanner, manager); //編集対象のindexを取得
                    if(index == -1){
                        break;
                    }
                    Todo todo = manager.getTodo(index); //Todoを取得

                    System.out.println("新しいタスク名を入力してください：");
                    String newTaskName = scanner.nextLine(); //新しいタスク名を入力

                    boolean result = manager.updateTodo(todo, newTaskName);//TodoManagerで更新
                    if(result){
                        System.out.println("タスクを更新しました！");
                        manager.showTodos();
                    }else{
                        System.out.println("タスクを更新できませんでした。");
                    } 
                }
                    break;

                default:
                    System.out.println("無効な数字です");  
                    break;

            }
        }
    }


    //メニュー入力処理メソッド　範囲外なら-1
    public static int selectTodoIndex(Scanner scanner, TodoManager manager) {

        if(manager.getTodoCount() == 0){
            System.out.println("タスクがありません。");
            return -1;
        }
        manager.showTodos(); // Todo一覧を表示
        System.out.println("タスク番号を入力してください：");

        //数字以外の処理
        int number;
        
        try{
            number = scanner.nextInt(); // タスク番号の入力
            scanner.nextLine();
        }catch(InputMismatchException e){
            System.out.println("⚠️ 数字を入力してください。");
            scanner.nextLine();
            return -1;
        }

        //数値範囲チェック
        if(number < 1 || number > manager.getTodoCount()){
            System.out.println("⚠️ 存在しない番号です！");
            return -1;
        }
        int index = number - 1; // indexに変換
        return index;
    }
}  
