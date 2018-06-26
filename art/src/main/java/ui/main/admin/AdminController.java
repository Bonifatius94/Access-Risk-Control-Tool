package ui.main.admin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import data.entities.AccessPattern;
import data.entities.DbUser;
import data.entities.DbUserRole;
import data.localdb.ArtDbContext;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
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
    public JFXTextField tfDbUserPassword;

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
    private HBox usernameValidationBox;

    @FXML
    private JFXButton addButton;

    @FXML
    private JFXButton applyButton;

    @FXML
    private Label usernameValidationLabel;


    private List<DbUser> addedUsers;
    private ArtDbContext database = AppComponents.getDbContext();
    private ResourceBundle bundle = ResourceBundle.getBundle("lang");
    private SimpleBooleanProperty newUserMode = new SimpleBooleanProperty();

    private DbUser editDbUser;

    private Set<DbUserRole> dbUserRoleSet = new HashSet<>();

    /**
     * is called by the FXMl and initializes all parts of the window.
     */
    public void initialize() {

        addedUsers = new ArrayList<>();

        initializeColumns();
        initializeCheckboxes();
        initializeValidation();

        editDbUser = new DbUser("", dbUserRoleSet);

        userTable.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                editUser(newValue);
            }
        }));

        // transform typed text to uppercase
        tfDbUserName.setTextFormatter(new TextFormatter<>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));

        // bind applyButton to the selectedItemProperty
        applyButton.disableProperty().bind(Bindings.isNull(userTable.getSelectionModel().selectedItemProperty()));

        // set newUserMode to false
        newUserMode.setValue(false);

        // bind table disable to newUserMode
        userTable.disableProperty().bind(newUserMode);

        // bind add button disable property to newUserMode
        addButton.disableProperty().bind(newUserMode);

        // bin username textfield to newUserMode
        tfDbUserName.editableProperty().bind(newUserMode);

        tableRefresh();

    }

    /**
     * Initializes the validation.
     */
    private void initializeValidation() {
        // validate the input with regex and display error message
        tfDbUserName.textProperty().addListener((ol, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                usernameValidationBox.setVisible(false);
            } else {
                try {
                    if (!newValue.equals(oldValue)) {
                        if (!newValue.matches("([A-Z]{3,}+(_|\\w)*)")) {
                            usernameValidationLabel.setText(bundle.getString("usernameInvalid"));
                            usernameValidationBox.setVisible(true);
                        } else if (userTable.isDisabled() && database.getDatabaseUsers().stream().map(x -> x.getUsername()).collect(Collectors.toList()).contains(newValue)) {
                            // not an edit, new user
                            usernameValidationLabel.setText(bundle.getString("usernameAlreadyExists"));
                            usernameValidationBox.setVisible(true);
                        } else {
                            usernameValidationBox.setVisible(false);
                        }
                        tfDbUserName.validate();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
                //check if the currently active user is going to be deleted
                if (user.getUsername().equalsIgnoreCase(database.getCurrentUser().getUsername())) {
                    CustomAlert customAlert = new CustomAlert(Alert.AlertType.WARNING, bundle.getString("deleteSelfTitle"), bundle.getString("deleteSelfMessage"));
                    customAlert.showAndWait();
                } else {
                    CustomAlert customAlert = new CustomAlert(Alert.AlertType.CONFIRMATION, bundle.getString("deleteConfirmTitle"),
                        bundle.getString("deleteConfirmMessage"), "Ok", "Cancel");

                    // delete the user
                    if (customAlert.showAndWait().get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                        database.deleteDatabaseUser(user.getUsername());
                        tableRefresh();
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
     * Adds new User with a random initial password.
     */
    public void addNewUser() {

        // prevent empty rows
        tableRefresh();
        newUserMode.setValue(true);

        // reset checkboxes
        initializeCheckboxes();

        // enable password field
        tfDbUserPassword.setDisable(false);

        // create a new user with random password and add him to the table
        DbUser newDbUser = new DbUser("", new HashSet<>());
        tfDbUserPassword.setText(generateFirstPassword(15));
        userTable.getItems().add(newDbUser);

        addedUsers.add(newDbUser);

        userTable.refresh();
        userTable.getSelectionModel().select(newDbUser);
    }

    /**
     * Saves user Changes to the database and refreshes the table.
     */
    public void saveUserChanges() throws Exception {
        if ((userTable.isDisabled() && tfDbUserName.validate() && tfDbUserPassword.validate() && !usernameValidationBox.isVisible())
            || (!userTable.isDisabled() && tfDbUserName.validate() && !usernameValidationBox.isVisible())) {
            if (!adminCheckbox.isSelected() && !dataAnalystCheckbox.isSelected() && !viewerCheckbox.isSelected()) {
                CustomAlert customAlert = new CustomAlert(Alert.AlertType.WARNING, bundle.getString("selectRolesTitle"), bundle.getString("selectRolesMessage"));
                customAlert.showAndWait();
            } else {
                editDbUser.setUsername(tfDbUserName.getText());

                // not an edit, new user
                if (userTable.isDisabled()) {
                    database.createDatabaseUser(this.editDbUser, tfDbUserPassword.getText());
                } else {
                    database.updateUserRoles(editDbUser);
                }

                // enable user table
                newUserMode.setValue(false);

                tableRefresh();
            }
        }
    }

    /**
     * Initializes all fields with the user data.
     *
     * @param newValue is the user , who is going to be edited
     */
    private void editUser(DbUser newValue) {

        // user is edited
        this.editDbUser = newValue;

        // set inputs to new user inputs
        if (!addedUsers.contains(newValue)) {
            tfDbUserPassword.setDisable(true);
            tfDbUserPassword.setText("");
        } else {
            tfDbUserPassword.setDisable(false);
            tfDbUserPassword.setEditable(false);
        }

        tfDbUserName.setText(editDbUser.getUsername());

        // set the correct roles
        adminCheckbox.setSelected(editDbUser.getRoles().contains(DbUserRole.Admin));
        viewerCheckbox.setSelected(editDbUser.getRoles().contains(DbUserRole.Viewer));
        dataAnalystCheckbox.setSelected(editDbUser.getRoles().contains(DbUserRole.DataAnalyst));
    }

    /**
     * Reloads all users from the database to the table.
     */
    private void tableRefresh() {
        try {
            List<DbUser> items = database.getDatabaseUsers();
            ObservableList<DbUser> list = FXCollections.observableList(items);

            userTable.getSelectionModel().clearSelection();
            userTable.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Generates a random password with the given length.
     *
     * @param length the length of the password
     * @return a randomly generated password
     */
    private String generateFirstPassword(int length) {
        final String lowercase = "abcdefghjklmnpqrstuvwxyz";
        final String uppercase = lowercase.toUpperCase();
        final String digits = "0123456789";
        final String special = "!@#$%&_-+<>?/";

        Random random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        sb.append(lowercase).append(uppercase).append(digits);
        sb.append(special);

        char[] characters = sb.toString().toCharArray();
        char[] pw = new char[length];
        for (int i = 0; i < length; i++) {
            pw[i] = characters[random.nextInt(characters.length)];
            System.out.println(pw[i]);
        }

        return new String(pw);
    }
}