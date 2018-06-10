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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui.App;
import ui.custom.controls.ButtonCell;
import ui.custom.controls.CustomWindow;




public class PatternsFormController {

    private AccessPattern pattern;

    @FXML
    private JFXTextField useCaseIdInput;

    @FXML
    private JFXTextField descriptionInput;

    @FXML
    private JFXComboBox<String> conditionTypeInput;

    @FXML
    private JFXComboBox<ConditionLinkage> linkageInput;

    @FXML
    private HBox conditionBox;

    @FXML
    private HBox linkageBox;

    @FXML
    private JFXTextField profileInput;

    @FXML
    private TableView<AccessPatternConditionProperty> conditionPropertiesTable;

    @FXML
    private TableColumn<AccessPatternConditionProperty, JFXButton> deleteColumn;

    @FXML
    private TableColumn<AccessPatternConditionProperty, JFXButton> editColumn;

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

        /* catch row double click */
        conditionPropertiesTable.setRowFactory(tv -> {
            TableRow<AccessPatternConditionProperty> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    AccessPatternConditionProperty accessPatternConditionProperty = row.getItem();
                    editAccessPatternConditionProperty(accessPatternConditionProperty);
                }
            });
            return row;
        });
    }

    /**
     * Prefills the inputs with the given pattern.
     *
     * @param pattern the selected pattern
     */
    public void giveSelectedAccessPattern(AccessPattern pattern) {
        this.pattern = pattern;

        this.useCaseIdInput.setText(pattern.getUsecaseId());
        this.descriptionInput.setText(pattern.getDescription());

        // Add the delete column
        deleteColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.DELETE, (AccessPatternConditionProperty accessPatternConditionProperty) -> {
            conditionPropertiesTable.getItems().remove(accessPatternConditionProperty);
            return accessPatternConditionProperty;
        }));

        // Add the edit column
        editColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.PENCIL, (AccessPatternConditionProperty accessPatternConditionProperty) -> {
            editAccessPatternConditionProperty(accessPatternConditionProperty);
            return accessPatternConditionProperty;
        }));

        // Fill table
        if (pattern.getConditions().get(0).getProfileCondition() == null) {
            this.conditionTypeInput.getSelectionModel().select("Condition");

            ObservableList<AccessPatternConditionProperty> entries = FXCollections.observableArrayList();

            for (AccessCondition condition : pattern.getConditions()) {
                entries.addAll(condition.getPatternCondition().getProperties());
            }

            this.conditionPropertiesTable.setItems(entries);
            this.conditionPropertiesTable.refresh();

            this.linkageInput.getSelectionModel().select(pattern.getLinkage());
        } else {
            this.conditionTypeInput.getSelectionModel().select("Profile");

            this.profileInput.setText(pattern.getConditions().get(0).getProfileCondition().getProfile());
        }
    }

    /**
     * Opens an modal edit dialog for a AccessPatternConditionProperty.
     *
     * @param accessPatternConditionProperty the selected AccessPatternConditionProperty
     */
    private void editAccessPatternConditionProperty(AccessPatternConditionProperty accessPatternConditionProperty) {

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

            // give the dialog the sapConfiguration
            AccessConditionPropertyFormController accessConditionPropertyFormEdit = loader.getController();
            accessConditionPropertyFormEdit.giveSelectedAccessConditionProperty(accessPatternConditionProperty);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * Saves the changes to the database.
     */
    public void saveChanges() {

    }


}
