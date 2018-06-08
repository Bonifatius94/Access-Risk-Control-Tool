package ui.main.patterns;

import com.jfoenix.controls.JFXButton;
import data.entities.AccessCondition;
import data.entities.AccessPattern;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

import io.msoffice.excel.AccessPatternImportHelper;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import ui.custom.controls.ButtonCell;


public class PatternsController {

    @FXML
    public TableView<AccessPattern> patternsTable;

    @FXML
    public TableColumn<AccessPattern, List<AccessCondition>> useCaseCountColumn;

    @FXML
    public TableColumn<AccessPattern, JFXButton> deleteColumn;

    @FXML
    public TableColumn<AccessPattern, JFXButton> editColumn;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        initializeTableColumns();

        // test the table with data from the Example - Zugriffsmuster.xlsx file
        try {
            AccessPatternImportHelper helper = new AccessPatternImportHelper();

            List<AccessPattern> patterns = helper.importAuthorizationPattern("Example - Zugriffsmuster.xlsx");
            patterns.addAll(patterns);
            ObservableList<AccessPattern> list = FXCollections.observableList(patterns);

            patternsTable.setItems(list);
            patternsTable.refresh();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the table columns that need extra content.
     */
    private void initializeTableColumns() {
        // Add the delete column
        deleteColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.DELETE, (AccessPattern accessPattern) -> {
            patternsTable.getItems().remove(accessPattern);
            return accessPattern;
        }));

        // Add the edit column
        editColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.PENCIL, (AccessPattern accessPattern) -> {
            editAccessPattern(accessPattern);
            return accessPattern;
        }));

        // overwrite the column in which the number of useCases is displayed
        useCaseCountColumn.setCellFactory(col -> new TableCell<AccessPattern, List<AccessCondition>>() {

            @Override
            protected void updateItem(List<AccessCondition> items, boolean empty) {

                // display nothing if the row is empty, otherwise the item count
                setText((empty || items == null) ? "" : "" + items.size());

            }

        });

        // custom comparator for the useCaseCountColumn
        useCaseCountColumn.setComparator((list1, list2) -> list1.size() <= list2.size() ? 0 : 1);
    }

    public void editAccessPattern(AccessPattern accessPattern) {
        System.out.println(accessPattern);
    }
}
