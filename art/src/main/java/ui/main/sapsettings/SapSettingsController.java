package ui.main.sapsettings;

import com.jfoenix.controls.JFXButton;
import data.entities.SapConfiguration;
import data.localdb.ArtDbContext;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

import sap.ISapConnector;
import sap.SapConnector;

import ui.App;
import ui.AppComponents;
import ui.custom.controls.ButtonCell;
import ui.custom.controls.CustomAlert;
import ui.custom.controls.CustomWindow;
import ui.custom.controls.filter.FilterController;


public class SapSettingsController {

    @FXML
    private TableView<SapConfiguration> sapConnectionTable;

    @FXML
    private TableColumn<SapConfiguration, JFXButton> editConfigColumn;

    @FXML
    private TableColumn<SapConfiguration, JFXButton> deleteConfigColumn;

    @FXML
    private TableColumn<SapConfiguration, ZonedDateTime> creationColumn;

    @FXML
    private Label itemCount;


    public FilterController filterController;

    private ArtDbContext database = AppComponents.getDbContext();
    private SimpleIntegerProperty numberOfItems = new SimpleIntegerProperty();
    ResourceBundle bundle = ResourceBundle.getBundle("lang");


    /**
     * Initialized the view and sets a dummy SapConfig.
     */
    @FXML
    public void initialize() throws Exception {
        initializeTableColumn();

        filterController.shouldFilterProperty.addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                try {
                    updateSapSettingsTable();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // set selection mode to MULTIPLE
        sapConnectionTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // catch row double click
        sapConnectionTable.setRowFactory(tv -> {
            TableRow<SapConfiguration> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    SapConfiguration sapConfiguration = row.getItem();
                    editConfig(sapConfiguration);
                }
            });
            return row;
        });

        // show an item count (+ selected)
        itemCount.textProperty().bind(Bindings.concat(Bindings.size(sapConnectionTable.getSelectionModel().getSelectedItems()).asString("%s / "),
            numberOfItems.asString("%s " + bundle.getString("selected"))));

        updateSapSettingsTable();
    }

    /**
     * updates Sap Setting Table.
     */
    void updateSapSettingsTable() throws Exception {
        List<SapConfiguration> sapConfigurationList = database.getFilteredSapConfigs(filterController.showArchivedProperty.getValue(),
            filterController.searchStringProperty.getValue(), filterController.startDateProperty.getValue(), filterController.endDateProperty.getValue(), 0);
        ObservableList<SapConfiguration> list = FXCollections.observableList(sapConfigurationList);
        sapConnectionTable.setItems(list);

        // update itemCount
        numberOfItems.setValue(list.size());

        sapConnectionTable.refresh();
    }


    /**
     * Initializes the edit and delete table columns.
     */
    private void initializeTableColumn() {
        // Add the delete column
        deleteConfigColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.DELETE, (SapConfiguration sapConfiguration) -> {
            try {
                database.deleteSapConfig(sapConfiguration);
                updateSapSettingsTable();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return sapConfiguration;
        }));

        // Add the edit column
        editConfigColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.PENCIL, (SapConfiguration sapConfiguration) -> {
            editConfig(sapConfiguration);
            return sapConfiguration;
        }));

        // format created at date column
        creationColumn.setCellFactory(col -> new TableCell<SapConfiguration, ZonedDateTime>() {
            @Override
            protected void updateItem(ZonedDateTime item, boolean empty) {
                setText((empty || item == null) ? "" : item.format(DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm")));
            }
        });
    }

    /**
     * Opens a new window in which an SapConfiguration can be edited.
     *
     * @param sapConfiguration the SapConfiguration to edit.
     */
    private void editConfig(SapConfiguration sapConfiguration) {

        try {
            // create a new FXML loader with the SapSettingsFormController
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ui/main/sapsettings/SapSettingsFormView.fxml"), bundle);
            CustomWindow customWindow = loader.load();

            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow);
            scene.getStylesheets().add("css/dark-theme.css");
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(App.primaryStage);
            customWindow.initStage(stage);
            stage.show();

            customWindow.setTitle(bundle.getString("editSapSettingsTitle"));

            SapSettingsFormController sapEdit = loader.getController();
            sapEdit.giveSelectedSapConfig(sapConfiguration);
            sapEdit.setParentController(this);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * Tests the connection to the SAP system.
     */
    public void connectAction() {
        if (sapConnectionTable.getSelectionModel().getSelectedItem() != null) {

            SapConfiguration sapConfiguration = sapConnectionTable.getSelectionModel().getSelectedItem();

            try {

                // get exception from server
                ISapConnector sapConnector = new SapConnector(sapConfiguration, "abs", "abs");
                sapConnector.canPingServer();

            } catch (Exception e) {

                // if exception contains error code 103, connection was successful
                if (e.getCause().toString().contains("103")) {
                    CustomAlert customAlert = new CustomAlert(Alert.AlertType.INFORMATION, bundle.getString("sapConnectTitle"), bundle.getString("sapConnectSuccessMessage"), "OK", "Cancel");
                    customAlert.showAndWait();
                } else {
                    CustomAlert customAlert = new CustomAlert(Alert.AlertType.WARNING, bundle.getString("sapConnectTitle"), bundle.getString("sapConnectFailedMessage"), "Ok", "Cancel");
                    customAlert.showAndWait();
                }
            }
        }
    }

    /**
     * Opens a new window in which a new SapConnection can be added.
     */
    public void newSapConnectionAction() {
        try {
            // create a new FXML loader with the NewSapSettingDialogView
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ui/main/sapsettings/SapSettingsFormView.fxml"), bundle);
            CustomWindow customWindow = loader.load();

            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow);
            scene.getStylesheets().add("css/dark-theme.css");
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(App.primaryStage);
            customWindow.initStage(stage);
            stage.show();

            customWindow.setTitle(bundle.getString("newSapSettingsTitle"));

            SapSettingsFormController sapEdit = loader.getController();
            sapEdit.giveSelectedSapConfig(null);
            sapEdit.setParentController(this);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * Clones the currently selected SapConfiguration table entries.
     */
    public void cloneAction() throws Exception {
        if (sapConnectionTable.getSelectionModel().getSelectedItems() != null && sapConnectionTable.getSelectionModel().getSelectedItems().size() != 0) {
            for (SapConfiguration config : sapConnectionTable.getSelectionModel().getSelectedItems()) {
                SapConfiguration clonedConfig = new SapConfiguration(config);
                clonedConfig.setDescription("Clone - " + config.getDescription());
                database.createSapConfig(config);
            }
            updateSapSettingsTable();
        }

    }


    /**
     * Opens the edit dialog with the currently selected SapConfiguration.
     */
    public void editAction() {
        if (sapConnectionTable.getSelectionModel().getSelectedItem() != null) {
            editConfig(sapConnectionTable.getSelectionModel().getSelectedItem());
        }
    }

    /**
     * Deletes the currently selected SapConfigurations.
     */
    public void deleteAction() throws Exception {
        if (sapConnectionTable.getSelectionModel().getSelectedItems() != null && sapConnectionTable.getSelectionModel().getSelectedItems().size() != 0) {
            // TODO show delete alert
            for (SapConfiguration config : sapConnectionTable.getSelectionModel().getSelectedItems()) {
                database.deleteSapConfig(config);
            }
            updateSapSettingsTable();

        }
    }
}
