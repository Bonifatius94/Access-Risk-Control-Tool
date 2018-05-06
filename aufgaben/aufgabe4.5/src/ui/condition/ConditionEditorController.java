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

/**
 * This class contains the user interface business logic for the conditions editor view
 * that is bound to an instance of this class.
 *
 * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
 */
public class ConditionEditorController {

    @FXML
    private TableView tblConditions;
    private ObservableList<ICondition> conditions = FXCollections.observableArrayList();

    /**
     * This method is automatically called when the according view is loaded. It initializes the logic behind the controls:
     * <ul>
     *     <li>an observable lists is attached to each table view / choice box</li>
     *     <li>a string property is attached to each text field / text area</li>
     *     <li>an event listener is attached to each event</li>
     * </ul>
     *
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @SuppressWarnings("unchecked")
    public void initialize() {

        // init table view items
        tblConditions.setItems(conditions);

        // init event listener
        tblConditions.setOnMouseClicked(this::onConditionClicked);
    }

    /**
     * This method sets the conditions shown in this view.
     *
     * @param conditions a list of conditions
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public void loadConditions(List<ICondition> conditions) {

        // load items into table view
        this.conditions.clear();
        this.conditions.addAll(conditions);
    }

    /**
     * This method handles the table view row clicked event. The main logic is only triggered on a double click.
     * It shows the auth pattern condition / auth profile condition dialog of an existing condition.
     *
     * @param click a click event handle
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
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

    /**
     * This method handles the button clicked event of the new auth pattern button.
     * It shows the auth pattern condition dialog as a modal dialog.
     *
     * @throws Exception an error when showing the dialog
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @FXML
    private void newAuthPatternCondition() throws Exception {

        showAuthPatternConditionDialog(null);

        // TODO: reload the list if changes were made
    }

    /**
     * This method handles the button clicked event of the new auth profile button.
     * It shows the auth pattern condition dialog as a modal dialog.
     *
     * @throws Exception an error when showing the dialog
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    @FXML
    private void newAuthProfileCondition() throws Exception {

        showProfileConditionDialog(null);
    }

    /**
     * This method shows an auth pattern condition dialog as a modal dialog and loads the overloaded auth pattern condition into it.
     *
     * @param condition an auth pattern condition that is loaded into the dialog
     * @throws Exception caused by auth pattern condition view resource not found
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
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

    /**
     * This method shows an auth profile condition dialog as a modal dialog and loads the overloaded auth profile condition into it.
     *
     * @param condition an auth profile condition that is loaded into the dialog
     * @throws Exception caused by auth pattern condition view resource not found
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
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
