package ui.main.sapqueries.modal.newquery;

import com.jfoenix.controls.JFXComboBox;
import data.entities.CriticalAccessEntry;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class AnalysisResultController {

    @FXML
    private TableView<CriticalAccessEntry> resultTable;

    @FXML
    private JFXComboBox exportFormatChooser;

    @FXML
    public void initialize() {
    }

    public void exportResults() {

    }
}
