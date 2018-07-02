package ui.main.patterns.modal;

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

import extensions.ResourceBundleHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import ui.AppComponents;
import ui.custom.controls.ButtonCell;
import ui.custom.controls.CustomAlert;
import ui.custom.controls.PTableColumn;
import ui.main.patterns.PatternsController;


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
    private HBox editConditionBox;

    @FXML
    private JFXButton deleteSelectedTableTabButton;

    @FXML
    private JFXButton addConditionButton;

    @FXML
    private JFXButton applyPopertyChangesButton;

    @FXML
    private JFXButton saveButton;

    @FXML
    private JFXButton addPropertyButton;

    @FXML
    private JFXButton copyPropertyButton;

    @FXML
    private Label atLeastOneCondWarning;

    private PatternsController parentController;

    private AccessPattern accessPattern;

    private List<TableViewWithAccessCondition> conditionTables;
    private TableView<AccessPatternConditionProperty> selectedTable;
    private List<PTableColumn<AccessPatternConditionProperty, JFXButton>> deleteColumns;
    private AccessPatternConditionProperty selectedProperty;
    private ResourceBundle bundle = ResourceBundleHelper.getInstance().getLanguageBundle();
    private int maxEntries = 10; // the maximum entries per table


    /**
     * Initializes the view and sets up bindings for component visibility.
     */
    @FXML
    public void initialize() {

        // initialize conditionTables
        this.conditionTables = new ArrayList<>();

        // initialize delete columns
        this.deleteColumns = new ArrayList<>();

        // set condition input items
        this.conditionTypeInput.getItems().setAll("Condition", "Profile");

        // set visibility of certain components according to user input
        this.profileInput.managedProperty().bind(this.profileInput.visibleProperty());
        this.conditionBox.managedProperty().bind(this.conditionBox.visibleProperty());
        this.linkageBox.managedProperty().bind(Bindings.not(this.conditionBox.disableProperty()));
        this.linkageBox.visibleProperty().bind(Bindings.not(this.conditionBox.disableProperty()));
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
        atLeastOneCondWarning.visibleProperty().bind(Bindings.and(Bindings.size(conditionTabs.getTabs()).isEqualTo(1), Bindings.not(editConditionBox.disableProperty())));

        // initialize condition type combo box component
        initializeConditionTypeComboBox();

        // initializes the validation binding on textinputs
        initializeValidation();

        // initialize linkage type combo box component
        initializeLinkageInput();
    }

    /**
     * Initializes the LinkageInputComboBox.
     */
    public void initializeLinkageInput() {
        linkageInput.getItems().setAll(ConditionLinkage.None, ConditionLinkage.And, ConditionLinkage.Or);

        linkageInput.getSelectionModel().selectedItemProperty().addListener((selected, oldValue, newValue) -> {
            if (newValue == ConditionLinkage.None) {
                conditionTabs.getTabs().clear();
                this.conditionTables.clear();
                if (accessPattern.getConditions().size() != 0) {
                    addConditionTableTab(accessPattern.getConditions().stream().findFirst().get());
                } else {
                    addConditionTableTab(new AccessCondition());
                }
                this.editConditionBox.disableProperty().unbind();
                this.editConditionBox.setDisable(true);
            } else {
                this.editConditionBox.disableProperty().bind(this.conditionBox.disableProperty());
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

            // add one tab for correct display
            addConditionTableTab(new AccessCondition());

            // disable condition box
            this.conditionBox.setDisable(true);
        } else {

            // prefill the inputs
            this.accessPattern = pattern;

            this.useCaseIdInput.setText(pattern.getUsecaseId());
            this.descriptionInput.setText(pattern.getDescription());

            // Fill choose box
            if (pattern.getConditions().stream().findFirst().get().getProfileCondition() == null) {
                this.conditionTypeInput.getSelectionModel().select("Condition");

                for (AccessCondition condition : pattern.getConditions().stream().sorted(Comparator.comparing(AccessCondition::getId)).collect(Collectors.toList())) {
                    addConditionTableTab(condition);
                }

                this.selectedTable = conditionTables.get(0).getTableView();


                // preselect correct linkage
                this.linkageInput.getSelectionModel().select(pattern.getLinkage());
            } else {
                this.conditionTypeInput.getSelectionModel().select("Profile");

                // add one tab for correct display
                addConditionTableTab(new AccessCondition());

                // disable condition box
                this.conditionBox.setDisable(true);

                this.profileInput.setText(pattern.getConditions().stream().findFirst().get().getProfileCondition().getProfile());
            }
        }
    }

    /**
     * Hides the stage.
     *
     * @param event the given ActionEvent
     */
    public void close(ActionEvent event) throws Exception {

        if (saveButton.isVisible()) {
            CustomAlert customAlert = new CustomAlert(Alert.AlertType.CONFIRMATION, bundle.getString("cancelWithoutSavingTitle"),
                bundle.getString("cancelWithoutSavingMessage"), "Ok", "Cancel");
            if (customAlert.showAndWait().get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                (((Button) event.getSource()).getScene().getWindow()).hide();
            }
        } else {
            (((Button) event.getSource()).getScene().getWindow()).hide();
        }

        // refresh the patternsTable in the parentController
        if (parentController != null) {
            parentController.updateTable();
        }
    }

    /**
     * Fills the detail form with the selected AccessConditionProperty.
     *
     * @param accessPatternConditionProperty the selected AccessConditionProperty
     */
    private void editSelectedAccessConditionProperty(AccessPatternConditionProperty accessPatternConditionProperty, TableView<AccessPatternConditionProperty> table) {

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
            resetDetails();
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
            tab.setText("COND " + ++i);
        }
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
    public void saveChanges(ActionEvent event) throws Exception {

        if (validateBeforeSubmit()) {
            // replace the useCaseId and the description with the text field values
            this.accessPattern.setUsecaseId(this.useCaseIdInput.getText());
            this.accessPattern.setDescription(this.descriptionInput.getText());
            this.accessPattern.setLinkage(linkageInput.getValue());

            // execute only if the pattern is no profile
            if (this.conditionTypeInput.getSelectionModel().getSelectedItem().equals("Condition")) {

                // copy conditions to the list
                for (TableViewWithAccessCondition tableViewWithAccessCondition : conditionTables) {

                    List<AccessPatternConditionProperty> items = tableViewWithAccessCondition.getTableView().getItems();
                    items = items.stream().filter(x -> x.getAuthObject() != null).collect(Collectors.toList());

                    // add table if it is not empty
                    if (items != null && items.size() != 0) {

                        if (tableViewWithAccessCondition.getAccessCondition().getId() == null) {

                            AccessPatternCondition patternCondition = new AccessPatternCondition();
                            AccessCondition accessCondition = new AccessCondition();

                            // set patternCondition Id
                            if (tableViewWithAccessCondition.getAccessCondition().getPatternCondition() != null) {
                                patternCondition.setId(tableViewWithAccessCondition.getAccessCondition().getPatternCondition().getId());
                            }

                            // set accessCondition Id
                            accessCondition.setId(tableViewWithAccessCondition.getAccessCondition().getId());

                            // add properties to patternCondition
                            patternCondition.setProperties(items);

                            // add patternConditon to accessCondition
                            accessCondition.setPatternCondition(patternCondition);

                            accessPattern.getConditions().add(accessCondition);

                        } else {

                            AccessCondition accessCondition = tableViewWithAccessCondition.getAccessCondition();
                            AccessPatternCondition patternCondition = tableViewWithAccessCondition.getAccessCondition().getPatternCondition();

                            List<AccessPatternConditionProperty> properties = new ArrayList<>(items);

                            patternCondition.setProperties(properties);
                            accessCondition.setPatternCondition(patternCondition);
                        }

                    } else {

                        if (tableViewWithAccessCondition.getAccessCondition().getId() != null) {
                            accessPattern.getConditions().remove(tableViewWithAccessCondition.getAccessCondition());
                        }

                    }
                }
            } else {

                // overwrite the profile
                AccessCondition accessCondition = new AccessCondition();
                AccessProfileCondition profileCond = new AccessProfileCondition();

                profileCond.setProfile(this.profileInput.getText());
                accessCondition.setProfileCondition(profileCond);

                List<AccessCondition> conditions = new ArrayList<>();
                conditions.add(accessCondition);

                accessPattern.setConditions(conditions);
            }

            // save the pattern to the database

            // new pattern, id is null
            if (accessPattern.getId() == null) {
                AppComponents.getInstance().getDbContext().createPattern(accessPattern);
            } else {
                AppComponents.getInstance().getDbContext().updatePattern(accessPattern);
            }

            close(event);
        }
    }

    /**
     * Checks if all inputs are valid before submitting.
     */
    private boolean validateBeforeSubmit() {
        // basic input validation
        boolean result = useCaseIdInput.validate() && descriptionInput.validate() && conditionTypeInput.getValue() != null;

        // profile validation
        if (conditionTypeInput.getValue() != null && this.conditionTypeInput.getValue().equals("Profile")) {
            return result && profileInput.validate();
        } else {
            // pattern validation
            // at least one table with one item
            boolean tableEmpty = conditionTables.get(0) != null
                && !conditionTables.get(0).getTableView().getItems().stream().filter(x -> x.getAuthObject() != null).collect(Collectors.toList()).isEmpty();

            return result && linkageInput.getValue() != null && tableEmpty;
        }
    }

    /**
     * Creates a new TableView, wraps it into a Tab and adds it to the ConditionTabs.
     *
     * @param condition the condition which items are displayed in the tableView
     */
    @SuppressWarnings("unchecked") // TODO: remove this annotation if possible
    public void addConditionTableTab(AccessCondition condition) {

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
        TableView<AccessPatternConditionProperty> conditionTable = new TableView<>();

        // add the delete column
        PTableColumn<AccessPatternConditionProperty, JFXButton> deleteColumn = new PTableColumn<>();
        deleteColumn.setPercentageWidth(0.07);
        deleteColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.DELETE, (AccessPatternConditionProperty accessPatternConditionProperty) -> {
            conditionTable.getItems().remove(accessPatternConditionProperty);

            //remove the property from the condition (call by reference)
            if (condition.getPatternCondition() != null) {
                condition.getPatternCondition().getProperties().remove(accessPatternConditionProperty);
            }
            return accessPatternConditionProperty;
        }));
        deleteColumns.add(deleteColumn);

        // listen to selects on conditionPropertiesTable
        conditionTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                editSelectedAccessConditionProperty(newValue, conditionTable);
            }
        });

        // add columns to the table
        conditionTable.getColumns().addAll(authObject, authObjectProperty, value1, value2, value3, value4, deleteColumn);

        // table to all tables
        this.conditionTables.add(new TableViewWithAccessCondition(conditionTable, condition));

        // add entries to the table
        if (condition.getPatternCondition() != null) {
            ObservableList<AccessPatternConditionProperty> entries = FXCollections.observableList(new ArrayList<>(condition.getPatternCondition().getProperties()));
            conditionTable.setItems(entries);
            conditionTable.refresh();
        }

        // presort table
        conditionTable.getSortOrder().addAll(authObject, authObjectProperty);

        // add warning label on 10 entries
        Label warningTextItemLimit = new Label(bundle.getString("warningTextItemLimit"));
        warningTextItemLimit.visibleProperty().bind(Bindings.size(conditionTable.getItems()).isEqualTo(maxEntries));

        // box for the button
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(warningTextItemLimit);
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

            // bind addPropertyButton
            this.addPropertyButton.disableProperty().bind(
                Bindings.or(Bindings.size(selectedTable.getItems()).isEqualTo(maxEntries),
                    Bindings.or(Bindings.isEmpty(authObjectInput.textProperty()),
                        Bindings.or(Bindings.isEmpty(authFieldInput.textProperty()),
                            Bindings.isEmpty(authFieldValue1Input.textProperty())))));

            // bind copyPropertyButton
            this.copyPropertyButton.disableProperty().bind(
                Bindings.or(Bindings.isNull(selectedTable.getSelectionModel().selectedItemProperty()),
                    Bindings.or(Bindings.size(selectedTable.getItems()).isEqualTo(maxEntries),
                        Bindings.or(Bindings.isEmpty(authObjectInput.textProperty()),
                            Bindings.or(Bindings.isEmpty(authFieldInput.textProperty()),
                                Bindings.isEmpty(authFieldValue1Input.textProperty()))))));

            // bind applyPropertyButton
            this.applyPopertyChangesButton.disableProperty().bind(
                Bindings.or(Bindings.isNull(selectedTable.getSelectionModel().selectedItemProperty()),
                    Bindings.or(Bindings.size(selectedTable.getItems()).isEqualTo(maxEntries),
                        Bindings.or(Bindings.isEmpty(authObjectInput.textProperty()),
                            Bindings.or(Bindings.isEmpty(authFieldInput.textProperty()),
                                Bindings.isEmpty(authFieldValue1Input.textProperty()))))));
        });

        this.conditionTabs.getTabs().add(tab);
    }

    /**
     * Edits the selected condition property.
     */
    public void applyChanges() {
        if (selectedProperty != null && authObjectInput.validate() && authFieldInput.validate() && authFieldValue1Input.validate()) {

            // store all the new values from the textFields
            selectedProperty.setAuthObject(authObjectInput.getText());
            selectedProperty.setAuthObjectProperty(authFieldInput.getText());
            selectedProperty.setValue1(authFieldValue1Input.getText());
            selectedProperty.setValue2(authFieldValue2Input.getText());
            selectedProperty.setValue3(authFieldValue3Input.getText());
            selectedProperty.setValue4(authFieldValue4Input.getText());

            selectedTable.getSelectionModel().clearSelection();
            resetDetails();
        }
    }

    /**
     * Adds a property to the selected table.
     */
    public void addConditionProperty() {
        // create the property to add and give it the parameters
        AccessPatternConditionProperty propertyToAdd = new AccessPatternConditionProperty();
        propertyToAdd.setAuthObject(authObjectInput.getText());
        propertyToAdd.setAuthObjectProperty(authFieldInput.getText());
        propertyToAdd.setValue1(authFieldValue1Input.getText());
        propertyToAdd.setValue2(authFieldValue2Input.getText());
        propertyToAdd.setValue3(authFieldValue3Input.getText());
        propertyToAdd.setValue4(authFieldValue4Input.getText());

        selectedTable.getItems().add(propertyToAdd);
        selectedTable.requestFocus();
        selectedTable.scrollTo(selectedTable.getItems().size() - 1);
        selectedTable.getSelectionModel().clearSelection();

        resetDetails();
    }


    /**
     * Copies the currently selected property.
     */
    public void copyConditionProperty() {
        if (selectedProperty != null) {
            // create the property to add and give it the parameters
            AccessPatternConditionProperty propertyToAdd = new AccessPatternConditionProperty();
            propertyToAdd.setAuthObject(authObjectInput.getText());
            propertyToAdd.setAuthObjectProperty(authFieldInput.getText());
            propertyToAdd.setValue1(authFieldValue1Input.getText());
            propertyToAdd.setValue2(authFieldValue2Input.getText());
            propertyToAdd.setValue3(authFieldValue3Input.getText());
            propertyToAdd.setValue4(authFieldValue4Input.getText());

            selectedTable.getItems().add(propertyToAdd);
            selectedTable.requestFocus();
            selectedTable.getSelectionModel().selectLast();
            selectedTable.getFocusModel().focus(selectedTable.getItems().size() - 1);
            selectedTable.scrollTo(selectedTable.getItems().size() - 1);
        }
    }


    /**
     * Resets the details.
     */
    private void resetDetails() {
        authObjectInput.setText("");
        authFieldInput.setText("");
        authFieldValue1Input.setText("");
        authFieldValue2Input.setText("");
        authFieldValue3Input.setText("");
        authFieldValue4Input.setText("");
    }

    /**
     * Makes all inputs not editable and hides the buttons.
     *
     * @param editable whether the components should be editable
     */
    public void setEditable(boolean editable) {

        // disable everything
        if (!editable) {

            // make text fields uneditable
            useCaseIdInput.setEditable(false);
            descriptionInput.setEditable(false);

            authObjectInput.setEditable(false);
            authFieldInput.setEditable(false);
            authFieldValue1Input.setEditable(false);
            authFieldValue2Input.setEditable(false);
            authFieldValue3Input.setEditable(false);
            authFieldValue4Input.setEditable(false);
            profileInput.setEditable(false);

            // disable combo boxes
            linkageInput.setDisable(true);
            conditionTypeInput.setDisable(true);

            // remove buttons
            addConditionButton.setVisible(false);
            deleteSelectedTableTabButton.setVisible(false);
            saveButton.setVisible(false);
            applyPopertyChangesButton.setVisible(false);
            addPropertyButton.setVisible(false);
            copyPropertyButton.setVisible(false);

            // remove delete column
            for (PTableColumn column : deleteColumns) {
                column.setVisible(false);
            }
        }
    }

    /**
     * Stores a TableView and an AccessCondition which is needed for updating a pattern correctly.
     */
    public class TableViewWithAccessCondition {

        private TableView<AccessPatternConditionProperty> tableView;
        private AccessCondition accessCondition;

        public TableViewWithAccessCondition(TableView<AccessPatternConditionProperty> tableView, AccessCondition accessCondition) {
            this.tableView = tableView;
            this.accessCondition = accessCondition;
        }

        public TableView<AccessPatternConditionProperty> getTableView() {
            return tableView;
        }

        public void setTableView(TableView<AccessPatternConditionProperty> tableView) {
            this.tableView = tableView;
        }

        public AccessCondition getAccessCondition() {
            return accessCondition;
        }

        public void setAccessCondition(AccessCondition accessCondition) {
            this.accessCondition = accessCondition;
        }
    }

    /**
     * Sets the parent controller.
     *
     * @param parentController the parent controller
     */
    public void setParentController(PatternsController parentController) {
        this.parentController = parentController;
    }
}
