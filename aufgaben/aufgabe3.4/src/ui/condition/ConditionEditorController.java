package ui.condition;

import excel.config.AuthorizationPatternCondition;
import excel.config.ICondition;
import excel.config.AuthorizationProfileCondition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class ConditionEditorController {

    @FXML
    private TableView tblConditions;
    private ObservableList<ICondition> conditions = FXCollections.observableArrayList();

    @SuppressWarnings("unchecked")
    public void initialize() {

        // init table view items
        tblConditions.setItems(conditions);

        // init event listener
        tblConditions.setOnMouseClicked(this::onConditionClicked);
    }

    public void loadConditions(List<ICondition> conditions) {

        // load items into table view
        this.conditions.clear();
        this.conditions.addAll(conditions);
    }

    private void onConditionClicked(MouseEvent click) {

        // check for double click
        if (click.getClickCount() == 2) {

            ICondition selectedItem = (ICondition) tblConditions.getSelectionModel().getSelectedItem();

            // show specific condition editor
            if (selectedItem instanceof AuthorizationPatternCondition) {

                // show auth pattern condition dialog
                showAuthPatternConditionDialog((AuthorizationPatternCondition) selectedItem);

            } else if (selectedItem instanceof AuthorizationProfileCondition) {

                // show profile condition dialog
                showProfileConditionDialog((AuthorizationProfileCondition) selectedItem);
            }
        }
    }

    @FXML
    private void newAuthPatternCondition() {

        showAuthPatternConditionDialog(null);
    }

    @FXML
    private void newAuthProfileCondition() {

        showProfileConditionDialog(null);
    }

    private void showAuthPatternConditionDialog(AuthorizationPatternCondition condition) {

        // TODO: implement logic
    }

    private void showProfileConditionDialog(AuthorizationProfileCondition condition) {

        // TODO: implement logic
    }

}
