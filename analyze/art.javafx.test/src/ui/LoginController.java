package ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tools.tracing.TraceOut;

public class LoginController {

    @FXML
    private TextField tfLoginUsername;

    @FXML
    private PasswordField tfLoginPassword;

    private StringProperty loginUsername = new SimpleStringProperty("");
    private StringProperty loginPassword = new SimpleStringProperty("");

    public void initialize() {

        TraceOut.enter();

        tfLoginUsername.textProperty().bindBidirectional(loginUsername);
        tfLoginPassword.textProperty().bindBidirectional(loginPassword);

        TraceOut.leave();
    }

    @FXML
    protected void btnOkClicked(ActionEvent e) {

        TraceOut.enter();

        // send login request
        sendLoginRequest(loginUsername.getValue(), loginPassword.getValue());

        // close window
        Button button = (Button)e.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();

        TraceOut.leave();
    }

    @FXML
    protected void btnCancelClicked(ActionEvent e) {

        TraceOut.enter();

        // close window
        Button button = (Button)e.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();

        TraceOut.leave();
    }

    // TODO: remove this suppress annotation
    @SuppressWarnings("unused")
    private void sendLoginRequest(String username, String password) {

        TraceOut.enter();

        // TODO: implement logic

        TraceOut.leave();
    }

}
