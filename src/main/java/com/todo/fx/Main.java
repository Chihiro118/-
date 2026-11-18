
package com.todo.fx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.CheckBox;
import javafx.scene.text.Text;

import com.todo.Todo;
import com.todo.TodoManager;

public class Main extends Application {

    private TodoManager manager = new TodoManager();

    @Override
    public void start(Stage stage) {

        Label title = new Label("My Todo App");
        
        manager.loadTodos(); //ファイルからTodoを読み込む

        //入力欄　TextField
        TextField taskInput = new TextField();
        taskInput.setPromptText("タスクを入力してください");

        TextField editInput = new TextField();
        editInput.setPromptText("新しいタスク名");

        //ボタン追加
        Button addButton = new Button("追加");
        Button deleteButton = new Button("削除");
        Button editButton = new Button("編集");

        // 選択中のタスクを管理する
        /*変数ではなく配列を使う。
        Javaのラムダ式（e -> { ... }）では、外側で宣言したローカル変数を変更できないから.
        配列そのもの(変数)は変更できないが、中身は変更できる。*/
        Todo[] selectedTodo = {null}; //選択したtodoを入れておく配列

        //初期データの表示
        VBox todoList = new VBox(10);
        for (int i = 0; i < manager.getTodoCount(); i++) {
            Todo todo = manager.getTodo(i);
            HBox row = createTodoRow(todo, selectedTodo, todoList, editInput);
            todoList.getChildren().add(row); //VBoxの子要素としてHBoxを追加
        }

        // タスク追加
        addButton.setOnAction(e -> {

            String input = taskInput.getText();

            if (input.isBlank()) {
                return;
            }

            Todo todo = new Todo(input);//Todoを生成
            manager.addTodo(todo);//TodoManagerに追加
            saveTodos();//追加した時点でファイルに自動保存される(保存忘れ防止)

            HBox row = createTodoRow(todo, selectedTodo, todoList, editInput);
            todoList.getChildren().add(row);//画面に表示

            taskInput.clear();
        });

        //削除ボタンが押されたときの処理
        deleteButton.setOnAction(e -> {

            Todo todo = selectedTodo[0];
            if (todo == null) {
                return;
            }
            
            int index = manager.getTodoIndex(todo);

            boolean result = manager.deleteTodo(todo);

            if (result && index != -1) {
                todoList.getChildren().remove(index);
                selectedTodo[0] = null; //選択状態を解除
                saveTodos(); //ファイル自動保存
            }
        });

        //編集ボタンを押した時の処理
        editButton.setOnAction(e -> {

            Todo todo = selectedTodo[0]; //配列の0番目に入っているTodoを取り出してtodo に代入

            if (todo == null) {
                return;
            }

            String newTaskName = editInput.getText();

            boolean result = manager.updateTodo(todo, newTaskName);

            if (result) {

                //選択したtodoのindex取得
                int index = manager.getTodoIndex(todo);
                //VBoxからその要素取り出す
                HBox row = (HBox) todoList.getChildren().get(index);  //型変換(VBoxの中から取り出した要素をHBox型として扱う)
                //取り出したタスク名取得
                Label taskLabel = (Label) row.getChildren().get(1); //今回のHBoxは、[0] → CheckBox、[1] → Label(タスク名)
                //タスク名更新
                taskLabel.setText(todo.getTaskName());
                //ファイルに自動保存
                saveTodos();
                //入力欄クリアにする
                editInput.clear();
            }
        });

        //1.ルートとなるレイアウトを作成 (HBox:縦並び, VBox:横並び)
        HBox inputArea = new HBox(10);
        inputArea.getChildren().addAll(taskInput, addButton);

        HBox editArea = new HBox(10);
        editArea.getChildren().addAll(editInput, editButton);

        VBox root = new VBox(20);
        root.getChildren().addAll(
            title,
            inputArea,
            todoList,
            deleteButton,
            editArea
        );

        //2.Sceneにルートとサイズを指定して作成  Scene：ステージ（ウィンドウ）の中身（表示内容の全体）を保持するコンテナ
        Scene scene = new Scene(root, 400, 350);

        //3.Stage（ウィンドウ）にSceneをセットして表示  Stage：ウィンドウ枠そのもの
        //ウィンドウを閉じるときに保存処理
        stage.setOnCloseRequest(e -> {
            saveTodos(); 
        });

        stage.setTitle("Todoアプリ");
        stage.setScene(scene);
        stage.show();
    }

    //保存メソッド　//リファクタリングでよく使う考え方「同じ処理を複数の場所に書かず、1か所にまとめる」
    /*将来の保存前の機能追加や変更の際に便利になる。
        保存に失敗したときにエラーメッセージを表示する
        保存した日時を記録する
        保存前にデータをチェックする
        保存処理の結果を画面に表示する など。*/

    private void saveTodos(){
        manager.saveTodos();
    }

    //チェックされた時の処理 isSelected():チェックが入っていればtrue、入っていなければfalse
    //Todoから画面表示用のHBoxを作る処理
    private HBox createTodoRow(
        Todo todo,
        Todo[] selectedTodo,
        VBox todoList,
        TextField editInput) { //editInputはstart()の中で宣言されているため、呼べるように入れておく

        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(todo.isComplete());
        
        //Label taskLabel = new Label(todo.getTaskName()); ❌ //Label：文字を表示するJavaFXの部品、Text：文字そのものを描画するJavaFXの部品
        //taskLabel.setStyle("-fx-strikethrough: + true;"); ❌ //JavaFXで有効なCSSプロパティではない
        Text taskLabel = new Text(todo.getTaskName()); 
        taskLabel.setStrikethrough(todo.isComplete()); //初期状態で完了のタスクにも取り消し線が表示

        HBox row = new HBox(10);

        //タスクをクリックしたら選択
        row.setOnMouseClicked(e -> {
            selectedTodo[0] = todo;
            editInput.setText(todo.getTaskName()); //タスク名クリックしたら編集欄に表示
        });

        //チェックボックスで完了状態を変更、取消線つける
        checkBox.setOnAction(e -> {

            //完了状態の更新
            todo.setComplete(checkBox.isSelected()); //Todoのデータを更新
            saveTodos(); //ファイル自動保存

            //取り消し線表示
            //CSS文字列(JavaからCSSの設定を文字列として渡している)　setStyle()：JavaFXの画面部品の見た目を設定するメソッド、-fx-strikethrough：JavaFXで取り消し線を指定するCSSプロパティ
            //❌taskLabel.setStyle("-fx-strikethrough: " + checkBox.isSelected() + ";");  //taskLabel.setStyle("-fx-strikethrough: true;");で取り消し戦表示
            taskLabel.setStrikethrough(checkBox.isSelected());
        });

        row.getChildren().addAll(checkBox, taskLabel);
        return row;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
