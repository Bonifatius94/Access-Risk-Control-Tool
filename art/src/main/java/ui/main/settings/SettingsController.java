package ui.main.settings;

import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXComboBox;

import java.util.Locale;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import settings.UserSettings;
import settings.UserSettingsHelper;


public class SettingsController {

    @FXML
    private JFXComboBox<Locale> languageChooser;

    @FXML
    private JFXColorPicker primaryColorChooser;

    private UserSettings settings;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() throws Exception {

        // create default settings file
        settings = new UserSettingsHelper().loadUserSettings();

        // prefill the inputs
        languageChooser.setItems(FXCollections.observableList(UserSettings.getAvailableLocales()));
        primaryColorChooser.setValue(settings.getPrimaryColor());

        initializeInputs();
    }

    private void initializeInputs() {
        languageChooser.setValue(settings.getLanguage());
    }

    /**
     * Saves the changes to the user settings file.
     */
    public void saveChanges() throws Exception {

        // get the input values
        settings.setLanguage(languageChooser.getValue());
        settings.setPrimaryColor(primaryColorChooser.getValue());

        new UserSettingsHelper().storeUserSettings(settings);

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
