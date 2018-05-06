package ui.config;

import excel.config.AuthorizationPattern;
import excel.config.ConditionLinkage;
import excel.config.ICondition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains the user interface business logic for the config editor view
 * that is bound to an instance of this class.
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
public class ConfigEditorController {

    @FXML
    private TextField tfUsecaseID;
    private StringProperty usecaseID = new SimpleStringProperty("");

    @FXML
    private TextArea taDescription;
    private StringProperty description = new SimpleStringProperty("");

    @FXML
    private TableView tblAuthPatterns;
    private ObservableList<AuthorizationPattern> authPatterns = FXCollections.observableArrayList();

    @FXML
    private TableView tblConditions;
    private ObservableList<ICondition> conditionsOfPattern = FXCollections.observableArrayList();

    @FXML
    private ChoiceBox chConditionToAdd;
    // TODO: add synchronization with conditions editor
    private ObservableList<ICondition> allConditions = FXCollections.observableArrayList();

    @FXML
    private ChoiceBox chLinkage;
    private ObservableList<ConditionLinkage> linkages = FXCollections.observableArrayList();

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
        tfUsecaseID.textProperty().bindBidirectional(usecaseID);
        taDescription.textProperty().bindBidirectional(description);

        // init config table view items
        tblAuthPatterns.setItems(authPatterns);

        // init allConditions table view items
        tblConditions.setItems(conditionsOfPattern);

        // init allConditions choice box items
        chConditionToAdd.setItems(allConditions);

        // init linkages choice box items
        linkages.addAll(ConditionLinkage.None, ConditionLinkage.And, ConditionLinkage.Or);
        chLinkage.setItems(linkages);
        chLinkage.getSelectionModel().select(0);

        // bind selection changed listener
        tblAuthPatterns.getSelectionModel().selectedItemProperty().addListener(this::onSelectionChanged);
    }

    /**
     * This method sets the auth patterns that are shown into the table view.
     *
     * @param patterns a list of auth patterns that are loaded into config editor view
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public void loadConfig(List<AuthorizationPattern> patterns) {

        // load items into table view
        this.authPatterns.clear();
        this.authPatterns.addAll(patterns);

        // select first record => init detail view with first record
        tblAuthPatterns.getSelectionModel().select(0);
    }

    /**
     * This method gets the auth patterns of the currently selected config.
     *
     * @return a list of auth patterns of the currently selected config
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public List<AuthorizationPattern> getConfig() {

        // pass a new list instance because original list is observable
        return new ArrayList<>(authPatterns);
    }

    /**
     * This method handles the selection changed event of the auth patterns table view.
     * It loads the selected auth pattern into the detail view section.
     *
     * @param observableValue the value that is observed
     * @param oldSelection the old selected value
     * @param newSelection the new selected value
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @SuppressWarnings("unused")
    private void onSelectionChanged(ObservableValue<Object> observableValue, Object oldSelection, Object newSelection) {

        Object selectedItem = tblAuthPatterns.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {

            AuthorizationPattern pattern = (AuthorizationPattern) selectedItem;
            updateSelectedDetailView(pattern);
        }
    }

    /**
     * This method loads the overloaded auth pattern into the detail view.
     *
     * @param pattern an auth pattern that is loaded into the detail view
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @SuppressWarnings("unchecked")
    private void updateSelectedDetailView(AuthorizationPattern pattern) {

        // set conditions table view
        conditionsOfPattern.clear();
        conditionsOfPattern.addAll(pattern.getConditions());

        // set linkage choice box
        chLinkage.getSelectionModel().select(pattern.getLinkage());

        // set text fields
        usecaseID.setValue(pattern.getUsecaseID());
        description.setValue(pattern.getDescription());
    }

    /**
     * This method handles the button clicked event of the add condition button.
     *
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @FXML
    private void addCondition() {

        // TODO: implement logic
    }

    /**
     * This method handles the observable list item changed event of the conditions editor view.
     *
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    // TODO: implement update mechanism that calls this method (e.g. using observable pattern)
    public void onConditionsChanged(List<ICondition> conditions) {

        this.allConditions.clear();
        this.allConditions.addAll(conditions);

        // TODO: handle selection
    }

    // TODO: implement validation logic

}
