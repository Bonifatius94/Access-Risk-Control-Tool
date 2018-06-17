package ui.main.sapqueries.modal.newquery;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import data.entities.SapConfiguration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.VBox;
import sap.SapConnector;

public class SapLoginController {

    @FXML
    private JFXTextField usernameInput;

    @FXML
    private JFXPasswordField passwordInput;

    @FXML
    private VBox loginPane;

    @FXML
    private Label errorLabel;

    private SapConfiguration configuration;
    private NewSapQueryController parentController;

    @FXML
    public void initialize() {
        initializeValidation();
    }

    /**
     * Handles the SAP login.
     */
    public void login(ActionEvent event) {
        if (validateBeforeSubmit()) {
            try {
                SapConnector connector = new SapConnector(configuration, usernameInput.getText(), passwordInput.getText());

                if (connector.canPingServer()) {
                    close(event);
                    parentController.runAnalysis();
                } else {
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
}
