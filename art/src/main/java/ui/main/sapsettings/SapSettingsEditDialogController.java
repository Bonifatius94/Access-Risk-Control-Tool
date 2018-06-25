package ui.main.sapsettings;

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


    public JFXTextField descriptionField;

    public JFXTextField tfPoolCapacity;

    public JFXTextField tfLanguage;

    private SapConfiguration sapConfig;
    private SapConfiguration oldSapConfig;

    private ArtDbContext database = AppComponents.getDbContext();

    private SapSettingsController parentController;


    /**
     * Initializes the view.
     */
    @FXML
    public void initialize() {
        startValidation();
    }

    /**
     * Saves the currently edited connection.
     */
    @FXML
    public void saveConnection() {

        if (!checkTextFields()) {
            CustomAlert alert = new CustomAlert(Alert.AlertType.WARNING, "WARNING Some Input missing",
                "If you want to save SAP Configuration/n you need Valid input in each of those Textfields", "Ok", "Cancel");
            alert.showAndWait();
        } else {
            this.sapConfig.setDescription(descriptionField.getText());
            this.sapConfig.setSysNr(sysNrField.getText());
            this.sapConfig.setServerDestination(hostServerField.getText());
            this.sapConfig.setClient(jcoClientField.getText());
            this.sapConfig.setPoolCapacity(tfPoolCapacity.getText());
            this.sapConfig.setLanguage(tfLanguage.getText());
            try {
                if (this.sapConfig.isArchived()) {
                    database.createSapConfig(this.sapConfig);
                    parentController.updateSapSettingsTable();

                } else {
                    database.updateSapConfig(this.sapConfig);
                    parentController.updateSapSettingsTable();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Checks if the all Edit Window textfields are filled.
     *
     * @return false if any(except password and username) textfield is empty, everytime else it returns true.
     */
    private Boolean checkTextFields() {
        return !(hostServerField.getText().equals("") || sysNrField.getText().equals("") || jcoClientField.getText().equals("") || tfLanguage.getText().equals("")
            || tfPoolCapacity.getText().equals("") || descriptionField.getText().equals(""));
    }


    /**
     * Establishes a test connection to SAP.
     */
    public void connect() {
        if (!checkTextFields()) {

            CustomAlert alert = new CustomAlert(Alert.AlertType.WARNING, "WARNING Missing some Input", "If you want to save SAP Configuration/n you need to set a valid Input", "Ok", "Cancel");
            alert.showAndWait();

        } else {
            this.sapConfig.setClient(jcoClientField.getText());
            this.sapConfig.setServerDestination(hostServerField.getText());
            this.sapConfig.setSysNr(sysNrField.getText());
            this.sapConfig.setLanguage(tfLanguage.getText());
            this.sapConfig.setPoolCapacity(tfPoolCapacity.getText());
            this.sapConfig.setDescription(descriptionField.getText());
            try {

                ISapConnector sapConnector = new SapConnector(this.sapConfig, "abs", "abs");
                sapConnector.canPingServer();

            } catch (Exception e) {

                if (e.getCause().toString().contains("103")) {
                    System.out.println(e.getCause().toString());
                    System.out.println(sapConfig.getServerDestination());
                    CustomAlert customAlert = new CustomAlert(Alert.AlertType.INFORMATION, "SAP Connection Status", "Connection Status: Success", "OK", "Cancel");
                    customAlert.showAndWait();
                } else {
                    CustomAlert customAlert = new CustomAlert(Alert.AlertType.WARNING, "Server Test Connection", "Connection Status: Error " + e.getCause().toString(), "Ok", "Cancel");
                    customAlert.showAndWait();
                }
            }
        }
    }


    /**
     * Prefills the inputs with the given SapConfig.
     *
     * @param sapConfig the given SapConfig
     */
    void giveSelectedSapConfig(SapConfiguration sapConfig) {
        this.sapConfig = sapConfig;
        this.oldSapConfig = new SapConfiguration();
        this.oldSapConfig.setId(sapConfig.getId());
        this.oldSapConfig.setCreatedBy(sapConfig.getCreatedBy());
        this.oldSapConfig.setPoolCapacity(sapConfig.getPoolCapacity());
        this.oldSapConfig.setLanguage(sapConfig.getLanguage());
        this.oldSapConfig.setClient(sapConfig.getClient());
        this.oldSapConfig.setServerDestination(sapConfig.getServerDestination());
        this.oldSapConfig.setSysNr(sapConfig.getSysNr());
        this.oldSapConfig.setArchived(sapConfig.isArchived());
        this.oldSapConfig.setCreatedAt(sapConfig.getCreatedAt());
        this.oldSapConfig.setDescription(sapConfig.getDescription());

        hostServerField.setText(sapConfig.getServerDestination());
        sysNrField.setText(sapConfig.getSysNr());
        jcoClientField.setText(sapConfig.getClient());
        tfLanguage.setText(sapConfig.getLanguage());
        tfPoolCapacity.setText(sapConfig.getPoolCapacity());
        descriptionField.setText(sapConfig.getDescription());


    }

    /**
     * reverts all changes of the SAP Configuration.
     */
    public void revertChanges() {
        this.sapConfig = oldSapConfig;
        hostServerField.setText(sapConfig.getServerDestination());
        sysNrField.setText(sapConfig.getSysNr());
        jcoClientField.setText(sapConfig.getClient());
        descriptionField.setText(sapConfig.getDescription());
        tfPoolCapacity.setText(sapConfig.getPoolCapacity());
        tfLanguage.setText(sapConfig.getLanguage());


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
        tfPoolCapacity.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                tfLanguage.validate();
            }
        });
        hostServerField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                hostServerField.validate();
            }
        });
        tfLanguage.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                tfLanguage.validate();
            }
        });
        descriptionField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                descriptionField.validate();
            }
        });


    }

    /**
     * Sets the parent Controller.
     *
     * @param sapSettingsController the parent Controller.
     */
    void setParentController(SapSettingsController sapSettingsController) {
        this.parentController = sapSettingsController;
    }
}
