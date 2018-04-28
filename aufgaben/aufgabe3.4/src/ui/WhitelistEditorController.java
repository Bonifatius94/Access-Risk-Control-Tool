package ui;

import excel.WhitelistUser;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

public class WhitelistEditorController {

    @FXML
    private TextField tfUsername;
    private StringProperty username = new SimpleStringProperty("");

    @FXML
    private TextField tfUserID;
    private StringProperty userID = new SimpleStringProperty("");

    @FXML
    private TableView tblWhitelist;

    private final ObservableList<WhitelistUser> users = FXCollections.observableArrayList();

    @SuppressWarnings("all")
    public void initialize() {

        // bind text property
        tfUsername.textProperty().bindBidirectional(username);
        tfUserID.textProperty().bindBidirectional(userID);

        // init table view items
        tblWhitelist.setItems(users);

        // bind selection changed listener
        tblWhitelist.getSelectionModel().selectedItemProperty().addListener(this::onSelectionChanged);
    }

    public void setUsers(List<WhitelistUser> newUsers) {
        users.clear();
        users.addAll(newUsers);
    }

    private void onSelectionChanged(ObservableValue<Object> observableValue, Object oldValue, Object newValue) {

        if (newValue instanceof WhitelistUser) {

            // apply values of selected record to detail view
            WhitelistUser selectedUser = (WhitelistUser) newValue;
            username.setValue(selectedUser.getUsername());
            userID.setValue(selectedUser.getUserID());
        }
    }

}
