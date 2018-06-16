package ui.main.sapqueries.modal;

import com.jfoenix.controls.JFXTextField;

import data.entities.CriticalAccessQuery;

import java.time.format.DateTimeFormatter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;


public class SapQueryDetailController {

    @FXML
    public JFXTextField configNameField;

    @FXML
    public JFXTextField configDescriptionField;

    @FXML
    public JFXTextField sapConfigurationNameField;

    @FXML
    public JFXTextField sapConfigurationDescriptionField;

    @FXML
    public JFXTextField dateField;

    @FXML
    public JFXTextField createdByField;


    private CriticalAccessQuery query;

    @FXML
    public void initialize() {

    }

    /**
     * Prefills the textfields with the given query.
     * @param query the selected query
     */
    public void giveSelectedQuery(CriticalAccessQuery query) {

        if (query != null) {
            this.query = query;

            dateField.setText(query.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm")));
            createdByField.setText(query.getCreatedBy());

            configNameField.setText(query.getConfig().getName());
            configDescriptionField.setText(query.getConfig().getDescription());

            sapConfigurationNameField.setText(query.getSapConfig().getServerDestination());
            sapConfigurationDescriptionField.setText(query.getSapConfig().getDescription());

        }
    }


    public void openConfigDetails() {

    }

    public void openSapConfigDetails() {

    }

    public void rerunQuery() {

    }

    /**
     * Hides the stage.
     * @param event the given ActionEvent
     */
    public void close(ActionEvent event) {
        (((Button) event.getSource()).getScene().getWindow()).hide();
    }

}
