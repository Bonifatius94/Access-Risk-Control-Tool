package ui.condition;

import excel.config.AuthorizationPatternCondition;
import excel.config.AuthorizationPatternConditionProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * This class contains the user interface business logic for the auth pattern condition view
 * that is bound to an instance of this class.
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
public class AuthPatternConditionController {

    @FXML
    private TextField tfAuthObject;
    private StringProperty authObject = new SimpleStringProperty("");

    @FXML
    private TextField tfAuthObjectParameter;
    private StringProperty authObjectParameter = new SimpleStringProperty("");

    @FXML
    private TextField tfValue1;
    private StringProperty value1 = new SimpleStringProperty("");

    @FXML
    private TextField tfValue2;
    private StringProperty value2 = new SimpleStringProperty("");

    @FXML
    private TextField tfValue3;
    private StringProperty value3 = new SimpleStringProperty("");

    @FXML
    private TextField tfValue4;
    private StringProperty value4 = new SimpleStringProperty("");

    @FXML
    private TableView tblConditionProperties;
    private ObservableList<AuthorizationPatternConditionProperty> properties = FXCollections.observableArrayList();

    private AuthorizationPatternCondition condition;

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
        /*tfUsecaseID.textProperty().bindBidirectional(usecaseID);
        taDescription.textProperty().bindBidirectional(description);*/
        tfAuthObject.textProperty().bindBidirectional(authObject);
        tfAuthObjectParameter.textProperty().bindBidirectional(authObjectParameter);
        tfValue1.textProperty().bindBidirectional(value1);
        tfValue2.textProperty().bindBidirectional(value2);
        tfValue3.textProperty().bindBidirectional(value3);
        tfValue4.textProperty().bindBidirectional(value4);

        // init table view items
        tblConditionProperties.setItems(properties);

        // bind selection changed listener
        tblConditionProperties.getSelectionModel().selectedItemProperty().addListener(this::onSelectionChanged);
    }

    /**
     * This method loads an auth pattern condition into this view.
     *
     * @param condition an auth pattern condition
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public void loadAuthPatternCondition(AuthorizationPatternCondition condition) {

        this.condition = condition;

        if (condition != null) {

            // init table view with property list
            properties.clear();
            properties.addAll(condition.getProperties());

            // select first record (loads detail data automatically)
            tblConditionProperties.getSelectionModel().select(0);
        }
    }

    /**
     * This method gets the auth pattern condition previously passed to this view by load method.
     * If a new condition was created this method passes the new condition.
     *
     * @return the condition loaded into this view
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public AuthorizationPatternCondition getCondition() {
        return condition;
    }

    /**
     * This method handles the selection changed event of condition properties table view.
     *
     * @param newValue the new selected value
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    private void onSelectionChanged(/*ObservableValue<Object> observableValue, Object oldValue,*/ Object newValue) {

        if (newValue instanceof AuthorizationPatternConditionProperty) {

            // apply values of selected record to detail view
            AuthorizationPatternConditionProperty property = (AuthorizationPatternConditionProperty) newValue;

            authObject.setValue(property.getAuthObject());
            authObjectParameter.setValue(property.getAuthObjectProperty());
            value1.setValue(property.getValue1());
            value2.setValue(property.getValue2());
            value3.setValue(property.getValue3());
            value4.setValue(property.getValue4());
        }
    }

    // TODO: add Save / Cancel buttons and event handlers similar to auth profile condition view

}
