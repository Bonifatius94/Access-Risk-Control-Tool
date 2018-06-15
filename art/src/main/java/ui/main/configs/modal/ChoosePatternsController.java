package ui.main.configs.modal;

import com.jfoenix.controls.JFXButton;

import data.entities.AccessCondition;
import data.entities.AccessPattern;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

import io.msoffice.excel.AccessPatternImportHelper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import ui.custom.controls.ButtonCell;


public class ChoosePatternsController {

    @FXML
    public TableView<AccessPattern> allPatternsTable;

    @FXML
    public TableView<AccessPattern> selectedPatternsTable;

    @FXML
    public TableColumn<AccessPattern, JFXButton> allPatternsTableDeleteColumn;

    @FXML
    public TableColumn<AccessPattern, JFXButton> selectedPatternsTableDeleteColumn;

    @FXML
    public TableColumn<AccessPattern, Set<AccessCondition>> allPatternsTableConditionCountColumn;

    @FXML
    public TableColumn<AccessPattern, Set<AccessCondition>> selectedPatternsTableConditionCountColumn;

    @FXML
    public JFXButton addToSelectedButton;

    @FXML
    public JFXButton removeFromSelectedButton;

    private ConfigsFormController parentController;
    private List<AccessPattern> alreadySelectedPatterns;

    @FXML
    public void initialize() {
        initializePatternsTables();
    }

    /**
     * Initializes the patterns table.
     */
    private void initializePatternsTables() {

        // set selection mode to MULTIPLE
        allPatternsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        selectedPatternsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // bind button disabled to tables selection models
        addToSelectedButton.disableProperty().bind(Bindings.size(allPatternsTable.getSelectionModel().getSelectedItems()).isEqualTo(0));
        removeFromSelectedButton.disableProperty().bind(Bindings.size(selectedPatternsTable.getSelectionModel().getSelectedItems()).isEqualTo(0));

        initializeAllPatternsTable();
        initializeSelectedPatternsTable();
    }

    /**
     * Initializes the allPatternsTable columns.
     */
    private void initializeAllPatternsTable() {

        // overwrite the column in which the number of useCases is displayed
        allPatternsTableConditionCountColumn.setCellFactory(col -> new TableCell<AccessPattern, Set<AccessCondition>>() {

            @Override
            protected void updateItem(Set<AccessCondition> items, boolean empty) {

                // display nothing if the row is empty, otherwise the item count
                setText((empty || items == null) ? "" : "" + items.size());

            }

        });

        // custom comparator for the conditionCountColumn
        allPatternsTableConditionCountColumn.setComparator((list1, list2) -> list1.size() <= list2.size() ? 0 : 1);
    }

    /**
     * Initializes the selectedPatternsTable columns.
     */
    private void initializeSelectedPatternsTable() {
        // Add the delete column
        selectedPatternsTableDeleteColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.DELETE, (AccessPattern accessPattern) -> {
            allPatternsTable.getItems().add(accessPattern);
            selectedPatternsTable.getItems().remove(accessPattern);
            return accessPattern;
        }));

        // overwrite the column in which the number of useCases is displayed
        selectedPatternsTableConditionCountColumn.setCellFactory(col -> new TableCell<AccessPattern, Set<AccessCondition>>() {

            @Override
            protected void updateItem(Set<AccessCondition> items, boolean empty) {

                // display nothing if the row is empty, otherwise the item count
                setText((empty || items == null) ? "" : "" + items.size());

            }

        });

        // custom comparator for the conditionCountColumn
        selectedPatternsTableConditionCountColumn.setComparator((list1, list2) -> list1.size() <= list2.size() ? 0 : 1);
    }

    /**
     * Removes the selected patterns from the selectedList.
     */
    public void removeFromSelected() {
        if (selectedPatternsTable.getSelectionModel().getSelectedItems() != null) {
            List<AccessPattern> selectedPatterns = selectedPatternsTable.getSelectionModel().getSelectedItems();
            allPatternsTable.getItems().addAll(selectedPatterns);
            selectedPatternsTable.getItems().removeAll(selectedPatterns);
            selectedPatternsTable.getSelectionModel().clearSelection();
            allPatternsTable.refresh();
            selectedPatternsTable.refresh();
        }
    }

    /**
     * Adds the selected patterns to the selectedList.
     */
    public void addToSelected() {
        if (allPatternsTable.getSelectionModel().getSelectedItems() != null) {
            List<AccessPattern> selectedPatterns = allPatternsTable.getSelectionModel().getSelectedItems();
            selectedPatternsTable.getItems().addAll(selectedPatterns);
            allPatternsTable.getItems().removeAll(selectedPatterns);
            allPatternsTable.getSelectionModel().clearSelection();
            allPatternsTable.refresh();
            selectedPatternsTable.refresh();
        }
    }

    /**
     * Prefills the selectedTable with the given patterns.
     *
     * @param selectedPatterns the already selected patterns
     */
    public void giveSelectedPatterns(List<AccessPattern> selectedPatterns) {

        this.alreadySelectedPatterns = selectedPatterns;

        // add the selected patterns to the selected list
        if (selectedPatterns != null || selectedPatterns.size() != 0) {
            ObservableList<AccessPattern> items = FXCollections.observableList(selectedPatterns);
            this.selectedPatternsTable.setItems(items);
        }

        // fill the allPatternsTable
        fillAllPatternsTable();
    }

    /**
     * Provides the data for the patternTable.
     */
    private void fillAllPatternsTable() {

        // test the table with data from the Example - Zugriffsmuster.xlsx file
        try {
            AccessPatternImportHelper helper = new AccessPatternImportHelper();

            List<AccessPattern> patterns = helper.importAuthorizationPattern("Example - Zugriffsmuster.xlsx");

            patterns.get(0).setId(0);
            patterns.get(1).setId(1);
            patterns.get(2).setId(2);
            patterns.get(3).setId(3);
            patterns.get(4).setId(4);

            // remove all entries that are already in the selectedList
            patterns = patterns.stream().filter(x -> {
                for (AccessPattern pattern : this.alreadySelectedPatterns) {
                    if (x.getId() == pattern.getId()) {
                        return false;
                    }
                }
                return true;
            }).collect(Collectors.toList());

            ObservableList<AccessPattern> list = FXCollections.observableList(patterns);

            allPatternsTable.setItems(list);
            allPatternsTable.refresh();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Saves the changes to the previous window.
     */
    public void saveChanges(ActionEvent event) {
        this.parentController.setPatterns(this.selectedPatternsTable.getItems());
        this.close(event);
    }

    /**
     * Sets the parent controller so we can give it some data.
     * @param controller the parent controller
     */
    public void setParentController(ConfigsFormController controller) {
        this.parentController = controller;
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
