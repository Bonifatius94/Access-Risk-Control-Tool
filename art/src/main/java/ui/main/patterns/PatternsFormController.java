package ui.main.patterns;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import data.entities.AccessCondition;
import data.entities.AccessPattern;
import data.entities.AccessPatternCondition;
import data.entities.AccessPatternConditionProperty;
import data.entities.ConditionLinkage;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui.App;
import ui.custom.controls.ButtonCell;
import ui.custom.controls.CustomWindow;
import ui.custom.controls.PTableColumn;


public class PatternsFormController {

    @FXML
    private JFXTextField useCaseIdInput;

    @FXML
    private JFXTextField descriptionInput;

    @FXML
    private JFXComboBox<String> conditionTypeInput;

    @FXML
    private JFXComboBox<ConditionLinkage> linkageInput;

    @FXML
    private HBox conditionBox;

    @FXML
    private HBox linkageBox;

    @FXML
    private JFXTextField profileInput;


    // Auth properties table

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
    private JFXTabPane conditionTabs;

    @FXML
    private JFXButton applyPropertyChanges;

    @FXML
    private HBox editConditionBox;

    @FXML
    private JFXButton deleteSelectedTableTabButton;

    @FXML
    private Label atLeastOneCondWarning;


    private AccessPattern accessPattern;
    private TableView selectedTable;
    private AccessPatternConditionProperty selectedProperty;


    /**
     * Initializes the view and sets up bindings for component visibility.
     */
    @FXML
    public void initialize() {
        // set condition input items
        this.conditionTypeInput.getItems().setAll("Condition", "Profile");

        // set visibility of certain components according to user input
        this.profileInput.managedProperty().bind(this.profileInput.visibleProperty());
        this.conditionBox.managedProperty().bind(this.conditionBox.visibleProperty());
        this.linkageBox.managedProperty().bind(this.conditionBox.visibleProperty());
        this.linkageBox.visibleProperty().bind(this.conditionBox.visibleProperty());
        this.editConditionBox.managedProperty().bind(this.conditionBox.visibleProperty());
        this.editConditionBox.visibleProperty().bind(this.conditionBox.visibleProperty());

        // don't allow less than 1 tab
        deleteSelectedTableTabButton.disableProperty().bind(Bindings.size(conditionTabs.getTabs()).isEqualTo(1));
        atLeastOneCondWarning.visibleProperty().bind(Bindings.size(conditionTabs.getTabs()).isEqualTo(1));

        // initialize condition type combo box component
        initializeConditionTypeComboBox();

        initializeValidation();

        initializeLinkageInput();


    }

    /**
     * Initializes the LinkageInputComboBox.
     */
    public void initializeLinkageInput() {
        linkageInput.getItems().setAll(ConditionLinkage.None, ConditionLinkage.And, ConditionLinkage.Or);

        linkageInput.getSelectionModel().selectedItemProperty().addListener((ChangeListener<ConditionLinkage>) (selected, oldValue, newValue) -> {
            if (newValue == ConditionLinkage.None) {
                conditionTabs.getTabs().clear();
                addConditionTableTab(accessPattern.getConditions().get(0));
                this.editConditionBox.managedProperty().unbind();
                this.editConditionBox.visibleProperty().unbind();
                this.editConditionBox.setVisible(false);
            } else {
                this.editConditionBox.managedProperty().bind(this.conditionBox.visibleProperty());
                this.editConditionBox.visibleProperty().bind(this.conditionBox.visibleProperty());
            }
        });
    }

    /**
     * Initializes the condition type combo box and manages the component visibility.
     */
    private void initializeConditionTypeComboBox() {
        // add a change listener to the conditionType input so other components are hidden accordingly
        this.conditionTypeInput.getSelectionModel().selectedItemProperty().addListener((ChangeListener<String>) (selected, oldValue, newValue) -> {

            if (oldValue != null) {
                switch (oldValue) {
                    case "Condition":
                        conditionBox.setVisible(false);
                        break;
                    case "Profile":
                        profileInput.setVisible(false);
                        break;
                    default:
                        profileInput.setVisible(false);
                        conditionBox.setVisible(false);
                        break;
                }
            }
            switch (newValue) {
                case "Condition":
                    conditionBox.setVisible(true);
                    break;
                case "Profile":
                    profileInput.setVisible(true);
                    break;
                default:
                    profileInput.setVisible(true);
                    conditionBox.setVisible(true);
                    break;
            }
        });

    }

