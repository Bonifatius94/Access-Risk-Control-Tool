package ui.main.sapsettings;

import com.jfoenix.controls.JFXButton;
import data.entities.SapConfiguration;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import ui.custom.controls.ButtonCell;
import ui.custom.controls.CustomWindow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class SapSettingsController {

    @FXML
    private TableView<SapConfiguration> sapConnectionTable;
    @FXML
    private TableColumn<SapConfiguration, JFXButton> editConfigColumn;
    @FXML
    private TableColumn<SapConfiguration, JFXButton> deleteConfigColumn;

    @FXML
    public void initialize() {
        initializeTableColumn();
        SapConfiguration sapConfig = new SapConfiguration();
        sapConfig.setSysNr("Peter");
        sapConfig.setServerDestination("Pertersserver");
        sapConfig.setClient("bla");
        sapConfig.setLanguage("English");
        sapConfig.setPoolCapacity("3");
        sapConfig.setCreatedBy("Hans");
        sapConfig.setId(3);
        List<SapConfiguration> sapConfigList = new ArrayList<>();
        sapConfigList.add(sapConfig);
        sapConfigList.add(sapConfig);
        ObservableList<SapConfiguration> list = FXCollections.observableList(sapConfigList);

        sapConnectionTable.setItems(list);
        sapConnectionTable.refresh();

    }

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

    private void editConfig(SapConfiguration sapConfiguration) {

        try {
            // create a new FXML loader with the SapSettingsEditDialogController
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SapSettingsEditDialogView.fxml"), bundle);
            CustomWindow customWindow = loader.load();

            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow, 600, 400);
            scene.getStylesheets().add("css/dark-theme.css");
            Stage stage = new Stage();
            stage.setScene(scene);
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

    @SuppressWarnings("all")
    public void connectAction(ActionEvent actionEvent) {
        SapConfiguration sapConfiguration = sapConnectionTable.getSelectionModel().getSelectedItem();
        //TODO:Saplogin Dialog
    }

    @SuppressWarnings("all")
    public void newSapConnectionAction(ActionEvent actionEvent) {
        //TODO: new SAP Dialog
    }

    @SuppressWarnings("all")
    public void cloneAction(ActionEvent actionEvent) {
        if(sapConnectionTable.getSelectionModel().getSelectedItem()!= null) {
            SapConfiguration sapConfiguration = sapConnectionTable.getSelectionModel().getSelectedItem();
            sapConnectionTable.getItems().add(sapConfiguration);
            sapConnectionTable.refresh();
        }

    }

    @SuppressWarnings("all")
    public void editAction(ActionEvent actionEvent) {
        editConfig(sapConnectionTable.getSelectionModel().getSelectedItem());
    }

    @SuppressWarnings("all")
    public void deleteAction(ActionEvent actionEvent) {
        SapConfiguration sapConfiguration = sapConnectionTable.getSelectionModel().getSelectedItem();
        sapConnectionTable.getItems().remove(sapConfiguration);
        sapConnectionTable.refresh();
    }
}
