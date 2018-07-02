package ui.main.patterns;

import com.jfoenix.controls.JFXButton;

import data.entities.AccessCondition;
import data.entities.AccessPattern;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

import extensions.ResourceBundleHelper;
import io.msoffice.excel.AccessPatternImportHelper;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import javafx.stage.FileChooser;
import ui.App;
import ui.AppComponents;
import ui.IUpdateTable;
import ui.custom.controls.ConditionTypeCellFactory;
import ui.custom.controls.CustomAlert;
import ui.custom.controls.DisableDeleteButtonCell;
import ui.custom.controls.DisableEditButtonCell;
import ui.custom.controls.filter.FilterController;
import ui.main.patterns.modal.PatternImportController;
import ui.main.patterns.modal.PatternsFormController;


public class PatternsController implements IUpdateTable {

    @FXML
    public TableView<AccessPattern> patternsTable;

    @FXML
    public TableColumn<AccessPattern, Set<AccessCondition>> conditionCountColumn;

    @FXML
    public TableColumn<AccessPattern, JFXButton> deleteColumn;

    @FXML
    public TableColumn<AccessPattern, JFXButton> editColumn;

    @FXML
    public TableColumn<AccessPattern, Set<AccessCondition>> conditionTypeColumn;

    @FXML
    public Label itemCount;

    @FXML
    public FilterController filterController;

