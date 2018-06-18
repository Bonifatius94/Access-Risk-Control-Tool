package ui.main.sapsettings;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import data.entities.SapConfiguration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import sap.ISapConnector;
import sap.SapConnector;
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

    private SapConfiguration sapConfiguration;
    //private StringProperty userName = new SimpleStringProperty();


    /**
     * Initializes the view and sets the validation listeners.
     */
    @FXML
    @SuppressWarnings("all")
    public void initialize() {
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

    }

    /**
     * Saves the currently edited connection.
     */
    public void saveConnection() {

        if (hostServerField.getText().equals("")) {
            CustomAlert alert = new CustomAlert(Alert.AlertType.WARNING, "WARNING No Host Server", "If you want to save SAP Configuration/n you need to set a valid Host server", "Ok", "Cancel");
            if (alert.showAndWait().get() == ButtonType.OK) {
                System.out.println("ok");
            }
        } else if (sysNrField.getText().equals("")) {
            CustomAlert alert = new CustomAlert(Alert.AlertType.WARNING, "WARNING No Sys Nr", "If you want to save SAP Configuration/n you need to set a valid Sys NR", "OK", "Cancel");
            if (alert.showAndWait().get() == ButtonType.OK) {
                System.out.println("ok");
            }
        } else if (jcoClientField.getText().equals("")) {
            CustomAlert alert = new CustomAlert(Alert.AlertType.WARNING, "WARNING No JCO Client", "If you want to save SAP Configuration/n you need to set a valid JCO Client", "OK", "Cancel");
            if (alert.showAndWait().get() == ButtonType.OK) {
                System.out.println("ok");
            }
        } else if (userNameField.getText().equals("")) {
            CustomAlert alert = new CustomAlert(Alert.AlertType.WARNING, "WARNING No UserName", "If you want to save SAP Configuration/n you need to set a valid JCO Language", "OK", "Cancel");
            if (alert.showAndWait().get() == ButtonType.OK) {
                System.out.println("ok");
            }
        } else {

            sapConfiguration.setClient(jcoClientField.getText());
            sapConfiguration.setServerDestination(hostServerField.getText());
            sapConfiguration.setSysNr(sysNrField.getText());
            sapConfiguration.setCreatedBy(userNameField.getText());
            //TODO: save data in Table doesn
            //SapSettingsController sapSettingsController = new SapSettingsController();
            //sapSettingsController.giveSavedSapSettings(this.sapConfiguration);

        }

    }

    /**
     * Tests if the SAP Settings are right.
     */
    public void testConnect() {
        if (hostServerField.getText().equals("")) {
            CustomAlert alert = new CustomAlert(Alert.AlertType.WARNING, "WARNING No Host Server", "If you want to save SAP Configuration/n you need to set a valid Host server", "Ok", "Cancel");
            if (alert.showAndWait().get() == ButtonType.OK) {
                System.out.println("ok");
            }
        } else if (sysNrField.getText().equals("")) {
            CustomAlert alert = new CustomAlert(Alert.AlertType.WARNING, "WARNING No Sys Nr", "If you want to save SAP Configuration/n you need to set a valid Sys NR", "OK", "Cancel");
            if (alert.showAndWait().get() == ButtonType.OK) {
                System.out.println("ok");
            }
        } else if (jcoClientField.getText().equals("")) {
            CustomAlert alert = new CustomAlert(Alert.AlertType.WARNING, "WARNING No JCO Client", "If you want to save SAP Configuration/n you need to set a valid JCO Client", "OK", "Cancel");
            if (alert.showAndWait().get() == ButtonType.OK) {
                System.out.println("ok");
            }
        } else if (userNameField.getText().equals("")) {
            CustomAlert alert = new CustomAlert(Alert.AlertType.WARNING, "WARNING No UserName", "If you want to save SAP Configuration/n you need to set a valid Sap Username", "OK", "Cancel");
            if (alert.showAndWait().get() == ButtonType.OK) {
                System.out.println("ok");
            }
        } else if (passwordField.getText().equals("")) {
            CustomAlert alert = new CustomAlert(Alert.AlertType.WARNING, "WARNING No Password", "If you want to save SAP Configuration/n you need to set a valid Sap Password", "OK", "Cancel");
            if (alert.showAndWait().get() == ButtonType.OK) {
                System.out.println("ok");
            }
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
                CustomAlert customAlert = new CustomAlert(Alert.AlertType.INFORMATION, "Ping was start", "Connection Status: " + pingServer);
                customAlert.showAndWait();
            } catch (Exception e) {
                CustomAlert customAlert = new CustomAlert(Alert.AlertType.WARNING, "SAP Connection Error", "Connection Status: Failed");
                customAlert.showAndWait();
                e.printStackTrace();
            }
        }
    }
}
