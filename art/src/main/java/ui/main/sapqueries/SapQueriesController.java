package ui.main.sapqueries;

import com.jfoenix.controls.JFXButton;

import data.entities.Configuration;
import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;
import data.entities.SapConfiguration;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

import extensions.ResourceBundleHelper;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import ui.AppComponents;
import ui.IUpdateTable;
import ui.custom.controls.ButtonCell;
import ui.custom.controls.CustomAlert;
import ui.custom.controls.SapQueryStatusCellFactory;
import ui.custom.controls.filter.FilterController;
import ui.main.sapqueries.modal.details.SapQueryDetailController;
import ui.main.sapqueries.modal.newquery.NewSapQueryController;

public class SapQueriesController implements IUpdateTable {

    @FXML
    public TableView<CriticalAccessQuery> queriesTable;

    @FXML
    public TableColumn<CriticalAccessQuery, Configuration> configurationColumn;

    @FXML
    public TableColumn<CriticalAccessQuery, SapConfiguration> sapConfigurationColumn;

    @FXML
    public TableColumn<CriticalAccessQuery, ZonedDateTime> createdAtColumn;

    @FXML
    public TableColumn<CriticalAccessQuery, JFXButton> archiveColumn;

    @FXML
    public TableColumn<CriticalAccessQuery, JFXButton> editColumn;

    @FXML
    public TableColumn<CriticalAccessQuery, Set<CriticalAccessEntry>> queryStatusColumn;

    @FXML
    public Label itemCount;

    @FXML
    public FilterController filterController;


    private ResourceBundle bundle;
    private SimpleIntegerProperty numberOfItems = new SimpleIntegerProperty();

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() throws Exception {

        // load the ResourceBundle
        bundle = ResourceBundleHelper.getInstance().getLanguageBundle();

        // initialize the table
        initializeQueriesTable();

        // check if the filters are applied
        filterController.shouldFilterProperty.addListener((o, oldValue, newValue) -> {
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
     * Updates the queriesTable items from the database, taking filters into account.
     */
    public void updateTable() throws Exception {
        List<CriticalAccessQuery> patterns = AppComponents.getInstance().getDbContext().getFilteredCriticalAccessQueries(filterController.showArchivedProperty.getValue(),
            filterController.searchStringProperty.getValue(), filterController.startDateProperty.getValue(),
            filterController.endDateProperty.getValue(), 0);
        ObservableList<CriticalAccessQuery> list = FXCollections.observableList(patterns);

        // update itemCount
        numberOfItems.setValue(list.size());

        queriesTable.setItems(list);
        queriesTable.refresh();
    }

    /**
     * Initializes the sapQueries table.
     */
    private void initializeQueriesTable() {

        // replace Placeholder of PatternsTable with other message
        queriesTable.setPlaceholder(new Label(bundle.getString("noEntries")));

        // catch row double click
        queriesTable.setRowFactory(tv -> {
            TableRow<CriticalAccessQuery> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    CriticalAccessQuery query = row.getItem();
                    openQuery(query);
                }
            });
            return row;
        });

        // set selection mode to MULTIPLE
        queriesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // show an item count (+ selected)
        itemCount.textProperty().bind(Bindings.concat(Bindings.size(queriesTable.getSelectionModel().getSelectedItems()).asString("%s / "),
            numberOfItems.asString("%s " + bundle.getString("selected"))));

        initializeTableColumns();
    }

    /**
     * Initializes the table columns that need extra content.
     */
    private void initializeTableColumns() {
        // Add the delete column
        archiveColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.ARCHIVE, bundle.getString("archive"), (CriticalAccessQuery query) -> {

            CustomAlert customAlert = new CustomAlert(Alert.AlertType.CONFIRMATION, bundle.getString("archiveConfirmTitle"),
                bundle.getString("archiveConfirmMessage"), "Ok", "Cancel");

            if (customAlert.showAndWait().get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                try {
                    archiveQuery(query);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return query;
        }));

        // Add the edit column
        editColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.OPEN_IN_NEW, bundle.getString("details"), (CriticalAccessQuery query) -> {
            openQuery(query);
            return query;
        }));

        // overwrite the column in which the configuration is displayed to display only its name
        configurationColumn.setCellFactory(col -> new TableCell<CriticalAccessQuery, Configuration>() {

            @Override
            protected void updateItem(Configuration configuration, boolean empty) {

                // display nothing if the row is empty, otherwise the item count
                setText((empty || configuration == null) ? "" : "" + configuration.getName());
            }
        });

        // overwrite the column in which the sapConfiguration is displayed to display only its name
        sapConfigurationColumn.setCellFactory(col -> new TableCell<CriticalAccessQuery, SapConfiguration>() {

            @Override
            protected void updateItem(SapConfiguration configuration, boolean empty) {

                // display nothing if the row is empty, otherwise the item count
                setText((empty || configuration == null) ? "" : "" + configuration.getDescription());
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

        // sets the icon of the condition to pattern or profile
        queryStatusColumn.setCellFactory(new SapQueryStatusCellFactory());
    }

    /**
     * Opens the selected query.
     *
     * @param query the selected query
     */
    private void openQuery(CriticalAccessQuery query) {
        try {

            FXMLLoader loader = AppComponents.getInstance().showScene("ui/main/sapqueries/modal/details/SapQueryDetailView.fxml","queryDetails");

            // give the dialog the query
            SapQueryDetailController queryDetail = loader.getController();
            queryDetail.giveSelectedQuery(query);
            queryDetail.setParentController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the edit dialog with the selected item.
     */
    public void editAction() {
        if (queriesTable.getSelectionModel().getSelectedItem() != null) {
            openQuery(queriesTable.getSelectionModel().getSelectedItem());
        }
    }

    /**
     * Archives all selected items.
     */
    public void archiveAction() throws Exception {
        if (queriesTable.getSelectionModel().getSelectedItems() != null && queriesTable.getSelectionModel().getSelectedItems().size() != 0) {
            CustomAlert customAlert;
            if (queriesTable.getSelectionModel().getSelectedItems().size() == 1) {
                customAlert = new CustomAlert(Alert.AlertType.CONFIRMATION, bundle.getString("archiveConfirmTitle"),
                    bundle.getString("archiveConfirmMessage"), "Ok", "Cancel");
            } else {
                customAlert = new CustomAlert(Alert.AlertType.CONFIRMATION, bundle.getString("archiveMultipleConfirmTitle"),
                    bundle.getString("archiveMultipleConfirmMessage"), "Ok", "Cancel");
            }

            if (customAlert.showAndWait().get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                // archive all selected items
                for (CriticalAccessQuery query : queriesTable.getSelectionModel().getSelectedItems()) {
                    archiveQuery(query);
                }
            }
            updateTable();
        }
    }

    /**
     * Opens the modal dialog to create a new item.
     */
    public void addAction() {
        try {

            FXMLLoader loader = AppComponents.getInstance().showScene("ui/main/sapqueries/modal/newquery/NewSapQueryView.fxml","newAnalysis");

            // give the dialog the query
            NewSapQueryController newQuery = loader.getController();
            newQuery.setParentController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void archiveQuery(CriticalAccessQuery query) throws Exception {
        if (query != null) {

            query.setArchived(true);
            AppComponents.getInstance().getDbContext().updateRecord(query);

            updateTable();
        }
    }
}
