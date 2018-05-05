package ui.main;

import excel.config.AuthorizationPattern;
import excel.config.AuthorizationPatternImportHelper;
import excel.whitelist.WhitelistImportHelper;
import excel.whitelist.WhitelistUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import ui.whitelist.WhitelistEditorController;
import ui.config.ConfigEditorController;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

public class MainController {

    @FXML
    private VBox vwMain;

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
            List<AuthorizationPattern> newPatterns = new AuthorizationPatternImportHelper().importAuthorizationPattern(file.getPath());

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
    private void exportConfig() throws Exception {

        Window parent = vwWhitelist.getScene().getWindow();
        FileChooser dlg = new FileChooser();
        File file = dlg.showSaveDialog(parent);

        if (file != null && file.exists()) {

            // TODO: implemnt logic
        }
    }

    @FXML
    private void exportWhitelist() throws Exception {

        Window parent = vwWhitelist.getScene().getWindow();
        FileChooser dlg = new FileChooser();
        File file = dlg.showSaveDialog(parent);

        if (file != null && file.exists()) {

            // TODO: implemnt logic
        }
    }

    @FXML
    private void exportResults() throws Exception {

        Window parent = vwWhitelist.getScene().getWindow();
        FileChooser dlg = new FileChooser();
        File file = dlg.showSaveDialog(parent);

        if (file != null && file.exists()) {

            // TODO: implemnt logic
        }
    }

    // =============================================
    //                 settings
    // =============================================

    @FXML
    private void showSapConnectionsDlg(ActionEvent e) throws Exception {

        // load view and init controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../sapconn/SapConnectionsView.fxml"), ResourceBundle.getBundle("lang"));
        Parent root = loader.load();
        //SapConnectionsController controller = loader.getController();

        // init stage
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("SAP Connections");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(vwMain.getScene().getWindow());

        // show view as modal dialog and wait for exit
        stage.showAndWait();
    }

    @FXML
    private void showUserSettingsDlg() throws Exception {

        // TODO: implemnt logic
    }

    // =============================================
    //                   about
    // =============================================

    @FXML
    private void showAboutDlg() throws Exception {

        // TODO: implemnt logic
    }

}
