package ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SapConnectionsController {

    @FXML
    private TextField tfServerHostname;
    private StringProperty serverHostname = new SimpleStringProperty("");

    @FXML
    private TextField tfServerPort;
    private StringProperty serverPort = new SimpleStringProperty("");

    @FXML
    private ChoiceBox chEncryptionType;

    // TODO: add this enum to sap connection interface lateron
    public enum EncryptionType {
        // TODO: replace test encryption types with actual types
        DiffieHellman, AES128, AES256;

        @Override
        public String toString() {
            return String.valueOf(this);
        }
    }

    private static final ObservableList<EncryptionType> encryptionTypes = FXCollections.observableArrayList();
    static {
        encryptionTypes.addAll(EncryptionType.DiffieHellman, EncryptionType.AES128, EncryptionType.AES256);
    }

    @SuppressWarnings("unchecked")
    public void initialize() {

        // init text field bindings
        tfServerHostname.textProperty().bindBidirectional(serverHostname);
        tfServerPort.textProperty().bindBidirectional(serverPort);

        // init choice box items
        chEncryptionType.setItems(encryptionTypes);
    }

    @FXML
    protected void showLoginDlg(ActionEvent e) throws Exception {

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("LoginView.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("Login");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Node)e.getSource()).getScene().getWindow());
        LoginController login = loader.getController();
        stage.showAndWait();

        // evaluate dialog results
        String username = login.getUsername();
        String password = login.getPassword();

        // TODO: send login request to sap server
    }

}
