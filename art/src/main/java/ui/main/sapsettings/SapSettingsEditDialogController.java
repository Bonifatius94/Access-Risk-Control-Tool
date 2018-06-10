package ui.main.sapsettings;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import data.entities.SapConfiguration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import ui.custom.controls.CustomAlert;

public class SapSettingsEditDialogController {


    public JFXTextField hostServerField;

    public JFXTextField sysNrField;

    public JFXTextField jcoClientField;

    public JFXTextField userNameField;

    public JFXPasswordField passwordField;
    public JFXTextField jcoLanguageField;
    @SuppressWarnings("all")
    private SapConfiguration sapConfig;

    /**
     * Initializes fields ???
     */
    @FXML
    @SuppressWarnings("all")
    public void initialize() {

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
        hostServerField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                hostServerField.validate();
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

    @SuppressWarnings("all")
    public void saveConnection(ActionEvent actionEvent) {
        //TODO: test if input is valid

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
            //TODO: Save changes
        }
    }

    @SuppressWarnings("all")
    public void connect(ActionEvent actionEvent) {
        //TODO:test if input is valid , Establish connection to Sap
    }

    void giveSelectedSapConfig(SapConfiguration sapConfig) {
        this.sapConfig = sapConfig;

        hostServerField.setText(sapConfig.getServerDestination());
        sysNrField.setText(sapConfig.getSysNr());
        jcoClientField.setText(sapConfig.getClient());
        userNameField.setText(sapConfig.getCreatedBy());
        jcoLanguageField.setText(sapConfig.getLanguage());
    }
}
