package ui.main.patterns;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import data.entities.AccessCondition;
import data.entities.AccessPattern;
import data.entities.AccessPatternConditionProperty;
import data.entities.ConditionLinkage;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui.App;
import ui.custom.controls.ButtonCell;
import ui.custom.controls.CustomWindow;


public class PatternsFormController {

    @FXML
    private JFXTextField useCaseIdInput;

    @FXML
    private JFXTextField descriptionInput;

    @FXML
    private JFXComboBox<String> conditionTypeInput;

    @FXML
    private JFXComboBox<ConditionLinkage> linkageInput;

    @FXML
    private VBox conditionBox;

    @FXML
    private HBox linkageBox;

    @FXML
    private JFXTextField profileInput;


    // Auth properties table

    @FXML
    private TableView<AccessPatternConditionProperty> conditionPropertiesTable;

    @FXML
    private TableColumn<AccessPatternConditionProperty, JFXButton> deleteColumn;

    @FXML
    private JFXComboBox<ConditionComboBoxEntry> conditionChooser;

    @FXML
    private JFXTextField authObjectInput;

    @FXML
    private JFXTextField authFieldInput;

    @FXML
    private JFXTextField authFieldValue1Input;

    @FXML
    private JFXTextField authFieldValue2Input;

    @FXML
    private JFXTextField authFieldValue3Input;

    @FXML
    private JFXTextField authFieldValue4Input;


    private AccessPattern accessPattern;

    /**
     * Initializes the view and sets up bindings for component visibility.
     */
    @FXML
    public void initialize() {

        // set condition input items
        this.conditionTypeInput.getItems().setAll("Condition", "Profile");
        this.linkageInput.getItems().setAll(ConditionLinkage.None, ConditionLinkage.And, ConditionLinkage.Or);

        // set visibility of certain components according to user input
        this.profileInput.managedProperty().bind(this.profileInput.visibleProperty());
        this.conditionBox.managedProperty().bind(this.conditionBox.visibleProperty());
        this.linkageBox.managedProperty().bind(this.conditionBox.visibleProperty());
        this.linkageBox.visibleProperty().bind(this.conditionBox.visibleProperty());
        this.conditionChooser.managedProperty().bind(this.conditionChooser.visibleProperty());

        // initialize condition type combo box component
        initializeConditionTypeComboBox();

        initializeValidation();

        initializeLinkageInput();
    }

    /**
     * Initializes the LinkageInputComboBox.
     */
    public void initializeLinkageInput() {
        linkageInput.getSelectionModel().selectedItemProperty().addListener((ChangeListener<ConditionLinkage>) (selected, oldValue, newValue) -> {
            if (newValue.equals(ConditionLinkage.None)) {
                this.conditionChooser.setVisible(false);
            } else {
                this.conditionChooser.setVisible(true);
            }
        });
    }

    /**
     * Initializes the condition type combo box and manages the component visibility.
     */
    private void initializeConditionTypeComboBox() {
        // add a change listener to the conditionType input so other components are hidden accordingly
        this.conditionTypeInput.getSelectionModel().selectedItemProperty().addListener((ChangeListener<String>) (selected, oldValue, newValue) -> {

            if (oldValue != null) {
                switch (oldValue) {
                    case "Condition":
                        conditionBox.setVisible(false);
                        break;
                    case "Profile":
                        profileInput.setVisible(false);
                        break;
                    default:
                        profileInput.setVisible(false);
                        conditionBox.setVisible(false);
                        break;
                }
            }
            switch (newValue) {
                case "Condition":
                    conditionBox.setVisible(true);
                    break;
                case "Profile":
                    profileInput.setVisible(true);
                    break;
                default:
                    profileInput.setVisible(true);
                    conditionBox.setVisible(true);
                    break;
            }
        });

    }

