package ui;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import settings.UserSettings;
import settings.UserSettingsHelper;

import setup.AppSetupHelper;
import tools.tracing.TraceLevel;
import tools.tracing.TraceMode;
import tools.tracing.TraceOut;
import ui.custom.controls.CustomAlert;


public class App extends Application {

    public static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // init logging tool
        TraceOut.enable("log.trc.txt", TraceMode.Overwrite, TraceLevel.Error);
        TraceOut.enter();

        try {

            // init global exception handling
            Thread.currentThread().setUncaughtExceptionHandler(this::unhandledExceptionOccurred);

            // apply user settings
            applyUserSettings();

            // add the icons to the primary stage
            addIconsToStage(primaryStage);

            // setup flexible app dependencies (sapjco)
            if (isSetupPreparationRequired()) {
                new AppSetupHelper().setupApp();
            }

            // show view according to existence of the database file
            if (isFirstUse()) {
                showFirstUseWizardView();
            } else {
                showLoginView();
            }

            // make the primaryStage available from other classes
            this.primaryStage = primaryStage;

            // close the database connection if the primaryStage (MainView) is closed
            primaryStage.setOnHidden(event -> {
                onAppClosing(event);
            });

        } catch (Exception ex) {

            // write error to trace
            ex.printStackTrace();
            TraceOut.writeException(ex);

            // close app
            primaryStage.close();
        }

        TraceOut.leave();
    }

    private boolean isSetupPreparationRequired() {
        return !Files.exists(Paths.get("lib", "sapjco3.jar"));
    }

    private boolean isFirstUse() {
        return !Files.exists(Paths.get("art.h2.mv.db"));
    }

    /**
     * Global error handling.
     *
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
     *
     * @throws Exception fxmloader fails to init
     */
    private void showFirstUseWizardView() throws Exception {

        TraceOut.enter();

        AppComponents.getInstance().showScene("ui/login/firstuse/FirstUseWizardView.fxml", "firstUse");

        TraceOut.leave();
    }

    /**
     * Shows the LoginView.
     *
     * @throws Exception fxmloader fails to init
     */
    private void showLoginView() throws Exception {

        TraceOut.enter();

        AppComponents.getInstance().showScene("ui/login/LoginView.fxml", "login");

        TraceOut.leave();
    }

    /**
     * Adds the icons to the given stage.
     *
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
        AppComponents.getInstance().getDbContext().close();

        // close all windows
        System.exit(0);
    }
}