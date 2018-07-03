package ui.main.whitelists.modal;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import data.entities.Whitelist;
import data.entities.WhitelistEntry;
import data.localdb.ArtDbContext;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

import extensions.ResourceBundleHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import ui.AppComponents;
import ui.custom.controls.ButtonCell;
import ui.custom.controls.CustomAlert;
import ui.custom.controls.PTableColumn;
import ui.main.configs.modal.ConfigsFormController;
import ui.main.whitelists.WhitelistsController;


public class WhitelistFormController {


    @FXML
    public TableView<WhitelistEntry> whitelistEditTable;

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
    private PTableColumn<WhitelistEntry, String> usecaseId;

    @FXML
    private PTableColumn<WhitelistEntry, String> userName;

    @FXML
    private JFXButton saveButton;

    @FXML
    private JFXButton applyButton;

    @FXML
    private JFXButton addButton;

    @FXML
    private JFXButton copyButton;


    private ArtDbContext whitelistDatabase = AppComponents.getInstance().getDbContext();
    private Whitelist whitelist;
    private Whitelist whitelistOld;
    private ResourceBundle bundle = ResourceBundleHelper.getInstance().getLanguageBundle();
    private WhitelistsController whitelistsController;
    private ConfigsFormController configsFormController;

    /**
     * Automatically called by FXML loader, starts initialize Columns.
     */
    public void initialize() {

        initializeTableColumns();
        initializeValidation();

        // addButton disable binding
        this.addButton.disableProperty().bind(Bindings.or(
            Bindings.isEmpty(tfUserName.textProperty()),
            Bindings.isEmpty(tfUsecaseId.textProperty())));

        // copyButton disable binding
        this.copyButton.disableProperty().bind(
            Bindings.or(Bindings.isNull(whitelistEditTable.getSelectionModel().selectedItemProperty()),
                Bindings.or(
                    Bindings.isEmpty(tfUserName.textProperty()),
                    Bindings.isEmpty(tfUsecaseId.textProperty()))));

        // applyButton disable binding
        this.applyButton.disableProperty().bind(
            Bindings.or(Bindings.isNull(whitelistEditTable.getSelectionModel().selectedItemProperty()),
                Bindings.or(
                    Bindings.isEmpty(tfUserName.textProperty()),
                    Bindings.isEmpty(tfUsecaseId.textProperty()))));

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
     * Loads use case and username from the selected entry to the textfield.
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
     * Creates a new Whitelist Entry.
     */
    public void addWhitelistEntry() {
        WhitelistEntry entry = new WhitelistEntry(tfUsecaseId.getText(), tfUserName.getText());

        whitelistEditTable.getItems().add(entry);
        this.whitelistEditTable.requestFocus();
        this.whitelistEditTable.scrollTo(whitelistEditTable.getItems().size() - 1);
        this.whitelistEditTable.getSelectionModel().clearSelection();

        resetDetails();
    }

    /**
     * Creates a new Whitelist Entry.
     */
    public void copyWhitelistEntry() {
        if (whitelistEditTable.getSelectionModel().getSelectedItem() != null) {
            WhitelistEntry entry = new WhitelistEntry(tfUsecaseId.getText(), tfUserName.getText());

            whitelistEditTable.getItems().add(entry);
            this.whitelistEditTable.requestFocus();
            this.whitelistEditTable.getSelectionModel().selectLast();
            this.whitelistEditTable.getFocusModel().focus(whitelistEditTable.getItems().size() - 1);
            this.whitelistEditTable.scrollTo(whitelistEditTable.getItems().size() - 1);
        }
    }


    /**
     * Applies the changes from the detail view to the table.
     */
    @FXML
    public void applyChanges() {
        if (tfUsecaseId.validate() && tfUserName.validate()) {
            if (whitelistEditTable.getSelectionModel().getSelectedItem() != null) {
                WhitelistEntry whitelistEntry = whitelistEditTable.getSelectionModel().getSelectedItem();
                whitelistEntry.setUsecaseId(tfUsecaseId.getText());
                whitelistEntry.setUsername(tfUserName.getText());

                resetDetails();

                this.whitelistEditTable.getSelectionModel().clearSelection();
                this.whitelistEditTable.refresh();
            }
        }
    }

    private void resetDetails() {
        tfUserName.setText("");
        tfUsecaseId.setText("");
    }

    /**
     * Save edited Whitelist.
     */
    @FXML
    private void saveEditWhitelist() throws Exception {
        if (checkNameAndDescription()) {

            if (!whitelistEditTable.getItems().isEmpty()) {
                whitelist.getEntries().addAll(whitelistEditTable.getItems().stream().filter(x -> x.getUsecaseId() != null).collect(Collectors.toList()));
                whitelist.setName(tfWhitelistName.getText());
                whitelist.setDescription(tfDescription.getText());

                // dialog was called from WhitelistsView
                if (whitelistsController != null) {

                    if (whitelist.getId() == null) {
                        whitelistDatabase.createWhitelist(whitelist);
                        whitelistsController.updateTable();
                    } else {
                        whitelistDatabase.updateWhitelist(whitelist);
                        whitelistsController.updateTable();
                    }
                } else if (configsFormController != null) { // dialog was called from ConfigsFormController
                    configsFormController.setWhitelist(whitelist);
                }

                ((Stage) whitelistEditTable.getScene().getWindow()).close();
            } else {
                CustomAlert customAlert = new CustomAlert(Alert.AlertType.WARNING, bundle.getString("whitelistEmptyAlertTitle"), bundle.getString("whitelistEmptyAlertMessage"));
                customAlert.showAndWait();
            }
        }
    }

    /**
     * Cancels the edit process of a whitelist.
     */
    @FXML
    private void cancelEditWhitelist() {

        // check if dialog is in edit mode
        if (applyButton.isVisible()) {
            CustomAlert customAlert = new CustomAlert(Alert.AlertType.CONFIRMATION, bundle.getString("cancelWithoutSavingTitle"),
                bundle.getString("cancelWithoutSavingMessage"), "Ok", "Cancel");
            if (customAlert.showAndWait().get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                ((Stage) whitelistEditTable.getScene().getWindow()).close();
                try {
                    whitelistsController.updateTable();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            ((Stage) whitelistEditTable.getScene().getWindow()).close();
        }
    }

    /**
     * Initializes all validation of text fields.
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
     * Sets the parent controller.
     *
     * @param whitelistsController the parent controller
     */
    public void setWhitelistsController(WhitelistsController whitelistsController) {
        this.whitelistsController = whitelistsController;
    }


    public void setConfigsFormController(ConfigsFormController configsFormController) {
        this.configsFormController = configsFormController;
    }

    /**
     * Checks if Whitelist name and whitelist Description is empty.
     *
     * @return true if name or description is empty.
     */
    private boolean checkNameAndDescription() {
        return tfDescription.validate() && tfWhitelistName.validate();
    }

    /**
     * Sets the editable attribute of all textfields and visibility of saveButton.
     */
    public void setEditable(boolean editable) {
        if (!editable) {
            tfWhitelistName.setEditable(false);
            tfDescription.setEditable(false);
            tfUsecaseId.setEditable(false);
            tfUserName.setEditable(false);
            saveButton.setVisible(false);
            addButton.setVisible(false);
            applyButton.setVisible(false);
            copyButton.setVisible(false);
            deleteWhitelistEntryColumn.setVisible(false);
        } else {
            tfWhitelistName.setEditable(true);
            tfDescription.setEditable(true);
            tfUsecaseId.setEditable(true);
            tfUserName.setEditable(true);
            saveButton.setVisible(true);
            addButton.setVisible(true);
            applyButton.setVisible(true);
            copyButton.setVisible(true);
            deleteWhitelistEntryColumn.setVisible(true);
        }
    }
}
