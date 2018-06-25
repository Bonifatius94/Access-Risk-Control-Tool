package ui.main.whitelists.modal;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import data.entities.Whitelist;
import data.entities.WhitelistEntry;
import data.localdb.ArtDbContext;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import ui.AppComponents;
import ui.custom.controls.ButtonCell;
import ui.custom.controls.CustomAlert;
import ui.custom.controls.PTableColumn;
import ui.main.whitelists.WhitelistsController;

public class WhitelistFormController {


    @FXML
    public TableView<WhitelistEntry> whitelistEditTable;
    //    public JFXButton cancelButton;
    @FXML
    public PTableColumn<WhitelistEntry, JFXButton> deleteWhitelistEntryColumn;
    @FXML
    public JFXTextField tfUsecaseId;
    @FXML
    public JFXTextField tfUserName;
    @FXML
    public JFXTextField tfDescription;
    @FXML
    public JFXTextField tfWhitelistName;
    @FXML
    public JFXButton addButton;
    @FXML
    private PTableColumn<WhitelistEntry, String> usecaseId;
    @FXML
    private PTableColumn<WhitelistEntry, String> userName;

    private WhitelistsController parentController;
    private ArtDbContext whitelistDatabase = AppComponents.getDbContext();
    private Whitelist whitelist;
    private Whitelist whitelistOld;

