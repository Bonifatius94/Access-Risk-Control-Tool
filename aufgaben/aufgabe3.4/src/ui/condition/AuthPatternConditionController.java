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

    // TODO: differentiate between rules (single or complex), conditions (auth pattern or profile) and properties (auth pattern condition)
    // Goal: make it easy for users to define conditions separately and then combine them to rules

    // 3 Layers:
    // =========
    //   o rule
    //   o condition
    //   o property

    public void loadAuthPatternCondition(AuthorizationPatternCondition condition) {

        // init table view with property list
        properties.clear();
        properties.addAll(condition.getProperties());

        // select first record (loads detail data automatically)
        tblConditionProperties.getSelectionModel().select(0);
    }

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

}
