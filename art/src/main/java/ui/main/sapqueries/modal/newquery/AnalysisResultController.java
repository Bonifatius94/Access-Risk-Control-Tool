package ui.main.sapqueries.modal.newquery;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import data.entities.AccessPattern;
import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui.App;
import ui.custom.controls.ButtonCell;
import ui.custom.controls.CustomWindow;
import ui.main.patterns.PatternsFormController;


public class AnalysisResultController {

    @FXML
    private Label resultLabel;

    @FXML
    private TableView<CriticalAccessEntry> resultTable;

    @FXML
    private JFXComboBox exportFormatChooser;

    @FXML
    public TableColumn<CriticalAccessEntry, JFXButton> viewPatternDetailsColumn;

    private  CriticalAccessQuery resultQuery;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        // Add the detail column
        viewPatternDetailsColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.OPEN_IN_NEW, (CriticalAccessEntry entry) -> {
            viewAccessPatternDetails(entry.getAccessPattern());
            return entry;
        }));
    }

    public void exportResults() {

    }

    /**
     * Gives the controller the result query.
     * @param query the result query
     */
    public void giveResultQuery(CriticalAccessQuery query) {
        resultQuery = query;

        this.resultLabel.setText(query.getEntries().size() + " CriticalAccessEntries were found.");
        this.resultTable.setItems(FXCollections.observableList(new ArrayList<>(query.getEntries())));
    }

    /**
     * Opens a modal edit dialog for the selected AccessPattern.
     *
     * @param accessPattern the selected AccessPattern
     */
    public void viewAccessPatternDetails(AccessPattern accessPattern) {
        try {
            // create a new FXML loader
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../../patterns/PatternsFormView.fxml"), bundle);
            CustomWindow customWindow = loader.load();

            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow, 1050, 750);
            scene.getStylesheets().add("css/dark-theme.css");
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(App.primaryStage);
            customWindow.initStage(stage);

            stage.show();

            // give the dialog the sapConfiguration
            PatternsFormController patternView = loader.getController();
            patternView.giveSelectedAccessPattern(accessPattern);
            patternView.setEditable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
