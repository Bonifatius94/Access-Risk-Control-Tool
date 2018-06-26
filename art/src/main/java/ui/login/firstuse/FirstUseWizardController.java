package ui.login.firstuse;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import data.entities.DbUser;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

import extensions.ResourceBundleHelper;

import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ui.App;
import ui.AppComponents;
import ui.custom.controls.CustomWindow;


public class FirstUseWizardController {

    @FXML
    private VBox welcomeBox;

    @FXML
    private VBox createUserBox;

    @FXML
    private VBox finishBox;

    @FXML
    private JFXTextField usernameInput;

    @FXML
    private JFXPasswordField passwordInput;

    @FXML
    private JFXTextField passwordInputPlain;

    @FXML
    private MaterialDesignIconView showPasswordIconView;

    @FXML
    private JFXButton nextButton;

    @FXML
    private JFXButton createUserButton;

    @FXML
    private JFXButton finishButton;

    @FXML
    private HBox usernameValidationBox;

    /**
     * Initializes the view with all needed bindings.
     */
    @FXML
    public void initialize() {

        // bind managed property to visibility
        welcomeBox.managedProperty().bind(welcomeBox.visibleProperty());
        createUserBox.managedProperty().bind(createUserBox.visibleProperty());
        finishBox.managedProperty().bind(finishBox.visibleProperty());
        passwordInput.managedProperty().bind(passwordInput.visibleProperty());
        passwordInputPlain.managedProperty().bind(passwordInputPlain.visibleProperty());

        // bind password inputs
        passwordInputPlain.visibleProperty().bind(Bindings.not(passwordInput.visibleProperty()));
        passwordInput.textProperty().bindBidirectional(passwordInputPlain.textProperty());

        // bind default button property
        nextButton.defaultButtonProperty().bind(nextButton.focusedProperty());
        createUserButton.defaultButtonProperty().bind(Bindings.isNotEmpty(usernameInput.textProperty()));
        finishButton.defaultButtonProperty().bind(finishButton.focusedProperty());

        // transform typed text to uppercase
        usernameInput.setTextFormatter(new TextFormatter<>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));

        initializeValidation();
    }

    /**
     * Toggles the password visibility.
     */
    public void togglePasswordDisplay() {
        if (passwordInput.isVisible()) {
            showPasswordIconView.setIcon(MaterialDesignIcon.EYE);
            passwordInput.visibleProperty().set(false);
        } else {
            showPasswordIconView.setIcon(MaterialDesignIcon.EYE_OFF);
            passwordInput.visibleProperty().set(true);
        }
    }

    /**
     * Initializes the validation for userCreation inputs.
     */
    private void initializeValidation() {

        // validate the input with regex and display error message
        usernameInput.textProperty().addListener((ol, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                usernameValidationBox.setVisible(false);
            } else {
                if (!newValue.equals(oldValue)) {
                    if (!newValue.matches("([A-Z]{3,}+(_|\\w)*)")) {
                        usernameValidationBox.setVisible(true);
                    } else {
                        usernameValidationBox.setVisible(false);
                    }
                    usernameInput.validate();
                }
            }
        });

        usernameInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                usernameInput.validate();
            }
        });

        passwordInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                passwordInput.validate();
            }
        });

        passwordInputPlain.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                passwordInputPlain.validate();
            }
        });

    }

    /**
     * Validates the userCreation inputs.
     *
     * @return if the inputs are valid
     */
    private boolean validateBeforeCreate() {
        return usernameInput.validate() && passwordInput.validate() && passwordInputPlain.validate() && !usernameValidationBox.isVisible();
    }

    /**
     * Advances to the userCreation.
     */
    public void goToUserCreation() {
        welcomeBox.setVisible(false);
        createUserBox.setVisible(true);
        finishBox.setVisible(false);
    }

    /**
     * Validates the inputs and advances to finish.
     */
    public void createUserAndGoToFinish() {

        if (validateBeforeCreate()) {

            if (AppComponents.tryInitDbContext(usernameInput.getText(), passwordInput.getText())) {

                try {

                    // give the first user the Admin user role
                    DbUser currentUser = new DbUser(usernameInput.getText(), true, false, false, false);
                    AppComponents.getDbContext().updateUserRoles(currentUser);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                welcomeBox.setVisible(false);
                createUserBox.setVisible(false);
                finishBox.setVisible(true);
            }
        }
    }

    /**
     * Closes the window and start the real application.
     *
     * @param event the action event of the clicked button
     */
    public void closeAndStartApp(ActionEvent event) {
        try {
            // create a new FXML loader with the SapSettingsEditDialogController
            ResourceBundle bundle = ResourceBundleHelper.getInstance().getLanguageBundle();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../main/MainView.fxml"), bundle);
            CustomWindow customWindow = loader.load();

            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow, 1050, 750);
            scene.getStylesheets().add("css/dark-theme.css");
            App.primaryStage.setScene(scene);
            App.primaryStage.setTitle(bundle.getString("art"));
            customWindow.initStage(App.primaryStage);

            close(event);
            App.primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Hides the stage.
     *
     * @param event the given ActionEvent
     */
    public void close(ActionEvent event) {
        (((Button) event.getSource()).getScene().getWindow()).hide();
    }

}
