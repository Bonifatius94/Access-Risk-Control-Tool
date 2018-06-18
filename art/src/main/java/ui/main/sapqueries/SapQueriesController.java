package ui.main.sapqueries;

import com.jfoenix.controls.JFXButton;

import data.entities.AccessPattern;
import data.entities.Configuration;
import data.entities.CriticalAccessQuery;
import data.entities.SapConfiguration;

import data.entities.Whitelist;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

import io.msoffice.excel.AccessPatternImportHelper;
import io.msoffice.excel.WhitelistImportHelper;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

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
import ui.main.sapqueries.modal.details.SapQueryDetailController;

public class SapQueriesController {

    @FXML
    public TableView<CriticalAccessQuery> queriesTable;

    @FXML
    public TableColumn<CriticalAccessQuery, Configuration> configurationColumn;

    @FXML
    public TableColumn<CriticalAccessQuery, SapConfiguration> sapConfigurationColumn;

    @FXML
    public TableColumn<CriticalAccessQuery, ZonedDateTime> createdAtColumn;

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

        // fills the table
        fillQueriesTable();

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
                    openQuery(query);
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
        editColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.OPEN_IN_NEW, bundle.getString("details"), (CriticalAccessQuery query) -> {
            openQuery(query);
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

        // overwrite the column in which the date is displayed for formatting
        createdAtColumn.setCellFactory(col -> new TableCell<CriticalAccessQuery, ZonedDateTime>() {

            @Override
            protected void updateItem(ZonedDateTime time, boolean empty) {

                // display nothing if the row is empty, otherwise the item count
                setText((empty || time == null) ? "" : "" + time.format(DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm")));
            }
        });
    }

    /**
     * Opens the selected query.
     * @param query the selected query
     */
    private void openQuery(CriticalAccessQuery query) {
        try {
            // create a new FXML loader with the SapSettingsEditDialogController
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("modal/details/SapQueryDetailView.fxml"), bundle);
            CustomWindow customWindow = loader.load();

            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow, 600, 500);
            scene.getStylesheets().add("css/dark-theme.css");
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(App.primaryStage);
            customWindow.initStage(stage);

            customWindow.setTitle(bundle.getString("queryDetails"));

            stage.show();

            // give the dialog the query
            SapQueryDetailController queryDetail = loader.getController();
            queryDetail.giveSelectedQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the edit dialog with the selected item.
     */
    public void editAction() {
        if (queriesTable.getSelectionModel().getSelectedItems() != null) {
            openQuery(queriesTable.getSelectionModel().getSelectedItem());
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
     * Fills the queries table with items.
     */
    private void fillQueriesTable() {
        try {
            // parsing test whitelist from excel file
            Whitelist whitelist = new WhitelistImportHelper().importWhitelist("Example - Whitelist.xlsx");
            whitelist.setName("Whitelist-Name");
            whitelist.setDescription("Whitelist-Description");

            // parsing test patterns from excel file
            List<AccessPattern> patterns = new AccessPatternImportHelper().importAuthorizationPattern("Example - Zugriffsmuster.xlsx");

            Configuration configuration = new Configuration();
            configuration.setWhitelist(whitelist);
            configuration.setPatterns(patterns);
            configuration.setName("Ein Name");
            configuration.setDescription("Eine Beschreibung");

            CriticalAccessQuery query = new CriticalAccessQuery();
            query.setConfig(configuration);

            // init sap settings (here: test server data)
            SapConfiguration sapConfig = new SapConfiguration("ec2-54-209-137-85.compute-1.amazonaws.com", "some description", "00", "001", "EN", "0");
            sapConfig.setDescription("Eine SAP Beschreibung");
            query.setSapConfig(sapConfig);

            // created at flag
            query.setCreatedAt(ZonedDateTime.now());

            // created by flag
            query.setCreatedBy("HEINZ_KARL");

            ObservableList<CriticalAccessQuery> items = FXCollections.observableArrayList();
            items.addAll(query);

            queriesTable.setItems(items);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the modal dialog to create a new item.
     */
    public void addAction() {
        try {
            // create a new FXML loader with the SapSettingsEditDialogController
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("modal/newquery/NewSapQueryView.fxml"), bundle);
            CustomWindow customWindow = loader.load();

            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow);
            scene.getStylesheets().add("css/dark-theme.css");
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(App.primaryStage);
            customWindow.initStage(stage);

            customWindow.setTitle(bundle.getString("newAnalysis"));

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
