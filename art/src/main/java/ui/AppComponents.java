package ui;

import data.entities.DbUserRole;
import data.localdb.ArtDbContext;

import extensions.ResourceBundleHelper;

import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import settings.UserSettingsHelper;

import tools.tracing.TraceOut;

import ui.custom.controls.CustomWindow;


public class AppComponents {

    // ===========================================
    //            S I N G L E T O N
    // ===========================================

    private AppComponents() {
        // nothing to do here ...
    }

    private static AppComponents instance;

    public static AppComponents getInstance() {
        return instance = ((instance != null) ? instance : new AppComponents());
    }


    // =============================
    //       database context
    // =============================

    private ArtDbContext dbContext = null;

    public ArtDbContext getDbContext() {
        return dbContext;
    }

    /**
     * This method creates a new instance of ArtDbContext using the given username and password.
     *
     * @param username the username of the new database context
     * @param password the password of the new database context
     * @return a boolean that indicates whether the user credentials are valid and db context initialization worked fine
     */
    public boolean tryInitDbContext(String username, String password) {

        TraceOut.enter();

        // close old context
        if (dbContext != null) {
            dbContext.close();
        }

        boolean ret = true;

        try {

            // create a new db context instance (if username or password is invalid an exception is thrown which results into returning false)
            dbContext = new ArtDbContext(username, password);

        } catch (Exception ex) {

            ex.printStackTrace();
            TraceOut.writeException(ex);
            ret = false;
        }

        TraceOut.leave();
        return ret;
    }

    /**
     * Shows the scene with the given parameters.
     */
    public FXMLLoader showScene(String path, String titleKey, Stage stage, Stage owner, Modality modality, int width, int height) throws Exception {

        // get the ResourceBundle
        ResourceBundle bundle = ResourceBundleHelper.getInstance().getLanguageBundle();

        // load the AboutView
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(path), ResourceBundleHelper.getInstance().getLanguageBundle());
        CustomWindow window = loader.load();

        // build the scene and add it to the stage
        Scene scene = new Scene(window);

        // add styles to the scene
        scene.getRoot().setStyle(new UserSettingsHelper().loadUserSettings().getDarkThemeCss());
        scene.getStylesheets().add("css/main.css");

        // add main-root to the root so tooltip styling is not affected
        scene.getRoot().getStyleClass().add("main-root");

        stage.setScene(scene);

        // set the owner of the stage
        if (owner != null) {
            stage.initOwner(owner);
        }

        // add
        addIconsToStage(stage);

        // set the modality of the window
        if (modality != null) {
            stage.initModality(modality);
        }

        window.initStage(stage);

        // set window title
        if (titleKey != null) {
            window.setTitle(bundle.getString(titleKey));
        }

        // set window dimensions
        if (width > 0 && height > 0) {
            window.setPrefWidth(width);
            window.setPrefHeight(height);
        }

        stage.show();

        return loader;
    }

    public FXMLLoader showScene(String path, String title, Stage stage, Stage owner, Modality modality) throws Exception {
        return showScene(path, title, stage, owner, modality, 0, 0);
    }

    public FXMLLoader showScene(String path, String title, int width, int height) throws Exception {
        return showScene(path, title, new Stage(), App.primaryStage, Modality.WINDOW_MODAL, width, height);
    }

    public FXMLLoader showScene(String path, String title) throws Exception {
        return showScene(path, title, new Stage(), App.primaryStage, Modality.WINDOW_MODAL, 0, 0);
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


    /**
     * Returns if the string is an already defined DbUserRole.
     */
    public static boolean isUserRole(String username) {
        return !(username.equalsIgnoreCase(DbUserRole.Viewer.toString()) || username.equalsIgnoreCase(DbUserRole.Configurator.toString()) || username.equalsIgnoreCase(DbUserRole.Admin.toString()));
    }
}
