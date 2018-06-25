package ui.main.configs;

import com.jfoenix.controls.JFXButton;

import data.entities.AccessPattern;
import data.entities.Configuration;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;

import javafx.stage.Modality;
import javafx.stage.Stage;
import ui.App;
import ui.AppComponents;
import ui.custom.controls.ButtonCell;
import ui.custom.controls.CustomWindow;
import ui.custom.controls.filter.FilterController;
import ui.main.configs.modal.ConfigsFormController;


public class ConfigsController {

    @FXML
    private TableView<Configuration> configsTable;

    @FXML
    private Label itemCount;

    @FXML
    public TableColumn<Configuration, JFXButton> deleteColumn;

    @FXML
    public TableColumn<Configuration, JFXButton> editColumn;

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
        bundle = ResourceBundle.getBundle("lang");

        // show an item count (+ selected)
        itemCount.textProperty().bind(Bindings.concat(Bindings.size(configsTable.getSelectionModel().getSelectedItems()).asString("%s / "),
            Bindings.size(configsTable.getItems()).asString("%s " + bundle.getString("selected"))));

        // initialize the configs table
        initializeConfigsTable();

        // check if the filters are applied
        filterController.shouldFilterProperty.addListener((o, oldValue, newValue) -> {
            if (newValue) {
                try {
                    updateConfigsTable();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // fill table with all entries from the database
        updateConfigsTable();

    }

    /**
     * Updates the configsTable items from the database, taking filters into account.
     */
    public void updateConfigsTable() throws Exception {

        List<Configuration> configs = AppComponents.getDbContext().getFilteredConfigs(filterController.showArchivedProperty.getValue(),
            filterController.searchStringProperty.getValue(), filterController.startDateProperty.getValue(),
            filterController.endDateProperty.getValue(), 0);
        ObservableList<Configuration> list = FXCollections.observableList(configs);

        // update itemCount
        numberOfItems.setValue(list.size());

        configsTable.setItems(list);
        configsTable.refresh();
    }

    /**
     * Initializes the configs table.
     */
    private void initializeConfigsTable() {

        // replace Placeholder of PatternsTable with other message
        configsTable.setPlaceholder(new Label(bundle.getString("noEntries")));

        // initialize table columns
        initializeTableColumns();

        // catch row double click
        configsTable.setRowFactory(tv -> {
            TableRow<Configuration> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Configuration configuration = row.getItem();
                    openConfigurationForm(configuration);
                }
            });
            return row;
        });

        // set selection mode to MULTIPLE
        configsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // show an item count (+ selected)
        itemCount.textProperty().bind(Bindings.concat(Bindings.size(configsTable.getSelectionModel().getSelectedItems()).asString("%s / "),
            numberOfItems.asString("%s " + bundle.getString("selected"))));

        initializeTableColumns();
    }


    /**
     * Initializes the table columns that need extra content.
     */
    private void initializeTableColumns() {

        // Add the delete column
        deleteColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.DELETE, (Configuration configuration) -> {
            try {
                AppComponents.getDbContext().deleteConfig(configuration);
                updateConfigsTable();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return configuration;
        }));

        // Add the edit column
        editColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.PENCIL, (Configuration configuration) -> {
            openConfigurationForm(configuration);
            return configuration;
        }));

    }


    /**
     * Clones the selected entry and adds it to the table.
     */
    public void cloneAction() throws Exception {
        if (configsTable.getSelectionModel().getSelectedItems() != null && configsTable.getSelectionModel().getSelectedItems().size() != 0) {

            for (Configuration configToClone : configsTable.getSelectionModel().getSelectedItems()) {

                // clone the currently selected item and add it to the table
                Configuration clonedConfiguration = new Configuration(configsTable.getSelectionModel().getSelectedItem());

                // reference whitelist and patterns in the new object
                clonedConfiguration.setPatterns(configToClone.getPatterns());
                clonedConfiguration.setWhitelist(configToClone.getWhitelist());

                // save the new item to the config
                AppComponents.getDbContext().createConfig(clonedConfiguration);
            }

            updateConfigsTable();
        }
    }

    /**
     * Opens the edit dialog with the selected item.
     */
    public void editAction() {
        if (configsTable.getSelectionModel().getSelectedItems() != null && configsTable.getSelectionModel().getSelectedItems().size() != 0) {
            openConfigurationForm(configsTable.getSelectionModel().getSelectedItem());
        }
    }

    /**
     * Deletes the item from the table.
     */
    public void deleteAction() throws Exception {
        if (configsTable.getSelectionModel().getSelectedItems() != null && configsTable.getSelectionModel().getSelectedItems().size() != 0) {

            // remove all selected items
            for (Configuration config : configsTable.getSelectionModel().getSelectedItems()) {
                AppComponents.getDbContext().deleteConfig(config);
            }

            updateConfigsTable();
        }
    }

    /**
     * Opens the modal dialog to create a new item.
     */
    public void addAction() {
        openConfigurationForm(null);
    }

    /**
     * Edits the given configuration.
     *
     * @param configuration the given config to edit, or null if a new one is created
     */
    private void openConfigurationForm(Configuration configuration) {
        try {
            // create a new FXML loader with the SapSettingsEditDialogController
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("modal/ConfigsFormView.fxml"), bundle);
            CustomWindow customWindow = loader.load();

            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow, 800, 800);
            scene.getStylesheets().add("css/dark-theme.css");
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(App.primaryStage);
            customWindow.initStage(stage);

            // set stage name
            if (configuration == null) {
                customWindow.setTitle(bundle.getString("newConfigTitle"));
            } else {
                customWindow.setTitle(bundle.getString("editConfigTitle"));
            }

            stage.show();

            // give the dialog the sapConfiguration
            ConfigsFormController configForm = loader.getController();
            configForm.giveSelectedConfiguration(configuration);
            configForm.setParentController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
