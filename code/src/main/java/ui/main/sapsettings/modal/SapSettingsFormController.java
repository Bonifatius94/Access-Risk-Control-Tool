package ui.main.sapsettings.modal;

import com.jfoenix.controls.IFXTextInputControl;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import data.entities.SapConfiguration;
import data.localdb.ArtDbContext;

import extensions.ResourceBundleHelper;

import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputControl;
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
     *
     * @author Franz Schulze/Merlin Albes
     */
    public void initialize() {
        // TODO: use SimpleStringProperty bindings instead and initialize them here
        startValidation();
    }

    /**
     * Saves the currently edited connection.
     *
     * @author Franz Schulze/Merlin Albes
     */
    public void saveConnection(ActionEvent event) throws Exception {

        if (checkTextFields()) {

            // TODO: try to work with the original data object instead of a new one (call by reference!!!)

            // TODO: test if this code works
            getDataFromUserInterface(this.sapConfig);

            /*this.sapConfig.setDescription(descriptionField.getText());
            this.sapConfig.setSysNr(sysNrField.getText());
            this.sapConfig.setServerDestination(hostServerField.getText());
            this.sapConfig.setClient(jcoClientField.getText());
            this.sapConfig.setPoolCapacity(tfPoolCapacity.getText());
            this.sapConfig.setLanguage(tfLanguage.getText());*/

            // create new config if id is null
            if (this.sapConfig.getId() == null) {
                database.createSapConfig(this.sapConfig);
            } else {
                database.updateSapConfig(this.sapConfig);
            }

            close(event);
        }
    }

    /**
     * Checks if the all Edit Window textfields are filled.
     *
     * @return false if any(except password and username) textfield is empty, everytime else it returns true.
     * @author Franz Schulze/Merlin Albes
     */
    private Boolean checkTextFields() {
        return (hostServerField.validate() && sysNrField.validate() && jcoClientField.validate() && tfLanguage.validate()
            && tfPoolCapacity.validate() && descriptionField.validate());
    }


    /**
     * Establishes a test connection to SAP.
     *
     * @author Franz Schulze/Merlin Albes
     */
    public void connect() {

        if (checkTextFields()) {

            // TODO: what happens if the sap config should not be saved?
            // better: create a new sap config object and apply the changes there

            // TODO: test if this code works
            getDataFromUserInterface(this.sapConfig);

            /*this.sapConfig.setClient(jcoClientField.getText());
            this.sapConfig.setServerDestination(hostServerField.getText());
            this.sapConfig.setSysNr(sysNrField.getText());
            this.sapConfig.setLanguage(tfLanguage.getText());
            this.sapConfig.setPoolCapacity(tfPoolCapacity.getText());
            this.sapConfig.setDescription(descriptionField.getText());*/

            try {

                // get exception from server
                ISapConnector sapConnector = new SapConnector(this.sapConfig, "abs", "abs");
                sapConnector.canPingServer();

            } catch (Exception e) {

                CustomAlert customAlert;

                // if exception contains error code 103, connection was successful
                if (e.getCause().toString().contains("103")) {

                    customAlert = new CustomAlert(
                        Alert.AlertType.INFORMATION,
                        bundle.getString("sapConnectTitle"),
                        bundle.getString("sapConnectSuccessMessage"),
                        "OK",
                        "Cancel");

                } else {

                    customAlert = new CustomAlert(
                        Alert.AlertType.WARNING,
                        bundle.getString("sapConnectTitle"),
                        bundle.getString("sapConnectFailedMessage"),
                        "Ok",
                        "Cancel");
                }

                customAlert.showAndWait();
            }
        }
    }

    /**
     * Prefills the inputs with the given SapConfig.
     *
     * @param sapConfig the given SapConfig
     * @author Franz Schulze/Merlin Albes
     */
    public void giveSelectedSapConfig(SapConfiguration sapConfig) {

        if (sapConfig != null) {

            this.sapConfig = sapConfig;

            // TODO: is cloning really necessary???
            this.oldSapConfig = new SapConfiguration(sapConfig);

            // TODO: test if this code works
            applyDataToUserInterface(sapConfig);

            /*hostServerField.setText(sapConfig.getServerDestination());
            sysNrField.setText(sapConfig.getSysNr());
            jcoClientField.setText(sapConfig.getClient());
            tfLanguage.setText(sapConfig.getLanguage());
            tfPoolCapacity.setText(sapConfig.getPoolCapacity());
            descriptionField.setText(sapConfig.getDescription());*/

        } else {
            this.sapConfig = new SapConfiguration();
        }
    }

    /**
     * reverts all changes of the SAP Configuration.
     */
    public void revertChanges() {

        this.sapConfig = oldSapConfig;

        // TODO: test if this code works
        applyDataToUserInterface(this.sapConfig);

        /*hostServerField.setText(sapConfig.getServerDestination());
        sysNrField.setText(sapConfig.getSysNr());
        jcoClientField.setText(sapConfig.getClient());
        descriptionField.setText(sapConfig.getDescription());
        tfPoolCapacity.setText(sapConfig.getPoolCapacity());
        tfLanguage.setText(sapConfig.getLanguage());*/
    }

    /**
     * starts validation of all Textfield.
     *
     * @author Franz Schulze/Merlin Albes
     */
    private void startValidation() {

        // TODO: test this code
        applyValidationOnFocus(jcoClientField);
        applyValidationOnFocus(sysNrField);
        applyValidationOnFocus(tfPoolCapacity);
        applyValidationOnFocus(hostServerField);
        applyValidationOnFocus(tfLanguage);
        applyValidationOnFocus(descriptionField);

        /*jcoClientField.focusedProperty().addListener((o, oldVal, newVal) -> {
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
                tfPoolCapacity.validate();
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
        });*/
    }



    /**
     * Sets the editable property of the textfields and removes save and connect button.
     *
     * @author Franz Schulze/Merlin Albes
     */
    public void setEditable(boolean editable) {

        jcoClientField.setEditable(editable);
        sysNrField.setEditable(editable);
        tfPoolCapacity.setEditable(editable);
        hostServerField.setEditable(editable);
        descriptionField.setEditable(editable);
        tfLanguage.setEditable(editable);
        connectButton.setVisible(editable);
        saveButton.setVisible(editable);
    }

    /**
     * Sets the parent Controller.
     *
     * @param sapSettingsController the parent Controller.
     * @author Franz Schulze/Merlin Albes
     */
    public void setParentController(SapSettingsController sapSettingsController) {
        this.parentController = sapSettingsController;
    }

    /**
     * Hides the stage.
     *
     * @param event the given ActionEvent
     * @author Franz Schulze/Merlin Albes
     */
    private void close(ActionEvent event) throws Exception {

        // TODO: why not close?
        (((Button) event.getSource()).getScene().getWindow()).hide();

        // refresh the sapSettingsTable in the parentController
        if (parentController != null) {
            parentController.updateTable();
        }
    }

    /**
     * Shows a dialog to confirm to discard unsaved changes.
     *
     * @author Merlin Albes
     */
    public void confirmClose(ActionEvent event) throws Exception {

        if (saveButton.isVisible()) {

            // TODO: check if this dislog is multi-language compatible (OK / Cancel button text is set hard-coded)
            CustomAlert customAlert = new CustomAlert(
                Alert.AlertType.CONFIRMATION,
                bundle.getString("cancelWithoutSavingTitle"),
                bundle.getString("cancelWithoutSavingMessage"),
                "Ok",
                "Cancel"
                );

            Optional<ButtonType> result = customAlert.showAndWait();

            if (result != null && result.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {

                // TODO: why not close?
                (((Button) event.getSource()).getScene().getWindow()).hide();
            }

        } else {
            close(event);
        }
    }

    // ======================================
    //               HELPERS
    // ======================================

    private void applyDataToUserInterface(SapConfiguration config) {

        hostServerField.setText(config.getServerDestination());
        sysNrField.setText(config.getSysNr());
        jcoClientField.setText(config.getClient());
        descriptionField.setText(config.getDescription());
        tfPoolCapacity.setText(config.getPoolCapacity());
        tfLanguage.setText(config.getLanguage());
    }

    private void getDataFromUserInterface(SapConfiguration config) {

        config.setDescription(descriptionField.getText());
        config.setSysNr(sysNrField.getText());
        config.setServerDestination(hostServerField.getText());
        config.setClient(jcoClientField.getText());
        config.setPoolCapacity(tfPoolCapacity.getText());
        config.setLanguage(tfLanguage.getText());
    }

    private <T extends Node & IFXTextInputControl> void applyValidationOnFocus(T control) {

        control.focusedProperty().addListener(new ValidationListener(control));
    }

    private class ValidationListener implements ChangeListener<Boolean> {

        public ValidationListener(IFXTextInputControl control) {
            this.control = control;
        }

        private IFXTextInputControl control;

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

            if (!newValue) {
                control.validate();
            }
        }
    }
}
