package ui.main.patterns;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import data.entities.AccessCondition;
import data.entities.AccessPattern;
import data.entities.AccessPatternCondition;
import data.entities.AccessPatternConditionProperty;
import data.entities.AccessProfileCondition;
import data.entities.ConditionLinkage;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

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
    private AccessPattern originalPattern;

    private List<TableView> conditionTables;
    private TableView<AccessPatternConditionProperty> selectedTable;
    private AccessPatternConditionProperty selectedProperty;


    /**
     * Initializes the view and sets up bindings for component visibility.
     */
    @FXML
    public void initialize() {

        // initialize conditionTables
        this.conditionTables = new ArrayList<>();

        // set condition input items
        this.conditionTypeInput.getItems().setAll("Condition", "Profile");

        // set visibility of certain components according to user input
        this.profileInput.managedProperty().bind(this.profileInput.visibleProperty());
        this.conditionBox.managedProperty().bind(this.conditionBox.visibleProperty());
        this.linkageBox.managedProperty().bind(Bindings.not(this.conditionBox.disableProperty()));
        this.linkageBox.visibleProperty().bind(Bindings.not(this.conditionBox.disableProperty()));
        this.editConditionBox.managedProperty().bind(this.conditionBox.disableProperty());
        this.editConditionBox.disableProperty().bind(this.conditionBox.disableProperty());

        // deselect on tab change
        this.conditionTabs.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            this.selectedProperty = null;

            if (selectedTable != null) {
                this.selectedTable.getSelectionModel().clearSelection();

                // remove all empty properties from the list
                this.selectedTable.getItems().removeAll(this.selectedTable.getItems().stream().filter(x -> x.getAuthObject() == null).collect(Collectors.toList()));
            }

            // reset inputs
            this.editSelectedAccessConditionProperty(null, null);
        });

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
                this.conditionTables.clear();
                if (accessPattern.getConditions().size() != 0) {
                    addConditionTableTab(accessPattern.getConditions().stream().findFirst().get());
                } else {
                    addConditionTableTab(new AccessCondition());
                }
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
                        conditionBox.setDisable(true);
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
                    conditionBox.setDisable(false);
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

        if (pattern == null) {
            this.accessPattern = new AccessPattern();
        } else {

            // prefill the inputs
            this.accessPattern = pattern;

            this.useCaseIdInput.setText(pattern.getUsecaseId());
            this.descriptionInput.setText(pattern.getDescription());

            // Fill choose box
            if (pattern.getConditions().stream().findFirst().get().getProfileCondition() == null) {
                this.conditionTypeInput.getSelectionModel().select("Condition");

                for (AccessCondition condition : pattern.getConditions()) {
                    addConditionTableTab(condition);
                }

                // preselect correct linkage
                this.linkageInput.getSelectionModel().select(pattern.getLinkage());
            } else {
                this.conditionTypeInput.getSelectionModel().select("Profile");

                this.profileInput.setText(pattern.getConditions().stream().findFirst().get().getProfileCondition().getProfile());
            }
        }

        // save the original pattern as deep copy
        this.originalPattern = new AccessPattern(this.accessPattern);
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

        if (accessPatternConditionProperty != null) {

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

        } else {

            // reset form
            authObjectInput.setText("");
            authFieldInput.setText("");
            authFieldValue1Input.setText("");
            authFieldValue2Input.setText("");
            authFieldValue3Input.setText("");
            authFieldValue4Input.setText("");

        }

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
        this.conditionTables.remove(selectedTable);

        // remove the tab from the conditionTabs
        this.conditionTabs.getTabs().remove(this.conditionTabs.getSelectionModel().getSelectedItem());

        // rename tabs after deleting so no numbers are left out
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

        this.giveSelectedAccessPattern(this.originalPattern);

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

        // replace the useCaseId and the description with the text field values
        this.accessPattern.setUsecaseId(this.useCaseIdInput.getText());
        this.accessPattern.setDescription(this.descriptionInput.getText());
        this.accessPattern.setLinkage(linkageInput.getValue());

        // store the condition in here
        List<AccessCondition> newConditions = new ArrayList<>();

        // execute only if the pattern is no profile
        if (this.conditionTypeInput.getSelectionModel().getSelectedItem().equals("Condition")) {

            // copy conditions to the list
            for (TableView<AccessPatternConditionProperty> condTable : conditionTables) {

                // add table if it is not empty
                if (condTable.getItems() != null && condTable.getItems().size() != 0) {

                    AccessPatternCondition patternCondition = new AccessPatternCondition();
                    AccessCondition accessCondition = new AccessCondition();

                    patternCondition.setProperties(condTable.getItems());
                    accessCondition.setPatternCondition(patternCondition);

                    newConditions.add(accessCondition);

                }
            }
        } else {

            // overwrite the profile
            AccessCondition accessCondition = new AccessCondition();
            AccessProfileCondition profileCond = new AccessProfileCondition();

            profileCond.setProfile(this.profileInput.getText());
            accessCondition.setProfileCondition(profileCond);

            newConditions.add(accessCondition);
        }

        // add the list to the AccessPattern
        this.accessPattern.setConditions(newConditions);

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

        // table to all tables
        this.conditionTables.add(conditionTable);

        // add entries to the table
        if (condition.getPatternCondition() != null) {
            ObservableList<AccessPatternConditionProperty> entries = FXCollections.observableList(new ArrayList<>(condition.getPatternCondition().getProperties()));
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
        Tab tab = new Tab("COND " + (this.conditionTabs.getTabs().size() + 1));
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