    private ResourceBundle bundle;
    private SimpleIntegerProperty numberOfItems = new SimpleIntegerProperty();

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() throws Exception {

        // load the ResourceBundle
        bundle = ResourceBundleHelper.getInstance().getLanguageBundle();

        // initialize the table
        initializePatternsTable();

        // check if the filters are applied
        filterController.shouldFilterProperty.addListener((o, oldValue, newValue) -> {
            if (newValue) {
                try {
                    updateTable();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Updates the patternsTable items from the database, taking filters into account.
     */
    public void updateTable() throws Exception {

        List<AccessPattern> patterns = AppComponents.getInstance().getDbContext().getFilteredPatterns(filterController.showArchivedProperty.getValue(),
            filterController.searchStringProperty.getValue(), filterController.startDateProperty.getValue(),
            filterController.endDateProperty.getValue(), 0);
        ObservableList<AccessPattern> list = FXCollections.observableList(patterns);

        // update itemCount
        numberOfItems.setValue(list.size());

        patternsTable.setItems(list);
        patternsTable.refresh();

    }

    /**
     * Initializes the patterns table.
     */
    private void initializePatternsTable() {

        // replace Placeholder of PatternsTable with other message
        patternsTable.setPlaceholder(new Label(bundle.getString("noEntries")));

        // catch row double click
        patternsTable.setRowFactory(tv -> {
            TableRow<AccessPattern> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    AccessPattern pattern = row.getItem();
                    if (!pattern.isArchived()) {
                        openAccessPatternForm(pattern);
                    }
                }
            });
            return row;
        });

        // set selection mode to MULTIPLE
        patternsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // show an item count (+ selected)
        itemCount.textProperty().bind(Bindings.concat(Bindings.size(patternsTable.getSelectionModel().getSelectedItems()).asString("%s / "),
            numberOfItems.asString("%s " + bundle.getString("selected"))));

        initializeTableColumns();
    }

    /**
     * Initializes the table columns that need extra content.
     */
    private void initializeTableColumns() {

        // Add the delete column
        deleteColumn.setCellFactory(DisableDeleteButtonCell.forTableColumn((AccessPattern accessPattern) -> {

            CustomAlert customAlert = new CustomAlert(Alert.AlertType.CONFIRMATION, bundle.getString("deleteConfirmTitle"),
                bundle.getString("deleteConfirmMessage"), "Ok", "Cancel");

            if (customAlert.showAndWait().get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                try {
                    AppComponents.getInstance().getDbContext().deletePattern(accessPattern);
                    updateTable();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return accessPattern;
        }));

        // Add the edit column
        editColumn.setCellFactory(DisableEditButtonCell.forTableColumn((AccessPattern accessPattern) -> {
            if (accessPattern.isArchived()) {
                viewAccessPatternDetails(accessPattern);
            } else {
                openAccessPatternForm(accessPattern);
            }
            return accessPattern;
        }));

        initializeConditionCountColumn();

        initializeConditionTypeColumn();

    }


    /**
     * Initialize the ConditionCountColumn so it displays the number of conditions of the AccessPattern.
     */
    private void initializeConditionCountColumn() {
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
     * Initialize ConditionTypeColumn to show the type of the condition as an icon.
     */
    private void initializeConditionTypeColumn() {

        // sets the icon of the condition to pattern or profile
        conditionTypeColumn.setCellFactory(new ConditionTypeCellFactory());
    }

    /**
     * Opens a modal edit dialog for the selected AccessPattern.
     *
     * @param accessPattern the selected AccessPattern to edit, or null if a new one is created
     */
    public void openAccessPatternForm(AccessPattern accessPattern) {
        try {

            FXMLLoader loader = AppComponents.getInstance().showScene("ui/main/patterns/modal/PatternsFormView.fxml",
                accessPattern == null ? "newPatternTitle" : "editPatternTitle", 1200, 750);

            // give the dialog the sapConfiguration
            PatternsFormController patternEdit = loader.getController();
            patternEdit.giveSelectedAccessPattern(accessPattern);
            patternEdit.setParentController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a modal details dialog for the selected AccessPattern.
     *
     * @param accessPattern the selected AccessPattern
     */
    public void viewAccessPatternDetails(AccessPattern accessPattern) {

        try {
            FXMLLoader loader = AppComponents.getInstance().showScene("ui/main/patterns/modal/PatternsFormView.fxml", "patternDetails", 1200, 750);

            // give the dialog the sapConfiguration
            PatternsFormController patternView = loader.getController();
            patternView.giveSelectedAccessPattern(accessPattern);

            // don't allow editing
            patternView.setEditable(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Clones the selected entry and adds it to the table.
     */
    public void cloneAction() throws Exception {
        if (patternsTable.getSelectionModel().getSelectedItems() != null && patternsTable.getSelectionModel().getSelectedItems().size() != 0) {

            for (AccessPattern patternToClone : patternsTable.getSelectionModel().getSelectedItems()) {

                AccessPattern clonedPattern = new AccessPattern(patternToClone);
                AppComponents.getInstance().getDbContext().createPattern(clonedPattern);
            }

            updateTable();
        }
    }

    /**
     * Deletes the item from the table.
     */
    public void deleteAction() throws Exception {
        if (patternsTable.getSelectionModel().getSelectedItems() != null && patternsTable.getSelectionModel().getSelectedItems().size() != 0) {
            CustomAlert customAlert;
            if (patternsTable.getSelectionModel().getSelectedItems().size() == 1) {
                customAlert = new CustomAlert(Alert.AlertType.CONFIRMATION, bundle.getString("deleteConfirmTitle"),
                    bundle.getString("deleteConfirmMessage"), "Ok", "Cancel");
            } else {
                customAlert = new CustomAlert(Alert.AlertType.CONFIRMATION, bundle.getString("deleteMultipleConfirmTitle"),
                    bundle.getString("deleteMultipleConfirmMessage"), "Ok", "Cancel");
            }

            if (customAlert.showAndWait().get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                for (AccessPattern pattern : patternsTable.getSelectionModel().getSelectedItems()) {
                    AppComponents.getInstance().getDbContext().deletePattern(pattern);
                }
                updateTable();
            }
        }
    }

    /**
     * Opens the import dialog for AccessPatterns.
     */
    public void importAction() throws Exception {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(bundle.getString("choosePatternFile"));
        //chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pattern Files (*.xlsx)", "*.xlsx"));
        File selectedFile = chooser.showOpenDialog(App.primaryStage);

        if (selectedFile != null) {

            FXMLLoader loader = AppComponents.getInstance().showScene("ui/main/patterns/modal/PatternImportView.fxml", "importPatterns");

            // import patterns with the AccessPatternImportHelper
            AccessPatternImportHelper importHelper = new AccessPatternImportHelper();
            List<AccessPattern> importedPatterns = importHelper.importAccessPatterns(selectedFile.getAbsolutePath());

            // give the dialog the controller and the patterns
            PatternImportController importController = loader.getController();
            importController.giveImportedPatterns(importedPatterns);
            importController.setPatternsController(this);
        }
    }

    /**
     * Opens the modal dialog to create a new item.
     */
    public void addAction() {
        openAccessPatternForm(null);
    }
}
