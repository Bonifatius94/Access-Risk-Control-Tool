package ui.main.sapqueries.modal.newquery;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSpinner;
import data.entities.Configuration;
import data.entities.SapConfiguration;

import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

    public void chooseConfig() {

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
     * Runs the analysis.
     */
    public void runAnalysis() {
        inputBox.setEffect(new BoxBlur());
        spinner.setVisible(true);

    }

    public void cancel() {

    }
}
