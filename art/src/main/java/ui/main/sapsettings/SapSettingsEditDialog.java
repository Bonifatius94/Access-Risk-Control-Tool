package ui.main.sapsettings;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import data.entities.SapConfiguration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class SapSettingsEditDialog {

    @FXML
    public JFXTextField hostServerField;
    @FXML
    public JFXTextField sysNrField;
    @FXML
    public JFXComboBox jcoClientComboBox;
    @FXML
    public JFXTextField userNameField;
    @FXML
    public JFXPasswordField passwordField;
    
    private SapConfiguration sapConfig;
    /**
     * Initializes fields ???
     */
    @FXML
    @SuppressWarnings("all")
    public void initialize(){
        hostServerField.setText(sapConfig.getServerDestination());
        sysNrField.setText(sapConfig.getSysNr());
        jcoClientComboBox.getItems().add(sapConfig.getClient());
        userNameField.setText(sapConfig.getCreatedBy());
    }
    @FXML
    @SuppressWarnings("all")
    public void saveConnection(ActionEvent actionEvent) {
    }
    @FXML
    @SuppressWarnings("all")
    public void connect(ActionEvent actionEvent) {
    }
    @SuppressWarnings("all")
    public SapSettingsEditDialog(SapConfiguration sapConfiguration) {
        this.sapConfig = sapConfiguration;
        
    }
}
