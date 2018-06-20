package ui.main.admin;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import data.entities.DbUser;
import data.entities.DbUserRole;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;


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
    private Set<DbUserRole> dbUserRolesSet = new HashSet<>();
    private Set<DbUserRole> dbUserRolesSet2 = new HashSet<>();

    /**
     * Function is called by a Checkbox event from the admin checkbox,
     * and adds or deletes the admin role from the DbUserRole set.
     */
    @FXML
    public void adminSelected() {
        if (adminCheckbox.isSelected()) {
            if (!dbUserRolesSet.contains(DbUserRole.Admin)) {
                dbUserRolesSet.add(DbUserRole.Admin);
            }

        } else if (!adminCheckbox.isSelected()) {
            if (dbUserRolesSet.contains(DbUserRole.Admin)) {
                dbUserRolesSet.remove(DbUserRole.Admin);
            }
        }
    }

    /**
     * Function is called by a Checkbox event from the viewer checkbox,
     * and adds or deletes the viewer role from the DbUserRole set.
     */
    @FXML
    public void viewerSelected() {
        if (viewerCheckbox.isSelected()) {
            if (!dbUserRolesSet.contains(DbUserRole.Viewer)) {
                dbUserRolesSet.add(DbUserRole.Viewer);
            }

        } else if (!viewerCheckbox.isSelected()) {
            if (dbUserRolesSet.contains(DbUserRole.Viewer)) {
                dbUserRolesSet.remove(DbUserRole.Viewer);
            }
        }
    }

    /**
     * Function is called by a Checkbox event from the dataAnalyst checkbox,
     * and adds or deletes the dataAnalyst role from the DbUserRole set.
     */
    @FXML
    public void dataAnalystSelected() {
        if (dataAnalystCheckbox.isSelected()) {
            if (!dbUserRolesSet.contains(DbUserRole.DataAnalyst)) {
                dbUserRolesSet.add(DbUserRole.DataAnalyst);
            }

        } else if (!dataAnalystCheckbox.isSelected()) {
            if (dbUserRolesSet.contains(DbUserRole.DataAnalyst)) {
                dbUserRolesSet.remove(DbUserRole.DataAnalyst);
            }
        }
    }

    /**
     * adds new User with a initial password , that password should be changed by this user.
     * TODO: Implementations
     */
    public void addNewUser() {
    }

    /**
     * Save user Changes to the table and the db.
     * TODO: Implementations
     */
    public void saveUserChanges() {
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

        userTable.setItems(list);
        userTable.refresh();
        //userTable.
        initializeColumns();
        initializeCheckboxes();
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


    }
}
