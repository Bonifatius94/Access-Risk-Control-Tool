package ui.login.firstlogin;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import data.entities.DbUser;

import extensions.ResourceBundleHelper;

import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import ui.App;
import ui.AppComponents;
import ui.custom.controls.CustomAlert;


public class FirstLoginController {

    @FXML
    private JFXPasswordField passwordInput;

    @FXML
    private JFXTextField passwordInputPlain;

    @FXML
    private JFXPasswordField confirmPasswordInput;

    @FXML
    private JFXTextField confirmPasswordInputPlain;


    private ResourceBundle bundle;

    /**
     * Initializes the view with all needed bindings.
     */
    @FXML
    public void initialize() {
        bundle = ResourceBundleHelper.getInstance().getLanguageBundle();

        // bind password inputs
        passwordInput.managedProperty().bind(passwordInput.visibleProperty());
        passwordInputPlain.managedProperty().bind(passwordInputPlain.visibleProperty());
        passwordInputPlain.visibleProperty().bind(Bindings.not(passwordInput.visibleProperty()));
        passwordInput.textProperty().bindBidirectional(passwordInputPlain.textProperty());

        // bind confirmPassword inputs
        confirmPasswordInput.managedProperty().bind(confirmPasswordInput.visibleProperty());
        confirmPasswordInputPlain.managedProperty().bind(confirmPasswordInputPlain.visibleProperty());
        confirmPasswordInputPlain.visibleProperty().bind(Bindings.not(confirmPasswordInput.visibleProperty()));
        confirmPasswordInput.textProperty().bindBidirectional(confirmPasswordInputPlain.textProperty());

        Platform.runLater(() ->
            passwordInput.getScene().getWindow().setOnHiding((e -> {
                // close the database
                AppComponents.getInstance().getDbContext().close();
            }))
        );

        initializeValidation();
    }

    /**
     * Handles the database login.
     */
    public void changePasswordAndLogin(ActionEvent event) throws Exception {
        if (validateBeforeSubmit()) {

            if (!passwordInput.getText().equals(confirmPasswordInput.getText())) {

                // show passwordsDontMatch error
                new CustomAlert(Alert.AlertType.WARNING, bundle.getString("passwordsDontMatchTitle"), bundle.getString("passwordsDontMatchMessage"));
                passwordInput.clear();
                confirmPasswordInput.clear();

            } else {

                DbUser currentUser = AppComponents.getInstance().getDbContext().getCurrentUser();

                // remove first login from user
                AppComponents.getInstance().getDbContext().setFirstLoginOfCurrentUser(currentUser, false);

                // change password of user
                AppComponents.getInstance().getDbContext().changePassword(currentUser.getUsername(), passwordInput.getText());

                // log out and in again
                AppComponents.getInstance().getDbContext().close();
                AppComponents.getInstance().tryInitDbContext(currentUser.getUsername(), passwordInput.getText());

                // start the app
                startApplication(event);
            }
        }
    }

    /**
     * Initializes the validation for certain text inputs in order to display an error message (e.g. required).
     */
    private void initializeValidation() {

        passwordInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                passwordInput.validate();
            }
        });

        confirmPasswordInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                confirmPasswordInput.validate();
            }
        });
    }

    /**
     * Validates the login inputs.
     *
     * @return if the inputs are valid
     */
    private boolean validateBeforeSubmit() {
        return passwordInput.validate() && passwordInputPlain.validate() && confirmPasswordInput.validate() && confirmPasswordInputPlain.validate();
    }

    /**
     * Starts the application by opening the MainView.
     */
    private void startApplication(ActionEvent event) {
        try {

            AppComponents.getInstance()
                .showScene("ui/main/MainView.fxml", "art", App.primaryStage, null, null, 1050, 720);

            close(event);
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
