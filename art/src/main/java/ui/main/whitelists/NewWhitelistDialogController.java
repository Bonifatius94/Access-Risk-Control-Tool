package ui.main.whitelists;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import data.entities.Whitelist;
import data.entities.WhitelistEntry;
import data.localdb.ArtDbContext;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import ui.AppComponents;
import ui.custom.controls.ButtonCell;
import ui.custom.controls.CustomAlert;
import ui.custom.controls.PTableColumn;


public class NewWhitelistDialogController {
    public TableView<WhitelistEntry> newWhitelistTable;
    public PTableColumn<WhitelistEntry, String> usecaseId;
    public PTableColumn<WhitelistEntry, String> userName;
    public PTableColumn<WhitelistEntry, JFXButton> deleteWhitelistEntryColumn;

    public JFXTextField tfUscaseId;
    public JFXTextField tfUserName;
    //public JFXButton cancelButton;
    public JFXButton addButton;
    public JFXTextField tfDescription;
    public JFXTextField whitelistName;

    private Whitelist whitelist;
    private ArtDbContext whitelistDatabase = AppComponents.getDbContext();
    private WhitelistsController parentController;

    /**
     * is automatically called by FXML loader and starts initializeColumns.
     */
    @FXML
    @SuppressWarnings("all")
    public void initialize() {
        initializeColumns();
        initializeValidation();
        try {
            List<WhitelistEntry> list = new ArrayList<WhitelistEntry>();
            ObservableList<WhitelistEntry> entryObservableList = FXCollections.observableArrayList(list);
            newWhitelistTable.setItems(entryObservableList);
            newWhitelistTable.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
        newWhitelistTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                editWhitelistEntry(newValue);
            }
        });
    }

    private void initializeValidation() {
        tfUserName.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                tfUserName.validate();
            }
        });
        tfUscaseId.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                tfUscaseId.validate();
            }
        });
        tfDescription.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                tfDescription.validate();
            }
        });
        whitelistName.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                whitelistName.validate();
            }
        });
    }

    /**
     * initializes delete and edit Button Columns.
     */
    private void initializeColumns() {
        deleteWhitelistEntryColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.DELETE, (WhitelistEntry whitelistEntry) -> {

            newWhitelistTable.getItems().remove(whitelistEntry);
            return whitelistEntry;
        }));

        deleteWhitelistEntryColumn.setSortable(false);

    }

    /**
     * loads already Existig WhitelistEntry in Textfields.
     *
     * @param whitelistEntry the WhitelistEntry that should be changed.
     */
    private void editWhitelistEntry(WhitelistEntry whitelistEntry) {

        tfUscaseId.setText(whitelistEntry.getUsecaseId());
        tfUserName.setText(whitelistEntry.getUsername());

        tfUserName.validate();
        tfUscaseId.validate();

    }

    /**
     * cancel New Whitelist deletes all information.
     */
    public void cancelNewWhitelist() {

        CustomAlert customAlert = new CustomAlert(Alert.AlertType.CONFIRMATION, "Still unsaved changes. Do you want to save them?", "By pressing cancel Whitelist will be deleted", "Save", "Cancel");
        ButtonType buttonType = customAlert.showAndWait().get();
        if (buttonType == ButtonType.OK) {
            saveNewWhitelist();
            Stage stage = (Stage) newWhitelistTable.getScene().getWindow();
            stage.close();
        } else if (buttonType == ButtonType.CANCEL) {
            Stage stage = (Stage) newWhitelistTable.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * saves New Whitelist.
     */
    public void saveNewWhitelist() {
        if (!checkNameAndDescription()) {
            if (!newWhitelistTable.getItems().isEmpty()) {
                whitelist.getEntries().addAll(newWhitelistTable.getItems());
                try {
                    whitelistDatabase.createWhitelist(whitelist);
                    parentController.updateWhitelistTable();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                CustomAlert customAlert = new CustomAlert(Alert.AlertType.WARNING, "Whitelist is empty.", "A Whitelist needs to contain at least one entry");
                customAlert.showAndWait();
            }
        } else {
            CustomAlert customAlert = new CustomAlert(Alert.AlertType.WARNING, "Whitelistname or description is not valid.", "A Whitelist needs a Name and a description");
            customAlert.showAndWait();
        }
    }

    /**
     * checks if Whitelist name and whitelist Description is empty.
     *
     * @return true if name or description is empty.
     */
    private Boolean checkNameAndDescription() {
        return tfDescription.getText().equals("") || whitelistName.getText().equals("");
    }

    /**
     * adds new WhitelistEntry to Table.
     */
    public void addNewWhitelistEntry() {

        if (!tfUserName.getText().equals("") && !tfUscaseId.getText().equals("")) {
            WhitelistEntry whitelistEntry = new WhitelistEntry(tfUscaseId.getText(), tfUserName.getText());
            newWhitelistTable.getItems().add(whitelistEntry);
        } else {
            tfUserName.clear();
            tfUserName.clear();
            this.newWhitelistTable.getItems().add(new WhitelistEntry());
            this.newWhitelistTable.requestFocus();
            this.newWhitelistTable.getSelectionModel().selectLast();
            this.newWhitelistTable.getFocusModel().focus(newWhitelistTable.getItems().size() - 1);
            this.newWhitelistTable.scrollTo(newWhitelistTable.getItems().size() - 1);
        }


    }

    /**
     * saves Entry changes.
     */
    @FXML
    public void saveEntry() {
        if (!tfUscaseId.getText().equals("") && !tfUserName.getText().equals("")) {
            if (newWhitelistTable.getSelectionModel().getSelectedItem() != null) {
                WhitelistEntry whitelistEntry = newWhitelistTable.getSelectionModel().getSelectedItem();
                whitelistEntry.setUsecaseId(tfUscaseId.getText());
                whitelistEntry.setUsername(tfUserName.getText());

                this.newWhitelistTable.refresh();

            } else {
                newWhitelistTable.getItems().add(new WhitelistEntry(tfUscaseId.getText(), tfUserName.getText()));
                this.newWhitelistTable.refresh();
            }
            //whitelistEditTable.getItems().add();

        } else {
            CustomAlert customAlert = new CustomAlert(Alert.AlertType.WARNING, "You need to fill out both fields", "An Whitelist entry needs to have an Usecase Id and an Username");
            customAlert.showAndWait();
        }
    }

    /**
     * Sets the parent controller.
     *
     * @param parentController the parent controller
     */
    public void setParentController(WhitelistsController parentController) {
        this.parentController = parentController;
    }


}
