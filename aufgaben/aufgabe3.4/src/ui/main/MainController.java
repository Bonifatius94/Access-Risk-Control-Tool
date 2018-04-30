package ui.main;

import excel.config.AuthorizationPattern;
import excel.config.AuthorizationPatternImportHelper;
import excel.whitelist.WhitelistImportHelper;
import excel.whitelist.WhitelistUser;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import ui.whitelist.WhitelistEditorController;
import ui.config.ConfigEditorController;

import java.io.File;
import java.util.List;

public class MainController {

    @FXML
    private Parent vwWhitelist;

    @FXML
    private Parent vwConfigs;

    @FXML
    private Parent vwConditions;

    @SuppressWarnings("unused")
    @FXML
    private WhitelistEditorController vwWhitelistController;

    @SuppressWarnings("unused")
    @FXML
    private ConfigEditorController vwConfigsController;

//    @SuppressWarnings("unused")
//    @FXML
//    private ConfigEditorController vwConditionsController;

    @SuppressWarnings("all")
    public void initialize() {

        // TODO: lock user interface and show login dialog
    }

    // =============================================
    //               import logic
    // =============================================

    @FXML
    private void importConfig() throws Exception {

        Window parent = vwConfigs.getScene().getWindow();
        FileChooser dlg = new FileChooser();
        File file = dlg.showOpenDialog(parent);

        if (file != null && file.exists()) {

            // get whitelist data from excel file with helper class
            List<AuthorizationPattern> newPatterns = new AuthorizationPatternImportHelper().importAccessPattern(file.getPath());

            // load config data into config editor view (list is cleared before adding items)
            vwConfigsController.loadConfig(newPatterns);
        }
    }

    @FXML
    private void importWhitelist() throws Exception {

        Window parent = vwWhitelist.getScene().getWindow();
        FileChooser dlg = new FileChooser();
        File file = dlg.showOpenDialog(parent);

        if (file != null && file.exists()) {

            // get whitelist data from excel file with helper class
            List<WhitelistUser> newUsers = new WhitelistImportHelper().importWhitelist(file.getPath());

            // load whitelist data into whitelist editor view (list is cleared before adding items)
            vwWhitelistController.setUsers(newUsers);
        }
    }

    // =============================================
    //               export logic
    // =============================================

    @FXML
    private void exportConfig() {

        // TODO: implemnt logic
    }

    @FXML
    private void exportWhitelist() {

        // TODO: implemnt logic
    }

    @FXML
    private void exportResults() {

        // TODO: implemnt logic
    }

    // =============================================
    //                 settings
    // =============================================

    @FXML
    private void showSapConnectionsDlg() {

        // TODO: implemnt logic
    }

    @FXML
    private void showUserSettingsDlg() {

        // TODO: implemnt logic
    }

    // =============================================
    //                   about
    // =============================================

    @FXML
    private void showAboutDlg() {

        // TODO: implemnt logic
    }

}
