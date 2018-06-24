package ui.main.whitelists;

import com.jfoenix.controls.JFXButton;
import data.entities.Whitelist;
import data.entities.WhitelistEntry;

import data.localdb.ArtDbContext;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import io.msoffice.excel.WhitelistImportHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import ui.App;
import ui.AppComponents;
import ui.custom.controls.ButtonCell;
import ui.custom.controls.CustomAlert;
import ui.custom.controls.CustomWindow;
import ui.custom.controls.PTableColumn;
import ui.custom.controls.filter.FilterController;
import ui.main.patterns.PatternsController;

public class WhitelistsController {

    @FXML
    public TableView<Whitelist> whitelistTable;
    public VBox whitelistViewFxId;

    @FXML
    private PTableColumn<Whitelist, JFXButton> editWhitelistColumn;
    @FXML
    private PTableColumn<Whitelist, JFXButton> deleteWhitelistColumn;

    @FXML
    private PTableColumn<Whitelist, Set<WhitelistEntry>> entryCountColumn;

    @FXML
    public FilterController filterController;

    ArtDbContext database = AppComponents.getDbContext();
    //private Boolean includeArchivedData = true;

    /**
     * this function is automatically called by FXML loader , its starts initialize.
     * TODO: change all database functions from test functions to end version functions
     */
    @FXML
    public void initialize() throws Exception {

        initializeColumns();


        try {
            //Test code

            WhitelistImportHelper whitelistImportHelper = new WhitelistImportHelper();
            Whitelist whitelist1 = whitelistImportHelper.importWhitelist("Example - Whitelist.xlsx");
            whitelist1.setDescription("test");
            whitelist1.setArchived(true);
            whitelist1.setName("bla");
            whitelist1.setArchived(true);
            database.createWhitelist(whitelist1);
            //database.createWhitelist(whitelist1);

            List<Whitelist> whitelistList;
            whitelistList = database.getWhitelists(true);
            ObservableList<Whitelist> observableList = FXCollections.observableArrayList(whitelistList);
            whitelistTable.getItems().addAll(observableList);
            whitelistTable.refresh();

        } catch (Exception e) {
            e.printStackTrace();
        }
        tableRefresh();

        filterController.shouldFilterProperty.addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                try {
                    updateWhitelistTable();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        updateWhitelistTable();

    }

    private void updateWhitelistTable() throws Exception {
        List<Whitelist> whitelists = database.getFilteredWhitelists(filterController.showArchivedProperty.getValue(), filterController.searchStringProperty.getValue(),
            filterController.startDateProperty.getValue(), filterController.startDateProperty.getValue(), 0);
        ObservableList<Whitelist> list = FXCollections.observableList(whitelists);
        whitelistTable.setItems(list);
        whitelistTable.refresh();
    }

    /**
     * Initializes Columns for delete and edit button.
     */
    private void initializeColumns() {

        deleteWhitelistColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.DELETE, (Whitelist whitelist) -> {
            try {
                database.deleteWhitelist(whitelist);
                tableRefresh();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return whitelist;
        }));
        deleteWhitelistColumn.setSortable(false);

        // Add the edit column
        editWhitelistColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.PENCIL, (Whitelist whitelist) -> {
            editDialogWhitelist(whitelist);
            return whitelist;
        }));
        editWhitelistColumn.setSortable(false);

        // overwrite the column in which the number of whitelistEntrys is displayed
        entryCountColumn.setCellFactory(col -> new TableCell<Whitelist, Set<WhitelistEntry>>() {
                @Override
                protected void updateItem(Set<WhitelistEntry> items, boolean empty) {
                    setText((empty || items == null) ? "" : "" + items.size());
                }
            }
        );
        entryCountColumn.setComparator((list1, list2) -> (list1.size() <= list2.size()) ? 0 : 1);
    }

    /**
     * starts new Whitelist Dialog.
     */
    @FXML
    public void newWhitelist() {
        //TODO: save new whitelist in Database bzw Description dialog hinzufügen

        try {
            // create a new FXML loader with the SapSettingsEditDialogController
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("NewWhitelistDialogView.fxml"), bundle);
            CustomWindow customWindow = loader.load();
            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow, 900, 650);

            scene.getStylesheets().add("css/dark-theme.css");
            Stage stage = new Stage();

            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(App.primaryStage);
            customWindow.initStage(stage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This functions starts Whitelist editView and Controller.
     *
     * @param whitelist is a Whitelist needed for Whitelist Edit dialog.
     */
    private void editDialogWhitelist(Whitelist whitelist) {

        try {
            // create a new FXML loader with the SapSettingsEditDialogController
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("WhitelistEditDialogView.fxml"), bundle);
            // give the dialog the
            WhitelistEditDialogController.giveSelectedWhitelist(whitelist);
            CustomWindow customWindow = loader.load();
            System.out.println(whitelist == null);

            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow, 900, 650);
            scene.getStylesheets().add("css/dark-theme.css");
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(App.primaryStage);
            customWindow.initStage(stage);
            stage.show();

            WhitelistEditDialogController editDialogController = loader.getController();
            editDialogController.setParentController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function deletes a selected Whitelist is called By delete Button.
     */
    @FXML
    void deleteWhitelist() {
        if (whitelistTable.getSelectionModel().getSelectedItem().equals(whitelistTable.getFocusModel().getFocusedItem()) && !whitelistTable.getSelectionModel().getSelectedItem().isArchived()) {
            CustomAlert customAlert = new CustomAlert(Alert.AlertType.CONFIRMATION, "Do you really want to delete Whitelist with Id"
                + whitelistTable.getSelectionModel().getSelectedItem().getId().toString(), "By clicking OK the Whitelist will be deleted, click cancel to stop the deletion", "OK", "Cancel");

            Whitelist whitelist = whitelistTable.getSelectionModel().getSelectedItem();
            if (customAlert.showAndWait().isPresent() && customAlert.showAndWait().get() == ButtonType.OK) {
                whitelistTable.getItems().remove(whitelist);
                //deletes whitelist from DB
                try {
                    database.deleteWhitelist(whitelist);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tableRefresh();
            } else if (customAlert.showAndWait().isPresent() && customAlert.showAndWait().get() == ButtonType.CANCEL) {
                customAlert.close();
            }
        }
    }

    /**
     * This function is called from a button press and starts edit.
     * TODO: save changes in database
     */
    @FXML
    void editWhitelist() {
        if (whitelistTable.getSelectionModel().getSelectedItem().equals(whitelistTable.getFocusModel().getFocusedItem())) {
            editDialogWhitelist(whitelistTable.getSelectionModel().getSelectedItem());
        }
        whitelistTable.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                editDialogWhitelist(whitelistTable.getSelectionModel().getSelectedItem());
            }
        });
    }

    /**
     * clones a selected Whitelist. TODO: needs some additional checking( on ID and stuff) eventually??.
     */
    @FXML
    void cloneWhitelist() {
        if (whitelistTable.getSelectionModel().getSelectedItem().equals(whitelistTable.getFocusModel().getFocusedItem())) {
            Whitelist whitelist = whitelistTable.getSelectionModel().getSelectedItem();
            try {
                database.createWhitelist(whitelist);
            } catch (Exception e) {
                e.printStackTrace();
            }
            tableRefresh();
        }
    }

    /**
     * starts Whitelist import.
     * TODO: Dialog
     */

    public void importWhitelist() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(App.primaryStage);
        if (file != null) {
            String path = file.getPath();
            Whitelist whitelist;
            try {
                WhitelistImportHelper whitelistImportHelper = new WhitelistImportHelper();
                Whitelist importedWhitelist = whitelistImportHelper.importWhitelist(path);
                startImportDialog(importedWhitelist);
                tableRefresh();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            CustomAlert customAlert = new CustomAlert(Alert.AlertType.WARNING, "No file selected", "you need to select an xlsx file to import", "Ok", "");
            customAlert.showAndWait();
        }
    }

    /**
     * Starts a dialog do edit imported whitelist.TODO: adding to table
     *
     * @param whitelist is the imported Whitlsit
     */
    private void startImportDialog(Whitelist whitelist) {
        editDialogWhitelist(whitelist);
    }

    //TODO: reaktivate if database is ready
    private void tableRefresh() {

        whitelistTable.getItems().clear();
        List<Whitelist> whitelists = null;
        try {
            whitelists = database.getWhitelists(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ObservableList<Whitelist> observableList = FXCollections.observableArrayList(whitelists);
        whitelistTable.getItems().addAll(observableList);
        whitelistTable.refresh();
    }
}

