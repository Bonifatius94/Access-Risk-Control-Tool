package ui.main.configs.modal;

import com.jfoenix.controls.JFXTextField;

import data.entities.CriticalAccessQuery;
import data.entities.Whitelist;
import data.entities.WhitelistEntry;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ui.AppComponents;
import ui.custom.controls.filter.FilterController;


public class ChooseWhitelistController {

    @FXML
    public TableView<Whitelist> whitelistTable;

    @FXML
    public TableColumn<Whitelist, Set<WhitelistEntry>> entryCountColumn;

    @FXML
    public JFXTextField nameField;

    @FXML
    public JFXTextField descriptionField;

    @FXML
    public TableView<WhitelistEntry> whitelistEntries;

    @FXML
    public FilterController filterController;

    @FXML
    public TableColumn<CriticalAccessQuery, ZonedDateTime> createdAtColumn;

    @FXML
    public Label noWhitelistChosenLabel;


    private ConfigsFormController parentController;


    /**
     * Initializes the view.
     */
    @FXML
    public void initialize() throws Exception {

        // set text fields uneditable
        this.nameField.setEditable(false);
        this.descriptionField.setEditable(false);

        initializeWhitelistTable();

        // show warning if no whitelist is selected
        noWhitelistChosenLabel.visibleProperty().bind(Bindings.isNull(whitelistTable.getSelectionModel().selectedItemProperty()));

        // check if the filters are applied
        filterController.shouldFilterProperty.addListener((o, oldValue, newValue) -> {
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
     * Initializes the patterns table.
     */
    private void initializeWhitelistTable() {

        // overwrite the column in which the number of useCases is displayed
        entryCountColumn.setCellFactory(col -> new TableCell<Whitelist, Set<WhitelistEntry>>() {

            @Override
            protected void updateItem(Set<WhitelistEntry> items, boolean empty) {

                // display nothing if the row is empty, otherwise the item count
                setText((empty || items == null) ? "" : "" + items.size());
            }
        });

        // custom comparator for the conditionCountColumn
        entryCountColumn.setComparator((list1, list2) -> list1.size() <= list2.size() ? 0 : 1);

        // listen to selects on whitelistTable
        whitelistTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showWhitelistDetails(newValue);
            }
        });

        // overwrite the column in which the date is displayed for formatting
        createdAtColumn.setCellFactory(col -> new TableCell<CriticalAccessQuery, ZonedDateTime>() {

            @Override
            protected void updateItem(ZonedDateTime time, boolean empty) {

                // display nothing if the row is empty, otherwise the item count
                setText((empty || time == null) ? "" : "" + time.format(DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm")));
            }
        });
    }

    /**
     * Shows the details of the selected whitelist by filling in text fields on the right side.
     * @param whitelist the selected whitelist
     */
    private void showWhitelistDetails(Whitelist whitelist) {
        this.nameField.setText(whitelist.getName());
        this.descriptionField.setText(whitelist.getDescription());

        ObservableList<WhitelistEntry> entries = FXCollections.observableList(new ArrayList<>(whitelist.getEntries()));
        this.whitelistEntries.setItems(entries);
    }

    /**
     * Saves the changes to the previous window.
     */
    public void saveChanges(ActionEvent event) {
        if (whitelistTable.getSelectionModel().getSelectedItem() != null) {
            this.parentController.setWhitelist(whitelistTable.getSelectionModel().getSelectedItem());
            this.close(event);
        }
    }

    /**
     * Sets the parent controller so we can give it some data.
     *
     * @param controller the parent controller
     */
    public void setParentController(ConfigsFormController controller) {
        this.parentController = controller;
    }

    /**
     * Hides the stage.
     *
     * @param event the given ActionEvent
     */
    public void close(ActionEvent event) {
        (((Button) event.getSource()).getScene().getWindow()).hide();
    }

    /**
     * Fills the whitelist table with entries.
     */
    private void updateWhitelistTable() throws Exception {
        List<Whitelist> whitelists = AppComponents.getInstance().getDbContext().getFilteredWhitelists(filterController.showArchivedProperty.getValue(),
            filterController.searchStringProperty.getValue(), filterController.startDateProperty.getValue(),
            filterController.endDateProperty.getValue(), 0);

        ObservableList<Whitelist> list = FXCollections.observableList(whitelists);

        whitelistTable.setItems(list);
        whitelistTable.refresh();
    }
}
