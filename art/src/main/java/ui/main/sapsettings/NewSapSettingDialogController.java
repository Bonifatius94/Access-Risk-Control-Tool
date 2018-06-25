package ui.main.sapsettings;

import com.jfoenix.controls.JFXTextField;
import data.entities.SapConfiguration;
import data.localdb.ArtDbContext;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import sap.ISapConnector;
import sap.SapConnector;
import ui.AppComponents;
import ui.custom.controls.CustomAlert;

public class NewSapSettingDialogController {


    public JFXTextField tfHostServerField;

    public JFXTextField sysNrField;

    public JFXTextField tfJcoClientField;

    public JFXTextField descriptionField;

    public JFXTextField tfLanguage;

    public JFXTextField tfPoolCapacity;

    private SapConfiguration sapConfiguration = new SapConfiguration();

    ArtDbContext database = AppComponents.getDbContext();

    SapSettingsController parentController;

    /**
     * Initializes the view and sets the validation listeners.
     */
    @FXML
    public void initialize() {
        initValidation();

    }

    /**
     * Saves the currently edited connection.
     */
    public void saveConnection() {

        if (!checkTextFields()) {
            CustomAlert alert = new CustomAlert(Alert.AlertType.CONFIRMATION, "WARNING Some input is missing", "If you want to save SAP Configuration\n you need to set valid Input"
                + " \nBy pressing Cancel you will close New Sap Settings", "Ok", "Cancel");
            ButtonType buttonType = alert.showAndWait().get();
            if (buttonType == ButtonType.CANCEL) {
                Stage stage = new Stage();
                stage.setScene(tfJcoClientField.getScene());
                stage.close();

            }
        } else {

            sapConfiguration.setClient(tfJcoClientField.getText());
            sapConfiguration.setServerDestination(tfHostServerField.getText());
            sapConfiguration.setSysNr(sysNrField.getText());
            sapConfiguration.setLanguage(tfLanguage.getText());
            sapConfiguration.setPoolCapacity(tfPoolCapacity.getText());
            sapConfiguration.setDescription(descriptionField.getText());

            try {
                database.createSapConfig(sapConfiguration);
                parentController.updateSapSettingsTable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Tests if the SAP Settings are right.
     */
    public void testConnect() {
        if (!checkTextFields()) {
            CustomAlert alert = new CustomAlert(Alert.AlertType.WARNING, "WARNING Missing some Input", "If you want to save SAP Configuration/n you need to set a valid Input", "Ok", "Cancel");
            alert.showAndWait();
        
        } else {
            this.sapConfiguration.setClient(tfJcoClientField.getText());
            this.sapConfiguration.setServerDestination(tfHostServerField.getText());
            this.sapConfiguration.setSysNr(sysNrField.getText());
            this.sapConfiguration.setLanguage(tfLanguage.getText());
            this.sapConfiguration.setPoolCapacity(tfPoolCapacity.getText());
            this.sapConfiguration.setDescription(descriptionField.getText());
            try {

                ISapConnector sapConnector = new SapConnector(this.sapConfiguration, "abs", "abs");
                Boolean pingServer = sapConnector.canPingServer();

            } catch (Exception e) {

                if (e.getCause().toString().contains("103")) {
                    System.out.println(e.getCause().toString());
                    CustomAlert customAlert = new CustomAlert(Alert.AlertType.INFORMATION, "SAP Connection Status", "Connection Status: Success", "OK", "Cancel");
                    customAlert.showAndWait();
                } else {
                    CustomAlert customAlert = new CustomAlert(Alert.AlertType.WARNING, "Server Test Connection", "Connection Status: Error " + e.getCause().toString(), "Ok", "Cancel");
                    System.out.println("bin ich jetzt hier?: " + e.getCause().toString());
                    customAlert.showAndWait();
                }
            }
        }
    }


    /**
     * Checks if the all Edit Window textfields are filled.
     *
     * @return false if any(except password and username) textfield is empty, everytime else it returns true.
     */
    private Boolean checkTextFields() {
        return !(tfHostServerField.getText().equals("") || tfJcoClientField.getText().equals("") || sysNrField.getText().equals("") || tfPoolCapacity.getText().equals("")
            || tfLanguage.getText().equals("") || descriptionField.getText().equals(""));
    }

    /**
     * Initializes the Validation of the Textfields.
     */
    private void initValidation() {
        tfHostServerField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                tfHostServerField.validate();
            }
        });
        tfJcoClientField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                tfJcoClientField.validate();
            }
        });
        sysNrField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                sysNrField.validate();
            }
        });
        tfLanguage.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                tfLanguage.validate();
            }
        });
        tfPoolCapacity.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                tfPoolCapacity.validate();
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
    public void setParentController(SapSettingsController sapSettingsController) {
        this.parentController = sapSettingsController;
    }
}
