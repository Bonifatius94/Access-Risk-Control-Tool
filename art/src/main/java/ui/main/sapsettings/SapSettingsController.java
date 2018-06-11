package ui.main.sapsettings;

import com.jfoenix.controls.JFXButton;
import data.entities.SapConfiguration;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

import ui.App;
import ui.custom.controls.ButtonCell;
import ui.custom.controls.CustomWindow;


public class SapSettingsController {

    @FXML
    private TableView<SapConfiguration> sapConnectionTable;
    @FXML
    private TableColumn<SapConfiguration, JFXButton> editConfigColumn;
    @FXML
    private TableColumn<SapConfiguration, JFXButton> deleteConfigColumn;

    /**
     * Initialized the view and sets a dummy SapConfig.
     */
    @FXML
    public void initialize() {
        initializeTableColumn();
        SapConfiguration sapConfig = new SapConfiguration();
        sapConfig.setSysNr("00");
        sapConfig.setServerDestination("Pertersserver");
        sapConfig.setClient("100");
        sapConfig.setLanguage("EN");
        sapConfig.setPoolCapacity("3");
        sapConfig.setCreatedBy("Hans");
        List<SapConfiguration> sapConfigList = new ArrayList<>();
        sapConfigList.add(sapConfig);
        sapConfigList.add(sapConfig);
        ObservableList<SapConfiguration> list = FXCollections.observableList(sapConfigList);

        sapConnectionTable.setItems(list);
        sapConnectionTable.refresh();

    }

    /**
     * Initializes the edit and delete table columns.
     */
    private void initializeTableColumn() {
        // Add the delete column
        deleteConfigColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.DELETE, (SapConfiguration sapConfiguration) -> {
            sapConnectionTable.getItems().remove(sapConfiguration);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SapSettingsEditDialogView.fxml"), bundle);
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

            // give the dialog the sapConfiguration
            SapSettingsEditDialogController sapEdit = loader.getController();
            sapEdit.giveSelectedSapConfig(sapConfiguration);

        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getClass().toString());
            e.printStackTrace();

        }


    }

    /**
     * Tests the connection to the SAP system.
     */
    public void connectAction() {
        if (sapConnectionTable.getFocusModel().getFocusedItem().equals(sapConnectionTable.getSelectionModel().getSelectedItem())) {

            SapConfiguration sapConfiguration = sapConnectionTable.getSelectionModel().getSelectedItem();
            //TODO: Saplogin Dialog
        }
    }

    /**
     * Opens a new window in which a new SapConnection can be added.
     */
    public void newSapConnectionAction() {
        try {
            // create a new FXML loader with the NewSapSettingDialogView
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("NewSapSettingDialogView.fxml"), bundle);
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

        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getClass().toString());
            e.printStackTrace();

        }
    }

    /**
     * Clones the currently selected SapConfiguration table entry.
     */
    public void cloneAction() {
        if (sapConnectionTable.getFocusModel().getFocusedItem().equals(sapConnectionTable.getSelectionModel().getSelectedItem())) {
            SapConfiguration sapConfiguration = sapConnectionTable.getSelectionModel().getSelectedItem();
            sapConnectionTable.getItems().add(sapConfiguration);
            sapConnectionTable.refresh();
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
            sapConnectionTable.getItems().remove(sapConfiguration);
            sapConnectionTable.refresh();
        }
    }

}
