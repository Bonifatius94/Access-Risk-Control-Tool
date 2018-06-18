package ui.main.sapqueries.modal.newquery;

import data.entities.SapConfiguration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;


public class SapConfigChooserController {

    @FXML
    public TableView<SapConfiguration> sapConnectionTable;

    private NewSapQueryController parentController;


    @FXML
    public void initialize() {

    }

    public void setParentController(NewSapQueryController controller) {
        this.parentController = controller;
    }

    /**
     * Return the chosen sapConfiguration to the parent controller and close the window.
     * @param event the event which is needed to close the stage.
     */
    public void chooseSapConfig(ActionEvent event) {
        if (this.sapConnectionTable.getSelectionModel().getSelectedItem() != null) {
            parentController.setSapConfig(this.sapConnectionTable.getSelectionModel().getSelectedItem());
            close(event);
        }
    }


    /**
     * Hides the stage.
     *
     * @param event the given ActionEvent
     */
    public void close(javafx.event.ActionEvent event) {
        (((Button) event.getSource()).getScene().getWindow()).hide();
    }
}
