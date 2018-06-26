package ui;

//for testing
import data.entities.CriticalAccessQuery;
import io.csvexport.CsvExport;

import extensions.ResourceBundleHelper;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import settings.UserSettings;
import settings.UserSettingsHelper;

import tools.tracing.TraceLevel;
import tools.tracing.TraceMode;
import tools.tracing.TraceOut;

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

        //TODO: entferne Csv export ist nur zum Testen da
        CsvExport.startCsvExport(new CriticalAccessQuery());

        // init global exception handling
        Thread.currentThread().setUncaughtExceptionHandler(this::unhandledExceptionOccurred);

        // apply user settings
        applyUserSettings();

        // add the icons to the primary stage
        addIconsToStage(primaryStage);

        // show view according to existence of the database file
        if (databaseFileExists()) {
            showLoginView();
        } else {
            showFirstUseWizardView();
        }

        // make the primaryStage available from other classes
        this.primaryStage = primaryStage;

        // close the database connection if the primaryStage (MainView) is closed
        primaryStage.setOnHidden(event -> {
            onAppClosing(event);
        });

        TraceOut.leave();
    }

    private boolean databaseFileExists() {
        return Files.exists(Paths.get("art.h2.mv.db"));
    }

    /**
     * Global error handling.
     * @param thread the thread the error occurred in
     * @param e the error
     */
    private void unhandledExceptionOccurred(Thread thread, Throwable e) {

        // write thread id and exception to console log
        System.out.println("Thread ID: " + thread.getId());
        System.out.println("Stack Trace:");
        System.out.println(e.getMessage());

        e.printStackTrace();

        // write exception to log file
        TraceOut.writeException(e);
    }

    private void applyUserSettings() throws Exception {

        // get user settings
        UserSettings settings = new UserSettingsHelper().loadUserSettings();

        // write properties to trace
        settings.entrySet().forEach(x -> TraceOut.writeInfo("user setting '" + x.getKey() + "' = '" + x.getValue() + "'", TraceLevel.Verbose));

        // apply settings
        Locale.setDefault(settings.getLanguage());
    }

    /**
     * Shows the FirstUseWizardView.
     * @throws Exception fxmloader fails to init
     */
    private void showFirstUseWizardView() throws Exception {

        TraceOut.enter();

        // load the FirstUseWizwardView
        ResourceBundle bundle = ResourceBundleHelper.getInstance().getLanguageBundle();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login/firstuse/FirstUseWizardView.fxml"), bundle);
        CustomWindow window = loader.load();

        // build the scene and add it to the stage
        Scene scene = new Scene(window);
        scene.getStylesheets().add("css/dark-theme.css");
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(App.primaryStage);
        window.initStage(stage);
        window.setTitle(bundle.getString("art"));

        // add the icons to the stage
        addIconsToStage(stage);

        stage.show();

        TraceOut.leave();
    }

    /**
     * Shows the LoginView.
     * @throws Exception fxmloader fails to init
     */
    private void showLoginView() throws Exception {

        TraceOut.enter();

        // load the LoginView
        ResourceBundle bundle = ResourceBundleHelper.getInstance().getLanguageBundle();
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ui/login/LoginView.fxml"), bundle);
        CustomWindow window = loader.load();

        // build the scene and add it to the stage
        Scene scene = new Scene(window);
        scene.getStylesheets().add("css/dark-theme.css");
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(App.primaryStage);
        window.initStage(stage);
        window.setTitle(bundle.getString("login"));

        // add the icons to the stage
        addIconsToStage(stage);

        stage.show();

        TraceOut.leave();
    }

    /**
     * Adds the icons to the given stage.
     * @param stage the given stage
     */
    private void addIconsToStage(Stage stage) {

        // add application icons in different resolutions
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/art_64.png")));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/art_128.png")));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/art_256.png")));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/art_512.png")));
    }

    private void onAppClosing(WindowEvent e) {

        // close database
        AppComponents.getDbContext().close();
    }
}