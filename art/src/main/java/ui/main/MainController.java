package ui.main;

import com.jfoenix.controls.JFXTabPane;

import data.entities.AccessPattern;
import data.entities.DbUser;
import data.entities.DbUserRole;
import data.entities.Whitelist;

import extensions.ResourceBundleHelper;
import io.msoffice.excel.AccessPatternImportHelper;
import io.msoffice.excel.WhitelistImportHelper;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import ui.AppComponents;
import ui.custom.controls.CustomAlert;
import ui.main.admin.AdminController;
import ui.main.configs.ConfigsController;
import ui.main.patterns.PatternsController;
import ui.main.patterns.modal.PatternImportController;
import ui.main.sapqueries.SapQueriesController;
import ui.main.sapsettings.SapSettingsController;
import ui.main.whitelists.WhitelistsController;
import ui.main.whitelists.modal.WhitelistFormController;


public class MainController {

    @FXML
    private JFXTabPane mainTabs;

    @FXML
    private SapQueriesController sapQueriesController;

    @FXML
    private SapSettingsController sapSettingsController;

    @FXML
    private ConfigsController configsController;

    @FXML
    private PatternsController patternsController;

    @FXML
    private WhitelistsController whitelistsController;

    @FXML
    private AdminController usersController;

    @FXML
    private Tab sapQueriesTab;

    @FXML
    private Tab sapSettingsTab;

    @FXML
    private Tab configurationsTab;

    @FXML
    private Tab patternsTab;

    @FXML
    private Tab whitelistsTab;

    @FXML
    private Tab usersTab;


    private ResourceBundle bundle = ResourceBundleHelper.getInstance().getLanguageBundle();

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() throws Exception {

        initializeAccessRestriction();

        initTableUpdates();

        updateAllTables();

        initializeDragAndDrop();
    }

    /**
     * Adds only the tabs that the user roles can access.
     */
    private void initializeAccessRestriction() throws Exception {

        // clear the tabs
        mainTabs.getTabs().clear();

        // get the current user
        DbUser currentUser = AppComponents.getInstance().getDbContext().getCurrentUser();

        // get the user roles
        boolean analyst = currentUser.getRoles().contains(DbUserRole.Configurator);
        boolean viewer = currentUser.getRoles().contains(DbUserRole.Viewer);
        boolean admin = currentUser.getRoles().contains(DbUserRole.Admin);

        // add the tabs according to the role
        if (viewer) {
            mainTabs.getTabs().add(sapQueriesTab);
        }
        if (analyst) {
            mainTabs.getTabs().addAll(sapSettingsTab, configurationsTab, patternsTab, whitelistsTab);
        }
        if (admin) {
            mainTabs.getTabs().add(usersTab);
        }
    }

    /**
     * Update all tables.
     */
    private void updateAllTables() throws Exception {
        sapQueriesController.updateTable();
        sapSettingsController.updateTable();
        configsController.updateTable();
        patternsController.updateTable();
        whitelistsController.updateTable();
        usersController.updateTable();
    }

    /**
     * Initialize table updates on tab change.
     */
    private void initTableUpdates() {

        sapQueriesTab.setOnSelectionChanged((event -> {
            try {
                sapQueriesController.updateTable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        sapSettingsTab.setOnSelectionChanged((event -> {
            try {
                sapSettingsController.updateTable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        configurationsTab.setOnSelectionChanged((event -> {
            try {
                configsController.updateTable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        patternsTab.setOnSelectionChanged((event -> {
            try {
                patternsController.updateTable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        whitelistsTab.setOnSelectionChanged((event -> {
            try {
                whitelistsController.updateTable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        usersTab.setOnSelectionChanged((event -> {
            try {
                usersController.updateTable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    /**
     * Initializes the drag and drop functionality for the MainTabs.
     */
    private void initializeDragAndDrop() {
        mainTabs.setOnDragOver(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != mainTabs
                    && event.getDragboard().hasFiles()
                    && event.getDragboard().getFiles().get(0).getAbsolutePath().endsWith(".xlsx")) {
                    /* allow for both copying and moving, whatever user chooses */
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            }
        });

        // on drag drop, start the import dialogs
        mainTabs.setOnDragDropped(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    File file = db.getFiles().get(0);

                    // file is a file that can be imported
                    if (file.getAbsolutePath().endsWith(".xlsx")) {
                        try {

                            // if importWhitelist throws an error, try importPatterns
                            importWhitelist(file);
                            success = true;
                        } catch (Exception e) {
                            try {
                                importPatterns(file);
                                success = true;
                            } catch (Exception ex) {

                                new CustomAlert(Alert.AlertType.WARNING, bundle.getString("wrongFileTitle"),
                                    bundle.getString("wrongFileMessage")).showAndWait();

                            }
                        }
                    }
                }
                /* let the source know whether the string was successfully
                 * transferred and used */
                event.setDropCompleted(success);

                event.consume();
            }
        });
    }

    /**
     * Import a whitelist from the given file.
     */
    private void importWhitelist(File file) throws Exception {
        Whitelist importedWhitelist = new WhitelistImportHelper().importWhitelist(file.getPath());

        FXMLLoader loader = AppComponents.getInstance().showScene("ui/main/whitelists/modal/WhitelistFormView.fxml", "importWhitelist", 900, 650);

        WhitelistFormController editDialogController = loader.getController();
        editDialogController.giveSelectedWhitelist(importedWhitelist);
        editDialogController.setWhitelistsController(whitelistsController);

        mainTabs.getSelectionModel().select(whitelistsTab);
    }

    /**
     * Imports patterns from the given file.
     */
    private void importPatterns(File file) throws Exception {
        List<AccessPattern> importedPatterns = new AccessPatternImportHelper().importAccessPatterns(file.getAbsolutePath());

        // open a modal import window
        FXMLLoader loader = AppComponents.getInstance().showScene("ui/main/patterns/modal/PatternImportView.fxml", "importPatterns");

        // give the dialog the controller and the patterns
        PatternImportController importController = loader.getController();
        importController.giveImportedPatterns(importedPatterns);
        importController.setPatternsController(patternsController);

        mainTabs.getSelectionModel().select(patternsTab);
    }
}
