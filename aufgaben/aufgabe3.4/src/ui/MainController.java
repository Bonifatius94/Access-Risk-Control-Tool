package ui;

import excel.AuthorizationPattern;
import excel.AuthorizationPatternImportHelper;
import excel.WhitelistImportHelper;
import excel.WhitelistUser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.List;

public class MainController {

    @FXML
    private Parent vwWhitelist;

    @FXML
    private WhitelistEditorController vwWhitelistController;

    @SuppressWarnings("all")
    public void initialize() {

        // nothing to do here ...
    }

    @FXML
    private void importConfig() throws Exception {

        Window parent = vwWhitelist.getScene().getWindow();
        FileChooser dlg = new FileChooser();
        File file = dlg.showOpenDialog(parent);

        if (file != null && file.exists()) {

            // get whitelist data from excel file with helper class
            List<AuthorizationPattern> newPatterns = new AuthorizationPatternImportHelper().importAccessPattern(file.getPath());

            for (AuthorizationPattern pattern : newPatterns) {
                System.out.println(pattern);
            }

            // TODO: implement logic
            //// load config data into table view (list is cleared before adding items)
            //this.users.clear();
            //this.users.addAll(newUsers);
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

}
