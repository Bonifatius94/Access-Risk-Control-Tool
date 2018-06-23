package ui.main.sapsettings;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import data.entities.SapConfiguration;
import data.localdb.ArtDbContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import sap.ISapConnector;
import sap.SapConnector;
import ui.AppComponents;
import ui.custom.controls.CustomAlert;

public class NewSapSettingDialogController {


    public JFXTextField hostServerField;
    //private StringProperty hostServer = new SimpleStringProperty();
    public JFXTextField sysNrField;
    //private StringProperty sysNr = new SimpleStringProperty();
    public JFXTextField jcoClientField;
    //private StringProperty jcoClient = new SimpleStringProperty();
    public JFXTextField userNameField;
    public JFXPasswordField passwordField;
    public JFXTextField descriptionField;

    private SapConfiguration sapConfiguration = new SapConfiguration();
    ArtDbContext database = AppComponents.getDbContext();

    /**
     * Initializes the view and sets the validation listeners.
     */
    @FXML
    @SuppressWarnings("all")
    public void initialize() {
        initValidation();

    }

    /**
     * Saves the currently edited connection.
     */
    public void saveConnection() {

        if (checkTextfields()) {
            CustomAlert alert = new CustomAlert(Alert.AlertType.CONFIRMATION, "WARNING Some input is missing", "If you want to save SAP Configuration\n you need to set valid Input"
                + " \nBy pressing Cancel you will close New Sap Settings", "Ok", "Cancel");
            ButtonType buttonType = alert.showAndWait().get();
            if (buttonType == ButtonType.CANCEL) {
                Stage stage = new Stage();
                stage.setScene(jcoClientField.getScene());
                stage.close();

            }
        } else {

            sapConfiguration.setClient(jcoClientField.getText());
            sapConfiguration.setServerDestination(hostServerField.getText());
            sapConfiguration.setSysNr(sysNrField.getText());
            sapConfiguration.setCreatedBy(userNameField.getText());
            try {
                database.createSapConfig(sapConfiguration);
                SapSettingsController.sapConfiguration = this.sapConfiguration;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Tests if the SAP Settings are right.
     */
    public void testConnect() {
        if (checkTextfieldsWithUsernameAndPassword()) {
            CustomAlert alert = new CustomAlert(Alert.AlertType.WARNING, "WARNING Missing some Input", "If you want to save SAP Configuration/n you need to set a valid Input", "Ok", "Cancel");
            alert.showAndWait();
        } else {
            this.sapConfiguration.setClient(jcoClientField.getText());
            this.sapConfiguration.setServerDestination(hostServerField.getText());
            this.sapConfiguration.setSysNr(sysNrField.getText());
            this.sapConfiguration.setLanguage("EN");
            this.sapConfiguration.setPoolCapacity("0");
            this.sapConfiguration.setDescription(descriptionField.getText());
            try {
                ISapConnector sapConnector = new SapConnector(this.sapConfiguration, userNameField.getText(), passwordField.getText());
                Boolean pingServer = sapConnector.canPingServer();
                CustomAlert customAlert = new CustomAlert(Alert.AlertType.INFORMATION, "SAP Server Testping", "Connection Status: " + pingServer);
                customAlert.showAndWait();
            } catch (Exception e) {
                CustomAlert customAlert = new CustomAlert(Alert.AlertType.WARNING, "SAP Connection Error", "Connection Status: Failed");
                customAlert.showAndWait();
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if the all Edit Window textfields are filled.
     *
     * @return false if any textfield is empty, everytime else it returns true.
     */
    private Boolean checkTextfieldsWithUsernameAndPassword() {
        return userNameField.getText().equals("") || passwordField.getText().equals("") || hostServerField.getText().equals("")
            || jcoClientField.getText().equals("") || sysNrField.getText().equals("");
    }

    /**
     * Checks if the all Edit Window textfields are filled.
     *
     * @return false if any(except password) textfield is empty, everytime else it returns true.
     */
    private Boolean checkTextfieldsWithUsername() {
        return userNameField.getText().equals("") || hostServerField.getText().equals("") || jcoClientField.getText().equals("") || sysNrField.getText().equals("");
    }

    /**
     * Checks if the all Edit Window textfields are filled.
     *
     * @return false if any(except password and username) textfield is empty, everytime else it returns true.
     */
    private Boolean checkTextfields() {
        return hostServerField.getText().equals("") || jcoClientField.getText().equals("") || sysNrField.getText().equals("");
    }

    /**
     * Initializes the Validation of the Textfields.
     */
    private void initValidation() {
        hostServerField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                hostServerField.validate();
            }
        });
        jcoClientField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                jcoClientField.validate();
            }
        });
        sysNrField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                sysNrField.validate();
            }
        });
        passwordField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                passwordField.validate();
            }
        });
        userNameField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                userNameField.validate();
            }
        });
    }
}
