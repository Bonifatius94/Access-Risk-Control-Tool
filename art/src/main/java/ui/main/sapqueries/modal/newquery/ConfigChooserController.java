package ui.main.sapqueries.modal.newquery;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import data.entities.AccessCondition;
import data.entities.AccessPattern;
import data.entities.Configuration;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import ui.App;
import ui.custom.controls.ButtonCell;
import ui.custom.controls.CustomWindow;
import ui.main.sapqueries.modal.details.ConfigDetailsController;



public class ConfigChooserController {

    @FXML
    public TableView<Configuration> configsTable;

    @FXML
    public TableView<AccessPattern> patternsTable;

    @FXML
    public TableColumn<AccessPattern, Set<AccessCondition>> useCaseCountColumn;

    @FXML
    public JFXTextField whitelistName;

    @FXML
    public JFXTextField whitelistDescription;

    @FXML
    public TableColumn<Configuration, JFXButton> viewDetailsColumn;

    private NewSapQueryController parentController;


    @FXML
    public void initialize() {
        initializeTables();
    }

    /**
     * Initializes extra table columns.
     */
    private void initializeTables() {

        // listen to selects on conditionPropertiesTable
        configsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showDetails(newValue);
            }
        });

        // Add the detail column
        viewDetailsColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.OPEN_IN_NEW, (Configuration configuration) -> {
            viewConfigDetails(configuration);
            return configuration;
        }));

        // overwrite the column in which the number of useCases is displayed
        useCaseCountColumn.setCellFactory(col -> new TableCell<AccessPattern, Set<AccessCondition>>() {

            @Override
            protected void updateItem(Set<AccessCondition> items, boolean empty) {

                // display nothing if the row is empty, otherwise the item count
                setText((empty || items == null) ? "" : "" + items.size());

            }

        });

        // custom comparator for the useCaseCountColumn
        useCaseCountColumn.setComparator((list1, list2) -> list1.size() <= list2.size() ? 0 : 1);
    }

    /**
     * Fills the detail view on the right with the config details.
     *
     * @param config the config which details are shown
     */
    private void showDetails(Configuration config) {

        if (config != null) {
            // fill whitelist text fields
            this.whitelistName.setText(config.getWhitelist().getName());
            this.whitelistDescription.setText(config.getWhitelist().getDescription());

            // fill patterns table
            this.patternsTable.setItems(FXCollections.observableList(new ArrayList<>(config.getPatterns())));
        }
    }

    public void setParentController(NewSapQueryController controller) {
        this.parentController = controller;
    }

    /**
     * Return the chosen configuration to the parent controller and close the window.
     * @param event the event which is needed to close the stage.
     */
    public void chooseConfig(ActionEvent event) {
        if (this.configsTable.getSelectionModel().getSelectedItem() != null) {
            parentController.setConfig(this.configsTable.getSelectionModel().getSelectedItem());
            close(event);
        }
    }

    /**
     * Opens the config details in a modal window.
     * @param configuration the configuration which details should be shown
     */
    private void viewConfigDetails(Configuration configuration) {
        try {
            // create a new FXML loader with the SapSettingsEditDialogController
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
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
            configDetails.giveConfiguration(configuration);
        } catch (Exception e) {
            e.printStackTrace();
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
