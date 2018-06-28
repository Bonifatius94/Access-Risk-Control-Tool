package ui.main.statusbar;

import data.entities.DbUser;

import extensions.ResourceBundleHelper;

import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import ui.App;
import ui.AppComponents;
import ui.custom.controls.CustomWindow;


public class StatusBarController {

    @FXML
    private Label loggedInUserLabel;

    ResourceBundle bundle = ResourceBundleHelper.getInstance().getLanguageBundle();

    /**
     * Initializes the view.
     */
    @FXML
    public void initialize() {
        try {
            // set the label text to the current user details
            DbUser currentUser = AppComponents.getInstance().getDbContext().getCurrentUser();
            loggedInUserLabel.setText(currentUser.getUsername() + " - " + currentUser.getRoles().stream().map(x -> x.toString()).reduce((x, y) -> x + ", " + y).get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the AboutView when the ? icon is clicked.
     */
    public void openAboutView() throws Exception {

        AppComponents.getInstance().showScene("ui/main/about/AboutView.fxml", "about");

    }

    /**
     * Opens the SettingsView when the gear icon is clicked.
     */
    public void openSettingsView() throws Exception {

        AppComponents.getInstance().showScene("ui/main/settings/SettingsView.fxml", "settings");

    }
}
