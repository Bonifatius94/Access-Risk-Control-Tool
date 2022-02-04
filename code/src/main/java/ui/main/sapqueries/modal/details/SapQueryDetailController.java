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
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.stage.Modality;
import javafx.stage.Stage;
import ui.AppComponents;
import ui.main.sapqueries.SapQueriesController;
import ui.main.sapqueries.modal.newquery.NewSapQueryController;
import ui.main.sapqueries.modal.results.AnalysisResultController;
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
    public void openConfigDetails() throws Exception {

        FXMLLoader loader = AppComponents.getInstance().showScene("ui/main/sapqueries/modal/details/ConfigDetailsView.fxml", "configDetails", 1050, 650);

        ConfigDetailsController configDetails = loader.getController();
        configDetails.giveConfiguration(query.getConfig());
    }

    /**
     * Opens the AnalysisResultView with the current query.
     */
    public void openResultDetails() throws Exception {

        FXMLLoader loader = AppComponents.getInstance().showScene("ui/main/sapqueries/modal/results/AnalysisResultView.fxml", "analysisResultTitle", new Stage(), null, Modality.NONE);

        AnalysisResultController resultController = loader.getController();
        resultController.giveResultQuery(query);
    }

    /**
     * Opens the SapConfig details in a modal, uneditable window.
     */
    public void openSapConfigDetails() throws Exception {

        FXMLLoader loader = AppComponents.getInstance().showScene("ui/main/sapsettings/modal/SapSettingsFormView.fxml", "detailSapSettingsTitle");

        SapSettingsFormController sapView = loader.getController();
        sapView.giveSelectedSapConfig(query.getSapConfig());
        sapView.setEditable(false);
    }

    /**
     * Opens the window for running the query with preselected values.
     */
    public void rerunQuery(ActionEvent event) {
        try {

            FXMLLoader loader = AppComponents.getInstance().showScene("ui/main/sapqueries/modal/newquery/NewSapQueryView.fxml", "newAnalysis");

            close(event);

            // give the dialog the sapConfiguration
            NewSapQueryController newQuery = loader.getController();
            newQuery.giveQuery(query);
            newQuery.setParentController(parentController);

            newQuery.setRerun(true);
            newQuery.setInputsDisable(true);
            newQuery.openLoginDialog();
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