    /**
     * Initializes the validation for certain text inputs in order to display an error message (e.g. required).
     */
    private void initializeValidation() {

        useCaseIdInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                useCaseIdInput.validate();
            }
        });

        descriptionInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                descriptionInput.validate();
            }
        });

        profileInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                profileInput.validate();
            }
        });

        authObjectInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                authObjectInput.validate();
            }
        });

        authFieldInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                authFieldInput.validate();
            }
        });

        authFieldValue1Input.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                authFieldValue1Input.validate();
            }
        });
    }

    /**
     * Prefills the inputs with the given pattern.
     *
     * @param pattern the selected pattern
     */
    public void giveSelectedAccessPattern(AccessPattern pattern) {

        this.accessPattern = pattern;

        this.useCaseIdInput.setText(pattern.getUsecaseId());
        this.descriptionInput.setText(pattern.getDescription());

        // listen to selects on conditionPropertiesTable
        conditionPropertiesTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                editSelectedAccessConditionProperty(newValue);
            }
        });

        // add listener to condition chooser which fills the table accordingly
        conditionChooser.getSelectionModel().selectedItemProperty().addListener((ChangeListener<ConditionComboBoxEntry>) (selected, oldValue, newValue) -> {
            ObservableList<AccessPatternConditionProperty> properties = FXCollections.observableList(newValue.getCondition().getPatternCondition().getProperties());
            conditionPropertiesTable.setItems(properties);
        });

        // Add the delete column
        deleteColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.DELETE, (AccessPatternConditionProperty accessPatternConditionProperty) -> {
            conditionPropertiesTable.getItems().remove(accessPatternConditionProperty);
            return accessPatternConditionProperty;
        }));

        // Fill choose box
        if (pattern.getConditions().get(0).getProfileCondition() == null) {
            this.conditionTypeInput.getSelectionModel().select("Condition");

            int i = 0;
            for (AccessCondition condition : pattern.getConditions()) {
                conditionChooser.getItems().add(new ConditionComboBoxEntry("Condition " + i, condition));
                i++;
            }
            // preselect first condition
            conditionChooser.getSelectionModel().select(0);

            // preselect correct linkage
            this.linkageInput.getSelectionModel().select(pattern.getLinkage());
        } else {
            this.conditionTypeInput.getSelectionModel().select("Profile");

            this.profileInput.setText(pattern.getConditions().get(0).getProfileCondition().getProfile());
        }
    }

    /**
     * Saves the changes to the database.
     */
    public void saveChanges() {

    }

    public void close(ActionEvent event) {
        (((Button) event.getSource()).getScene().getWindow()).hide();
    }

    /**
     * Fills the detail form with the selected AccessConditionProperty.
     *
     * @param accessPatternConditionProperty the selected AccessConditionProperty
     */
    private void editSelectedAccessConditionProperty(AccessPatternConditionProperty accessPatternConditionProperty) {

        // set the inputs to the given values
        authObjectInput.setText(accessPatternConditionProperty.getAuthObject());
        authFieldInput.setText(accessPatternConditionProperty.getAuthObjectProperty());
        authFieldValue1Input.setText(accessPatternConditionProperty.getValue1());
        authFieldValue2Input.setText(accessPatternConditionProperty.getValue2());
        authFieldValue3Input.setText(accessPatternConditionProperty.getValue3());
        authFieldValue4Input.setText(accessPatternConditionProperty.getValue4());

        // reset error validation
        authObjectInput.validate();
        authFieldInput.validate();
        authFieldValue1Input.validate();
    }

    /**
     * Opens a modal dialog for adding a new AccessPatternConditionProperty.
     */
    public void addAccessPatternConditionProperty() {

        try {
            // create a new FXML loader with the SapSettingsEditDialogController
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AccessConditionPropertyFormView.fxml"), bundle);
            CustomWindow customWindow = loader.load();

            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow, 300, 500);
            scene.getStylesheets().add("css/dark-theme.css");
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(App.primaryStage);
            customWindow.initStage(stage);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * Used for displaying the Conditions with a name in a ComboBox.
     */
    class ConditionComboBoxEntry {

        private String name;
        private AccessCondition condition;

        public ConditionComboBoxEntry(String name, AccessCondition condition) {
            this.name = name;
            this.condition = condition;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public AccessCondition getCondition() {
            return condition;
        }

        public void setCondition(AccessCondition condition) {
            this.condition = condition;
        }

        public String toString() {
            return name;
        }
    }
}
