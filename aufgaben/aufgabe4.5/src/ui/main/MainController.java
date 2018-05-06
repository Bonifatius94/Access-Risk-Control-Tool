package ui.main;

import excel.config.AuthorizationPattern;
import excel.config.AuthorizationPatternExportHelper;
import excel.config.AuthorizationPatternImportHelper;
import excel.whitelist.WhitelistExportHelper;
import excel.whitelist.WhitelistImportHelper;
import excel.whitelist.WhitelistEntry;
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

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

/**
 * This class contains the user interface business logic for the main view
 * that is bound to an instance of this class.
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
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

    /**
     * This method is automatically called when the according view is loaded. It initializes the logic behind the controls:
     * <ul>
     *     <li>an observable lists is attached to each table view / choice box</li>
     *     <li>a string property is attached to each text field / text area</li>
     *     <li>an event listener is attached to each event</li>
     * </ul>
     *
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @SuppressWarnings("all")
    public void initialize() {

        // TODO: lock user interface and show login dialog at startup
    }

    // =============================================
    //               import logic
    // =============================================

    /**
     * This method handles the context menu clicked event of import config option in the file menu.
     * It first shows an open file dialog and then imports the data from the selected file.
     *
     * @throws Exception caused by an error during MS Excel import
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @FXML
    private void importConfig() throws Exception {

        Window parent = vwConfigs.getScene().getWindow();
        FileChooser dlg = new FileChooser();
        File file = dlg.showOpenDialog(parent);

        if (file != null && file.exists()) {

            // get whitelist data from excel file with helper class
            List<AuthorizationPattern> newPatterns = new AuthorizationPatternImportHelper().importAuthorizationPattern(file.getPath());

            // TODO: save config in local database

            // load config data into config editor view (list is cleared before adding items)
            vwConfigsController.loadConfig(newPatterns);
        }
    }

    /**
     * This method handles the context menu clicked event of import whitelist option in the file menu.
     * It first shows an open file dialog and then imports the data from the selected file.
     *
     * @throws Exception caused by an error during MS Excel import
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @FXML
    private void importWhitelist() throws Exception {

        Window parent = vwWhitelist.getScene().getWindow();
        FileChooser dlg = new FileChooser();
        File file = dlg.showOpenDialog(parent);

        if (file != null && file.exists()) {

            // get whitelist data from excel file with helper class
            List<WhitelistEntry> newUsers = new WhitelistImportHelper().importWhitelist(file.getPath());

            // TODO: save whitelist in local database

            // load whitelist data into whitelist editor view (list is cleared before adding items)
            vwWhitelistController.setUsers(newUsers);
        }
    }

    // =============================================
    //               export logic
    // =============================================

    /**
     * This method handles the context menu clicked event of export config option in the file menu.
     * It first shows a save file dialog and then exports the data to the selected file.
     * Asking whether the file should be overwritten is automatically handled by save file dialog.
     *
     * @throws Exception caused by an error during MS Excel export
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @FXML
    private void exportConfig() throws Exception {

        Window parent = vwWhitelist.getScene().getWindow();
        FileChooser dlg = new FileChooser();
        File file = dlg.showSaveDialog(parent);

        if (file != null) {

            // export currently selected config
            List<AuthorizationPattern> config = vwConfigsController.getConfig();
            new AuthorizationPatternExportHelper().exportAuthorizationPattern(file.getPath(), config);

            // open exported file with standard program
            Desktop.getDesktop().open(file);
        }
    }

    /**
     * This method handles the context menu clicked event of export whitelist option in the file menu.
     * It first shows a save file dialog and then exports the data to the selected file.
     * Asking whether the file should be overwritten is automatically handled by save file dialog.
     *
     * @throws Exception caused by an error during MS Excel export
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @FXML
    private void exportWhitelist() throws Exception {

        Window parent = vwWhitelist.getScene().getWindow();
        FileChooser dlg = new FileChooser();
        File file = dlg.showSaveDialog(parent);

        if (file != null) {

            // export currently selected whitelist
            List<WhitelistEntry> whitelist = vwWhitelistController.getWhitelist();
            new WhitelistExportHelper().exportWhitelist(file.getPath(), whitelist);

            // open exported file with standard program
            Desktop.getDesktop().open(file);
        }
    }

    /**
     * This method handles the context menu clicked event of export whitelist option in the file menu.
     * It first shows a save file dialog and then exports the data to the selected file.
     * Asking whether the file should be overwritten is automatically handled by save file dialog.
     * The user can also select the output file type in save file dialog.
     *
     * @throws Exception caused by an error during export
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @FXML
    private void exportResults() throws Exception {

        Window parent = vwWhitelist.getScene().getWindow();
        FileChooser dlg = new FileChooser();
        File file = dlg.showSaveDialog(parent);

        // TODO: add file extensions *.docx, *.pdf, *.csv to filter

        if (file != null) {

            // TODO: recognize which file type should be exported
            // TODO: implement export logic
        }
    }

    // =============================================
    //                 settings
    // =============================================

    /**
     * This method handles the context menu clicked event of the sap settings option in the settings menu.
     * It shows the sap connections dialog as a modal dialog.
     *
     * @throws Exception caused by sap connections view resource file not found
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @FXML
    private void showSapConnectionsDlg(/*ActionEvent e*/) throws Exception {

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

    /**
     * This method handles the context menu clicked event of the user settings option in the settings menu.
     * It shows the user settings dialog as a modal dialog.
     *
     * @throws Exception caused by user settings view resource file not found
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @FXML
    private void showUserSettingsDlg() throws Exception {

        // TODO: implemnt logic
    }

    // =============================================
    //                   about
    // =============================================

    /**
     * This method handles the context menu clicked event of the about menu.
     * It shows the about dialog as a modal dialog.
     *
     * @throws Exception caused by about view resource file not found
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @FXML
    private void showAboutDlg() throws Exception {

        // TODO: implemnt logic
    }

}
