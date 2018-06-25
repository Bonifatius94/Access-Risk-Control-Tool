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
import javafx.scene.control.ButtonBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
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
import ui.main.whitelists.modal.WhitelistFormController;

public class WhitelistsController {

    @FXML
    public TableView<Whitelist> whitelistTable;

    @FXML
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
    private ResourceBundle bundle = ResourceBundle.getBundle("lang");

    /**
     * this function is automatically called by FXML loader , its starts initialize.
     */
    @FXML
    public void initialize() throws Exception {

        initializeColumns();

        // catch row double click
        whitelistTable.setRowFactory(tv -> {
            TableRow<Whitelist> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Whitelist whitelist = row.getItem();
                    editDialogWhitelist(whitelist);
                }
            });
            return row;
        });

        // set selection mode to MULTIPLE
        whitelistTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // listen for filter changes
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

    /**
     * Updates Whitelist Table.
     *
     * @throws Exception if a Database error occurred.
     */
    public void updateWhitelistTable() throws Exception {
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
                updateWhitelistTable();
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

        try {
            // create a new FXML loader with the SapSettingsEditDialogController
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ui/main/whitelists/modal/WhitelistFormView.fxml"), bundle);
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

            WhitelistFormController whitelistFormController = loader.getController();
            whitelistFormController.setParentController(this);
            whitelistFormController.giveSelectedWhitelist(null);
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
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ui/main/whitelists/modal/WhitelistFormView.fxml"), bundle);
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

            // give parameters to the dialog
            WhitelistFormController editDialogController = loader.getController();
            editDialogController.giveSelectedWhitelist(whitelist);
            editDialogController.setParentController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function deletes a selected Whitelist is called By delete Button.
     */
    public void deleteWhitelist() throws Exception {
        if (whitelistTable.getSelectionModel().getSelectedItems() != null && whitelistTable.getSelectionModel().getSelectedItems().size() != 0) {
            CustomAlert customAlert = new CustomAlert(Alert.AlertType.CONFIRMATION, bundle.getString("deleteConfirmTitle"),
                bundle.getString("deleteConfirmMessage") + " (" + whitelistTable.getSelectionModel().getSelectedItems().size() + ")", "Ok", "Cancel");

            if (customAlert.showAndWait().get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                // deletes whitelists from DB
                for (Whitelist whitelist : whitelistTable.getSelectionModel().getSelectedItems()) {
                    database.deleteWhitelist(whitelist);
                }
                updateWhitelistTable();
            }
        }
    }

    /**
     * This function is called from a button press and starts edit.
     */
    public void editWhitelist() {
        if (whitelistTable.getSelectionModel().getSelectedItem() != null) {
            editDialogWhitelist(whitelistTable.getSelectionModel().getSelectedItem());
        }
    }

    /**
     * clones a selected Whitelist.
     */
    public void cloneWhitelist() throws Exception {
        if (whitelistTable.getSelectionModel().getSelectedItems() != null && whitelistTable.getSelectionModel().getSelectedItems().size() != 0) {
            for (Whitelist whitelist : whitelistTable.getSelectionModel().getSelectedItems()) {
                Whitelist whitelistToAdd = new Whitelist(whitelist);
                whitelistToAdd.setDescription("Clone - " + whitelist.getDescription());
                database.createWhitelist(whitelistToAdd);
            }
            updateWhitelistTable();
        }
    }

    /**
     * starts Whitelist import.
     */
    public void importWhitelist() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(App.primaryStage);
        if (file != null) {
            String path = file.getPath();
            try {
                WhitelistImportHelper whitelistImportHelper = new WhitelistImportHelper();
                Whitelist importedWhitelist = whitelistImportHelper.importWhitelist(path);
                startImportDialog(importedWhitelist);
                updateWhitelistTable();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Starts a dialog do edit imported whitelist.
     *
     * @param whitelist is the imported Whitlsit
     */
    private void startImportDialog(Whitelist whitelist) {
        editDialogWhitelist(whitelist);
    }

}

