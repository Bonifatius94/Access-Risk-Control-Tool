package ui.main.sapqueries.modal.newquery;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;

import data.entities.AccessPattern;
import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

import extensions.ResourceBundleHelper;

import io.msoffice.ExportHelper;
import io.msoffice.ReportExportType;
import io.msoffice.excel.AccessPatternExportHelper;
import io.msoffice.excel.WhitelistExportHelper;

import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import settings.UserSettings;

import ui.App;
import ui.AppComponents;
import ui.custom.controls.ButtonCell;
import ui.main.patterns.modal.PatternsFormController;


public class AnalysisResultController {

    @FXML
    private MaterialDesignIconView statusIcon;

    @FXML
    private Label criticalAccessCount;

    @FXML
    private TableView<CriticalAccessEntry> resultTable;

    @FXML
    private JFXComboBox<ReportExportType> exportFormatChooser;

    @FXML
    private JFXComboBox<Locale> exportLanguageChooser;

    @FXML
    public TableColumn<CriticalAccessEntry, JFXButton> viewPatternDetailsColumn;

    @FXML
    public TableColumn<CriticalAccessEntry, AccessPattern> conditionTypeColumn;

    @FXML
    public TableColumn<CriticalAccessEntry, AccessPattern> usecaseIdColumn;

    @FXML
    public TableColumn<CriticalAccessEntry, AccessPattern> descriptionColumn;

    @FXML
    public JFXCheckBox includePatternsCheckbox;

    @FXML
    public JFXCheckBox includeWhitelistCheckbox;


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

        initializeConditionTypeColumn();
        initializeUsecaseIdColumn();
        initializeDescriptionColumn();

        // init export format chooser
        exportFormatChooser.setItems(FXCollections.observableList(Arrays.asList(ReportExportType.Word, ReportExportType.Pdf, ReportExportType.Csv)));
        exportFormatChooser.getSelectionModel().select(ReportExportType.Word);

        // init export languages chooser
        exportLanguageChooser.setItems(FXCollections.observableList(UserSettings.getAvailableLocales()));
        exportLanguageChooser.getSelectionModel().select(Locale.GERMAN);
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
     * Exports the query results with the given format / language.
     */
    public void exportQuery() throws Exception {

        ReportExportType exportType = exportFormatChooser.getSelectionModel().getSelectedItem();
        Locale exportLanguage = exportLanguageChooser.getSelectionModel().getSelectedItem();

        String extension;
        String description;

        switch (exportType) {
            case Csv:
                extension = "*.csv";
                description = "CSV files (*.csv)";
                break;
            case Word:
                extension = "*.docx";
                description = "MS Word files (*.docx)";
                break;
            case Pdf:
                extension = "*.pdf";
                description = "PDF files (*.pdf)";
                break;
            default:
                throw new IllegalArgumentException("unknown export type");
        }

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(description, extension);
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showSaveDialog(App.primaryStage);

        if (file != null) {

            // export results
            exportAndOpenResults(file, exportType, exportLanguage);

            if (includePatternsCheckbox.isSelected()) {
                exportPatterns(file);
            }
            if (includeWhitelistCheckbox.isSelected()) {
                exportWhitelist(file);
            }
        }
    }

    /**
     * Exports the query results with the given format / language.
     */
    public void exportAndOpenResults(File file, ReportExportType exportType, Locale exportLanguage) throws Exception {

        // export the query results to the chosen output file
        new ExportHelper().exportDocument(resultQuery, file, exportType, exportLanguage);

        // open exported file with a suitable program
        if (file.exists() && Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(file);
        }
    }

    /**
     * Exports the whitelist used for the analysis.
     */
    public void exportWhitelist(File file) throws Exception {

        String path = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf('.')) + " - whitelist.xlsx";

        // export the whitelist to the chosen output file
        new WhitelistExportHelper().exportWhitelist(path, resultQuery.getConfig().getWhitelist());

        // open exported file with a suitable program
        /*
        if (file.exists() && Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(file);
        }
        */
    }

    /**
     * Exports the access patterns used for the analysis.
     */
    public void exportPatterns(File file) throws Exception {

        String path = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf('.')) + " - patterns.xlsx";

        // order patterns by usecase id
        List<AccessPattern> patterns =
            resultQuery.getConfig().getPatterns().stream()
                .sorted(Comparator.comparing(AccessPattern::getUsecaseId)).collect(Collectors.toList());

        // export the patterns to the chosen output file
        new AccessPatternExportHelper().exportAccessPatterns(path, patterns);

        // open exported file with a suitable program
        /*
        if (file.exists() && Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(file);
        }
        */
    }

    /**
     * Gives the controller the result query.
     *
     * @param query the result query
     */
    public void giveResultQuery(CriticalAccessQuery query) throws Exception {
        resultQuery = query;

        // set the label
        if (query.getEntries() != null && query.getEntries().size() != 0) {
            criticalAccessCount.setText(bundle.getString("criticalAccessCount") + " " + query.getEntries().size());
            statusIcon.setIcon(MaterialDesignIcon.CLOSE);
            statusIcon.setStyle("-fx-fill: -fx-error");
        } else {
            criticalAccessCount.setText(bundle.getString("noCriticalAccess"));
            statusIcon.setIcon(MaterialDesignIcon.CHECK);
            statusIcon.setStyle("-fx-fill: -fx-success");
        }

        this.resultTable.setItems(FXCollections.observableList(new ArrayList<>(query.getEntries())));
        this.resultTable.getSortOrder().add(usecaseIdColumn);

        // whitelist is null, don't allow export
        if (query.getConfig().getWhitelist() == null) {
            includeWhitelistCheckbox.setDisable(true);
            includeWhitelistCheckbox.setSelected(false);
        }
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

    /**
     * Hides the stage.
     *
     * @param event the given ActionEvent
     */
    public void close(ActionEvent event) {
        (((Button) event.getSource()).getScene().getWindow()).hide();
    }
}
