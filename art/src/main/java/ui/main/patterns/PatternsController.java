package ui.main.patterns;

import data.entities.AccessPattern;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import io.msoffice.excel.AccessPatternImportHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ui.custom.controls.ButtonCell;

import java.util.List;

public class PatternsController {

    @FXML
    public TableView patternsTable;

    @FXML
    public TableColumn deleteColumn;

    @FXML
    public void initialize() {
        deleteColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.DELETE, (AccessPattern accessPattern) -> {
            patternsTable.getItems().remove(accessPattern);
            return accessPattern;
        }));

        AccessPatternImportHelper helper = new AccessPatternImportHelper();

        try {
            List<AccessPattern> patterns = helper.importAuthorizationPattern("Example - Zugriffsmuster.xlsx");

            ObservableList<AccessPattern> list = FXCollections.observableList(patterns);

            patternsTable.setItems(list);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
