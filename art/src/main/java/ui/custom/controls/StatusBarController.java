package ui.custom.controls;

import data.entities.DbUser;

import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import ui.App;
import ui.AppComponents;


public class StatusBarController {

    @FXML
    private Label loggedInUserLabel;

    /**
     * Initializes the view.
     */
    @FXML
    public void initialize() {
        try {
            // set the label text to the current user details
            DbUser currentUser = AppComponents.getDbContext().getCurrentUser();
            loggedInUserLabel.setText(currentUser.getUsername() + " - " + currentUser.getRoles().stream().map(x -> x.toString()).reduce((x, y) -> x + ", " + y).get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the AboutView when the ? icon is clicked.
     */
    public void openAboutView() {
        try {
            // load the AboutView
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../main/about/AboutView.fxml"), bundle);
            CustomWindow window = loader.load();

            // build the scene and add it to the stage
            Scene scene = new Scene(window);
            scene.getStylesheets().add("css/dark-theme.css");
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(App.primaryStage);
            window.initStage(stage);

            window.setTitle(bundle.getString("about"));

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
