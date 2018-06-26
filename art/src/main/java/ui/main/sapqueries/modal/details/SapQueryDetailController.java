package ui.main.sapqueries.modal.details;

import com.jfoenix.controls.JFXTextField;

import data.entities.CriticalAccessQuery;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

import extensions.ResourceBundleHelper;

import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import ui.App;
import ui.custom.controls.CustomWindow;
import ui.main.sapqueries.SapQueriesController;
import ui.main.sapqueries.modal.newquery.AnalysisResultController;
import ui.main.sapqueries.modal.newquery.NewSapQueryController;
import ui.main.sapsettings.modal.SapSettingsFormController;


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

    @FXML
    public Label criticalAccessCount;

    @FXML
    public MaterialDesignIconView statusIcon;


    ResourceBundle bundle = ResourceBundleHelper.getInstance().getLanguageBundle();
    private CriticalAccessQuery query;
    private SapQueriesController parentController;

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

            // set the label
            if (query.getEntries() != null && query.getEntries().size() != 0) {
                criticalAccessCount.setText(bundle.getString("criticalAccessCount") + " " + query.getEntries().size());
                statusIcon.setIcon(MaterialDesignIcon.CLOSE);
                statusIcon.setStyle("-fx-fill: -fx-error");
            } else {
                criticalAccessCount.setText(bundle.getString("noCriticalAccess"));
                statusIcon.setIcon(MaterialDesignIcon.CHECK);
                statusIcon.setStyle("-fx-fill: -fx-success");
            }
        }
    }

    /**
     * Opens the config details in a modal window.
     */
    public void openConfigDetails() {
        try {
            // create a new FXML loader with the SapSettingsEditDialogController
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../details/ConfigDetailsView.fxml"), bundle);
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

    /**
     * Opens the AnalysisResultView with the current query.
     */
    public void openResultDetails() throws Exception {
        // open the analysisResultView and give it the results
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ui/main/sapqueries/modal/newquery/AnalysisResultView.fxml"), bundle);
        CustomWindow customWindow = loader.load();

        // build the scene and add it to the stage
        Scene scene = new Scene(customWindow);
        scene.getStylesheets().add("css/dark-theme.css");
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(App.primaryStage);
        customWindow.initStage(stage);

        customWindow.setTitle(bundle.getString("analysisResultTitle"));

        stage.show();

        AnalysisResultController resultController = loader.getController();
        resultController.giveResultQuery(query);
    }

    /**
     * Opens the SapConfig details in a modal, uneditable window.
     */
    public void openSapConfigDetails() throws Exception {
        // create a new FXML loader with the SapSettingsFormController
        ResourceBundle bundle = ResourceBundleHelper.getInstance().getLanguageBundle();
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ui/main/sapsettings/modal/SapSettingsFormView.fxml"), bundle);
        CustomWindow customWindow = loader.load();

        // build the scene and add it to the stage
        Scene scene = new Scene(customWindow);
        scene.getStylesheets().add("css/dark-theme.css");
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(App.primaryStage);
        customWindow.initStage(stage);
        stage.show();

        customWindow.setTitle(bundle.getString("detailSapSettingsTitle"));

        SapSettingsFormController sapView = loader.getController();
        sapView.setEditable(false);
    }

    /**
     * Opens the window for running the query with preselected values.
     */
    public void rerunQuery(ActionEvent event) {
        try {
            // create a new FXML loader with the SapSettingsEditDialogController
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../newquery/NewSapQueryView.fxml"), bundle);
            CustomWindow customWindow = loader.load();

            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow);
            scene.getStylesheets().add("css/dark-theme.css");
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(App.primaryStage);
            customWindow.initStage(stage);

            customWindow.setTitle(bundle.getString("newAnalysis"));

            close(event);
            stage.show();

            // give the dialog the sapConfiguration
            NewSapQueryController newQuery = loader.getController();
            newQuery.giveQuery(query);
            newQuery.setParentController(parentController);
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

    public void setParentController(SapQueriesController controller) {
        this.parentController = controller;
    }
}
