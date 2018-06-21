package ui.main.admin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import data.entities.DbUser;
import data.entities.DbUserRole;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

import ui.custom.controls.ButtonCell;
import ui.custom.controls.PTableColumn;


public class AdminController {
    @FXML
    public TableView<DbUser> userTable;
    @FXML
    public JFXTextField tfDbUserName;
    @FXML
    public JFXPasswordField tfDbUserPassword;
    @FXML
    public JFXCheckBox adminCheckbox;
    @FXML
    public JFXCheckBox viewerCheckbox;
    @FXML
    public JFXCheckBox dataAnalystCheckbox;
    @FXML
    public PTableColumn<DbUser, JFXButton> deleteColumn;
    @FXML
    public PTableColumn<DbUser, String> passwordColumn;
    @FXML
    private Set<DbUserRole> dbUserRolesSet = new HashSet<>();
    @FXML
    private Set<DbUserRole> dbUserRolesSet2 = new HashSet<>();

    /**
     * Function is called by a Checkbox event from the admin checkbox,
     * and adds or deletes the admin role from the DbUserRole set.
     */
    @FXML
    public void adminSelected() {
        DbUser user = userTable.getSelectionModel().getSelectedItem();
        if (adminCheckbox.isSelected()) {
            user.addRole(DbUserRole.Admin);

        } else if (!adminCheckbox.isSelected()) {
            user.removeRole(DbUserRole.Admin);
        }
    }

    /**
     * Function is called by a Checkbox event from the viewer checkbox,
     * and adds or deletes the viewer role from the DbUserRole set.
     */
    @FXML
    public void viewerSelected() {
        DbUser user = userTable.getSelectionModel().getSelectedItem();
        if (viewerCheckbox.isSelected()) {
            user.addRole(DbUserRole.Viewer);

        } else if (!viewerCheckbox.isSelected()) {
            user.removeRole(DbUserRole.Viewer);
        }
    }

    /**
     * Function is called by a Checkbox event from the dataAnalyst checkbox,
     * and adds or deletes the dataAnalyst role from the DbUserRole set.
     */
    @FXML
    public void dataAnalystSelected() {
        DbUser user = userTable.getSelectionModel().getSelectedItem();
        if (dataAnalystCheckbox.isSelected()) {
            user.addRole(DbUserRole.DataAnalyst);

        } else if (!dataAnalystCheckbox.isSelected()) {
            user.removeRole(DbUserRole.DataAnalyst);
        }
    }

    /**
     * adds new User with a initial password , that password should be changed by this user.
     * TODO: further Implementations
     */
    public void addNewUser() {
        initializeCheckboxes();
        //Set<DbUserRole> userRoles
        //DbUser newDbUser = new DbUser(tfDbUserName.getText(), )
        userTable.getItems().add(new DbUser("", new HashSet<>()));

    }

    /**
     * Save user Changes to the table and the db.
     */
    public void saveUserChanges() {

        userTable.getSelectionModel().getSelectedItem().setUsername(tfDbUserName.getText());
        userTable.refresh();

    }

    /**
     * is called by the FXMl and intializes all parts of the window.
     * TODO: futher implementations
     */
    public void initialize() {
        //TODO: laden der nutzer aus der Datenbank

        dbUserRolesSet.add(DbUserRole.Admin);
        dbUserRolesSet.add(DbUserRole.Viewer);
        dbUserRolesSet2.add(DbUserRole.DataAnalyst);

        //Testcode
        DbUser dbUser1 = new DbUser("Peter", dbUserRolesSet);
        DbUser dbUser2 = new DbUser("Hans", dbUserRolesSet2);
        List<DbUser> dbUsersSet = new ArrayList<DbUser>();
        dbUsersSet.add(dbUser1);
        dbUsersSet.add(dbUser2);

        ObservableList<DbUser> list = FXCollections.observableList(dbUsersSet);
        userTable.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                editUser(newValue);
            }
        }));
        userTable.setItems(list);
        dbUserRolesSet.clear();
        userTable.refresh();
        //userTable.
        initializeColumns();
        initializeCheckboxes();
    }

    private void editUser(DbUser newValue) {
        tfDbUserName.setText(newValue.getUsername());
        tfDbUserPassword.setDisable(true);
        adminCheckbox.setSelected(newValue.getRoles().contains(DbUserRole.Admin));
        viewerCheckbox.setSelected(newValue.getRoles().contains(DbUserRole.Viewer));
        dataAnalystCheckbox.setSelected(newValue.getRoles().contains(DbUserRole.DataAnalyst));
    }

    /**
     * Intializes the checkboxes.
     */
    private void initializeCheckboxes() {
        viewerCheckbox.setSelected(false);
        adminCheckbox.setSelected(false);
        dataAnalystCheckbox.setSelected(false);
    }

    /**
     * Intiliazes the Columns.
     */
    private void initializeColumns() {
        //TODO: initialPassword column initialization
        deleteColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.DELETE, (DbUser user) -> {
            if (userTable.getSelectionModel().getSelectedItem() == user) {
                userTable.getItems().remove(user);
                initializeCheckboxes();
            } else {
                userTable.getItems().remove(user);
            }

            return user;
        }));

    }
}
