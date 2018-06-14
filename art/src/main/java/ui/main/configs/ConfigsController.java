package ui.main.configs;

import com.jfoenix.controls.JFXButton;

import data.entities.Configuration;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;

import ui.custom.controls.ButtonCell;


public class ConfigsController {

    @FXML
    private TableView<Configuration> configsTable;

    @FXML
    private Label itemCount;

    @FXML
    public TableColumn<Configuration, JFXButton> deleteColumn;

    @FXML
    public TableColumn<Configuration, JFXButton> editColumn;


    private ResourceBundle bundle;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {

        // load the ResourceBundle
        bundle = ResourceBundle.getBundle("lang");

        // show an item count (+ selected)
        itemCount.textProperty().bind(Bindings.concat(Bindings.size(configsTable.getSelectionModel().getSelectedItems()).asString("%s / "),
            Bindings.size(configsTable.getItems()).asString("%s " + bundle.getString("selected"))));

        // initialize the configs table
        initializeConfigsTable();

    }

    /**
     * Initializes the configs table.
     */
    private void initializeConfigsTable() {

        // initialize table columns
        initializeTableColumns();

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

        configsTable.setPlaceholder(addButton);

        // catch row double click
        configsTable.setRowFactory(tv -> {
            TableRow<Configuration> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Configuration configuration = row.getItem();
                    editConfig(configuration);
                }
            });
            return row;
        });

        // set selection mode to MULTIPLE
        configsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        initializeTableColumns();
    }


    /**
     * Initializes the table columns that need extra content.
     */
    private void initializeTableColumns() {

        // Add the delete column
        deleteColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.DELETE, (Configuration configuration) -> {
            configsTable.getItems().remove(configuration);
            return configuration;
        }));

        // Add the edit column
        editColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.PENCIL, (Configuration configuration) -> {
            editConfig(configuration);
            return configuration;
        }));

    }


    /**
     * Clones the selected entry and adds it to the table.
     */
    public void cloneAction() {
        if (configsTable.getFocusModel().getFocusedItem().equals(configsTable.getSelectionModel().getSelectedItem())) {

            // clone the currently selected item and add it to the table
            Configuration clonedConfiguration = configsTable.getSelectionModel().getSelectedItem();
            configsTable.getItems().add(0, clonedConfiguration);

            // select the clone
            configsTable.getSelectionModel().clearSelection();
            configsTable.getSelectionModel().select(clonedConfiguration);
            configsTable.scrollTo(clonedConfiguration);
            configsTable.refresh();
        }
    }

    /**
     * Opens the edit dialog with the selected item.
     */
    public void editAction() {
        if (configsTable.getFocusModel().getFocusedItem().equals(configsTable.getSelectionModel().getSelectedItem())) {
            editConfig(configsTable.getSelectionModel().getSelectedItem());
        }
    }

    /**
     * Deletes the item from the table.
     */
    public void deleteAction() {
        if (configsTable.getSelectionModel().getSelectedItems() != null && configsTable.getFocusModel().getFocusedItem().equals(configsTable.getSelectionModel().getSelectedItem())) {

            // remove all selected items
            configsTable.getItems().removeAll(configsTable.getSelectionModel().getSelectedItems());
            configsTable.refresh();
        }
    }

    /**
     * Opens the modal dialog to create a new item.
     */
    public void addAction() {
        editConfig(null);
    }

    /**
     * Edits the given configuration.
     *
     * @param configuration the given config
     */
    private void editConfig(Configuration configuration) {

    }
}
