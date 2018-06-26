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
    private HBox errorBox;


    private ResourceBundle bundle;
    private int loginAttempts;
    private long startTime = 0;
    private final int maxAttempts = 3; // 3 attempts
    private final int penaltyTime = 60000; // one minute penalty time


    /**
     * Initializes the view with all needed bindings.
     */
    @FXML
    public void initialize() {
        bundle = ResourceBundle.getBundle("lang");

        loginAttempts = 0;

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

        if (System.currentTimeMillis() - startTime > penaltyTime) {

            errorBox.setVisible(false);
            errorLabel.setText(bundle.getString("databaseLoginError") + " " + (maxAttempts - loginAttempts) + ")");

            if (validateBeforeSubmit()) {
                if (AppComponents.tryInitDbContext(usernameInput.getText(), passwordInput.getText())) {
                    startApplication(event);
                } else {
                    // reset the attempts and show penalty error
                    if (loginAttempts++ == maxAttempts) {
                        startTime = System.currentTimeMillis();
                        errorLabel.setText(bundle.getString("penaltyError"));
                        loginAttempts = 0;
                    }
                    errorBox.setVisible(true);
                }
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
        return usernameInput.validate() && passwordInput.validate() && passwordInputPlain.validate();
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
