package ui.main.configs;

import com.jfoenix.controls.JFXButton;

import data.entities.Configuration;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

import extensions.ResourceBundleHelper;

import java.util.List;
import java.util.ResourceBundle;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import ui.AppComponents;
import ui.IUpdateTable;
import ui.custom.controls.CustomAlert;
import ui.custom.controls.DisableDeleteButtonCell;
import ui.custom.controls.DisableEditButtonCell;
import ui.custom.controls.filter.FilterController;
import ui.main.configs.modal.ConfigsFormController;
import ui.main.sapqueries.modal.details.ConfigDetailsController;


public class ConfigsController implements IUpdateTable {

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
        bundle = ResourceBundleHelper.getInstance().getLanguageBundle();

        // show an item count (+ selected)
        itemCount.textProperty().bind(Bindings.concat(Bindings.size(configsTable.getSelectionModel().getSelectedItems()).asString("%s / "),
            Bindings.size(configsTable.getItems()).asString("%s " + bundle.getString("selected"))));

        // initialize the configs table
        initializeConfigsTable();

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
     * Updates the configsTable items from the database, taking filters into account.
     */
    public void updateTable() throws Exception {

        List<Configuration> configs = AppComponents.getInstance().getDbContext().getFilteredConfigs(filterController.showArchivedProperty.getValue(),
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
                    if (configuration.isArchived()) {
                        viewConfigDetails(configuration);
                    } else {
                        openConfigurationForm(configuration);
                    }
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
        deleteColumn.setCellFactory(DisableDeleteButtonCell.forTableColumn((Configuration configuration) -> {

            CustomAlert customAlert = new CustomAlert(Alert.AlertType.CONFIRMATION, bundle.getString("deleteConfirmTitle"),
                bundle.getString("deleteConfirmMessage"), "Ok", "Cancel");

            if (customAlert.showAndWait().get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                try {
                    AppComponents.getInstance().getDbContext().deleteConfig(configuration);
                    updateTable();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return configuration;
        }));

        // Add the edit column
        editColumn.setCellFactory(DisableEditButtonCell.forTableColumn((Configuration configuration) -> {
            if (configuration.isArchived()) {
                viewConfigDetails(configuration);
            } else {
                openConfigurationForm(configuration);
            }
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
                AppComponents.getInstance().getDbContext().createConfig(clonedConfiguration);
            }

            updateTable();
        }
    }

    /**
     * Deletes the item from the table.
     */
    public void deleteAction() throws Exception {
        if (configsTable.getSelectionModel().getSelectedItems() != null && configsTable.getSelectionModel().getSelectedItems().size() != 0) {
            CustomAlert customAlert;
            if (configsTable.getSelectionModel().getSelectedItems().size() == 1) {
                customAlert = new CustomAlert(Alert.AlertType.CONFIRMATION, bundle.getString("deleteConfirmTitle"),
                    bundle.getString("deleteConfirmMessage"), "Ok", "Cancel");
            } else {
                customAlert = new CustomAlert(Alert.AlertType.CONFIRMATION, bundle.getString("deleteMultipleConfirmTitle"),
                    bundle.getString("deleteMultipleConfirmMessage"), "Ok", "Cancel");
            }

            if (customAlert.showAndWait().get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                // remove all selected items
                for (Configuration config : configsTable.getSelectionModel().getSelectedItems()) {
                    AppComponents.getInstance().getDbContext().deleteConfig(config);
                }

                if (configsTable.getSelectionModel().getSelectedItems().stream().anyMatch(x -> x.isArchived())) {
                    customAlert = new CustomAlert(Alert.AlertType.INFORMATION, bundle.getString("alreadyArchivedTitle"), bundle.getString("alreadyArchivedMessage"));
                    customAlert.showAndWait();
                }

                updateTable();
            }
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
            ResourceBundle bundle = ResourceBundleHelper.getInstance().getLanguageBundle();
            FXMLLoader loader;

            loader = AppComponents.getInstance()
                .showScene("ui/main/configs/modal/ConfigsFormView.fxml", configuration == null ? "newConfigTitle" : "editConfigTitle", 800, 800);

            // give the dialog the sapConfiguration
            ConfigsFormController configForm = loader.getController();
            configForm.giveSelectedConfiguration(configuration);
            configForm.setParentController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the config details in a modal window.
     * @param configuration the configuration which details should be shown
     */
    private void viewConfigDetails(Configuration configuration) {
        try {

            FXMLLoader loader = AppComponents.getInstance().showScene("ui/main/sapqueries/modal/details/ConfigDetailsView.fxml","configDetails", 1050, 650);

            // give the dialog the sapConfiguration
            ConfigDetailsController configDetails = loader.getController();
            configDetails.giveConfiguration(configuration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