    /**
     * Initializes the validation for certain text inputs in order to display an error message (e.g. required).
     */
    private void initializeValidation() {

        useCaseIdInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                useCaseIdInput.validate();
            }
        });

        descriptionInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                descriptionInput.validate();
            }
        });

        profileInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                profileInput.validate();
            }
        });

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

    /**
     * Prefills the inputs with the given pattern.
     *
     * @param pattern the selected pattern
     */
    public void giveSelectedAccessPattern(AccessPattern pattern) {
        this.accessPattern = pattern;

        this.useCaseIdInput.setText(pattern.getUsecaseId());
        this.descriptionInput.setText(pattern.getDescription());

        // Fill choose box
        if (pattern.getConditions().get(0).getProfileCondition() == null) {
            this.conditionTypeInput.getSelectionModel().select("Condition");

            for (AccessCondition condition : pattern.getConditions()) {
                addConditionTableTab(condition);
            }

            // preselect correct linkage
            this.linkageInput.getSelectionModel().select(pattern.getLinkage());
        } else {
            this.conditionTypeInput.getSelectionModel().select("Profile");

            this.profileInput.setText(pattern.getConditions().get(0).getProfileCondition().getProfile());
        }
    }

    public void close(ActionEvent event) {
        (((Button) event.getSource()).getScene().getWindow()).hide();
    }

    /**
     * Fills the detail form with the selected AccessConditionProperty.
     *
     * @param accessPatternConditionProperty the selected AccessConditionProperty
     */
    private void editSelectedAccessConditionProperty(AccessPatternConditionProperty accessPatternConditionProperty, TableView table) {

        this.selectedProperty = accessPatternConditionProperty;
        this.selectedTable = table;

        // set the inputs to the given values
        authObjectInput.setText(accessPatternConditionProperty.getAuthObject());
        authFieldInput.setText(accessPatternConditionProperty.getAuthObjectProperty());
        authFieldValue1Input.setText(accessPatternConditionProperty.getValue1());
        authFieldValue2Input.setText(accessPatternConditionProperty.getValue2());
        authFieldValue3Input.setText(accessPatternConditionProperty.getValue3());
        authFieldValue4Input.setText(accessPatternConditionProperty.getValue4());

        // reset error validation
        authObjectInput.validate();
        authFieldInput.validate();
        authFieldValue1Input.validate();
    }

    public void addEmptyConditionTableTab() {
        this.addConditionTableTab(new AccessCondition());
    }

    /**
     * Deletes the selected Tab from the conditionTabs and removes its items from the AccessPattern.
     */
    public void deleteSelectedTableTab() {

        // remove items from AccessPattern
        this.selectedTable.getItems().clear();

        // remove the tab from the conditionTabs
        this.conditionTabs.getTabs().remove(this.conditionTabs.getSelectionModel().getSelectedItem());

        // rename tabs to after deleting so no numbers are left out
        int i = 0;
        for (Tab tab : this.conditionTabs.getTabs()) {
            tab.setText("Condition " + ++i);
        }

    }

    /**
     * Resets the form.
     */
    public void resetForm() {
        this.conditionTabs.getTabs().clear();

        // TODO: Deep copy of accessPattern for AccessConditions
        this.giveSelectedAccessPattern(accessPattern);

        this.validateInputs();
    }

    /**
     * Validates the main inputs once (e.g to remove errors).
     */
    private void validateInputs() {
        useCaseIdInput.validate();
        descriptionInput.validate();
        profileInput.validate();
    }

    /**
     * Saves the changes to the database.
     */
    public void saveChanges() {
        this.accessPattern.setUsecaseId(this.useCaseIdInput.getText());
        this.accessPattern.setDescription(this.descriptionInput.getText());

        System.out.println(this.accessPattern);
    }

    /**
     * Creates a new TableView, wraps it into a Tab and adds it to the ConditionTabs.
     *
     * @param condition the condition which items are displayed in the tableView
     */
    public void addConditionTableTab(AccessCondition condition) {

        // get the resource bundle for internationalization
        ResourceBundle bundle = ResourceBundle.getBundle("lang");

        // AuthObject Column
        PTableColumn<AccessPatternConditionProperty, String> authObject = new PTableColumn<>();
        authObject.setCellValueFactory(new PropertyValueFactory<>("authObject"));
        authObject.setPercentageWidth(0.2);
        authObject.setText(bundle.getString("authObject"));

        // AuthField Column
        PTableColumn<AccessPatternConditionProperty, String> authObjectProperty = new PTableColumn<>();
        authObjectProperty.setCellValueFactory(new PropertyValueFactory<>("authObjectProperty"));
        authObjectProperty.setPercentageWidth(0.2);
        authObjectProperty.setText(bundle.getString("authField"));

        // Field Value 1 Column
        PTableColumn<AccessPatternConditionProperty, String> value1 = new PTableColumn<>();
        value1.setCellValueFactory(new PropertyValueFactory<>("value1"));
        value1.setPercentageWidth(0.13);
        value1.setText("1");

        // Field Value 2 Column
        PTableColumn<AccessPatternConditionProperty, String> value2 = new PTableColumn<>();
        value2.setCellValueFactory(new PropertyValueFactory<>("value2"));
        value2.setPercentageWidth(0.13);
        value2.setText("2");

        // Field Value 3 Column
        PTableColumn<AccessPatternConditionProperty, String> value3 = new PTableColumn<>();
        value3.setCellValueFactory(new PropertyValueFactory<>("value3"));
        value3.setPercentageWidth(0.13);
        value3.setText("3");

        // Field Value 4 Column
        PTableColumn<AccessPatternConditionProperty, String> value4 = new PTableColumn<>();
        value4.setCellValueFactory(new PropertyValueFactory<>("value4"));
        value4.setPercentageWidth(0.13);
        value4.setText("4");

        // create a new TableView of type AccessPatternConditionProperty
        TableView<AccessPatternConditionProperty> conditionTable = new TableView();

        // add the delete column
        PTableColumn<AccessPatternConditionProperty, JFXButton> deleteColumn = new PTableColumn<>();
        deleteColumn.setPercentageWidth(0.07);
        deleteColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.DELETE, (AccessPatternConditionProperty accessPatternConditionProperty) -> {
            conditionTable.getItems().remove(accessPatternConditionProperty);
            return accessPatternConditionProperty;
        }));

        // listen to selects on conditionPropertiesTable
        conditionTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                editSelectedAccessConditionProperty(newValue, conditionTable);
            }
        });

        // add columns to the table
        conditionTable.getColumns().addAll(authObject, authObjectProperty, value1, value2, value3, value4, deleteColumn);

        // add entries to the table
        if (condition.getPatternCondition() != null) {
            ObservableList<AccessPatternConditionProperty> entries = FXCollections.observableList(condition.getPatternCondition().getProperties());
            conditionTable.setItems(entries);
            conditionTable.refresh();
        }

        // create add button
        JFXButton addButton = new JFXButton();
        addButton.setOnAction(event -> {
            conditionTable.getItems().add(new AccessPatternConditionProperty());
            conditionTable.requestFocus();
            conditionTable.getSelectionModel().selectLast();
            conditionTable.getFocusModel().focus(conditionTable.getItems().size() - 1);
            conditionTable.scrollTo(conditionTable.getItems().size() - 1);
        });
        MaterialDesignIconView view = new MaterialDesignIconView(MaterialDesignIcon.PLUS);
        addButton.setGraphic(view);
        addButton.setTooltip(new Tooltip(bundle.getString("addProperty")));
        addButton.getStyleClass().add("round-button");
        addButton.setMinHeight(30);
        addButton.setPrefHeight(30);

        // disable button on 10 entries
        addButton.disableProperty().bind(Bindings.size(conditionTable.getItems()).isEqualTo(10));

        // add warning label on 10 entries
        Label warningTextItemLimit = new Label(bundle.getString("warningTextItemLimit"));
        warningTextItemLimit.visibleProperty().bind(Bindings.size(conditionTable.getItems()).isEqualTo(10));

        // box for the button
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(warningTextItemLimit, addButton);
        buttonBox.setSpacing(20);
        buttonBox.setPadding(new Insets(0, 20, 0, 0));
        HBox.setHgrow(buttonBox, Priority.ALWAYS);

        // wrapper for everything
        VBox wrapper = new VBox();
        wrapper.setSpacing(15);

        // add everything to wrapper
        wrapper.getChildren().addAll(conditionTable, buttonBox);

        // add table to tab and add tab to TabPane
        Tab tab = new Tab("Condition " + (this.conditionTabs.getTabs().size() + 1));
        tab.setContent(wrapper);

        // "bind" the table to the tab
        tab.setOnSelectionChanged((event) -> {
            this.selectedTable = conditionTable;
        });

        this.conditionTabs.getTabs().add(tab);
    }

    /**
     * Edits the selected condition property.
     */
    public void editConditionProperty() {

        if (selectedProperty != null && authObjectInput.validate() && authFieldInput.validate() && authFieldValue1Input.validate()) {

            // store all the new values from the textFields
            selectedProperty.setAuthObject(authObjectInput.getText());
            selectedProperty.setAuthObjectProperty(authFieldInput.getText());
            selectedProperty.setValue1(authFieldValue1Input.getText());
            selectedProperty.setValue2(authFieldValue2Input.getText());
            selectedProperty.setValue3(authFieldValue3Input.getText());
            selectedProperty.setValue4(authFieldValue4Input.getText());

            this.selectedTable.refresh();
        }

    }
}
