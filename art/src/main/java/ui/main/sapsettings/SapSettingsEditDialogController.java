package ui.main.sapsettings;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import data.entities.SapConfiguration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class SapSettingsEditDialogController {


    public JFXTextField hostServerField;

    public JFXTextField sysNrField;

    public JFXTextField jcoClientComboBox;

    public JFXTextField userNameField;

    public JFXPasswordField passwordField;
    @SuppressWarnings("all")
    private SapConfiguration sapConfig;

    /**
     * Initializes fields ???
     */
    @FXML
    public void initialize() {

    }

    @SuppressWarnings("all")
    public void saveConnection(ActionEvent actionEvent) {
        //TODO: test if input is valid
    }

    @SuppressWarnings("all")
    public void connect(ActionEvent actionEvent) {
        //TODO:test if input is valid , Establish connection to Sap
    }

    void giveSelectedSapConfig(SapConfiguration sapConfig) {
        this.sapConfig = sapConfig;

        hostServerField.setText(sapConfig.getServerDestination());
        sysNrField.setText(sapConfig.getSysNr());
        jcoClientComboBox.setText(sapConfig.getClient());
        userNameField.setText(sapConfig.getCreatedBy());
    }
}
