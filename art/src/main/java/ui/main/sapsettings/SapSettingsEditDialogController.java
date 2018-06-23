package ui.main.sapsettings;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import data.entities.SapConfiguration;
import data.localdb.ArtDbContext;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import sap.ISapConnector;
import sap.SapConnector;
import ui.AppComponents;
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

    ArtDbContext database = AppComponents.getDbContext();


    /**
     * Initializes the view.
     */
    @FXML
    @SuppressWarnings("all")
    public void initialize() {
        startValidation();

    }

    /**
     * Saves the currently edited connection.
     */
    @FXML
    public void saveConnection() {

        if (checkTextFieldsWithUsername()) {
            CustomAlert alert = new CustomAlert(Alert.AlertType.WARNING, "WARNING Some Input missing",
                "If you want to save SAP Configuration/n you need Valid input in each of those Textfields", "Ok", "Cancel");
            alert.showAndWait();
        } else {
            this.sapConfig.setDescription(descriptionField.getText());
            this.sapConfig.setSysNr(sysNrField.getText());
            this.sapConfig.setServerDestination(hostServerField.getText());
            this.sapConfig.setClient(jcoClientField.getText());
            try {
                if (this.sapConfig.isArchived()) {
                    database.createSapConfig(this.sapConfig);

                } else {
                    database.updateSapConfig(this.sapConfig);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if the all Edit Window textfields are filled.
     *
     * @return false if any textfield is empty, everytime else it returns true.
     */
    private Boolean checkTextFieldsWithPasswordAndUsername() {
        return (hostServerField.getText().equals("") || sysNrField.getText().equals("") || jcoClientField.getText().equals("") || userNameField.getText().equals("")
            || passwordField.getText().equals(""));
    }

    /**
     * Checks if the all Edit Window textfields are filled.
     *
     * @return false if any(except password and username) textfield is empty, everytime else it returns true.
     */
    private Boolean checkTextFields() {
        return hostServerField.getText().equals("") || sysNrField.getText().equals("") || jcoClientField.getText().equals("");
    }

    /**
     * Checks if the all Edit Window textfields are filled.
     *
     * @return false if any(except password) textfield is empty, everytime else it returns true.
     */
    private Boolean checkTextFieldsWithUsername() {
        return hostServerField.getText().equals("") || sysNrField.getText().equals("") || jcoClientField.getText().equals("") || userNameField.getText().equals("");
    }

    /**
     * Establishes a test connection to SAP.
     */
    public void connect() {
        try {
            //further Testing
            if (checkTextFieldsWithPasswordAndUsername()) {
                ISapConnector sapConnector = new SapConnector(this.sapConfig, userNameField.getText(), passwordField.getText());
                Boolean pingServer = sapConnector.canPingServer();
                CustomAlert customAlert = new CustomAlert(Alert.AlertType.INFORMATION, "Server Test Connection", "Connection Status: " + pingServer);
                customAlert.showAndWait();
            }
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

    /**
     * reverts all changes of the SAP Configuration.
     */
    public void revertChanges() {
        this.sapConfig = oldsapConfig;
        hostServerField.setText(sapConfig.getServerDestination());
        sysNrField.setText(sapConfig.getSysNr());
        jcoClientField.setText(sapConfig.getClient());
        descriptionField.setText(sapConfig.getDescription());

        //TODO: still missing somethings??
    }

    /**
     * starts validation of all Textfield.
     */
    private void startValidation() {
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
}
