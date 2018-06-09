package ui.main.sapsettings;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
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
    //private StringProperty userName = new SimpleStringProperty();


    /**
     * Initializes fields ???
     */
    @FXML
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
    }

    @SuppressWarnings("all")
    public void saveConnection(ActionEvent actionEvent) {
        //TODO: implement Test if input is valid

        if (hostServerField.getText().equals("")) {
            CustomAlert alert = new CustomAlert(Alert.AlertType.WARNING, "WARNING No Host Server", "If you want to save SAP Configuration/n you need to set a valid Host server", "Ok", "Cancel");
            if (alert.showAndWait().get() == ButtonType.OK) {
                System.out.println("ok");
            }
        }
        if (sysNrField.getText().equals("")) {
            CustomAlert alert = new CustomAlert(Alert.AlertType.WARNING, "WARNING No Sys Nr", "If you want to save SAP Configuration/n you need to set a valid Sys NR", "OK", "Cancel");
            if (alert.showAndWait().get() == ButtonType.OK) {
                System.out.println("ok");
            }
        }
        if (jcoClientField.getText().equals("")) {
            CustomAlert alert = new CustomAlert(Alert.AlertType.WARNING, "WARNING No JCO Client", "If you want to save SAP Configuration/n you need to set a valid JCO Client", "OK", "Cancel");
            if (alert.showAndWait().get() == ButtonType.OK) {
                System.out.println("ok");
            }
        }

    }

}
