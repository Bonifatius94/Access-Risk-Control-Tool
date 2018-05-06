package ui.sapconn;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui.login.LoginController;

import java.util.ResourceBundle;

/**
 * This class contains the user interface business logic for the sap connections view
 * that is bound to an instance of this class.
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
public class SapConnectionsController {

    @FXML
    private TextField tfServerHostname;
    private StringProperty serverHostname = new SimpleStringProperty("");

    @FXML
    private TextField tfServerPort;
    private StringProperty serverPort = new SimpleStringProperty("");

    @FXML
    private ChoiceBox chEncryptionType;

    // TODO: add this enum to sapconn connection interface lateron
    public enum EncryptionType {
        // TODO: replace test encryption types with actual types
        DiffieHellman, AES128, AES256;

        @Override
        public String toString() {
            return (this == DiffieHellman) ? "Diffie Hellman" : (this == AES128) ? "AES 128" : "AES 256";
        }
    }

    private static final ObservableList<EncryptionType> encryptionTypes = FXCollections.observableArrayList();
    static {
        encryptionTypes.addAll(EncryptionType.DiffieHellman, EncryptionType.AES128, EncryptionType.AES256);
    }

    /**
     * This method is automatically called when the according view is loaded. It initializes the logic behind the controls:
     * <ul>
     *     <li>an observable lists is attached to each table view / choice box</li>
     *     <li>a string property is attached to each text field / text area</li>
     *     <li>an event listener is attached to each event</li>
     * </ul>
     *
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @SuppressWarnings("unchecked")
    public void initialize() {

        // init text field bindings
        tfServerHostname.textProperty().bindBidirectional(serverHostname);
        tfServerPort.textProperty().bindBidirectional(serverPort);

        // init choice box items
        chEncryptionType.setItems(encryptionTypes);
        chEncryptionType.getSelectionModel().select(0);
    }

    /**
     * This method handles the button clicked event of the connect button.
     *
     * <p>
     *     It opens a dialog for entering login credentials for authentication at the selected sap server.
     *     If the login is successful, the user will be asked if he wants to download data from sap server.
     * </p>
     *
     * @throws Exception fxml resource file not found exception or sap exception caused by login failing
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @FXML
    private void connect() throws Exception {

        // load view and init controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../login/LoginView.fxml"), ResourceBundle.getBundle("lang"));
        Parent root = loader.load();
        LoginController login = loader.getController();

        // init stage
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Login");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(tfServerHostname.getScene().getWindow());

        // show view as modal dialog and wait for exit
        stage.showAndWait();

        // evaluate dialog results
        String username = login.getUsername();
        String password = login.getPassword();

        // TODO: send login request to sap server

        // TODO: if login was successful => ask user if he wants to download data from sap server
    }

}
