package ui.condition;


import excel.config.AuthorizationProfileCondition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * This class contains the user interface business logic for the auth profile condition view
 * that is bound to an instance of this class.
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
public class AuthProfileConditionController {

    @FXML
    private TextField tfConditionName;
    private StringProperty conditionName = new SimpleStringProperty("");

    @FXML
    private TextField tfProfile;
    private StringProperty profile = new SimpleStringProperty("");

    private AuthorizationProfileCondition condition = new AuthorizationProfileCondition(null);

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
        tfConditionName.textProperty().bindBidirectional(conditionName);
        tfProfile.textProperty().bindBidirectional(profile);
    }

    /**
     * This method loads an auth profile condition into this view.
     *
     * @param condition an auth profile condition that is loaded into this view
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public void loadCondition(AuthorizationProfileCondition condition) {

        // load condition properties into text fields
        this.condition = condition;

        if (condition != null) {

            conditionName.setValue(condition.getConditionName());
            profile.setValue(condition.getAuthorizationProfile());
        }
    }

    /**
     * This method gets the auth profile condition previously passed to this view by load method.
     * If a new condition was created this method passes the new condition.
     *
     * @return the condition loaded into this view
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public AuthorizationProfileCondition getCondition() {

        // TODO: throw exception if save button was not pressed before (like in login view)
        return condition;
    }

    /**
     * This method handles the button clicked event of the save changes button.
     * It saves the changes to the local database if the data is valid.
     *
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @FXML
    private void saveChanges() {
        // TODO: implement logic
    }

    /**
     * This method handles the button clicked event of the cancel button.
     * It saves the changes to the local database if the data is valid.
     *
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @FXML
    private void cancel() {
        // TODO: implement logic
    }

    // TODO: implement logic for validating condition

}
