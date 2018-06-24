package ui.main.sapqueries.modal.newquery;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXProgressBar;
import data.entities.Configuration;
import data.entities.CriticalAccessQuery;
import data.entities.SapConfiguration;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import javafx.util.StringConverter;

import sap.SapConnector;

import ui.App;
import ui.AppComponents;
import ui.custom.controls.AutoCompleteComboBoxListener;
import ui.custom.controls.CustomWindow;
import ui.main.sapqueries.modal.choosers.ConfigChooserController;
import ui.main.sapqueries.modal.choosers.SapConfigChooserController;


public class NewSapQueryController {

    @FXML
    public JFXComboBox<Configuration> configChooser;

    @FXML
    public JFXComboBox<SapConfiguration> sapSettingsChooser;

    @FXML
    public VBox inputBox;

    @FXML
    public VBox spinner;

    @FXML
    private JFXButton runAnalysisButton;

    @FXML
    private JFXProgressBar progressBar;

    @FXML
    private Label progressLabel;

    @FXML
    private Label connectionLabel;


    private Configuration configuration;
    private SapConfiguration sapConfiguration;
    private ResourceBundle bundle;


    /**
     * Initializes the view.
     */
    @FXML
    public void initialize() {
        bundle = ResourceBundle.getBundle("lang");

        initializeConfigChooser();

        initalizeSapSettingsChooser();
    }

