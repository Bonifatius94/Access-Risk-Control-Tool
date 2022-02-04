package ui.main.configs.modal;

import com.jfoenix.controls.JFXButton;

import data.entities.AccessCondition;
import data.entities.AccessPattern;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

import extensions.ResourceBundleHelper;

import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import ui.AppComponents;
import ui.custom.controls.ButtonCell;
import ui.custom.controls.ConditionTypeCellFactory;
import ui.custom.controls.CustomAlert;
import ui.custom.controls.filter.FilterController;


public class ChoosePatternsController {

    @FXML
    public TableView<AccessPattern> allPatternsTable;

    @FXML
    public TableView<AccessPattern> selectedPatternsTable;

    @FXML
    public TableColumn<AccessPattern, JFXButton> selectedPatternsTableDeleteColumn;

    @FXML
    public TableColumn<AccessPattern, Set<AccessCondition>> allPatternsTableConditionCountColumn;

    @FXML
    public TableColumn<AccessPattern, Set<AccessCondition>> allPatternsTableConditionTypeColumn;

    @FXML
    public TableColumn<AccessPattern, Set<AccessCondition>> selectedPatternsTableConditionTypeColumn;

    @FXML
    public JFXButton addToSelectedButton;

    @FXML
    public JFXButton removeFromSelectedButton;

    @FXML
    public FilterController filterController;


    private ConfigsFormController parentController;
    private List<AccessPattern> alreadySelectedPatterns;
    private ResourceBundle bundle;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {

        // load the ResourceBundle
        bundle = ResourceBundleHelper.getInstance().getLanguageBundle();

        // initialize the tables
        initializePatternsTables();

        // check if the filters are applied
        filterController.shouldFilterProperty.addListener((o, oldValue, newValue) -> {
            if (newValue) {
                try {
                    updateAllPatternsTable();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Initializes the patterns table.
     */
    private void initializePatternsTables() {

        // set selection mode to MULTIPLE
        allPatternsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        selectedPatternsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // bind button disabled to tables selection models
        addToSelectedButton.disableProperty().bind(Bindings.size(allPatternsTable.getSelectionModel().getSelectedItems()).isEqualTo(0));
        removeFromSelectedButton.disableProperty().bind(Bindings.size(selectedPatternsTable.getSelectionModel().getSelectedItems()).isEqualTo(0));

        initializeAllPatternsTable();
        initializeSelectedPatternsTable();
    }

    /**
     * Initializes the allPatternsTable columns.
     */
    private void initializeAllPatternsTable() {

        // overwrite the column in which the number of useCases is displayed
        allPatternsTableConditionCountColumn.setCellFactory(col -> new TableCell<AccessPattern, Set<AccessCondition>>() {

            @Override
            protected void updateItem(Set<AccessCondition> items, boolean empty) {

                // display nothing if the row is empty, otherwise the item count
                setText((empty || items == null) ? "" : "" + items.size());

            }

        });

        // custom comparator for the conditionCountColumn
        allPatternsTableConditionCountColumn.setComparator((list1, list2) -> list1.size() <= list2.size() ? 0 : 1);

        // sets the icon of the condition to pattern or profile
        allPatternsTableConditionTypeColumn.setCellFactory(new ConditionTypeCellFactory());
    }

    /**
     * Initializes the selectedPatternsTable columns.
     */
    private void initializeSelectedPatternsTable() {
        // Add the delete column
        selectedPatternsTableDeleteColumn.setCellFactory(ButtonCell.forTableColumn(MaterialDesignIcon.DELETE, (AccessPattern accessPattern) -> {
            selectedPatternsTable.getItems().remove(accessPattern);
            selectedPatternsTable.refresh();
            try {
                updateAllPatternsTable();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return accessPattern;
        }));

        // sets the icon of the condition to pattern or profile
        selectedPatternsTableConditionTypeColumn.setCellFactory(new ConditionTypeCellFactory());
    }

    /**
     * Removes the selected patterns from the selectedList.
     */
    public void removeFromSelected() throws Exception {
        if (selectedPatternsTable.getSelectionModel().getSelectedItems() != null) {
            List<AccessPattern> selectedPatterns = selectedPatternsTable.getSelectionModel().getSelectedItems();
            selectedPatternsTable.getItems().removeAll(selectedPatterns);
            selectedPatternsTable.getSelectionModel().clearSelection();
            selectedPatternsTable.refresh();
            updateAllPatternsTable();
        }
    }

    /**
     * Adds the selected patterns to the selectedList.
     */
    public void addToSelected() throws  Exception {
        if (allPatternsTable.getSelectionModel().getSelectedItems() != null) {
            List<AccessPattern> selectedPatterns = allPatternsTable.getSelectionModel().getSelectedItems();
            if (!hasDuplicateUseCaseIds(selectedPatterns)) {
                selectedPatternsTable.getItems().addAll(selectedPatterns);
                allPatternsTable.getSelectionModel().clearSelection();
                selectedPatternsTable.refresh();
                updateAllPatternsTable();
            } else {
                CustomAlert alert = new CustomAlert(Alert.AlertType.WARNING, bundle.getString("patternsDuplicateTitle"),  bundle.getString("patternsDuplicateMessage"));
                alert.showAndWait();
            }
        }
    }

    /**
     * Returns if the list has duplicate usecaseIds.
     * @param patterns the list that is tested
     * @return if the list has duplicates
     */
    private boolean hasDuplicateUseCaseIds(List<AccessPattern> patterns) {
        List<String> usecaseIds = patterns.stream().map(x -> x.getUsecaseId()).collect(Collectors.toList());
        return usecaseIds.size() != usecaseIds.stream().distinct().count();
    }

    /**
     * Prefills the selectedTable with the given patterns.
     *
     * @param selectedPatterns the already selected patterns
     */
    public void giveSelectedPatterns(List<AccessPattern> selectedPatterns) throws Exception {

        this.alreadySelectedPatterns = selectedPatterns;

        // add the selected patterns to the selected list
        if (selectedPatterns != null || selectedPatterns.size() != 0) {
            ObservableList<AccessPattern> items = FXCollections.observableList(selectedPatterns);
            this.selectedPatternsTable.setItems(items);
        }

        // fill the allPatternsTable
        updateAllPatternsTable();
    }

    /**
     * Provides the data for the patternTable.
     */
    private void updateAllPatternsTable() throws Exception {

        List<AccessPattern> patterns = AppComponents.getInstance().getDbContext().getFilteredPatterns(filterController.showArchivedProperty.getValue(),
            filterController.searchStringProperty.getValue(), filterController.startDateProperty.getValue(),
            filterController.endDateProperty.getValue(), 0);

        // remove all entries that are already in the selectedList
        patterns = patterns.stream().filter(x -> {
            for (AccessPattern pattern : this.alreadySelectedPatterns) {
                // not the same id or usecaseId
                if (x.getId().equals(pattern.getId()) || x.getUsecaseId().equals(pattern.getUsecaseId())) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());

        ObservableList<AccessPattern> list = FXCollections.observableList(patterns);

        allPatternsTable.setItems(list);
        allPatternsTable.refresh();

    }

    /**
     * Saves the changes to the previous window.
     */
    public void saveChanges(ActionEvent event) {
        this.parentController.setPatterns(this.selectedPatternsTable.getItems());
        this.close(event);
    }

    /**
     * Sets the parent controller so we can give it some data.
     *
     * @param controller the parent controller
     */
    public void setParentController(ConfigsFormController controller) {
        this.parentController = controller;
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
