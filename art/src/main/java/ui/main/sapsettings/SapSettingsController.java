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
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.stage.WindowEvent;
import sap.ISapConnector;
import sap.SapConnector;
import ui.App;
import ui.custom.controls.ButtonCell;
import ui.custom.controls.CustomAlert;
import ui.custom.controls.CustomWindow;


public class SapSettingsController {

    @FXML
    private TableView<SapConfiguration> sapConnectionTable;
    @FXML
    private TableColumn<SapConfiguration, JFXButton> editConfigColumn;
    @FXML
    private TableColumn<SapConfiguration, JFXButton> deleteConfigColumn;

    public static SapConfiguration sapConfiguration;

    /**
     * Initialized the view and sets a dummy SapConfig.
     */
    @FXML
    public void initialize() {
        initializeTableColumn();
        SapConfiguration sapConfig = new SapConfiguration();
        sapConfig.setSysNr("00");
        sapConfig.setServerDestination("ec2-54-209-137-85.compute-1.amazonaws.com");
        sapConfig.setClient("001");
        sapConfig.setLanguage("EN");
        sapConfig.setPoolCapacity("0");
        sapConfig.setCreatedBy("Hans");
        sapConfig.setDescription("this is a tryout");
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
            sapConnectionTable.refresh();
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
            customWindow.isVisible();
            // give the dialog the sapConfiguration
            SapSettingsEditDialogController sapEdit = loader.getController();
            sapEdit.giveSelectedSapConfig(sapConfiguration);

            stage.setOnCloseRequest((WindowEvent event) -> {
                System.out.println("Was geht jetzt?");
            } );

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

            sapConfiguration.setLanguage("EN");
            sapConfiguration.setPoolCapacity("0");

            try {
                //test Code TODO: real implementation
                String username = "GROUP_11";
                String password = "Wir sind das beste Team!";
                ISapConnector sapConnector = new SapConnector(sapConfiguration, username, password);
                Boolean pingServer = sapConnector.canPingServer();
                CustomAlert customAlert = new CustomAlert(Alert.AlertType.INFORMATION, "Ping was start", "Connection Status: " + pingServer);
                customAlert.showAndWait();
            } catch (Exception e) {
                CustomAlert customAlert = new CustomAlert(Alert.AlertType.WARNING, "SAP Connection Error", "Connection Status: Failed");
                customAlert.showAndWait();
                e.printStackTrace();
            }

            //SapConfiguration sapConfiguration = sapConnectionTable.getSelectionModel().getSelectedItem();
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
            if(!stage.isShowing()){
                //saveInTable();
                sapConnectionTable.refresh();
            }

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

    /**
     * should give new Sap setting to table. TODO: further implementations
     *
     */
    public void giveSavedSapSettings(SapConfiguration sapConfiguration) {

        sapConnectionTable.getItems().add(sapConfiguration);
        sapConnectionTable.refresh();
    }
    //TODO: is not needed(i think)
    //private void addToTable() {
    //}

}
