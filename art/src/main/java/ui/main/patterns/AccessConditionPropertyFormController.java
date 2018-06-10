package ui.main.patterns;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import data.entities.AccessPatternConditionProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

public class AccessConditionPropertyFormController {

    private AccessPatternConditionProperty accessPatternConditionProperty;

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

    }

    public void giveSelectedAccessConditionProperty(AccessPatternConditionProperty accessPatternConditionProperty) {
        this.accessPatternConditionProperty = accessPatternConditionProperty;

        authObjectInput.setText(accessPatternConditionProperty.getAuthObject());
        authFieldInput.setText(accessPatternConditionProperty.getAuthObjectProperty());
        authFieldValue1Input.setText(accessPatternConditionProperty.getValue1());
        authFieldValue2Input.setText(accessPatternConditionProperty.getValue2());
        authFieldValue3Input.setText(accessPatternConditionProperty.getValue3());
        authFieldValue4Input.setText(accessPatternConditionProperty.getValue4());
    }

    public void saveChanges() {

    }
}
