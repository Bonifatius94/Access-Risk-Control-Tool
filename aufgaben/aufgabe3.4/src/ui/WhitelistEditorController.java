package ui;

import excel.WhitelistUser;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
    private TextField tfUsecaseID;
    private StringProperty usecaseID = new SimpleStringProperty("");

    @FXML
    private TableView tblWhitelist;

    private final ObservableList<WhitelistUser> users = FXCollections.observableArrayList();

    @SuppressWarnings("all")
    public void initialize() {

        // bind text property
        tfUsername.textProperty().bindBidirectional(username);
        tfUsecaseID.textProperty().bindBidirectional(usecaseID);

        // init table view items
        tblWhitelist.setItems(users);

        // bind selection changed listener
        tblWhitelist.getSelectionModel().selectedItemProperty().addListener(this::onSelectionChanged);
    }

    public void setUsers(List<WhitelistUser> newUsers) {

        // remove all objects from list and init list with new objects
        users.clear();
        users.addAll(newUsers);

        // TODO: add sort logic and other list preparations
    }

    private void onSelectionChanged(/*ObservableValue<Object> observableValue, Object oldValue,*/ Object newValue) {

        if (newValue instanceof WhitelistUser) {

            // apply values of selected record to detail view
            WhitelistUser selectedUser = (WhitelistUser) newValue;
            username.setValue(selectedUser.getUsername());
            usecaseID.setValue(selectedUser.getUsecaseID());
        }
    }

}
