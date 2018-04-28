package ui;

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

    @FXML
    private PasswordField tfLoginPassword;

    private StringProperty loginUsername = new SimpleStringProperty("");
    private StringProperty loginPassword = new SimpleStringProperty("");

    public void initialize() {

        tfLoginUsername.textProperty().bindBidirectional(loginUsername);
        tfLoginPassword.textProperty().bindBidirectional(loginPassword);
    }

    @FXML
    protected void btnOkClicked(ActionEvent e) {

        // send login request
        sendLoginRequest(loginUsername.getValue(), loginPassword.getValue());

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

    // TODO: remove this suppress annotation
    @SuppressWarnings("unused")
    private void sendLoginRequest(String username, String password) {

        // TODO: implement logic
    }

}
