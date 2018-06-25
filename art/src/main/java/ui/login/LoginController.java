package ui.login;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import ui.App;
import ui.AppComponents;
import ui.custom.controls.CustomWindow;


public class LoginController {

    @FXML
    private JFXTextField usernameInput;

    @FXML
    private JFXPasswordField passwordInput;

    @FXML
    private JFXTextField passwordInputPlain;

    @FXML
    private MaterialDesignIconView showPasswordIconView;

    @FXML
    private Label errorLabel;

    @FXML
    private HBox usernameValidationBox;


    /**
     * Initializes the view with all needed bindings.
     */
    @FXML
    public void initialize() {

        // bind password inputs
        passwordInput.managedProperty().bind(passwordInput.visibleProperty());
        passwordInputPlain.managedProperty().bind(passwordInputPlain.visibleProperty());
        passwordInputPlain.visibleProperty().bind(Bindings.not(passwordInput.visibleProperty()));
        passwordInput.textProperty().bindBidirectional(passwordInputPlain.textProperty());

        // transform typed text to uppercase
        usernameInput.setTextFormatter(new TextFormatter<>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));

        initializeValidation();
    }

    /**
     * Handles the database login.
     */
    public void login(ActionEvent event) {
        if (validateBeforeSubmit()) {
            if (AppComponents.tryInitDbContext(usernameInput.getText(), passwordInput.getText())) {
                startApplication(event);
            } else {
                errorLabel.setVisible(true);
            }
        }
    }

    /**
     * Toggles the password visibility.
     */
    public void togglePasswordDisplay() {
        if (passwordInput.isVisible()) {
            showPasswordIconView.setIcon(MaterialDesignIcon.EYE);
            passwordInput.visibleProperty().set(false);
        } else {
            showPasswordIconView.setIcon(MaterialDesignIcon.EYE_OFF);
            passwordInput.visibleProperty().set(true);
        }
    }

    /**
     * Initializes the validation for certain text inputs in order to display an error message (e.g. required).
     */
    private void initializeValidation() {

        // validate the input with regex and display error message
        usernameInput.textProperty().addListener((ol, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                usernameValidationBox.setVisible(false);
            } else {
                if (!newValue.equals(oldValue)) {
                    if (!newValue.matches("([A-Z]{3,}+(_|\\w)*)")) {
                        usernameValidationBox.setVisible(true);
                    } else {
                        usernameValidationBox.setVisible(false);
                    }
                    usernameInput.validate();
                }
            }
        });
        
        usernameInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                usernameInput.validate();
            }
        });

        passwordInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                passwordInput.validate();
            }
        });
    }

    /**
     * Validates the login inputs.
     *
     * @return if the inputs are valid
     */
    private boolean validateBeforeSubmit() {
        return usernameInput.validate() && passwordInput.validate() && passwordInputPlain.validate() && !usernameValidationBox.isVisible();
    }

    /**
     * Starts the application by opening the MainView.
     */
    private void startApplication(ActionEvent event) {
        try {
            // create a new FXML loader with the SapSettingsEditDialogController
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../main/MainView.fxml"), bundle);
            CustomWindow customWindow = loader.load();

            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow, 1050, 750);
            scene.getStylesheets().add("css/dark-theme.css");
            App.primaryStage.setScene(scene);
            App.primaryStage.setTitle(bundle.getString("art"));
            customWindow.initStage(App.primaryStage);

            close(event);
            App.primaryStage.show();
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
            e.printStackTrace();
        }
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
