package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        // init global exception handling
        Thread.currentThread().setUncaughtExceptionHandler(this::unhandledExceptionOccurred);

        // show main view
        showMainView(primaryStage);

    }

    private void showMainView(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("main/MainView.fxml"), ResourceBundle.getBundle("lang"));
        primaryStage.setTitle("Access Risk Tool");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    private void unhandledExceptionOccurred(Thread thread, Throwable e) {

        // write thread id and exception to console log
        System.out.println("Thread ID: " + thread.getId());
        System.out.println("Stack Trace:");
        System.out.println(e.getMessage());
    }
    
}
