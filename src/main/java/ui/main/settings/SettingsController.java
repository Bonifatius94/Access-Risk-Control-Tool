package ui.main.settings;

import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXComboBox;

import extensions.ResourceBundleHelper;

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import settings.UserSettings;
import settings.UserSettingsHelper;

import ui.App;
import ui.custom.controls.CustomAlert;


public class SettingsController {

    @FXML
    private JFXComboBox<Locale> languageChooser;

    @FXML
    private JFXColorPicker primaryColorChooser;

    @FXML
    private Label restartLabel;

    private UserSettings settings;
    private SimpleBooleanProperty changesMade = new SimpleBooleanProperty();

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() throws Exception {

        // create default settings file
        settings = new UserSettingsHelper().loadUserSettings();

        // prefill the inputs
        languageChooser.setItems(FXCollections.observableList(UserSettings.getAvailableLocales()));

        initializeInputs();
    }

    /**
     * Initialize the inputs and their listeners.
     */
    private void initializeInputs() {
        languageChooser.setValue(settings.getLanguage());
        primaryColorChooser.setValue(settings.getPrimaryColor());

        // listen for language changes
        languageChooser.valueProperty().addListener((ol, oldValue, newValue) -> {
            if (newValue != null) {
                if (settings.getLanguage().equals(newValue)) {
                    changesMade.setValue(false);
                } else {
                    changesMade.setValue(true);
                }
            }
        });

        // listen for primary color changes
        primaryColorChooser.valueProperty().addListener((ol, oldValue, newValue) -> {
            if (newValue != null) {
                if (settings.getPrimaryColor().equals(newValue)) {
                    changesMade.setValue(false);
                } else {
                    changesMade.setValue(true);
                }
            }
        });

        // only show a restartLabel if changes were made
        restartLabel.visibleProperty().bind(changesMade);
    }

    /**
     * Saves the changes to the user settings file.
     */
    public void saveChanges(ActionEvent event) throws Exception {

        if (changesMade.getValue()) {

            // get the input values
            settings.setLanguage(languageChooser.getValue());
            settings.setPrimaryColor(primaryColorChooser.getValue());

            // store the values
            new UserSettingsHelper().storeUserSettings(settings);

            ResourceBundle bundle = ResourceBundleHelper.getInstance().getLanguageBundle();

            //show restart prompt
            CustomAlert alert = new CustomAlert(Alert.AlertType.CONFIRMATION,
                bundle.getString("restartAlertTitle"), bundle.getString("restartAlertMessage"),
                bundle.getString("restart"), bundle.getString("selfRestart"));

            if (alert.showAndWait().get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                // restart the app
                restartApp(event);
            } else {
                // do nothing and close
                close(event);
            }

        } else {
            close(event);
        }
    }

    /**
     * Restarts the application so the changes are applied.
     */
    private void restartApp(ActionEvent event) throws Exception {
        close(event);

        // a bit dirty, but restarts the app
        App.primaryStage.close();
        Platform.runLater(() -> {
            try {
                new App().start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Resets all inputs back to default.
     */
    public void resetToDefault() {
        languageChooser.setValue(UserSettings.defaultSettings().getLanguage());
        primaryColorChooser.setValue(UserSettings.defaultSettings().getPrimaryColor());
    }

    /**
     * Hides the stage.
     *
     * @param event the given ActionEvent
     */
    public void close(ActionEvent event) {
        (((Button) event.getSource()).getScene().getWindow()).hide();
    }
}
