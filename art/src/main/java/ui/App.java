package ui;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import tools.tracing.TraceLevel;
import tools.tracing.TraceMode;
import tools.tracing.TraceOut;

import ui.custom.controls.CustomAlert;
import ui.custom.controls.CustomWindow;


public class App extends Application {

    public static Stage primaryStage;

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

        // add application icons
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/art_64.png")));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/art_128.png")));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/art_256.png")));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/art_512.png")));

        // show view according to existence of the database file
        if (databaseFileExists()) {
            showLoginView(primaryStage);
        } else {
            showFirstUseWizardView(primaryStage);
        }

        this.primaryStage = primaryStage;

        TraceOut.leave();
    }

    private boolean databaseFileExists() {
        return Files.exists(Paths.get("art.h2.mv.db"));
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

    private void showFirstUseWizardView(Stage primaryStage) throws Exception {

        TraceOut.enter();

        ResourceBundle bundle = ResourceBundle.getBundle("lang");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login/firstuse/FirstUseWizardView.fxml"), bundle);
        CustomWindow window = loader.load();

        Scene scene = new Scene(window);
        scene.getStylesheets().add("css/dark-theme.css");
        primaryStage.setScene(scene);

        window.initStage(primaryStage);
        window.setTitle("Access Risk Control Tool");
        primaryStage.show();

        TraceOut.leave();
    }

    private void showLoginView(Stage primaryStage) throws Exception {

        TraceOut.enter();

        ResourceBundle bundle = ResourceBundle.getBundle("lang");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login/LoginView.fxml"), bundle);
        CustomWindow window = loader.load();

        Scene scene = new Scene(window);
        scene.getStylesheets().add("css/dark-theme.css");
        primaryStage.setScene(scene);

        window.initStage(primaryStage);
        primaryStage.show();

        TraceOut.leave();
    }
}