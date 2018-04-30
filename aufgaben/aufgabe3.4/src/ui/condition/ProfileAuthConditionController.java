package ui.condition;


import excel.config.AuthorizationProfileCondition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ProfileAuthConditionController {

    @FXML
    private TextField tfConditionName;
    private StringProperty conditionName = new SimpleStringProperty("");

    @FXML
    private TextField tfProfile;
    private StringProperty profile = new SimpleStringProperty("");

    private AuthorizationProfileCondition condition = new AuthorizationProfileCondition(null);

    public void initialize() {

        // init text field bindings
        tfConditionName.textProperty().bindBidirectional(conditionName);
        tfProfile.textProperty().bindBidirectional(profile);
    }

    public void loadCondition(AuthorizationProfileCondition condition) {

        // load condition properties into text fields
        this.condition = condition;
        conditionName.setValue(condition.getConditionName());
        profile.setValue(condition.getAuthorizationProfile());
    }

    public AuthorizationProfileCondition getCondition() {
        return condition;
    }

    @FXML
    private void saveChanges() {
        // TODO: implement logic
    }

    @FXML
    private void cancel() {
        // TODO: implement logic
    }

    // TODO: implement logic for validating condition

}
