package ui.main.sapqueries.modal.newquery;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSpinner;
import data.entities.Configuration;
import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;
import data.entities.SapConfiguration;

import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import ui.App;
import ui.custom.controls.CustomWindow;


public class NewSapQueryController {

    @FXML
    public JFXComboBox<Configuration> configAutocomplete;

    @FXML
    public JFXComboBox<SapConfiguration> sapSettingsAutocomplete;

    @FXML
    public VBox inputBox;

    @FXML
    public VBox spinner;

    @FXML
    private JFXButton runAnalysisButton;


    private Configuration configuration;
    private SapConfiguration sapConfiguration;


    @FXML
    public void initialize() {
        // init sap settings (here: test server data)
        this.sapConfiguration = new SapConfiguration("ec2-54-209-137-85.compute-1.amazonaws.com", "some description", "00", "001", "EN", "0");
    }

    /**
     * Opens modal window in which a config can be chosen.
     */
    public void chooseConfig() {
        try {
            // create a new FXML loader with the SapSettingsEditDialogController
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ConfigChooserView.fxml"), bundle);
            CustomWindow customWindow = loader.load();

            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow, 1000, 700);
            scene.getStylesheets().add("css/dark-theme.css");
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(App.primaryStage);
            customWindow.initStage(stage);

            customWindow.setTitle(bundle.getString("chooseConfig"));

            stage.show();

            ConfigChooserController configChooser = loader.getController();
            configChooser.setParentController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void chooseSapSettings() {

    }

    /**
     * Opens the SAP login dialog.
     */
    public void openLoginDialog() {
        try {
            // create a new FXML loader with the SapSettingsEditDialogController
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SapLoginView.fxml"), bundle);
            CustomWindow customWindow = loader.load();

            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow, 250, 300);
            scene.getStylesheets().add("css/dark-theme.css");
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(App.primaryStage);
            customWindow.initStage(stage);

            customWindow.setTitle(bundle.getString("sapLogin"));

            stage.show();

            SapLoginController loginController = loader.getController();
            loginController.setParentController(this);
            loginController.giveSapConfig(sapConfiguration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Prefills the inputs with the given query.
     * @param query the given query
     */
    public void giveQuery(CriticalAccessQuery query) {
        sapConfiguration = query.getSapConfig();
        configuration = query.getConfig();

        if (sapConfiguration != null && configuration != null) {

            // prefill the config
            configAutocomplete.getItems().add(configuration);
            configAutocomplete.getSelectionModel().select(configuration);

            // prefill the sapConfig
            sapSettingsAutocomplete.getItems().add(sapConfiguration);
            sapSettingsAutocomplete.getSelectionModel().select(sapConfiguration);
        }
    }

    /**
     * Runs the analysis.
     */
    public void runAnalysis() {
        inputBox.setEffect(new BoxBlur());
        spinner.setVisible(true);

        // TODO: run sap query here and wait for it to finish, maybe even with a little progress status


        // remove the spinner and blur effect
        inputBox.setEffect(null);
        spinner.setVisible(false);

        // open the analysisResultView and give it the results
        try {
            // create a new FXML loader with the SapSettingsEditDialogController
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AnalysisResultView.fxml"), bundle);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        // close this window
        runAnalysisButton.getScene().getWindow().hide();
    }

    /**
     * Sets the config input to the given config.
     * @param config the given config
     */
    public void setConfig(Configuration config) {

        if (config != null) {
            this.configAutocomplete.getItems().add(config);
            this.configAutocomplete.getSelectionModel().select(config);
        }
    }

    /**
     * Sets the config input to the given sapConfig.
     * @param sapConfig the given sapConfig
     */
    public void setSapConfig(SapConfiguration sapConfig) {

        if (sapConfig != null) {
            this.sapSettingsAutocomplete.getItems().add(sapConfig);
            this.sapSettingsAutocomplete.getSelectionModel().select(sapConfig);
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
}
