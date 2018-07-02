package ui.main.whitelists;

import com.jfoenix.controls.JFXButton;
import data.entities.Whitelist;
import data.entities.WhitelistEntry;

import data.localdb.ArtDbContext;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import extensions.ResourceBundleHelper;
import io.msoffice.excel.WhitelistImportHelper;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import ui.App;
import ui.AppComponents;
import ui.IUpdateTable;
import ui.custom.controls.ButtonCell;
import ui.custom.controls.CustomAlert;
import ui.custom.controls.PTableColumn;
import ui.custom.controls.filter.FilterController;
import ui.main.whitelists.modal.WhitelistFormController;

public class WhitelistsController implements IUpdateTable {

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
    private PTableColumn<Whitelist, ZonedDateTime> creationColumn;

    @FXML
    private Label itemCount;

    @FXML
    public FilterController filterController;

    ArtDbContext database = AppComponents.getInstance().getDbContext();
    private SimpleIntegerProperty numberOfItems = new SimpleIntegerProperty();
    private ResourceBundle bundle = ResourceBundleHelper.getInstance().getLanguageBundle();

    /**
     * this function is automatically called by FXML loader , its starts initialize.
     */
    @FXML
    public void initialize() throws Exception {

        initializeColumns();

        // replace Placeholder of PatternsTable with other message
        whitelistTable.setPlaceholder(new Label(bundle.getString("noEntries")));

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

        // show an item count (+ selected)
        itemCount.textProperty().bind(Bindings.concat(Bindings.size(whitelistTable.getSelectionModel().getSelectedItems()).asString("%s / "),
            numberOfItems.asString("%s " + bundle.getString("selected"))));


        // set selection mode to MULTIPLE
        whitelistTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // listen for filter changes
        filterController.shouldFilterProperty.addListener((obs, oldValue, newValue) -> {
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
     * Updates Whitelist Table.
     *
     * @throws Exception if a Database error occurred.
     */
    public void updateTable() throws Exception {
        List<Whitelist> whitelists = database.getFilteredWhitelists(filterController.showArchivedProperty.getValue(), filterController.searchStringProperty.getValue(),
            filterController.startDateProperty.getValue(), filterController.startDateProperty.getValue(), 0);
        ObservableList<Whitelist> list = FXCollections.observableList(whitelists);
        whitelistTable.setItems(list);

        // update itemCount
        numberOfItems.setValue(list.size());

        whitelistTable.refresh();
    }

    /**
     * Initializes Columns for delete and edit button.
     */
    private void initializeColumns() {

        deleteWhitelistColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.DELETE, bundle.getString("delete"), (Whitelist whitelist) -> {
            if (!whitelist.isArchived()) {

                CustomAlert customAlert = new CustomAlert(Alert.AlertType.CONFIRMATION, bundle.getString("deleteConfirmTitle"),
                    bundle.getString("deleteConfirmMessage"), "Ok", "Cancel");

                if (customAlert.showAndWait().get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {

                    try {
                        database.deleteWhitelist(whitelist);
                        updateTable();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                CustomAlert alert = new CustomAlert(Alert.AlertType.WARNING, bundle.getString("alreadyArchived"), "", "Ok", "Ok");
                alert.showAndWait();
            }
            return whitelist;
        }));
        deleteWhitelistColumn.setSortable(false);

        // Add the edit column
        editWhitelistColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.PENCIL, bundle.getString("edit"), (Whitelist whitelist) -> {
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

        // format create timestamp
        creationColumn.setCellFactory(col -> new TableCell<Whitelist, ZonedDateTime>() {
            @Override
            protected void updateItem(ZonedDateTime item, boolean empty) {
                setText((empty || item == null) ? "" : item.format(DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm")));
            }
        });
    }

    /**
     * starts new Whitelist Dialog.
     */
    @FXML
    public void newWhitelist() {

        try {
            FXMLLoader loader = AppComponents.getInstance().showScene("ui/main/whitelists/modal/WhitelistFormView.fxml", "newWhitelist", 900, 650);

            WhitelistFormController whitelistFormController = loader.getController();
            whitelistFormController.setWhitelistsController(this);
            whitelistFormController.giveSelectedWhitelist(null);
        } catch (Exception e) {
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

            FXMLLoader loader = AppComponents.getInstance().showScene("ui/main/whitelists/modal/WhitelistFormView.fxml", "editWhitelist", 900, 650);

            WhitelistFormController editDialogController = loader.getController();
            editDialogController.giveSelectedWhitelist(whitelist);
            editDialogController.setWhitelistsController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This function deletes a selected Whitelist is called By delete Button.
     */
    public void deleteWhitelist() throws Exception {
        if (whitelistTable.getSelectionModel().getSelectedItems() != null && whitelistTable.getSelectionModel().getSelectedItems().size() != 0) {
            CustomAlert customAlert;
            if (whitelistTable.getSelectionModel().getSelectedItem().isArchived()) {
                customAlert = new CustomAlert(Alert.AlertType.WARNING, bundle.getString("alreadyArchived"), "", "Ok", "Ok");
                customAlert.showAndWait();
            } else {
                if (whitelistTable.getSelectionModel().getSelectedItems().size() == 1) {
                    customAlert = new CustomAlert(Alert.AlertType.CONFIRMATION, bundle.getString("deleteConfirmTitle"),
                        bundle.getString("deleteConfirmMessage"), "Ok", "Cancel");
                } else {
                    customAlert = new CustomAlert(Alert.AlertType.CONFIRMATION, bundle.getString("deleteMultipleConfirmTitle"),
                        bundle.getString("deleteMultipleConfirmMessage"), "Ok", "Cancel");
                }

                if (customAlert.showAndWait().get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                    // deletes whitelists from DB
                    for (Whitelist whitelist : whitelistTable.getSelectionModel().getSelectedItems()) {
                        database.deleteWhitelist(whitelist);
                    }
                    updateTable();
                }
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
                whitelistToAdd.setArchived(false);
                whitelistToAdd.setDescription("Clone - " + whitelist.getDescription());
                database.createWhitelist(whitelistToAdd);
            }
            updateTable();
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
            } catch (Exception e) {
                if (e.toString().contains("NullPointerException")) {
                    CustomAlert alert = new CustomAlert(Alert.AlertType.WARNING, bundle.getString("wrongFileType"), bundle.getString("selectFileCarefully"), "Ok", "Ok");
                    alert.showAndWait();
                } else {
                    e.printStackTrace();
                }
                //if()
            }
        }
    }

    /**
     * Starts a dialog do edit imported whitelist.
     *
     * @param whitelist is the imported Whitelist
     */
    private void startImportDialog(Whitelist whitelist) throws Exception {
        FXMLLoader loader = AppComponents.getInstance().showScene("ui/main/whitelists/modal/WhitelistFormView.fxml", "importWhitelist", 900, 650);

        WhitelistFormController editDialogController = loader.getController();
        editDialogController.giveSelectedWhitelist(whitelist);
        editDialogController.setWhitelistsController(this);
    }

}

