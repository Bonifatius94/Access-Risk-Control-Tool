package ui.main.sapqueries.modal.results;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;

import com.jfoenix.controls.JFXTabPane;
import data.entities.AccessPattern;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.stage.FileChooser;

import settings.UserSettings;

import ui.App;
import ui.AppComponents;
import ui.main.sapqueries.modal.results.views.AnalysisHistoryController;
import ui.main.sapqueries.modal.results.views.AnalysisTableController;


public class AnalysisResultController {

    @FXML
    private MaterialDesignIconView statusIcon;

    @FXML
    private Label criticalAccessCount;

    @FXML
    private JFXComboBox<ReportExportType> exportFormatChooser;

    @FXML
    private JFXComboBox<Locale> exportLanguageChooser;

    @FXML
    private JFXCheckBox includePatternsCheckbox;

    @FXML
    private JFXCheckBox includeWhitelistCheckbox;

    @FXML
    private AnalysisTableController analysisTableController;

    @FXML
    private AnalysisHistoryController analysisHistoryController;

    @FXML
    private JFXTabPane resultTabs;

    @FXML
    private Tab tableTab;

    @FXML
    private Tab historyTab;


    private CriticalAccessQuery resultQuery;
    private ResourceBundle bundle;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        bundle = ResourceBundleHelper.getInstance().getLanguageBundle();

        // init export format chooser
        exportFormatChooser.setItems(FXCollections.observableList(Arrays.asList(ReportExportType.Word, ReportExportType.Pdf, ReportExportType.Csv)));
        exportFormatChooser.getSelectionModel().select(ReportExportType.Word);

        // init export languages chooser
        exportLanguageChooser.setItems(FXCollections.observableList(UserSettings.getAvailableLocales()));
        exportLanguageChooser.getSelectionModel().select(Locale.GERMAN);
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

        // whitelist is null, don't allow export
        if (query.getConfig().getWhitelist() == null) {
            includeWhitelistCheckbox.setDisable(true);
            includeWhitelistCheckbox.setSelected(false);
        }

        // get the related queries
        List<CriticalAccessQuery> relatedQueries = AppComponents.getInstance().getDbContext().getRelatedSapQueries(query, false);

        // not enough entries for a history
        if (relatedQueries.size() < 2) {
            resultTabs.getTabs().remove(historyTab);
        } else {

            // give the data to the historyController
            analysisHistoryController.giveData(query);
        }

        // give the query to the tableController
        analysisTableController.giveResultQuery(query);
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
