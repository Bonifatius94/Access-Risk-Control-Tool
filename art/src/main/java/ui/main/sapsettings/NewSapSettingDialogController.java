package ui.main.sapsettings;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import data.entities.SapConfiguration;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
    public JFXTextField jcoLanguageField;
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
        jcoLanguageField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                jcoLanguageField.validate();
            }
        });
    }

    /**
     * Saves the currently edited connection.
     */
    public void saveConnection() {

        //TODO: implement Test if input is valid

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
        } else if (jcoClientField.getText().equals("")) {
            CustomAlert alert = new CustomAlert(Alert.AlertType.WARNING, "WARNING No JCO Language", "If you want to save SAP Configuration/n you need to set a valid JCO Language", "OK", "Cancel");
            if (alert.showAndWait().get() == ButtonType.OK) {
                System.out.println("ok");
            }
        } else {

            SapConfiguration sapConfiguration = new SapConfiguration();
            sapConfiguration.setClient(jcoClientField.getText());
            sapConfiguration.setServerDestination(hostServerField.getText());
            sapConfiguration.setSysNr(sysNrField.getText());
            sapConfiguration.setLanguage(jcoLanguageField.getText());
            sapConfiguration.setCreatedBy(userNameField.getText());
            //TODO: save data in Table


        }

    }

}
