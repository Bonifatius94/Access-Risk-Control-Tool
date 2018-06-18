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

public class SapSettingsEditDialogController {


    public JFXTextField hostServerField;

    public JFXTextField sysNrField;

    public JFXTextField jcoClientField;

    public JFXTextField userNameField;

    public JFXPasswordField passwordField;
    public JFXTextField descriptionField;
    @SuppressWarnings("all")
    private SapConfiguration sapConfig;
    private SapConfiguration oldsapConfig;

    /**
     * Initializes the view.
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

    }

    /**
     * Saves the currently edited connection.
     */
    public void saveConnection() {
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
        } else if (userNameField.getText().equals("")) {
            CustomAlert alert = new CustomAlert(Alert.AlertType.WARNING, "WARNING No JCO Language", "If you want to save SAP Configuration/n you need to set a valid JCO Language", "OK", "Cancel");
            if (alert.showAndWait().get() == ButtonType.OK) {
                System.out.println("ok");
            }
        } else {
            //TODO: Save changes
            this.sapConfig.setDescription(descriptionField.getText());
            this.sapConfig.setSysNr(sysNrField.getText());
            this.sapConfig.setServerDestination(hostServerField.getText());
            this.sapConfig.setClient(jcoClientField.getText());
            //SapSettingsController sapSettingsController = new SapSettingsController();
            //sapSettingsController.giveSavedSapSettings(this.sapConfig);
        }
    }

    /**
     * Establishes a test connection to SAP.
     */
    public void connect() {
        try {
            //further Testing
            ISapConnector sapConnector = new SapConnector(this.sapConfig, userNameField.getText(), passwordField.getText());
            Boolean pingServer = sapConnector.canPingServer();
            CustomAlert customAlert = new CustomAlert(Alert.AlertType.INFORMATION, "Ping was start", "Connection Status: " + pingServer);
            customAlert.showAndWait();
        } catch (Exception e) {
            CustomAlert customAlert = new CustomAlert(Alert.AlertType.WARNING, "SAP Connection Error", "Connection Status: Failed");
            customAlert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Prefills the inputs with the given SapConfig.
     *
     * @param sapConfig the given SapConfig
     */
    void giveSelectedSapConfig(SapConfiguration sapConfig) {
        this.sapConfig = sapConfig;
        this.oldsapConfig = new SapConfiguration();
        this.oldsapConfig.setId(sapConfig.getId());
        this.oldsapConfig.setCreatedBy(sapConfig.getCreatedBy());
        this.oldsapConfig.setPoolCapacity(sapConfig.getPoolCapacity());
        this.oldsapConfig.setLanguage(sapConfig.getLanguage());
        this.oldsapConfig.setClient(sapConfig.getClient());
        this.oldsapConfig.setServerDestination(sapConfig.getServerDestination());
        this.oldsapConfig.setSysNr(sapConfig.getSysNr());
        this.oldsapConfig.setArchived(sapConfig.isArchived());
        this.oldsapConfig.setCreatedAt(sapConfig.getCreatedAt());
        this.oldsapConfig.setDescription(sapConfig.getDescription());

        hostServerField.setText(sapConfig.getServerDestination());
        sysNrField.setText(sapConfig.getSysNr());
        jcoClientField.setText(sapConfig.getClient());

        //TODO: Entfernen echten test hinzufügen
        userNameField.setText("GROUP_11");
        passwordField.setText("Wir sind das beste Team!");

    }

    public void revertChanges() {
        this.sapConfig = oldsapConfig;
        //TODO: further implementation
    }
}
