package ui.main.sapsettings.modal;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import data.entities.SapConfiguration;
import data.localdb.ArtDbContext;

import extensions.ResourceBundleHelper;

import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import sap.ISapConnector;
import sap.SapConnector;

import ui.AppComponents;
import ui.custom.controls.CustomAlert;
import ui.main.sapsettings.SapSettingsController;


public class SapSettingsFormController {

    @FXML
    public JFXTextField hostServerField;

    @FXML
    public JFXTextField sysNrField;

    @FXML
    public JFXTextField jcoClientField;

    @FXML
    public JFXTextField descriptionField;

    @FXML
    public JFXTextField tfPoolCapacity;

    @FXML
    public JFXTextField tfLanguage;

    @FXML
    public JFXButton connectButton;

    @FXML
    public JFXButton saveButton;


    private SapConfiguration sapConfig;
    private SapConfiguration oldSapConfig;

    private ArtDbContext database = AppComponents.getInstance().getDbContext();

    private SapSettingsController parentController;
    private ResourceBundle bundle = ResourceBundleHelper.getInstance().getLanguageBundle();


    /**
     * Initializes the view.
     */
    public void initialize() {
        startValidation();
    }

    /**
     * Saves the currently edited connection.
     */
    public void saveConnection(ActionEvent event) throws Exception {

        if (checkTextFields()) {
            this.sapConfig.setDescription(descriptionField.getText());
            this.sapConfig.setSysNr(sysNrField.getText());
            this.sapConfig.setServerDestination(hostServerField.getText());
            this.sapConfig.setClient(jcoClientField.getText());
            this.sapConfig.setPoolCapacity(tfPoolCapacity.getText());
            this.sapConfig.setLanguage(tfLanguage.getText());

            // create new config if id is null
            if (this.sapConfig.getId() == null) {
                database.createSapConfig(this.sapConfig);
            } else {
                database.updateSapConfig(this.sapConfig);
            }

            close(event);
            parentController.updateTable();
        }
    }


    /**
     * Checks if the all Edit Window textfields are filled.
     *
     * @return false if any(except password and username) textfield is empty, everytime else it returns true.
     */
    private Boolean checkTextFields() {
        return (hostServerField.validate() && sysNrField.validate() && jcoClientField.validate() && tfLanguage.validate()
            && tfPoolCapacity.validate() && descriptionField.validate());
    }


    /**
     * Establishes a test connection to SAP.
     */
    public void connect() {
        if (checkTextFields()) {

            this.sapConfig.setClient(jcoClientField.getText());
            this.sapConfig.setServerDestination(hostServerField.getText());
            this.sapConfig.setSysNr(sysNrField.getText());
            this.sapConfig.setLanguage(tfLanguage.getText());
            this.sapConfig.setPoolCapacity(tfPoolCapacity.getText());
            this.sapConfig.setDescription(descriptionField.getText());

            try {

                // get exception from server
                ISapConnector sapConnector = new SapConnector(this.sapConfig, "abs", "abs");
                sapConnector.canPingServer();

            } catch (Exception e) {

                // if exception contains error code 103, connection was successful
                if (e.getCause().toString().contains("103")) {
                    CustomAlert customAlert = new CustomAlert(Alert.AlertType.INFORMATION, bundle.getString("sapConnectTitle"), bundle.getString("sapConnectSuccessMessage"), "OK", "Cancel");
                    customAlert.showAndWait();
                } else {
                    CustomAlert customAlert = new CustomAlert(Alert.AlertType.WARNING, bundle.getString("sapConnectTitle"), bundle.getString("sapConnectFailedMessage"), "Ok", "Cancel");
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
    public void giveSelectedSapConfig(SapConfiguration sapConfig) {

        if (sapConfig != null) {
            this.sapConfig = sapConfig;
            this.oldSapConfig = new SapConfiguration(sapConfig);

            hostServerField.setText(sapConfig.getServerDestination());
            sysNrField.setText(sapConfig.getSysNr());
            jcoClientField.setText(sapConfig.getClient());
            tfLanguage.setText(sapConfig.getLanguage());
            tfPoolCapacity.setText(sapConfig.getPoolCapacity());
            descriptionField.setText(sapConfig.getDescription());
        } else {
            this.sapConfig = new SapConfiguration();
        }
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
     * Sets the editable property of the textfields and removes save and connect button.
     */
    public void setEditable(boolean editable) {
        if (!editable) {
            jcoClientField.setEditable(false);
            sysNrField.setEditable(false);
            tfPoolCapacity.setEditable(false);
            hostServerField.setEditable(false);
            descriptionField.setEditable(false);
            connectButton.setVisible(false);
            saveButton.setVisible(false);
        } else {
            jcoClientField.setEditable(true);
            sysNrField.setEditable(true);
            tfPoolCapacity.setEditable(true);
            hostServerField.setEditable(true);
            descriptionField.setEditable(true);
            connectButton.setVisible(true);
            saveButton.setVisible(true);
        }
    }

    /**
     * Sets the parent Controller.
     *
     * @param sapSettingsController the parent Controller.
     */
    public void setParentController(SapSettingsController sapSettingsController) {
        this.parentController = sapSettingsController;
    }

    /**
     * Hides the stage.
     *
     * @param event the given ActionEvent
     */
    public void close(ActionEvent event) {
        (((Button) event.getSource()).getScene().getWindow()).hide();
    }
}
