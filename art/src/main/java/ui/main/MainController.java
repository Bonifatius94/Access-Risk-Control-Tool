package ui.main;

import com.jfoenix.controls.JFXTabPane;
import javafx.fxml.FXML;
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
    private AdminController adminController;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() throws Exception {

        updateAllTables();

        // update the tables on tab change
        mainTabs.getSelectionModel().selectedItemProperty().addListener((ol, oldValue, newValue) -> {
            try {
                updateAllTables();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Update all tables (maybe find a more efficient way).
     */
    private void updateAllTables() throws Exception {
        sapQueriesController.updateTable();
        sapSettingsController.updateTable();
        configsController.updateTable();
        patternsController.updateTable();
        whitelistsController.updateTable();
    }
}
