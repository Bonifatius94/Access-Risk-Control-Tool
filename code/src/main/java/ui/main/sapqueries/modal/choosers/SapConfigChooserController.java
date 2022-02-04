package ui.main.sapqueries.modal.choosers;

import data.entities.SapConfiguration;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import ui.AppComponents;
import ui.custom.controls.filter.FilterController;
import ui.main.sapqueries.modal.newquery.NewSapQueryController;


public class SapConfigChooserController {

    @FXML
    public TableView<SapConfiguration> sapSettingsTable;

    @FXML
    public FilterController filterController;

    @FXML
    private TableColumn<SapConfiguration, ZonedDateTime> creationColumn;

    private NewSapQueryController parentController;


    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() throws Exception {

        // check if the filters are applied
        filterController.shouldFilterProperty.addListener((o, oldValue, newValue) -> {
            if (newValue) {
                try {
                    updateSapConfigsTable();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // format created at date column
        creationColumn.setCellFactory(col -> new TableCell<SapConfiguration, ZonedDateTime>() {
            @Override
            protected void updateItem(ZonedDateTime item, boolean empty) {
                setText((empty || item == null) ? "" : item.format(DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm")));
            }
        });

        updateSapConfigsTable();
    }

    public void setParentController(NewSapQueryController controller) {
        this.parentController = controller;
    }

    /**
     * Return the chosen sapConfiguration to the parent controller and close the window.
     *
     * @param event the event which is needed to close the stage.
     */
    public void chooseSapConfig(ActionEvent event) {
        if (this.sapSettingsTable.getSelectionModel().getSelectedItem() != null) {
            parentController.setSapConfig(this.sapSettingsTable.getSelectionModel().getSelectedItem());
            close(event);
        }
    }

    /**
     * Updates the sapConfigsTable items from the database, taking filters into account.
     */
    public void updateSapConfigsTable() throws Exception {

        List<SapConfiguration> sapConfigs = AppComponents.getInstance().getDbContext().getFilteredSapConfigs(filterController.showArchivedProperty.getValue(),
            filterController.searchStringProperty.getValue(), filterController.startDateProperty.getValue(),
            filterController.endDateProperty.getValue(), 0);
        ObservableList<SapConfiguration> list = FXCollections.observableList(sapConfigs);

        sapSettingsTable.setItems(list);
        sapSettingsTable.refresh();
    }

    /**
     * Hides the stage.
     *
     * @param event the given ActionEvent
     */
    public void close(javafx.event.ActionEvent event) {
        (((Button) event.getSource()).getScene().getWindow()).hide();
    }
}
