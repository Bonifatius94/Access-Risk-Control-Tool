package ui.main.configs;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import data.entities.AccessCondition;
import data.entities.AccessPattern;
import data.entities.Configuration;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ui.custom.controls.AutoCompleteComboBoxListener;
import ui.custom.controls.ButtonCell;

import java.util.Set;


public class ConfigsFormController {

    private Configuration configuration;


    @FXML
    private JFXTextField nameInput;

    @FXML
    private JFXTextField descriptionInput;

    @FXML
    private JFXComboBox whitelistChooser;

    @FXML
    private JFXComboBox patternChooser;

    @FXML
    private TableView patternsTable;

    @FXML
    public TableColumn<AccessPattern, JFXButton> deletePatternColumn;

    @FXML
    public TableColumn<AccessPattern, Set<AccessCondition>> useCaseCountColumn;

    @FXML
    public void initialize() {
        new AutoCompleteComboBoxListener<>(whitelistChooser);
        new AutoCompleteComboBoxListener<>(patternChooser);
    }

    /**
     * Initializes the patterns table.
     */
    private void initializePatternsTable() {

        // set selection mode to MULTIPLE
        patternsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        initializeTableColumns();
    }

    /**
     * Initializes the table columns that need extra content.
     */
    private void initializeTableColumns() {
        // Add the delete column
        deletePatternColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.DELETE, (AccessPattern accessPattern) -> {
            patternsTable.getItems().remove(accessPattern);
            return accessPattern;
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
     * Fills the detail form with the selected Configuration.
     *
     * @param configuration the selected Configuration
     */
    public void giveSelectedConfiguration(Configuration configuration) {

        if (configuration != null) {
            this.configuration = configuration;

            nameInput.setText(configuration.getName());
            descriptionInput.setText(configuration.getDescription());
        }
    }

    public void importWhitelist() {

    }

    public void chooseWhitelist() {

    }

    public void choosePattern() {

    }

    public void importPatterns() {

    }

    /**
     * Resets the form.
     */
    public void resetForm() {

    }

    /**
     * Saves the changes to the database.
     */
    public void saveChanges() {

    }

    /**
     * Hides the stage.
     * @param event the given ActionEvent
     */
    public void close(ActionEvent event) {
        (((Button) event.getSource()).getScene().getWindow()).hide();
    }
}
