package ui;

import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import tools.tracing.TraceLevel;
import tools.tracing.TraceMode;
import tools.tracing.TraceOut;

import ui.custom.controls.CustomAlert;
import ui.custom.controls.CustomWindow;
import ui.main.MainController;

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

        e.printStackTrace();

        // write exception to log file
        TraceOut.writeException(e);
    }

    private void showMainView(Stage primaryStage) throws Exception {

        TraceOut.enter();

        ResourceBundle bundle = ResourceBundle.getBundle("lang");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main/MainView.fxml"), bundle);
        CustomWindow window = loader.load();

        Scene scene = new Scene(window, 1000, 600);
        scene.getStylesheets().add("css/dark-theme.css");
        primaryStage.setScene(scene);

        window.initStage(primaryStage);
        window.setTitle("Access Risk Control Tool");
        primaryStage.show();

        // test alert
        CustomAlert alert = new CustomAlert(Alert.AlertType.CONFIRMATION, "Titel", "Sample text message boii.", "Yeee", "Nope");
        alert.showAndWait();

        TraceOut.leave();
    }
}