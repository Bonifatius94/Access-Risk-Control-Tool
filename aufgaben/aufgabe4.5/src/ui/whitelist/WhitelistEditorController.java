package ui.whitelist;

import excel.whitelist.WhitelistEntry;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains the user interface business logic for the whitelist editor view
 * that is bound to an instance of this class.
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
public class WhitelistEditorController {

    @FXML
    private TextField tfUsername;
    private StringProperty username = new SimpleStringProperty("");

    @FXML
    private TextField tfUsecaseID;
    private StringProperty usecaseID = new SimpleStringProperty("");

    @FXML
    private TableView tblWhitelist;
    private final ObservableList<WhitelistEntry> users = FXCollections.observableArrayList();

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

        // bind text property
        tfUsername.textProperty().bindBidirectional(username);
        tfUsecaseID.textProperty().bindBidirectional(usecaseID);

        // init table view items
        tblWhitelist.setItems(users);

        // bind selection changed listener
        tblWhitelist.getSelectionModel().selectedItemProperty().addListener(this::onSelectionChanged);
    }

    /**
     * This method is used to load a list of whitelist users into this view (e.g. by MS Excel import).
     *
     * @param newUsers A list of whitelist users that are shown in the table view
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public void setUsers(List<WhitelistEntry> newUsers) {

        // remove all objects from list and init list with new objects
        users.clear();
        users.addAll(newUsers);

        // TODO: add sort logic and other list preparations
    }

    public List<WhitelistEntry> getWhitelist() {

        // pass a new list instance because original list is observable
        return new ArrayList<>(users);
    }

    /**
     * This method handles the selection changed event of the table view that shows the whitelist users.
     * When the selection is not null and of the whitelist user type, then the data of that record
     * is loaded into the detail view (text fields) above.
     *
     * @param newValue The new selected object (automatically passed by selection model of table view)
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    private void onSelectionChanged(/*ObservableValue<Object> observableValue, Object oldValue,*/ Object newValue) {

        // check if the selected object is not null and of the whitelist user type
        if (newValue instanceof WhitelistEntry) {

            // TODO: implement logic that warns the user when changes are unsaved and the selection is changed.

            // apply values of selected record to detail view
            WhitelistEntry selectedUser = (WhitelistEntry) newValue;
            username.setValue(selectedUser.getUsername());
            usecaseID.setValue(selectedUser.getUsecaseID());
        }
    }

}
