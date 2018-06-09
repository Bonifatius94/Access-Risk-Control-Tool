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
import ui.custom.controls.PTableColumn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class SapSettingsController {

    @FXML
    public JFXButton cloneButton;
    @FXML
    public JFXButton editButton;
    @FXML
    public JFXButton deleteButton;
    @FXML
    public JFXButton connectButton;
    @FXML
    public JFXButton newSapConnectionButton;

    public PTableColumn<SapConfiguration, Integer> id;
    public PTableColumn<SapConfiguration, String> hostnameColumn;
    public PTableColumn<SapConfiguration, String> sysNr;
    public PTableColumn<SapConfiguration, String> jcoCLient;
    public PTableColumn<SapConfiguration, String> language;
    public PTableColumn<SapConfiguration, String> poolCapacity;
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
            // create a new FXML loader with the SapSettingsEditDialog
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SapSettingsEditDialog.fxml"), bundle);
            CustomWindow customWindow = loader.load();

            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow, 600,400);
            scene.getStylesheets().add("css/dark-theme.css");
            Stage stage = new Stage();
            stage.setScene(scene);
            customWindow.initStage(stage);

            stage.show();

            // give the dialog the sapConfiguration
            SapSettingsEditDialog sapEdit = loader.getController();
            sapEdit.fillDialog(sapConfiguration);

        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getClass().toString());
            e.printStackTrace();

        }


    }
    @SuppressWarnings("all")
    public void connectAction(ActionEvent actionEvent) {
    }
    @SuppressWarnings("all")
    public void newSapConnectionAction(ActionEvent actionEvent) {
    }
    @SuppressWarnings("all")
    public void cloneAction(ActionEvent actionEvent) {
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
