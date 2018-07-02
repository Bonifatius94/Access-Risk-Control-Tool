package ui.main;

import com.jfoenix.controls.JFXTabPane;
import data.entities.DbUser;
import data.entities.DbUserRole;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import ui.AppComponents;
import ui.main.admin.AdminController;
import ui.main.configs.ConfigsController;
import ui.main.patterns.PatternsController;
import ui.main.sapqueries.SapQueriesController;
import ui.main.sapsettings.SapSettingsController;
import ui.main.whitelists.WhitelistsController;

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


    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() throws Exception {

        initializeAccessRestriction();

        initTableUpdates();

        updateAllTables();
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
        boolean analyst = currentUser.getRoles().contains(DbUserRole.DataAnalyst);
        boolean viewer = currentUser.getRoles().contains(DbUserRole.Viewer);
        boolean admin = currentUser.getRoles().contains(DbUserRole.Admin);

        // add the tabs according to the role
        if (analyst || viewer) {
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
}
