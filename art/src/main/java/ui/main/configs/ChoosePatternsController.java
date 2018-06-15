package ui.main.configs;

import com.jfoenix.controls.JFXButton;
import data.entities.AccessCondition;
import data.entities.AccessPattern;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ui.custom.controls.ButtonCell;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    private List<AccessPattern> selectedPatterns;

    @FXML
    public void initialize() {

    }

    /**
     * Initializes the patterns table.
     */
    private void initializePatternsTables() {

        // set selection mode to MULTIPLE
        allPatternsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        selectedPatternsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        initializeAllPatternsTable();
        initializeSelectedPatternsTable();
    }

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

    private void initializeSelectedPatternsTable() {
        // Add the delete column
        selectedPatternsTableDeleteColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.DELETE, (AccessPattern accessPattern) -> {
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

    public void removeFromSelected() {

    }

    public void addToSelected() {

    }

    public void giveSelectedPatterns(List<AccessPattern> selectedItems) {

        this.selectedPatterns = selectedItems;

        // add the selected patterns to the selected list
        if (selectedItems != null || selectedItems.size() != 0) {
            ObservableList<AccessPattern> items = FXCollections.observableList(selectedItems);
            this.selectedPatternsTable.setItems(items);

            // fill the allPatternsTable
            fillAllPatternsTable();
        }
    }

    private void fillAllPatternsTable() {

    }
}
