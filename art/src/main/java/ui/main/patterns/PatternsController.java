package ui.main.patterns;

import data.entities.AccessPattern;

import data.entities.Whitelist;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

import io.msoffice.excel.AccessPatternImportHelper;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import ui.custom.controls.ButtonCell;


public class PatternsController {

    @FXML
    public TableView patternsTable;

    @FXML
    public TableColumn deleteColumn;

    @FXML
    public TableColumn editColumn;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        deleteColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.DELETE, (AccessPattern accessPattern) -> {
            patternsTable.getItems().remove(accessPattern);
            return accessPattern;
        }));
        deleteColumn.getStyleClass().add("undecorated-column");

        editColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.PENCIL, (AccessPattern accessPattern) -> {
            editAccessPattern(accessPattern);
            return accessPattern;
        }));
        editColumn.getStyleClass().add("undecorated-column");

        AccessPatternImportHelper helper = new AccessPatternImportHelper();

        try {
            List<AccessPattern> patterns = helper.importAuthorizationPattern("Example - Zugriffsmuster.xlsx");

            ObservableList<AccessPattern> list = FXCollections.observableList(patterns);

            patternsTable.setItems(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void editAccessPattern(AccessPattern accessPattern) {
        System.out.println(accessPattern);
    }
}
