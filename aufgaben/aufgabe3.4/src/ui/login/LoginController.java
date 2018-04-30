package ui.login;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField tfLoginUsername;
    private StringProperty loginUsername = new SimpleStringProperty("");

    @FXML
    private PasswordField tfLoginPassword;
    private StringProperty loginPassword = new SimpleStringProperty("");

    public void initialize() {

        // init text field bindings
        tfLoginUsername.textProperty().bindBidirectional(loginUsername);
        tfLoginPassword.textProperty().bindBidirectional(loginPassword);
    }

    @FXML
    protected void btnOkClicked(ActionEvent e) {

        // close window
        Button button = (Button)e.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void btnCancelClicked(ActionEvent e) {

        // close window
        Button button = (Button)e.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

    public String getUsername() {
        return loginUsername.get();
    }

    public String getPassword() {
        return loginPassword.get();
    }

}
