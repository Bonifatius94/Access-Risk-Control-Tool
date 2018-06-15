package ui.main.patterns;

import com.jfoenix.controls.JFXButton;
import data.entities.AccessCondition;
import data.entities.AccessPattern;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import io.msoffice.excel.AccessPatternImportHelper;

import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import javafx.scene.control.Tooltip;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui.App;
import ui.custom.controls.ButtonCell;
import ui.custom.controls.CustomWindow;
import ui.main.patterns.modal.PatternsFormController;


public class PatternsController {

    @FXML
    public TableView<AccessPattern> patternsTable;

    @FXML
    public TableColumn<AccessPattern, Set<AccessCondition>> conditionCountColumn;

    @FXML
    public TableColumn<AccessPattern, JFXButton> deleteColumn;

    @FXML
    public TableColumn<AccessPattern, JFXButton> editColumn;

    @FXML
    public Label itemCount;

    private ResourceBundle bundle;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {

        // load the ResourceBundle
        bundle = ResourceBundle.getBundle("lang");

        // initialize the table
        initializePatternsTable();

        // fill in table data
        fillPatternsTable();
    }

    /**
     * Initializes the patterns table.
     */
    private void initializePatternsTable() {

        // replace Placeholder of PatternsTable with addButton
        JFXButton addButton = new JFXButton();
        addButton.setOnAction(event -> {
            addAction();
        });
        MaterialDesignIconView view = new MaterialDesignIconView(MaterialDesignIcon.PLUS);
        addButton.setGraphic(view);
        addButton.setTooltip(new Tooltip(bundle.getString("firstPattern")));
        addButton.getStyleClass().add("round-button");
        addButton.setMinHeight(30);
        addButton.setPrefHeight(30);

        patternsTable.setPlaceholder(addButton);

        // catch row double click
        patternsTable.setRowFactory(tv -> {
            TableRow<AccessPattern> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    AccessPattern pattern = row.getItem();
                    openAccessPatternForm(pattern);
                }
            });
            return row;
        });

        // set selection mode to MULTIPLE
        patternsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // show an item count (+ selected)
        itemCount.textProperty().bind(Bindings.concat(Bindings.size(patternsTable.getSelectionModel().getSelectedItems()).asString("%s / "),
            Bindings.size(patternsTable.getItems()).asString("%s " + bundle.getString("selected"))));

        initializeTableColumns();
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
            openAccessPatternForm(accessPattern);
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
     * Opens a modal edit dialog for the selected AccessPattern.
     *
     * @param accessPattern the selected AccessPattern to edit, or null if a new one is created
     */
    public void openAccessPatternForm(AccessPattern accessPattern) {
        try {
            // create a new FXML loader with the SapSettingsEditDialogController
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("modal/PatternsFormView.fxml"), bundle);
            CustomWindow customWindow = loader.load();

            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow, 1050, 750);
            scene.getStylesheets().add("css/dark-theme.css");
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(App.primaryStage);
            customWindow.initStage(stage);

            // set stage name
            if (accessPattern == null) {
                customWindow.setTitle(bundle.getString("newPatternTitle"));
            } else {
                customWindow.setTitle(bundle.getString("editPatternTitle"));
            }

            stage.show();

            // give the dialog the sapConfiguration
            PatternsFormController patternEdit = loader.getController();
            patternEdit.giveSelectedAccessPattern(accessPattern);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Clones the selected entry and adds it to the table.
     */
    public void cloneAction() {
        if (patternsTable.getSelectionModel().getSelectedItems().size() != 0 && patternsTable.getFocusModel().getFocusedItem().equals(patternsTable.getSelectionModel().getSelectedItem())) {

            // clone the currently selected item and add it to the table
            AccessPattern clonedPattern = patternsTable.getSelectionModel().getSelectedItem();
            patternsTable.getItems().add(0, clonedPattern);

            // select the clone
            patternsTable.getSelectionModel().clearSelection();
            patternsTable.getSelectionModel().select(clonedPattern);
            patternsTable.scrollTo(clonedPattern);
            patternsTable.refresh();
        }
    }

    /**
     * Opens the edit dialog with the selected item.
     */
    public void editAction() {
        if (patternsTable.getSelectionModel().getSelectedItems().size() != 0 && patternsTable.getFocusModel().getFocusedItem().equals(patternsTable.getSelectionModel().getSelectedItem())) {
            openAccessPatternForm(patternsTable.getSelectionModel().getSelectedItem());
        }
    }

    /**
     * Deletes the item from the table.
     */
    public void deleteAction() {
        if (patternsTable.getSelectionModel().getSelectedItems() != null && patternsTable.getFocusModel().getFocusedItem().equals(patternsTable.getSelectionModel().getSelectedItem())) {

            // remove all selected items
            patternsTable.getItems().removeAll(patternsTable.getSelectionModel().getSelectedItems());
            patternsTable.refresh();
        }
    }

    /**
     * Opens the modal dialog to create a new item.
     */
    public void addAction() {
        openAccessPatternForm(null);
    }

    /**
     * Provides the data for the patternTable.
     */
    private void fillPatternsTable() {

        // test the table with data from the Example - Zugriffsmuster.xlsx file
        try {
            AccessPatternImportHelper helper = new AccessPatternImportHelper();

            List<AccessPattern> patterns = helper.importAuthorizationPattern("Example - Zugriffsmuster.xlsx");
            ObservableList<AccessPattern> list = FXCollections.observableList(patterns);

            patternsTable.setItems(list);
            patternsTable.refresh();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
