package ui;

import excel.WhitelistImportHelper;
import excel.WhitelistUser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.List;

public class MainController {

    @FXML
    private TableView tblWhitelist;

    private final ObservableList<WhitelistUser> users = FXCollections.observableArrayList();

    @SuppressWarnings("all")
    public void initialize() {

        tblWhitelist.setItems(users);
    }

    @FXML
    private void importWhitelist() throws Exception {

        Window parent = tblWhitelist.getScene().getWindow();
        FileChooser dlg = new FileChooser();
        File file = dlg.showOpenDialog(parent);

        if (file.exists()) {

            // get whitelist data from excel file with helper class
            List<WhitelistUser> newUsers = new WhitelistImportHelper().importWhitelist(file.getPath());

            // load whitelist data into table view (list is cleared before adding items)
            this.users.clear();
            this.users.addAll(newUsers);
        }
    }

}
