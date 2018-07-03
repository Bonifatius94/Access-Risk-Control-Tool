package ui.main.statusbar;

import static ui.main.sapqueries.modal.newquery.NewSapQueryController.analysisRunning;

import data.entities.DbUser;

import extensions.ResourceBundleHelper;

import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import ui.AppComponents;

public class StatusBarController {

    @FXML
    private Label loggedInUserLabel;

    @FXML
    private HBox copyrightBox;

    @FXML
    private Label analysisProgressLabel;

    @FXML
    private HBox analysisProgressBox;


    private ResourceBundle bundle = ResourceBundleHelper.getInstance().getLanguageBundle();

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

        analysisProgressBox.visibleProperty().bind(analysisRunning.greaterThan(0));
        analysisProgressLabel.textProperty().bind(Bindings.convert(analysisRunning).concat(" " + bundle.getString("analysisRunning")));
        analysisProgressBox.managedProperty().bind(analysisProgressBox.visibleProperty());

        copyrightBox.visibleProperty().bind(Bindings.not(analysisProgressBox.visibleProperty()));
        copyrightBox.managedProperty().bind(copyrightBox.visibleProperty());
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
