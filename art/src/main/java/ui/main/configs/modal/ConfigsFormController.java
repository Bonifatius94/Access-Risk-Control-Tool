package ui.main.configs.modal;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import data.entities.AccessCondition;
import data.entities.AccessPattern;
import data.entities.Configuration;
import data.entities.Whitelist;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import ui.App;
import ui.AppComponents;
import ui.custom.controls.AutoCompleteComboBoxListener;
import ui.custom.controls.ButtonCell;
import ui.custom.controls.ConditionTypeCellFactory;
import ui.custom.controls.CustomWindow;
import ui.main.configs.ConfigsController;


public class ConfigsFormController {

    @FXML
    private JFXTextField nameInput;

    @FXML
    private JFXTextField descriptionInput;

    @FXML
    private JFXComboBox<Whitelist> whitelistChooser;

    @FXML
    private JFXComboBox<AccessPattern> patternChooser;

    @FXML
    private TableView<AccessPattern> patternsTable;

    @FXML
    public TableColumn<AccessPattern, JFXButton> deletePatternColumn;

    @FXML
    public TableColumn<AccessPattern, Set<AccessCondition>> conditionCountColumn;

    @FXML
    public TableColumn<AccessPattern, Set<AccessCondition>> conditionTypeColumn;

    private ConfigsController parentController;
    private Configuration configuration;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {

        initializePatternsTable();

        initializeValidation();

        initializePatternChooser();

        initializeWhitelistChooser();

    }

