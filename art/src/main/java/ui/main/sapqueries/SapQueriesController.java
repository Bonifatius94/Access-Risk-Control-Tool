package ui.main.sapqueries;

import com.jfoenix.controls.JFXButton;

import data.entities.Configuration;
import data.entities.CriticalAccessQuery;
import data.entities.SapConfiguration;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import ui.custom.controls.ButtonCell;

public class SapQueriesController {

    @FXML
    public TableView<CriticalAccessQuery> queriesTable;

    @FXML
    public TableColumn<CriticalAccessQuery, Configuration> configurationColumn;

    @FXML
    public TableColumn<CriticalAccessQuery, SapConfiguration> sapConfigurationColumn;

    @FXML
    public TableColumn<CriticalAccessQuery, JFXButton> deleteColumn;

    @FXML
    public TableColumn<CriticalAccessQuery, JFXButton> editColumn;

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
        initializeQueriesTable();

        // show an item count (+ selected)
        itemCount.textProperty().bind(Bindings.concat(Bindings.size(queriesTable.getSelectionModel().getSelectedItems()).asString("%s / "),
            Bindings.size(queriesTable.getItems()).asString("%s " + bundle.getString("selected"))));
    }

    /**
     * Initializes the sapQueries table.
     */
    private void initializeQueriesTable() {

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

        queriesTable.setPlaceholder(addButton);

        // catch row double click
        queriesTable.setRowFactory(tv -> {
            TableRow<CriticalAccessQuery> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    CriticalAccessQuery query = row.getItem();
                    editQuery(query);
                }
            });
            return row;
        });

        // set selection mode to MULTIPLE
        queriesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        initializeTableColumns();
    }

    /**
     * Initializes the table columns that need extra content.
     */
    private void initializeTableColumns() {
        // Add the delete column
        deleteColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.DELETE, (CriticalAccessQuery query) -> {
            queriesTable.getItems().remove(query);
            return query;
        }));

        // Add the edit column
        editColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.PENCIL, (CriticalAccessQuery query) -> {
            editQuery(query);
            return query;
        }));

        // overwrite the column in which the configuration is displayed to display only its name
        configurationColumn.setCellFactory(col -> new TableCell<CriticalAccessQuery, Configuration>() {

            @Override
            protected void updateItem(Configuration configuration, boolean empty) {

                // display nothing if the row is empty, otherwise the item count
                setText((empty || configuration == null) ? "" : "" + configuration.getName());
            }
        });

        // overwrite the column in which the sapConfiguration is displayed to display only its name
        sapConfigurationColumn.setCellFactory(col -> new TableCell<CriticalAccessQuery, SapConfiguration>() {

            @Override
            protected void updateItem(SapConfiguration configuration, boolean empty) {

                // display nothing if the row is empty, otherwise the item count
                setText((empty || configuration == null) ? "" : "" + configuration.getDescription());
            }
        });
    }

    private void editQuery(CriticalAccessQuery query) {

    }

    /**
     * Opens the edit dialog with the selected item.
     */
    public void editAction() {
        if (queriesTable.getSelectionModel().getSelectedItems() != null) {
            editQuery(queriesTable.getSelectionModel().getSelectedItem());
        }
    }

    /**
     * Deletes the item from the table.
     */
    public void deleteAction() {
        if (queriesTable.getSelectionModel().getSelectedItems() != null) {

            // remove all selected items
            queriesTable.getItems().removeAll(queriesTable.getSelectionModel().getSelectedItems());
            queriesTable.refresh();
        }
    }

    /**
     * Opens the modal dialog to create a new item.
     */
    public void addAction() {
        editQuery(null);
    }
}
