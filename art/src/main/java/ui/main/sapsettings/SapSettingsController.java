package ui.main.sapsettings;

import com.jfoenix.controls.JFXButton;
import data.entities.SapConfiguration;
import data.localdb.ArtDbContext;
import data.localdb.IArtDbContext;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
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


    public FilterController filterController;

    private ArtDbContext database = AppComponents.getDbContext();


    /**
     * Initialized the view and sets a dummy SapConfig.
     */
    @FXML
    public void initialize() {
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

        try {
            updateSapSettingsTable();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * updates Sap Setting Table.
     */
    void updateSapSettingsTable() throws Exception {
        List<SapConfiguration> sapConfigurationList = database.getFilteredSapConfigs(filterController.showArchivedProperty.getValue(),
            filterController.searchStringProperty.getValue(), filterController.startDateProperty.getValue(), filterController.endDateProperty.getValue(), 0);
        ObservableList<SapConfiguration> list = FXCollections.observableList(sapConfigurationList);
        sapConnectionTable.setItems(list);
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

    }

    /**
     * Opens a new window in which an SapConfiguration can be edited.
     *
     * @param sapConfiguration the SapConfiguration to edit.
     */
    private void editConfig(SapConfiguration sapConfiguration) {

        try {
            // create a new FXML loader with the SapSettingsEditDialogController
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ui/main/sapsettings/SapSettingsEditDialogView.fxml"), bundle);
            CustomWindow customWindow = loader.load();

            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow, 500, 550);
            scene.getStylesheets().add("css/dark-theme.css");
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(App.primaryStage);
            customWindow.initStage(stage);

            // give the dialog the sapConfiguration
            stage.show();
            SapSettingsEditDialogController sapEdit = loader.getController();
            sapEdit.giveSelectedSapConfig(sapConfiguration);
            sapEdit.setParentController(this);
            updateSapSettingsTable();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * TODO: real implementation
     * Tests the connection to the SAP system.
     */
    public void connectAction() {
        //test Code
        if (sapConnectionTable.getFocusModel().getFocusedItem().equals(sapConnectionTable.getSelectionModel().getSelectedItem())) {

            SapConfiguration sapConfiguration = sapConnectionTable.getSelectionModel().getSelectedItem();
            sapConfiguration.setLanguage("EN");
            sapConfiguration.setPoolCapacity("0");

            try {

                ISapConnector sapConnector = new SapConnector(sapConfiguration, "abs", "abs");
                sapConnector.canPingServer();

            } catch (Exception e) {

                if (e.getCause().toString().contains("103")) {
                    CustomAlert customAlert = new CustomAlert(Alert.AlertType.INFORMATION, "SAP Connection Status", "Connection Status: Success", "OK", "Cancel");
                    customAlert.showAndWait();
                } else {
                    CustomAlert customAlert = new CustomAlert(Alert.AlertType.WARNING, "Server Test Connection", "Connection Status: Error " + e.getCause().toString(), "Ok", "Cancel");
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
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ui/main/sapsettings/NewSapSettingDialogView.fxml"), bundle);
            CustomWindow customWindow = loader.load();

            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow, 500, 550);
            scene.getStylesheets().add("css/dark-theme.css");
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(App.primaryStage);
            customWindow.initStage(stage);
            stage.show();
            NewSapSettingDialogController controller = loader.getController();
            controller.setParentController(this);
            updateSapSettingsTable();


        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * Clones the currently selected SapConfiguration table entry.
     */
    public void cloneAction() {
        if (sapConnectionTable.getFocusModel().getFocusedItem().equals(sapConnectionTable.getSelectionModel().getSelectedItem())) {
            try {
                SapConfiguration sapConfiguration = new SapConfiguration(sapConnectionTable.getSelectionModel().getSelectedItem());
                sapConfiguration.setDescription("Clone - " + sapConnectionTable.getSelectionModel().getSelectedItem().getDescription());
                database.createSapConfig(sapConfiguration);
                updateSapSettingsTable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Opens the edit dialog with the currently selected SapConfiguration.
     */
    public void editAction() {
        sapConnectionTable.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                editConfig(sapConnectionTable.getSelectionModel().getSelectedItem());
            }
        });
        if (sapConnectionTable.getFocusModel().getFocusedItem().equals(sapConnectionTable.getSelectionModel().getSelectedItem())) {
            editConfig(sapConnectionTable.getSelectionModel().getSelectedItem());
        }
    }

    /**
     * Deletes the currently selected SapConfiguration.
     */
    public void deleteAction() {
        if (sapConnectionTable.getFocusModel().getFocusedItem().equals(sapConnectionTable.getSelectionModel().getSelectedItem())) {
            SapConfiguration sapConfiguration = sapConnectionTable.getSelectionModel().getSelectedItem();
            try {
                database.deleteSapConfig(sapConfiguration);
                updateSapSettingsTable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