    /**
     * Initialize the patternChooser as an autocomplete.
     */
    private void initializePatternChooser() {

        // create the autocomplete binding
        new AutoCompleteComboBoxListener<>(patternChooser);

        // change the items of the autocomplete according to the input
        patternChooser.getEditor().textProperty().addListener((event) -> {
            if (!patternChooser.getEditor().getText().isEmpty()) {
                try {
                    List<AccessPattern> result = AppComponents.getDbContext().getFilteredPatterns(false, patternChooser.getEditor().getText(), null, null, 5);
                    result = result.stream().filter(x -> !patternsTable.getItems().contains(x)).collect(Collectors.toList());

                    // remove all entries that are already in the selectedList
                    result = result.stream().filter(x -> {
                        for (AccessPattern pattern : this.patternsTable.getItems()) {
                            if (x.getId().equals(pattern.getId())) {
                                return false;
                            }
                        }
                        return true;
                    }).collect(Collectors.toList());

                    if (result.size() != 0) {
                        ObservableList<AccessPattern> items = FXCollections.observableArrayList(new ArrayList<>(result));
                        patternChooser.setItems(items);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // listen for value changes and add the selected item to the table
        patternChooser.valueProperty().addListener((ol, oldValue, newValue) -> {
            if (newValue != null) {
                patternsTable.getItems().add(newValue);
                patternsTable.refresh();
                Platform.runLater(() -> {
                    patternChooser.setValue(null);
                    patternChooser.getItems().clear();
                });
            }
        });

        // set a string converter for the patternChooser to the items are correctly displayed
        patternChooser.setConverter(new PatternStringConverter());
    }

    /**
     * Initialize the whitelistChooser as an autocomplete.
     */
    private void initializeWhitelistChooser() {

        // create the autocomplete binding
        new AutoCompleteComboBoxListener<>(whitelistChooser);

        // change the items of the autocomplete according to the input
        whitelistChooser.getEditor().textProperty().addListener((event) -> {
            try {
                List<Whitelist> result = AppComponents.getDbContext().getFilteredWhitelists(false, whitelistChooser.getEditor().getText(), null, null, 5);
                if (result.size() != 0) {
                    ObservableList<Whitelist> items = FXCollections.observableArrayList(new ArrayList<>(result));
                    whitelistChooser.setItems(items);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // set a string converter for the whitelistChooser to the items are correctly displayed
        whitelistChooser.setConverter(new WhitelistStringConverter());
    }

    /**
     * Initializes the patterns table.
     */
    private void initializePatternsTable() {

        // set selection mode to MULTIPLE
        patternsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        initializeTableColumns();
    }

    /**
     * Initializes the table columns that need extra content.
     */
    private void initializeTableColumns() {
        // Add the delete column
        deletePatternColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.DELETE, (AccessPattern accessPattern) -> {
            patternsTable.getItems().remove(accessPattern);
            configuration.getPatterns().remove(accessPattern);
            patternsTable.refresh();
            return accessPattern;
        }));

        // overwrite the column in which the number of useCases is displayed
        conditionCountColumn.setCellFactory(col -> new TableCell<AccessPattern, Set<AccessCondition>>() {

            @Override
            protected void updateItem(Set<AccessCondition> items, boolean empty) {

                // display nothing if the row is empty, otherwise the item count
                setText((empty || items == null) ? "" : "" + items.size());

            }

        });

        // custom comparator for the conditionCountColumn
        conditionCountColumn.setComparator((list1, list2) -> list1.size() <= list2.size() ? 0 : 1);

        // sets the icon of the condition to pattern or profile
        conditionTypeColumn.setCellFactory(new ConditionTypeCellFactory());
    }

    /**
     * Initializes the validation for certain text inputs in order to display an error message (e.g. required).
     */
    private void initializeValidation() {

        nameInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                nameInput.validate();
            }
        });

        descriptionInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                descriptionInput.validate();
            }
        });

    }

    /**
     * Validates all relevant inputs before saving.
     *
     * @return if the inputs are all valid
     */
    private boolean validateBeforeSave() {
        return nameInput.validate()
            && descriptionInput.validate()
            && patternsTable.getItems() != null
            && whitelistChooser.getSelectionModel().getSelectedItem() != null;
    }

    /**
     * Fills the detail form with the selected Configuration.
     *
     * @param configuration the selected Configuration
     */
    public void giveSelectedConfiguration(Configuration configuration) {

        if (configuration != null) {
            this.configuration = configuration;

            nameInput.setText(configuration.getName());
            descriptionInput.setText(configuration.getDescription());

            patternsTable.setItems(FXCollections.observableList(new ArrayList<>(configuration.getPatterns())));
            whitelistChooser.setValue(configuration.getWhitelist());
        } else {
            this.configuration = new Configuration();
        }
    }

    /**
     * Opens a modal window where a whitelist can be selected.
     */
    public void chooseWhitelist() {
        try {
            // create a new FXML loader with the SapSettingsEditDialogController
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChooseWhitelistView.fxml"), bundle);
            CustomWindow customWindow = loader.load();

            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow);
            scene.getStylesheets().add("css/dark-theme.css");
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(App.primaryStage);
            customWindow.initStage(stage);

            // set stage name
            customWindow.setTitle(bundle.getString("selectWhitelistTitle"));

            stage.show();

            // give the dialog the controller
            ChooseWhitelistController chooseWhitelist = loader.getController();
            chooseWhitelist.setParentController(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Opens a modal window where patterns can be selected.
     */
    public void choosePattern() {
        try {
            // create a new FXML loader with the SapSettingsEditDialogController
            ResourceBundle bundle = ResourceBundle.getBundle("lang");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChoosePatternsView.fxml"), bundle);
            CustomWindow customWindow = loader.load();

            // build the scene and add it to the stage
            Scene scene = new Scene(customWindow);
            scene.getStylesheets().add("css/dark-theme.css");
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(App.primaryStage);
            customWindow.initStage(stage);

            // set stage name
            customWindow.setTitle(bundle.getString("selectPatternsTitle"));

            stage.show();

            // give the dialog the currently selected items
            ChoosePatternsController choosePatterns = loader.getController();
            choosePatterns.giveSelectedPatterns(patternsTable.getItems());
            choosePatterns.setParentController(this);

            // choosePatterns.selectedPatternsTable.itemsProperty().bind(this.patternsTable.itemsProperty());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the patternTable items to the given items.
     *
     * @param patterns the given patterns
     */
    public void setPatterns(List<AccessPattern> patterns) {
        ObservableList<AccessPattern> items = FXCollections.observableList(patterns);
        this.patternsTable.setItems(items);
    }

    /**
     * Set the whitelist input to the given Whitelist.
     *
     * @param whitelist the given whitelist
     */
    public void setWhitelist(Whitelist whitelist) {
        whitelistChooser.setValue(whitelist);
    }


    /**
     * Opens a dialog which lets you choose a Whitelist file to import and imports that into the application.
     */
    public void importWhitelist() {

    }

    /**
     * Opens a dialog which lets you choose an AccessPattern file to import and imports that into the application.
     */
    public void importPatterns() {

    }

    /**
     * Saves the changes to the database.
     */
    public void saveChanges(ActionEvent event) throws Exception {

        if (validateBeforeSave()) {

            this.configuration.setName(this.nameInput.getText());
            this.configuration.setDescription(this.descriptionInput.getText());
            this.configuration.setPatterns(patternsTable.getItems());
            this.configuration.setWhitelist(whitelistChooser.getValue());

            // new config, id is null
            if (configuration.getId() == null) {
                AppComponents.getDbContext().createConfig(configuration);
            } else {
                AppComponents.getDbContext().updateConfig(configuration);
            }

            // refresh the configsTable in the parentController
            parentController.updateConfigsTable();
            close(event);
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
     * Sets the parent controller.
     *
     * @param parentController the parent controller
     */
    public void setParentController(ConfigsController parentController) {
        this.parentController = parentController;
    }

    /**
     * WhitelistStringConverter for the whitelistChooser ComboBox.
     */
    private class WhitelistStringConverter extends StringConverter<Whitelist> {
        private Map<String, Whitelist> map = new HashMap<>();

        @Override
        public String toString(Whitelist whitelist) {
            if (whitelist == null) {
                return "";
            }
            String str = whitelist.getName() + " - " + whitelist.getDescription();
            map.put(str, whitelist);
            return str;
        }

        @Override
        public Whitelist fromString(String string) {
            if (!map.containsKey(string)) {
                return null;
            }
            return map.get(string);
        }
    }

    /**
     * PatternStringConverter for the patternChooser ComboBox.
     */
    private class PatternStringConverter extends StringConverter<AccessPattern> {
        private Map<String, AccessPattern> map = new HashMap<>();

        @Override
        public String toString(AccessPattern pattern) {
            if (pattern == null) {
                return "";
            }
            String str = pattern.getUsecaseId() + " - " + pattern.getDescription();
            map.put(str, pattern);
            return str;
        }

        @Override
        public AccessPattern fromString(String string) {
            if (!map.containsKey(string)) {
                return null;
            }
            return map.get(string);
        }
    }
}
