package ui.login;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;

/**
 * This class contains the user interface business logic for the login view
 * that is bound to an instance of this class.
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
public class LoginController {

    @FXML
    private TextField tfLoginUsername;
    private StringProperty loginUsername = new SimpleStringProperty("");

    @FXML
    private PasswordField tfLoginPassword;
    private StringProperty loginPassword = new SimpleStringProperty("");

    private boolean areLoginCredentialsConfirmed = false;

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
    public void initialize() {

        // init text field bindings
        tfLoginUsername.textProperty().bindBidirectional(loginUsername);
        tfLoginPassword.textProperty().bindBidirectional(loginPassword);
    }

    /**
     * This method handles the button clicked event of the OK button.
     * It marks the login credentials as confirmed and closes the dialog.
     *
     * @param e This parameter is automatically passed to this method.
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @FXML
    private void btnOkClicked(ActionEvent e) {

        areLoginCredentialsConfirmed = true;

        // close window
        Button button = (Button)e.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

    /**
     * This method handles the button clicked event of the OK button.
     * It marks the login credentials as confirmed and closes the dialog.
     *
     * @param e This parameter is automatically passed to this method.
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @FXML
    private void btnCancelClicked(ActionEvent e) {

        // close window
        Button button = (Button)e.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

    /**
     * This method gets the dialog result.
     *
     * @return a boolean value that indicates whether the user has pressed OK (true) or Cancel (false) button.
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public boolean areLoginCredentialsConfirmed() {
        return areLoginCredentialsConfirmed;
    }

    /**
     * This method gets the selected username for login.
     * If user did not press OK button before, a invalid operation exception will be thrown.
     *
     * @return the selected username
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public String getUsername() {

        if (!areLoginCredentialsConfirmed) {
            throw new InvalidOperationException("This getter must not be used when the login credentials are not confirmed.");
        }

        return loginUsername.get();
    }

    /**
     * This method gets the selected password for login.
     * If user did not press OK button before, a invalid operation exception will be thrown.
     *
     * @return the selected password
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public String getPassword() {

        if (!areLoginCredentialsConfirmed) {
            throw new InvalidOperationException("This getter must not be used when the login credentials are not confirmed.");
        }

        return loginPassword.get();
    }

}
