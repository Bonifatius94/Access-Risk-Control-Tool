package ui.main.patterns.modal;

import com.jfoenix.controls.JFXCheckBox;

import data.entities.AccessCondition;
import data.entities.AccessPattern;

import extensions.ResourceBundleHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import ui.AppComponents;
import ui.custom.controls.ConditionTypeCellFactory;
import ui.main.configs.modal.ConfigsFormController;
import ui.main.patterns.PatternsController;


public class PatternImportController {

    @FXML
    public TableView<AccessPattern> patternsTable;

    @FXML
    public TableColumn<AccessPattern, Set<AccessCondition>> conditionCountColumn;

    @FXML
    public TableColumn<AccessPattern, Set<AccessCondition>> conditionTypeColumn;

    @FXML
    public TableColumn<AccessPattern, AccessPattern> checkRowColumn;

    private ConfigsFormController configsFormController;
    private PatternsController patternsController;

    private ResourceBundle bundle;
    private List<AccessPattern> selectedPatterns;


    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() throws Exception {

        selectedPatterns = new ArrayList<>();

        // load the ResourceBundle
        bundle = ResourceBundleHelper.getInstance().getLanguageBundle();

        // initialize the table
        initializeTableColumns();
    }

    /**
     * Initializes the table columns that need extra content.
     */
    private void initializeTableColumns() {

        initializeConditionCountColumn();

        initializeConditionTypeColumn();

        initializeCheckRowColumn();
    }

    /**
     * Initializes the CheckRowColumn which displays a checkbox.
     */
    private void initializeCheckRowColumn() {
        checkRowColumn.setCellFactory(
            col -> new TableCell<AccessPattern, AccessPattern>() {

                @Override
                protected void updateItem(AccessPattern item, boolean empty) {
                    if (empty) {
                        setText("");
                    } else {

                        // add a checkBox
                        JFXCheckBox checkBox = new JFXCheckBox();
                        AccessPattern pattern = patternsTable.getItems().get(getIndex());

                        // preselect boxes
                        checkBox.setSelected(true);

                        // add/remove the selected patterns to/from the list
                        checkBox.selectedProperty().addListener((ol, oldValue, newValue) -> {
                            if (newValue) {
                                selectedPatterns.add(pattern);
                            } else {
                                selectedPatterns.remove(pattern);
                            }
                        });

                        setGraphic(checkBox);
                    }
                }
            });
    }

    /**
     * Initialize the ConditionCountColumn so it displays the number of conditions of the AccessPattern.
     */
    private void initializeConditionCountColumn() {

        // overwrite the column in which the number of useCases is displayed
        conditionCountColumn.setCellFactory(col -> new TableCell<AccessPattern, Set<AccessCondition>>() {

            @Override
            protected void updateItem(Set<AccessCondition> items, boolean empty) {

                // display nothing if the row is empty, otherwise the item count
                setText((empty || items == null) ? "" : "" + items.size());

            }

        });

        // custom comparator for the conditionCountColumn
        conditionCountColumn.setComparator((list1, list2) -> list1.size() <= list2.size() ? 0 : 1);
    }

    /**
     * Initialize ConditionTypeColumn to show the type of the condition as an icon.
     */
    private void initializeConditionTypeColumn() {

        // sets the icon of the condition to pattern or profile
        conditionTypeColumn.setCellFactory(new ConditionTypeCellFactory());
    }

    /**
     * Gives the controller the imported patterns.
     *
     * @param importedPatterns the imported patterns
     */
    public void giveImportedPatterns(List<AccessPattern> importedPatterns) {
        if (importedPatterns != null) {
            selectedPatterns = importedPatterns;
            ObservableList<AccessPattern> items = FXCollections.observableArrayList(importedPatterns);
            patternsTable.setItems(items);
        }
    }

    /**
     * Gives the parentControllers the imported patterns and closes the window.
     */
    public void saveChanges(ActionEvent event) throws Exception {

        if (selectedPatterns.size() != 0) {

            close(event);

            // give the parent controllers the patterns
            if (configsFormController != null) {
                configsFormController.addPatterns(selectedPatterns);
            } else if (patternsController != null) {

                // save the patterns to the database
                for (AccessPattern pattern : selectedPatterns) {
                    AppComponents.getInstance().getDbContext().createPattern(pattern);
                }

                patternsController.updateTable();
            }
        }
    }

    /**
     * Sets the parent controller.
     *
     * @param parentController the parent controller
     */
    public void setConfigsFormController(ConfigsFormController parentController) {
        this.configsFormController = parentController;
    }

    /**
     * Sets the parent controller.
     *
     * @param parentController the parent controller
     */
    public void setPatternsController(PatternsController parentController) {
        this.patternsController = parentController;
    }

    /**
     * Hides the stage.
     *
     * @param event the given ActionEvent
     */
    public void close(ActionEvent event) {
        (((Button) event.getSource()).getScene().getWindow()).hide();
    }
}