    /**
     * Initialize the configChooser as an autocomplete.
     */
    private void initializeConfigChooser() {

        // create the autocomplete binding
        new AutoCompleteComboBoxListener<>(configChooser);

        // change the items of the autocomplete according to the input
        configChooser.getEditor().textProperty().addListener((event) -> {
            try {
                List<Configuration> result = AppComponents.getDbContext().getFilteredConfigs(false, configChooser.getEditor().getText(), null, null, 5);
                ObservableList<Configuration> items = FXCollections.observableArrayList();
                if (result.size() != 0) {
                    items.addAll(result);
                }
                Platform.runLater(() -> configChooser.setItems(items));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // set a string converter for the whitelistChooser to the items are correctly displayed
        configChooser.setConverter(new ConfigurationStringConverter());
    }

    /**
     * Initialize the configChooser as an autocomplete.
     */
    private void initalizeSapSettingsChooser() {

        // create the autocomplete binding
        new AutoCompleteComboBoxListener<>(sapSettingsChooser);

        // change the items of the autocomplete according to the input
        sapSettingsChooser.getEditor().textProperty().addListener((event) -> {
            try {
                List<SapConfiguration> result = AppComponents.getDbContext().getFilteredSapConfigs(false, configChooser.getEditor().getText(), null, null, 5);
                ObservableList<SapConfiguration> items = FXCollections.observableArrayList();
                if (result.size() != 0) {
                    items.addAll(result);
                }
                Platform.runLater(() -> sapSettingsChooser.setItems(items));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // set a string converter for the whitelistChooser to the items are correctly displayed
        sapSettingsChooser.setConverter(new SapSettingsStringConverter());
    }

    /**
     * Opens modal window in which a config can be chosen.
     */
    public void chooseConfig() {
        try {
            // create a new FXML loader with the SapSettingsEditDialogController
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../choosers/ConfigChooserView.fxml"), bundle);
            CustomWindow customWindow = loader.load();

            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow, 1100, 700);
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

    /**
     * Opens modal window in which a sapConfig can be chosen.
     */
    public void chooseSapSettings() {
        try {
            // create a new FXML loader with the SapSettingsEditDialogController
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../choosers/SapConfigChooserView.fxml"), bundle);
            CustomWindow customWindow = loader.load();

            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow, 900, 600);
            scene.getStylesheets().add("css/dark-theme.css");
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(App.primaryStage);
            customWindow.initStage(stage);

            customWindow.setTitle(bundle.getString("chooseSapSettings"));

            stage.show();

            SapConfigChooserController sapSettingsChooser = loader.getController();
            sapSettingsChooser.setParentController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the SAP login dialog.
     */
    public void openLoginDialog() {
        try {
            // create a new FXML loader with the SapSettingsEditDialogController
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SapLoginView.fxml"), bundle);
            CustomWindow customWindow = loader.load();

            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow, 300, 280);
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
     *
     * @param query the given query
     */
    public void giveQuery(CriticalAccessQuery query) {
        sapConfiguration = query.getSapConfig();
        configuration = query.getConfig();

        if (sapConfiguration != null && configuration != null) {

            // prefill the config
            configChooser.setValue(configuration);

            // prefill the sapConfig
            sapSettingsChooser.setValue(sapConfiguration);
        }
    }

    /**
     * Runs the analysis by creating a new SapTask.
     */
    public void runAnalysis(String username, String password) {
        inputBox.setEffect(new BoxBlur(15, 15, 10));
        spinner.setVisible(true);
        connectionLabel.setText(bundle.getString("connectingToSap"));

        CriticalAccessQuery query = new CriticalAccessQuery();
        query.setConfig(this.configuration);
        query.setSapConfig(this.sapConfiguration);
        query.setCreatedAt(ZonedDateTime.now());

        startSapTask(query, username, password);
    }

    /**
     * Starts a new SapTask with the given parameters.
     * @param query the given query parameters
     * @param username the username
     * @param password the password
     */
    private void startSapTask(final CriticalAccessQuery query, final String username, final String password) {
        Task<CriticalAccessQuery> runQueryTask = new Task<CriticalAccessQuery>() {
            @Override
            protected CriticalAccessQuery call() {
                // run sap query with config and sap settings
                try (SapConnector connector = new SapConnector(query.getSapConfig(), username, password)) {

                    // show progress
                    connector.register(percentage -> {
                        this.updateProgress(percentage, 1);
                    });

                    return connector.runAnalysis(configuration);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        };


        runQueryTask.setOnSucceeded(e -> {
                try {
                    // open the analysisResultView and give it the results
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

                    AnalysisResultController resultController = loader.getController();
                    resultController.giveResultQuery(runQueryTask.getValue());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                // close this window
                runAnalysisButton.getScene().getWindow().hide();
            }
        );


        // bind progress display to the task progressProperty
        progressBar.progressProperty().bind(runQueryTask.progressProperty());
        progressLabel.managedProperty().bind(progressLabel.visibleProperty());
        progressLabel.visibleProperty().bind(runQueryTask.progressProperty().greaterThan(0));
        connectionLabel.visibleProperty().bind(Bindings.not(progressLabel.visibleProperty()));
        connectionLabel.managedProperty().bind(connectionLabel.visibleProperty());
        progressLabel.textProperty().bind(runQueryTask.progressProperty().multiply(100).asString("Analyse läuft... Fortschritt %.0f%%"));

        // start a new thread with the task
        Thread thread = new Thread(runQueryTask);
        thread.start();
    }

    /**
     * Sets the config input to the given config.
     *
     * @param config the given config
     */
    public void setConfig(Configuration config) {

        if (config != null) {
            this.configChooser.getItems().add(config);
            this.configChooser.getSelectionModel().select(config);
        }
    }

    /**
     * Sets the sapConfig input to the given sapConfig.
     *
     * @param sapConfig the given sapConfig
     */
    public void setSapConfig(SapConfiguration sapConfig) {

        if (sapConfig != null) {
            this.sapSettingsChooser.getItems().add(sapConfig);
            this.sapSettingsChooser.getSelectionModel().select(sapConfig);
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

    /**
     * ConfigurationStringConverter for the patternChooser ComboBox.
     */
    private class ConfigurationStringConverter extends StringConverter<Configuration> {
        private Map<String, Configuration> map = new HashMap<>();

        @Override
        public String toString(Configuration configuration) {
            if (configuration == null) {
                return "";
            }
            String str = configuration.getName() + " - " + configuration.getDescription();
            map.put(str, configuration);
            return str;
        }

        @Override
        public Configuration fromString(String string) {
            if (!map.containsKey(string)) {
                return null;
            }
            return map.get(string);
        }
    }

    /**
     * SapSettingsStringConverter for the patternChooser ComboBox.
     */
    private class SapSettingsStringConverter extends StringConverter<SapConfiguration> {
        private Map<String, SapConfiguration> map = new HashMap<>();

        @Override
        public String toString(SapConfiguration configuration) {
            if (configuration == null) {
                return "";
            }
            String str = configuration.getServerDestination() + " - " + configuration.getDescription();
            map.put(str, configuration);
            return str;
        }

        @Override
        public SapConfiguration fromString(String string) {
            if (!map.containsKey(string)) {
                return null;
            }
            return map.get(string);
        }
    }
}
