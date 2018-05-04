package ui.condition;

import excel.config.AuthorizationPatternCondition;
import excel.config.ICondition;
import excel.config.AuthorizationProfileCondition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.ResourceBundle;

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

        try {

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void newAuthPatternCondition() throws Exception {

        showAuthPatternConditionDialog(null);
    }

    @FXML
    private void newAuthProfileCondition() throws Exception {

        showProfileConditionDialog(null);
    }

    private void showAuthPatternConditionDialog(AuthorizationPatternCondition condition) throws Exception{

        // load view and init controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AuthPatternConditionView.fxml"), ResourceBundle.getBundle("lang"));
        Parent root = loader.load();
        AuthPatternConditionController controller = loader.getController();

        // init stage
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Auth Pattern Condition");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(tblConditions.getScene().getWindow());

        // show view as modal dialog and wait for exit
        stage.showAndWait();

        // evaluate condition
        // TODO: implement logic
    }

    private void showProfileConditionDialog(AuthorizationProfileCondition condition) throws Exception {

        // load view and init controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AuthProfileConditionView.fxml"), ResourceBundle.getBundle("lang"));
        Parent root = loader.load();
        AuthPatternConditionController controller = loader.getController();

        // init stage
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Auth Profile Condition");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(tblConditions.getScene().getWindow());

        // show view as modal dialog and wait for exit
        stage.showAndWait();

        // evaluate condition
        // TODO: implement logic
    }

}
