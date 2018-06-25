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

public class WhitelistEditDialogController {


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
    private static Whitelist whitelist;
    private static Whitelist whitelistOld;
    // private WhitelistEntry whitelistEntry;

    /**
     * automatically called by FXML loader, starts unitialize Columns.
     */
    public void initialize() {

        initializeTableColumns();
        initializeValidation();
        if (whitelist.getName() == null) {
            tfWhitelistName.setText("");
        } else {
            tfWhitelistName.setText(whitelist.getName());
        }
        if (whitelist.getDescription() == null) {
            tfDescription.setText("");
        } else {
            tfDescription.setText(whitelist.getDescription());
        }
        try {

            List<WhitelistEntry> whitelistEntryList = new ArrayList<>(whitelist.getEntries());

            ObservableList<WhitelistEntry> list = FXCollections.observableArrayList(whitelistEntryList);

            whitelistEditTable.setItems(list);
            usecaseId.setEditable(true);
            userName.setEditable(true);
            whitelistEditTable.setEditable(false);
            whitelistEditTable.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
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


        if (whitelistEditTable.getFocusModel().getFocusedItem() != null) {

            assert whitelistEntry != null;
            tfUsecaseId.setText(whitelistEntry.getUsecaseId());
            tfUserName.setText(whitelistEntry.getUsername());
        }
    }

    /**
     * Is called if an Whitelist edit is started by the WhitelistController class.
     *
     * @param whitelist1 the whitelist that is loaded to edit dialog.
     */
    static void giveSelectedWhitelist(Whitelist whitelist1) {

        whitelist = whitelist1;
        whitelistOld = new Whitelist(whitelist1);


    }

    /**
     * creates a new Whitelist entry. TODO: check if needed
     */
    public void newWhitelistEntry() {
        // whitelistEditTable
        if (!tfUserName.getText().equals("") && !tfUsecaseId.getText().equals("")) {
            WhitelistEntry whitelistEntry = new WhitelistEntry(tfUsecaseId.getText(), tfUserName.getText());
            whitelistEditTable.getItems().add(whitelistEntry);

        } else {
            tfUserName.clear();
            tfUserName.clear();
            this.whitelistEditTable.getItems().add(new WhitelistEntry());
            this.whitelistEditTable.requestFocus();
            this.whitelistEditTable.getSelectionModel().selectLast();
            this.whitelistEditTable.getFocusModel().focus(whitelistEditTable.getItems().size() - 1);
            this.whitelistEditTable.scrollTo(whitelistEditTable.getItems().size() - 1);
        }

    }

    /**
     * saves New WhitelistEntry and warns if id or username is missing.
     */
    @FXML
    public void saveEntry() {
        if (!tfUsecaseId.getText().equals("") && !tfUserName.getText().equals("")) {
            if (whitelistEditTable.getSelectionModel().getSelectedItem() != null) {
                WhitelistEntry whitelistEntry = whitelistEditTable.getSelectionModel().getSelectedItem();
                whitelistEntry.setUsecaseId(tfUsecaseId.getText());
                whitelistEntry.setUsername(tfUserName.getText());


                this.whitelistEditTable.refresh();

            } else {
                whitelistEditTable.getItems().add(new WhitelistEntry(tfUsecaseId.getText(), tfUserName.getText()));
                this.whitelistEditTable.refresh();
            }
            //whitelistEditTable.getItems().add();

        } else {
            CustomAlert customAlert = new CustomAlert(Alert.AlertType.WARNING, "You need to fill out both fields", "An Whitelist entry needs to have an Usecase Id and an Username", "Ok", "Ok");
            customAlert.showAndWait();
        }
    }

    /**
     * save edited Whitelist.
     */
    @FXML
    private void saveEditWhitelist() {
        if (!checkNameAndDescription()) {
            if (!whitelistEditTable.getItems().isEmpty()) {
                whitelist.getEntries().addAll(whitelistEditTable.getItems());
                whitelist.setName(tfWhitelistName.getText());
                whitelist.setDescription(tfDescription.getText());
                try {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                CustomAlert customAlert = new CustomAlert(Alert.AlertType.WARNING, "Whitelist is empty.", "A Whitelist needs to contain at least one entry", "Ok", "Ok");
                customAlert.showAndWait();
            }
        } else {
            CustomAlert customAlert = new CustomAlert(Alert.AlertType.WARNING, "Whitelistname or description is not valid.", "A Whitelist needs a Name and a description", "Ok", "Ok");
            customAlert.showAndWait();
        }
    }

    /**
     * cancels the edit process of a whitelist.
     */
    @FXML
    private void cancelEditWhitelist() {
        //not sure if this is needed ? (DEEP_COPY???)
        if (!whitelist.equals(whitelistOld)) {
            CustomAlert customAlert = new CustomAlert(Alert.AlertType.CONFIRMATION, "Still unsaved changes in Whitelist", "Do you want to save changes", "Save", "Cancel");
            String buttonType = customAlert.showAndWait().get().getText();
            if (buttonType.equals("Save")) {
                saveEditWhitelist();
                ((Stage) whitelistEditTable.getScene().getWindow()).close();

            } else if (buttonType.equals("Cancel")) {
                whitelistEditTable.getScene().getWindow().hide();
            }
        }
        whitelist = whitelistOld;
        ((Stage) whitelistEditTable.getScene().getWindow()).close();

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
