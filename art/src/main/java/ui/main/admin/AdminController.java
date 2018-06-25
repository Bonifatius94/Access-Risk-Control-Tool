package ui.main.admin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import data.entities.DbUser;
import data.entities.DbUserRole;
import data.localdb.ArtDbContext;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;

import ui.AppComponents;
import ui.custom.controls.ButtonCell;
import ui.custom.controls.CustomAlert;
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

    ArtDbContext database = AppComponents.getDbContext();

    private DbUser editDbUser;

    private DbUser oldDbUser;

    private Set<DbUserRole> dbUserRoleSet = new HashSet<>();

    /**
     * is called by the FXMl and initializes all parts of the window.
     */
    public void initialize() {

        initializeColumns();
        initializeCheckboxes();
        editDbUser = new DbUser("", dbUserRoleSet);

        userTable.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                editUser(newValue);
            }
        }));
        tableRefresh();

    }

    /**
     * Initializes the checkboxes, by setting selected on false.
     */
    private void initializeCheckboxes() {
        viewerCheckbox.setSelected(false);
        adminCheckbox.setSelected(false);
        dataAnalystCheckbox.setSelected(false);
    }

    /**
     * Initializes the Columns with delete Buttons.
     */
    private void initializeColumns() {
        deleteColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.DELETE, (DbUser user) -> {
            try {
                //check if the currently active user, is going to be deleted
                if (user.getUsername().equals(database.getCurrentUser().getUsername().toUpperCase())) {
                    CustomAlert customAlert = new CustomAlert(Alert.AlertType.WARNING, "You can not delete yourself", "It is not possible to delete the currently active USer", "Ok", "Ok");
                    customAlert.showAndWait();
                } else {
                    //Confirmation dialog if the admin really wants to delete the selected user
                    CustomAlert customAlert = new CustomAlert(Alert.AlertType.CONFIRMATION, "Do you really want to delete user: " + user.getUsername() + " ?",
                        "By clicking Ok the user will be deleted, if you changed your mind or missclicked press cancel", "Ok", "Cancel");
                    String buttonText = customAlert.showAndWait().get().getText();
                    if (buttonText.equals("Ok")) {
                        if (database.getDatabaseUsers().contains(user)) {
                            database.deleteDatabaseUser(user.getUsername());
                            tableRefresh();
                        } else {
                            //if the user was not saved (in database) due to an eventual error
                            userTable.getItems().remove(user);
                        }
                    }
                }
                initializeCheckboxes();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return user;
        }));

    }

    /**
     * Function is called by a Checkbox event from the admin checkbox,
     * and adds or deletes the admin role from the DbUserRole set.
     */
    @FXML
    public void adminSelected() {
        if (adminCheckbox.isSelected()) {
            this.editDbUser.addRole(DbUserRole.Admin);

        } else if (!adminCheckbox.isSelected()) {
            this.editDbUser.removeRole(DbUserRole.Admin);
        }
    }

    /**
     * Function is called by a Checkbox event from the viewer checkbox,
     * and adds or deletes the viewer role from the DbUserRole set.
     */
    @FXML
    public void viewerSelected() {
        if (viewerCheckbox.isSelected()) {
            this.editDbUser.addRole(DbUserRole.Viewer);
        } else if (!viewerCheckbox.isSelected()) {
            this.editDbUser.removeRole(DbUserRole.Viewer);
        }

    }

    /**
     * Function is called by a Checkbox event from the dataAnalyst checkbox,
     * and adds or deletes the dataAnalyst role from the DbUserRole set(from the currently edited user).
     */
    @FXML
    public void dataAnalystSelected() {

        if (dataAnalystCheckbox.isSelected()) {
            this.editDbUser.addRole(DbUserRole.DataAnalyst);

        } else if (!dataAnalystCheckbox.isSelected()) {
            this.editDbUser.removeRole(DbUserRole.DataAnalyst);
        }

    }

    /**
     * adds new User with a initial password , that password should be changed by this user.
     */
    public void addNewUser() {
        initializeCheckboxes();
        DbUser newDbUser = new DbUser("", new HashSet<>());
        tfDbUserPassword.clear();
        userTable.getItems().add(newDbUser);
        userTable.refresh();
        userTable.getSelectionModel().select(newDbUser);

    }

    /**
     * Save user Changes to the table and the db.
     */
    public void saveUserChanges() {
        if (!tfDbUserName.getText().equals("") || !tfDbUserPassword.getText().equals("")) {
            try {
                //check if the user already exists
                Boolean userExists = false;
                this.editDbUser.setUsername(tfDbUserName.getText());
                List<DbUser> dbUserList = database.getDatabaseUsers();
                for (DbUser db : dbUserList) {
                    if (db.getUsername().equals(this.editDbUser.getUsername().toUpperCase())) {
                        userExists = true;
                    }
                }
                //if the user exists update roles
                if (userExists) {
                    database.updateUserRoles(editDbUser);
                } else {
                    //else create new user with password
                    database.createDatabaseUser(this.editDbUser, tfDbUserPassword.getText());
                }
                tableRefresh();
            } catch (Exception e) {
                e.printStackTrace();

            }
        } else {
            CustomAlert customAlert = new CustomAlert(Alert.AlertType.WARNING, "Missing input", "User always need an Username and a password", "Ok", "Ok");
            customAlert.showAndWait();
        }

    }

    /**
     * initializes all fields with the user data.
     *
     * @param newValue is the user , who is going to be edited
     */
    private void editUser(DbUser newValue) {
        this.oldDbUser = new DbUser(newValue.getUsername(), newValue.getRoles());
        this.editDbUser = newValue;
        tfDbUserPassword.setDisable(true);
        tfDbUserName.setText(editDbUser.getUsername());
        adminCheckbox.setSelected(editDbUser.getRoles().contains(DbUserRole.Admin));
        viewerCheckbox.setSelected(editDbUser.getRoles().contains(DbUserRole.Viewer));
        dataAnalystCheckbox.setSelected(editDbUser.getRoles().contains(DbUserRole.DataAnalyst));
        if (editDbUser.getUsername().equals("")) {
            tfDbUserPassword.setDisable(false);
        }
    }

    /**
     * reloads all users from database and fills Tableview.
     */
    private void tableRefresh() {
        try {
            userTable.getItems().clear();
            userTable.getItems().addAll(database.getDatabaseUsers());
            userTable.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
