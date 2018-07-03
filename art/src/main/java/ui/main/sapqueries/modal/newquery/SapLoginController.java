package ui.main.sapqueries.modal.newquery;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import data.entities.SapConfiguration;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import sap.SapConnector;

public class SapLoginController {

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

    private SapConfiguration configuration;
    private NewSapQueryController parentController;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        // bind password inputs
        passwordInput.managedProperty().bind(passwordInput.visibleProperty());
        passwordInputPlain.managedProperty().bind(passwordInputPlain.visibleProperty());
        passwordInputPlain.visibleProperty().bind(Bindings.not(passwordInput.visibleProperty()));
        passwordInput.textProperty().bindBidirectional(passwordInputPlain.textProperty());

        initializeValidation();
    }

    /**
     * Handles the SAP login.
     */
    public void login(ActionEvent event) {
        if (validateBeforeSubmit()) {
            try {
                SapConnector connector = new SapConnector(configuration, usernameInput.getText(), passwordInput.getText());

                try {
                    connector.canPingServer();
                    close(event);
                    parentController.runAnalysis(usernameInput.getText(), passwordInput.getText());
                } catch (Exception e) {
                    errorLabel.setVisible(true);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
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

    private boolean validateBeforeSubmit() {
        return usernameInput.validate() && passwordInput.validate();
    }

    /**
     * Hides the stage.
     *
     * @param event the given ActionEvent
     */
    public void close(ActionEvent event) {
        (((Button) event.getSource()).getScene().getWindow()).hide();
    }

    public void giveSapConfig(SapConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setParentController(NewSapQueryController parentController) {
        this.parentController = parentController;
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
}
