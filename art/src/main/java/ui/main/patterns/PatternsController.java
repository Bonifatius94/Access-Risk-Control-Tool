package ui.main.patterns;

import com.jfoenix.controls.JFXButton;
import data.entities.AccessCondition;
import data.entities.AccessPattern;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

import io.msoffice.excel.AccessPatternImportHelper;

import java.util.List;

import javafx.application.Platform;
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
            ObservableList<AccessPattern> list = FXCollections.observableList(patterns);

            patternsTable.setItems(list);

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
        deleteColumn.getStyleClass().add("undecorated-column");

        // Add the edit column
        editColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.PENCIL, (AccessPattern accessPattern) -> {
            editAccessPattern(accessPattern);
            return accessPattern;
        }));
        editColumn.getStyleClass().add("undecorated-column");

        // overwrite the column in which the number of useCases is displayed
        useCaseCountColumn.setCellFactory(col -> new TableCell<AccessPattern, List<AccessCondition>>() {
            @Override
            protected void updateItem(List<AccessCondition> items, boolean empty) {
                if (empty || items == null) {
                    setText("" + 0);
                } else {
                    // set image for non-empty cell
                    setText("" + items.size());
                }
            }

        });
    }

    public void editAccessPattern(AccessPattern accessPattern) {
        System.out.println(accessPattern);
    }
}
