package ui.main.patterns;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import data.entities.AccessCondition;
import data.entities.AccessPattern;
import data.entities.AccessPatternCondition;
import data.entities.AccessPatternConditionProperty;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import ui.custom.controls.ButtonCell;


public class PatternsFormController {

    private AccessPattern pattern;

    @FXML
    private JFXTextField useCaseIDInput;

    @FXML
    private JFXTextField descriptionInput;

    @FXML
    private JFXComboBox conditionTypeInput;

    @FXML
    private HBox profileBox;

    @FXML
    private HBox conditionBox;

    @FXML
    private JFXTextField profileInput;

    @FXML
    private TableView<AccessPatternConditionProperty> conditionPropertiesTable;

    @FXML
    private TableColumn<AccessPatternConditionProperty, JFXButton> deleteColumn;

    @FXML
    private TableColumn<AccessPatternConditionProperty, JFXButton> editColumn;

    @FXML
    public void initialize() {
        this.conditionTypeInput.getItems().setAll("Condition", "Profile");

        this.profileBox.managedProperty().bind(this.profileBox.visibleProperty());
        this.conditionBox.managedProperty().bind(this.conditionBox.visibleProperty());

        this.conditionTypeInput.getSelectionModel().selectedItemProperty().addListener((ChangeListener<String>) (selected, oldValue, newValue) -> {

            if (oldValue != null) {
                switch (oldValue) {
                    case "Condition":
                        conditionBox.setVisible(false);
                        break;
                    case "Profile":
                        profileBox.setVisible(false);
                        break;
                }
            }
            switch (newValue) {
                case "Condition":
                    conditionBox.setVisible(true);
                    break;
                case "Profile":
                    profileBox.setVisible(true);
                    break;
            }
        });

    }

    public void giveSelectedAccessPattern(AccessPattern pattern) {
        this.pattern = pattern;

        this.useCaseIDInput.setText(pattern.getUsecaseId());
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
            conditionTypeInput.getSelectionModel().select("Condition");

            ObservableList<AccessPatternConditionProperty> entries = FXCollections.observableArrayList();

            for (AccessCondition condition : pattern.getConditions()) {
                entries.addAll(condition.getPatternCondition().getProperties());
            }

            this.conditionPropertiesTable.setItems(entries);
            this.conditionPropertiesTable.refresh();
        } else {
            conditionTypeInput.getSelectionModel().select("Profile");

            this.profileInput.setText(pattern.getConditions().get(0).getProfileCondition().getProfile());
        }
    }

    private void editAccessPatternConditionProperty(AccessPatternConditionProperty accessPatternConditionProperty) {
        System.out.println(accessPatternConditionProperty);
    }

    public void saveChanges() {

    }
}
