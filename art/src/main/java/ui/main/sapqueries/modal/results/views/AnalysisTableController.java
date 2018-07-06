package ui.main.sapqueries.modal.results.views;

import com.jfoenix.controls.JFXButton;

import data.entities.AccessPattern;
import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

import extensions.ResourceBundleHelper;

import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;
import ui.AppComponents;
import ui.custom.controls.ButtonCell;
import ui.main.patterns.modal.PatternsFormController;


public class AnalysisTableController {

    @FXML
    private TableView<CriticalAccessEntry> resultTable;

    @FXML
    public TableColumn<CriticalAccessEntry, JFXButton> viewPatternDetailsColumn;

    @FXML
    public TableColumn<CriticalAccessEntry, AccessPattern> conditionTypeColumn;

    @FXML
    public TableColumn<CriticalAccessEntry, AccessPattern> usecaseIdColumn;

    @FXML
    public TableColumn<CriticalAccessEntry, AccessPattern> descriptionColumn;


    private CriticalAccessQuery resultQuery;
    private ResourceBundle bundle;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        bundle = ResourceBundleHelper.getInstance().getLanguageBundle();

        // Add the detail column
        viewPatternDetailsColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.OPEN_IN_NEW, (CriticalAccessEntry entry) -> {
            viewAccessPatternDetails(entry.getAccessPattern());
            return entry;
        }));

        resultTable.setPlaceholder(new Label(bundle.getString("noEntries")));

        initializeResultTable();
    }

    /**
     * Initializes all important columns and the double click.
     */
    private void initializeResultTable() {

        // catch row double click
        resultTable.setRowFactory(tv -> {
            TableRow<CriticalAccessEntry> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    CriticalAccessEntry entry = row.getItem();
                    viewAccessPatternDetails(entry.getAccessPattern());
                }
            });
            return row;
        });

        initializeConditionTypeColumn();
        initializeUsecaseIdColumn();
        initializeDescriptionColumn();
    }

    /**
     * Initializes the usecaseId Column.
     */
    private void initializeUsecaseIdColumn() {
        usecaseIdColumn.setCellFactory((col -> new TableCell<CriticalAccessEntry, AccessPattern>() {

            @Override
            protected void updateItem(AccessPattern entry, boolean empty) {

                // display nothing if the row is empty, otherwise the item count
                setText((empty || entry == null) ? "" : "" + entry.getUsecaseId());
            }
        }));
    }

    /**
     * Initializes the description Column.
     */
    private void initializeDescriptionColumn() {
        descriptionColumn.setCellFactory((col -> new TableCell<CriticalAccessEntry, AccessPattern>() {

            @Override
            protected void updateItem(AccessPattern entry, boolean empty) {

                // display nothing if the row is empty, otherwise the item count
                setText((empty || entry == null) ? "" : "" + entry.getDescription());
            }
        }));
    }

    /**
     * Gives the controller the result query.
     *
     * @param query the result query
     */
    public void giveResultQuery(CriticalAccessQuery query) throws Exception {
        resultQuery = query;

        this.resultTable.setItems(FXCollections.observableList(new ArrayList<>(query.getEntries())));
        this.resultTable.getSortOrder().add(usecaseIdColumn);

    }

    /**
     * Opens a modal edit dialog for the selected AccessPattern.
     *
     * @param accessPattern the selected AccessPattern
     */
    public void viewAccessPatternDetails(AccessPattern accessPattern) {
        try {

            FXMLLoader loader = AppComponents.getInstance().showScene("ui/main/patterns/modal/PatternsFormView.fxml", "patternDetails", 1200, 700);

            // give the dialog the sapConfiguration
            PatternsFormController patternView = loader.getController();
            patternView.giveSelectedAccessPattern(accessPattern);
            patternView.setEditable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the conditionType Column.
     */
    private void initializeConditionTypeColumn() {
        // sets the icon of the condition to pattern or profile
        conditionTypeColumn.setCellFactory(new Callback<TableColumn<CriticalAccessEntry, AccessPattern>, TableCell<CriticalAccessEntry, AccessPattern>>() {
            public TableCell<CriticalAccessEntry, AccessPattern> call(TableColumn<CriticalAccessEntry, AccessPattern> param) {
                TableCell<CriticalAccessEntry, AccessPattern> cell = new TableCell<CriticalAccessEntry, AccessPattern>() {
                    protected void updateItem(AccessPattern item, boolean empty) {

                        // display nothing if the row is empty, otherwise the item count
                        if (empty || item == null) {

                            // nothing to display
                            setText("");
                            setGraphic(null);

                        } else {

                            // add the icon
                            MaterialDesignIconView iconView = new MaterialDesignIconView();
                            iconView.setStyle("-fx-font-size: 1.6em");

                            // wrapper label for showing a tooltip
                            Label wrapper = new Label();
                            wrapper.setGraphic(iconView);

                            if (item.getConditions().stream().findFirst().get().getProfileCondition() == null) {

                                // pattern
                                iconView.setIcon(MaterialDesignIcon.VIEW_GRID);
                                wrapper.setTooltip(new Tooltip(bundle.getString("patternCondition")));

                            } else {

                                // profile
                                iconView.setIcon(MaterialDesignIcon.ACCOUNT_BOX_OUTLINE);
                                wrapper.setTooltip(new Tooltip(bundle.getString("profileCondition")));

                            }

                            setGraphic(wrapper);

                        }
                    }
                };
                return cell;
            }
        });
    }
}
