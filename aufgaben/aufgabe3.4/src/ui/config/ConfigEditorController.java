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

import java.util.List;

public class ConfigEditorController {

    @FXML
    private TextField tfUsecaseID;
    private StringProperty usecaseID = new SimpleStringProperty("");

    @FXML
    private TextArea taDescription;
    private StringProperty description = new SimpleStringProperty("");

    @FXML
    private TableView tblConfigs;
    private ObservableList<AuthorizationPattern> patterns = FXCollections.observableArrayList();

    @FXML
    private ChoiceBox chConditionToAdd;
    // TODO: add synchronization with conditions editor
    private ObservableList<ICondition> conditions = FXCollections.observableArrayList();

    @FXML
    private ChoiceBox chLinkage;
    private ObservableList<ConditionLinkage> linkages = FXCollections.observableArrayList();

    @SuppressWarnings("unchecked")
    public void initialize() {

        // init text field bindings
        tfUsecaseID.textProperty().bindBidirectional(usecaseID);
        taDescription.textProperty().bindBidirectional(description);

        // init table view items
        tblConfigs.setItems(patterns);

        // init conditions choice box items
        chConditionToAdd.setItems(conditions);

        // init linkages choice box items
        linkages.addAll(ConditionLinkage.None, ConditionLinkage.And, ConditionLinkage.Or);
        chLinkage.setItems(linkages);
        chLinkage.getSelectionModel().select(0);

        // bind selection changed listener
        tblConfigs.getSelectionModel().selectedItemProperty().addListener(this::onSelectionChanged);
    }

    public void loadConfig(List<AuthorizationPattern> patterns) {

        // load items into table view
        this.patterns.clear();
        this.patterns.addAll(patterns);

        // select first record => init detail view with first record
        tblConfigs.getSelectionModel().select(0);
    }

    private void onSelectionChanged(ObservableValue<Object> observableValue, Object oldSelection, Object newSelection) {

        Object selectedItem = tblConfigs.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {

            AuthorizationPattern pattern = (AuthorizationPattern) selectedItem;
            updateSelectedDetailView(pattern);
        }
    }

    private void updateSelectedDetailView(AuthorizationPattern pattern) {

        // TODO:
    }

    @FXML
    private void addCondition() {

        // TODO: implement logic
    }

    // TODO: implement update mechanism that calls this method (e.g. using observable pattern)
    public void onConditionsChanged(List<ICondition> conditions) {

        this.conditions.clear();
        this.conditions.addAll(conditions);

        // TODO: handle selection
    }

    // TODO: implement validation logic

}
