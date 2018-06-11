package ui.main.patterns;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import data.entities.AccessPatternConditionProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

public class AccessConditionPropertyFormController {

    @FXML
    private JFXTextField authObjectInput;

    @FXML
    private JFXTextField authFieldInput;

    @FXML
    private JFXTextField authFieldValue1Input;

    @FXML
    private JFXTextField authFieldValue2Input;

    @FXML
    private JFXTextField authFieldValue3Input;

    @FXML
    private JFXTextField authFieldValue4Input;


    @FXML
    public void initialize() {
        initializeValidation();
    }

    /**
     * Initializes the validation for certain text inputs in order to display an error message (e.g. required).
     */
    private void initializeValidation() {
        authObjectInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                authObjectInput.validate();
            }
        });

        authFieldInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                authFieldInput.validate();
            }
        });

        authFieldValue1Input.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                authFieldValue1Input.validate();
            }
        });
    }

    public void saveChanges() {

    }

    public void close(ActionEvent event) {
        (((Button) event.getSource()).getScene().getWindow()).hide();
    }
}
