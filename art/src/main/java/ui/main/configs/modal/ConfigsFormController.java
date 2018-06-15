package ui.main.configs.modal;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import data.entities.AccessCondition;
import data.entities.AccessPattern;
import data.entities.Configuration;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

import io.msoffice.excel.AccessPatternImportHelper;

import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import ui.App;
import ui.custom.controls.AutoCompleteComboBoxListener;
import ui.custom.controls.ButtonCell;
import ui.custom.controls.CustomWindow;


public class ConfigsFormController {

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
    public TableColumn<AccessPattern, Set<AccessCondition>> conditionCountColumn;

    @FXML
    private JFXButton resetFormButton;


    private Configuration configuration;

    /**
     * Initializes the view.
     */
    @FXML
    public void initialize() {
        new AutoCompleteComboBoxListener<>(whitelistChooser);
        new AutoCompleteComboBoxListener<>(patternChooser);

        initializePatternsTable();

        fillPatternsTable();

        initializeValidation();

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
        conditionCountColumn.setCellFactory(col -> new TableCell<AccessPattern, Set<AccessCondition>>() {

            @Override
            protected void updateItem(Set<AccessCondition> items, boolean empty) {

                // display nothing if the row is empty, otherwise the item count
                setText((empty || items == null) ? "" : "" + items.size());

            }

        });

        // custom comparator for the conditionCountColumn
        conditionCountColumn.setComparator((list1, list2) -> list1.size() <= list2.size() ? 0 : 1);
    }

    /**
     * Initializes the validation for certain text inputs in order to display an error message (e.g. required).
     */
    private void initializeValidation() {

        nameInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                nameInput.validate();
            }
        });

        descriptionInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                descriptionInput.validate();
            }
        });

    }

    /**
     * Validates all relevant inputs before saving.
     * @return if the inputs are all valid
     */
    private boolean validateBeforeSave() {
        return nameInput.validate() && descriptionInput.validate() && patternsTable.getItems() != null && whitelistChooser.getSelectionModel().getSelectedItem() != null;
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

            // TODO: prefill patterns, prefill whitelist input
        } else {
            this.configuration = new Configuration();
            resetFormButton.setVisible(false);
        }
    }

    public void importWhitelist() {

    }

    public void chooseWhitelist() {

    }


    /**
     * Opens a modal window where patterns can be selected.
     */
    public void choosePattern() {
        try {
            // create a new FXML loader with the SapSettingsEditDialogController
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChoosePatternsView.fxml"), bundle);
            CustomWindow customWindow = loader.load();

            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow, 1200, 550);
            scene.getStylesheets().add("css/dark-theme.css");
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(App.primaryStage);
            customWindow.initStage(stage);

            // set stage name
            customWindow.setTitle(bundle.getString("selectPatternsTitle"));

            stage.show();

            // give the dialog the currently selected items
            ChoosePatternsController choosePatterns = loader.getController();
            choosePatterns.giveSelectedPatterns(patternsTable.getItems());
            choosePatterns.setParentController(this);

            // choosePatterns.selectedPatternsTable.itemsProperty().bind(this.patternsTable.itemsProperty());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPatterns(List<AccessPattern> patterns) {
        ObservableList<AccessPattern> items = FXCollections.observableList(patterns);
        this.patternsTable.setItems(items);
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

        if (validateBeforeSave()) {
            this.configuration.setName(this.nameInput.getText());
            this.configuration.setDescription(this.descriptionInput.getText());

            this.configuration.setPatterns(patternsTable.getItems());

            // this.configuration.setWhitelist();

            System.out.println(this.configuration);
        }
    }

    /**
     * Hides the stage.
     *
     * @param event the given ActionEvent
     */
    public void close(ActionEvent event) {
        (((Button) event.getSource()).getScene().getWindow()).hide();
    }

    /**
     * Provides the data for the patternTable.
     */
    private void fillPatternsTable() {

        // test the table with data from the Example - Zugriffsmuster.xlsx file
        try {
            AccessPatternImportHelper helper = new AccessPatternImportHelper();

            List<AccessPattern> patterns = helper.importAuthorizationPattern("Example - Zugriffsmuster.xlsx");
            patterns.get(0).setId(0);
            patterns.get(1).setId(1);
            patterns.get(2).setId(2);
            patterns.remove(3);
            patterns.remove(4);

            ObservableList<AccessPattern> list = FXCollections.observableList(patterns);

            patternsTable.setItems(list);
            patternsTable.refresh();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
