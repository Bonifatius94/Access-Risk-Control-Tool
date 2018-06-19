package ui.main.whitelists;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import data.entities.Whitelist;
import data.entities.WhitelistEntry;
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

    // private WhitelistDatabase whitelistDatabase;
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
     * TODO: Ersetzung des vorherigen objekts
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
        tfUserName.clear();
        tfUserName.clear();
        this.whitelistEditTable.getItems().add(new WhitelistEntry());
        this.whitelistEditTable.requestFocus();
        this.whitelistEditTable.getSelectionModel().selectLast();
        this.whitelistEditTable.getFocusModel().focus(whitelistEditTable.getItems().size() - 1);
        this.whitelistEditTable.scrollTo(whitelistEditTable.getItems().size() - 1);


        /* if (!tfUserName.getText().equals("") && !tfUsecaseId.getText().equals("")) {
            WhitelistEntry whitelistEntry = new WhitelistEntry(tfUsecaseId.getText(), tfUserName.getText());
            //TODO: was muss hier rein ???)
            if (true) {
                whitelistEditTable.getItems().add(whitelistEntry);
            }
        }*/
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
            CustomAlert customAlert = new CustomAlert(Alert.AlertType.WARNING, "You need to fill out both fields", "An Whitelist entry needs to have an Usecase Id and an Username");
            customAlert.showAndWait();
        }
    }

    /**
     * save edited Whitelist. TODO:implementation. missing saving
     */
    @FXML
    private void saveEditWhitelist() {
        /*if (whitelist.isArchived()) {
            //whitelistDatabase.save(whitelist);
        } else {
            //whitelistDatabase.update(whitelist);
        }*/

    }

    /**
     * cancels the edit process of a whitelist TODO: implementation.
     */
    @FXML
    private void cancelEditWhitelist() {
        //not sure if this is needed ? (DEEP_COPY???)
        if (whitelist.getEntries().size() != whitelistOld.getEntries().size()) {
            CustomAlert customAlert = new CustomAlert(Alert.AlertType.CONFIRMATION, "Still unsaved changes in Whitelist", "Do you want to save changes", "Save", "Chancel");
            ButtonType buttonType = customAlert.showAndWait().get();
            if (buttonType == ButtonType.OK) {
                saveEditWhitelist();
            } else if (buttonType == ButtonType.CANCEL) {
                whitelistEditTable.getScene().getWindow().hide();
            }
        }
        whitelist = whitelistOld;
        ((Stage) whitelistEditTable.getScene().getWindow()).close();


    }

    /**
     * initializes all validation of textfields.
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

        whitelistEditTable.setItems(list);
        usecaseId.setEditable(false);
        userName.setEditable(false);
        whitelistEditTable.setEditable(false);
        whitelistEditTable.refresh();
    }
}
