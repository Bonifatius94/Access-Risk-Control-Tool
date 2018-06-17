package ui.main.sapqueries.modal.details;

import com.jfoenix.controls.JFXTextField;

import data.entities.CriticalAccessQuery;

import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui.App;
import ui.custom.controls.CustomWindow;


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
     *
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

    /**
     * Opens the config details in a modal window.
     */
    public void openConfigDetails() {
        try {
            // create a new FXML loader with the SapSettingsEditDialogController
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ConfigDetailsView.fxml"), bundle);
            CustomWindow customWindow = loader.load();

            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow, 1050, 650);
            scene.getStylesheets().add("css/dark-theme.css");
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(App.primaryStage);
            customWindow.initStage(stage);

            customWindow.setTitle(bundle.getString("configDetails"));

            stage.show();

            // give the dialog the sapConfiguration
            ConfigDetailsController configDetails = loader.getController();
            configDetails.giveConfiguration(query.getConfig());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openSapConfigDetails() {
        // TODO: open a modal window with SAPConfig Details
    }

    public void rerunQuery() {

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
