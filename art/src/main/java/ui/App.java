package ui;

import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.stage.StageStyle;
import tools.tracing.TraceLevel;
import tools.tracing.TraceMode;
import tools.tracing.TraceOut;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // TODO: change trace level to error for release version
        // init logging tool
        TraceOut.enable("log.trc.txt", TraceMode.Overwrite, TraceLevel.All);
        TraceOut.enter();

        // init global exception handling
        Thread.currentThread().setUncaughtExceptionHandler(this::unhandledExceptionOccurred);

        // show main view
        showMainView(primaryStage);

        TraceOut.leave();
    }

    private void unhandledExceptionOccurred(Thread thread, Throwable e) {

        // write thread id and exception to console log
        System.out.println("Thread ID: " + thread.getId());
        System.out.println("Stack Trace:");
        System.out.println(e.getMessage());

        // write exception to log file
        TraceOut.writeException(e);
    }

    private void showMainView(Stage primaryStage) throws Exception {

        TraceOut.enter();

        ResourceBundle bundle = ResourceBundle.getBundle("lang");
        Parent root = FXMLLoader.load(getClass().getResource("window/WindowContainer.fxml"), bundle);

        Scene scene = new Scene(root, 300, 275);
        scene.getStylesheets().add("css/dark-theme.css");

        // TODO: implement close button to exit application cleanly
        // remove OS dependent window frame (with headline , nimize, maximize close buttons, etc.)
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("Login");

        primaryStage.setScene(scene);
        primaryStage.show();

        TraceOut.leave();
    }
}