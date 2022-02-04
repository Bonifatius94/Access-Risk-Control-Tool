package ui.main.sapqueries.modal.details;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import data.entities.AccessCondition;
import data.entities.AccessPattern;
import data.entities.Configuration;
import data.entities.WhitelistEntry;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

import extensions.ResourceBundleHelper;

import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import ui.App;
import ui.AppComponents;
import ui.custom.controls.ButtonCell;
import ui.custom.controls.CustomWindow;
import ui.main.patterns.modal.PatternsFormController;


public class ConfigDetailsController {

    @FXML
    public TableView<AccessPattern> patternsTable;

    @FXML
    public TableColumn<AccessPattern, Set<AccessCondition>> useCaseCountColumn;

    @FXML
    public TableColumn<AccessPattern, JFXButton> viewDetailsColumn;

    @FXML
    public TableView<WhitelistEntry> whitelistEntries;

    @FXML
    public JFXTextField whitelistNameField;

    @FXML
    public JFXTextField whitelistDescriptionField;


    private ResourceBundle bundle = ResourceBundleHelper.getInstance().getLanguageBundle();
    private Configuration config;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        initializeTableColumns();

        // catch row double click
        patternsTable.setRowFactory(tv -> {
            TableRow<AccessPattern> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    AccessPattern pattern = row.getItem();
                    try {
                        viewAccessPatternDetails(pattern);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return row;
        });
    }

    /**
     * Initializes the table columns that need extra content.
     */
    private void initializeTableColumns() {

        // Add the detail column
        viewDetailsColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.OPEN_IN_NEW, (AccessPattern pattern) -> {
            try {
                viewAccessPatternDetails(pattern);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return pattern;
        }));

        // overwrite the column in which the number of useCases is displayed
        useCaseCountColumn.setCellFactory(col -> new TableCell<AccessPattern, Set<AccessCondition>>() {

            @Override
            protected void updateItem(Set<AccessCondition> items, boolean empty) {

                // display nothing if the row is empty, otherwise the item count
                setText((empty || items == null) ? "" : "" + items.size());

            }

        });

        // custom comparator for the useCaseCountColumn
        useCaseCountColumn.setComparator((list1, list2) -> list1.size() <= list2.size() ? 0 : 1);
    }

    /**
     * Prefills the inputs with the given config.
     *
     * @param config the selected Configuration
     */
    public void giveConfiguration(Configuration config) {

        if (config != null) {
            this.config = config;

            // check if whitelist is null
            if (config.getWhitelist() != null) {
                whitelistNameField.setText(config.getWhitelist().getName());
                whitelistDescriptionField.setText(config.getWhitelist().getDescription());
                whitelistEntries.setItems(FXCollections.observableList(new ArrayList<>(config.getWhitelist().getEntries())).sorted());
            } else {
                whitelistNameField.setText(bundle.getString("noWhitelist"));
                whitelistDescriptionField.clear();
            }

            patternsTable.setItems(FXCollections.observableList(new ArrayList<>(config.getPatterns())).sorted());
        }

    }

    /**
     * Opens a modal edit dialog for the selected AccessPattern.
     *
     * @param accessPattern the selected AccessPattern
     */
    public void viewAccessPatternDetails(AccessPattern accessPattern) throws Exception {

        FXMLLoader loader = AppComponents.getInstance().showScene("ui/main/patterns/modal/PatternsFormView.fxml", "patternDetails", 1200, 720);

        // give the dialog the sapConfiguration
        PatternsFormController patternView = loader.getController();
        patternView.giveSelectedAccessPattern(accessPattern);
        patternView.setEditable(false);
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