    /**
     * automatically called by FXML loader, starts initialize Columns.
     */
    public void initialize() {

        initializeTableColumns();
        initializeValidation();


        whitelistEditTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                editWhitelistEntry(newValue);
            }
        });

    }

    /**
     * Initializes delete and edit Column with Buttons.
     */
    private void initializeTableColumns() {
        deleteWhitelistEntryColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.DELETE, (WhitelistEntry whitelistEntry) -> {
            whitelist.getEntries().remove(whitelistEntry);
            whitelistEditTable.getItems().remove(whitelistEntry);
            return whitelistEntry;
        }));

        deleteWhitelistEntryColumn.setSortable(false);

    }

    /**
     * loads use case and username from the selected entry to the textfield.
     *
     * @param whitelistEntry is the by call given whitelist entry.
     */
    private void editWhitelistEntry(WhitelistEntry whitelistEntry) {
        if (whitelistEntry != null) {
            tfUsecaseId.setText(whitelistEntry.getUsecaseId());
            tfUserName.setText(whitelistEntry.getUsername());

            tfUserName.validate();
            tfUsecaseId.validate();
        } else {
            tfUsecaseId.setText("");
            tfUserName.setText("");
        }
    }

    /**
     * Is called if an Whitelist edit is started by the WhitelistController class.
     *
     * @param whitelist the whitelist that is loaded to edit dialog.
     */
    public void giveSelectedWhitelist(Whitelist whitelist) {

        if (whitelist != null) {
            this.whitelist = whitelist;
            whitelistOld = new Whitelist(whitelist);

            tfWhitelistName.setText(whitelist.getName());
            tfDescription.setText(whitelist.getDescription());

            List<WhitelistEntry> whitelistEntryList = new ArrayList<>(whitelist.getEntries());

            ObservableList<WhitelistEntry> list = FXCollections.observableArrayList(whitelistEntryList);
            whitelistEditTable.setItems(list);
        } else {
            this.whitelist = new Whitelist();
        }
    }

    /**
     * creates a new Whitelist entry.
     */
    public void addWhitelistEntry() {
        // whitelistEditTable
        WhitelistEntry entry = new WhitelistEntry();
        if (tfUsecaseId.validate() && tfUserName.validate()) {
            entry = new WhitelistEntry(tfUsecaseId.getText(), tfUserName.getText());
        }
        whitelistEditTable.getItems().add(entry);

        this.whitelistEditTable.requestFocus();
        this.whitelistEditTable.getSelectionModel().selectLast();
        this.whitelistEditTable.getFocusModel().focus(whitelistEditTable.getItems().size() - 1);
        this.whitelistEditTable.scrollTo(whitelistEditTable.getItems().size() - 1);
    }

    /**
     * saves New WhitelistEntry and warns if id or username is missing.
     */
    @FXML
    public void saveEntry() {
        if (tfUsecaseId.validate() && tfUserName.validate()) {
            if (whitelistEditTable.getSelectionModel().getSelectedItem() != null) {
                WhitelistEntry whitelistEntry = whitelistEditTable.getSelectionModel().getSelectedItem();
                whitelistEntry.setUsecaseId(tfUsecaseId.getText());
                whitelistEntry.setUsername(tfUserName.getText());

                this.whitelistEditTable.refresh();
            }

        } else {
            CustomAlert customAlert = new CustomAlert(Alert.AlertType.WARNING, "You need to fill out both fields", "An Whitelist entry needs to have an Usecase Id and an Username", "Ok", "Ok");
            customAlert.showAndWait();
        }
    }

    /**
     * save edited Whitelist.
     */
    @FXML
    private void saveEditWhitelist() throws Exception {
        if (!checkNameAndDescription()) {
            if (!whitelistEditTable.getItems().isEmpty()) {
                whitelist.getEntries().addAll(whitelistEditTable.getItems().stream().filter(x -> x.getUsecaseId() != null).collect(Collectors.toList()));
                whitelist.setName(tfWhitelistName.getText());
                whitelist.setDescription(tfDescription.getText());
                if (whitelist.isArchived()) {
                    whitelistDatabase.createWhitelist(whitelist);
                    parentController.updateWhitelistTable();

                } else if (whitelist.getId() == null) {
                    whitelistDatabase.createWhitelist(whitelist);
                    parentController.updateWhitelistTable();
                } else {
                    whitelistDatabase.updateWhitelist(whitelist);
                    parentController.updateWhitelistTable();
                }

                ((Stage) whitelistEditTable.getScene().getWindow()).close();
            } else {
                CustomAlert customAlert = new CustomAlert(Alert.AlertType.WARNING, "Whitelist is empty.", "A Whitelist needs to contain at least one entry", "Ok", "Ok");
                customAlert.showAndWait();
            }
        }
    }

    /**
     * cancels the edit process of a whitelist.
     */
    @FXML
    private void cancelEditWhitelist() {
        if (!whitelist.equals(whitelistOld)) {
            CustomAlert customAlert = new CustomAlert(Alert.AlertType.CONFIRMATION, "Still unsaved changes in Whitelist", "Changes are lost", "Ok", "Cancel");
            if (customAlert.showAndWait().get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                ((Stage) whitelistEditTable.getScene().getWindow()).close();
            }
        }
    }

    /**
     * initializes all validation of text fields.
     */
    private void initializeValidation() {
        tfUserName.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                tfUserName.validate();
            }
        });
        tfUsecaseId.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                tfUsecaseId.validate();
            }
        });
        tfDescription.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                tfDescription.validate();
            }
        });
        tfWhitelistName.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                tfWhitelistName.validate();
            }
        });
    }

    /**
     * reverts all fields and table to old stand.
     */
    public void revertEdit() {
        whitelist = whitelistOld;
        List<WhitelistEntry> whitelistEntryList = new ArrayList<>(whitelist.getEntries());

        ObservableList<WhitelistEntry> list = FXCollections.observableArrayList(whitelistEntryList);
        whitelistEditTable.getItems().clear();
        whitelistEditTable.setItems(list);
        usecaseId.setEditable(false);
        userName.setEditable(false);
        whitelistEditTable.setEditable(false);
        whitelistEditTable.refresh();
        tfWhitelistName.setText(whitelist.getName());
        tfDescription.setText(whitelist.getDescription());
    }


    /**
     * Sets the parent controller.
     *
     * @param parentController the parent controller
     */
    public void setParentController(WhitelistsController parentController) {
        this.parentController = parentController;
    }

    /**
     * checks if Whitelist name and whitelist Description is empty.
     *
     * @return true if name or description is empty.
     */
    private Boolean checkNameAndDescription() {
        return tfDescription.getText().equals("") || tfWhitelistName.getText().equals("");
    }
}
