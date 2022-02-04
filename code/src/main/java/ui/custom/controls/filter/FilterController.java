package ui.custom.controls.filter;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;


public class FilterController {

    @FXML
    private JFXTextField searchStringInput;

    @FXML
    private JFXDatePicker startDatePicker;

    @FXML
    private JFXDatePicker endDatePicker;

    @FXML
    private JFXToggleButton showArchivedToggle;

    @FXML
    private JFXButton applyFilterButton;

    public SimpleObjectProperty<ZonedDateTime> startDateProperty;
    public SimpleObjectProperty<ZonedDateTime> endDateProperty;
    public SimpleStringProperty searchStringProperty;
    public SimpleBooleanProperty showArchivedProperty;
    public SimpleBooleanProperty shouldFilterProperty;

    /**
     * Initializes the filter view with all filter inputs.
     */
    @FXML
    public void initialize() {

        // initialize the properties
        startDateProperty = new SimpleObjectProperty<>();
        endDateProperty = new SimpleObjectProperty<>();
        searchStringProperty = new SimpleStringProperty();
        showArchivedProperty = new SimpleBooleanProperty();
        shouldFilterProperty = new SimpleBooleanProperty();

        // bind the properties to the actual inputs
        searchStringProperty.bind(searchStringInput.textProperty());
        showArchivedProperty.bind(showArchivedToggle.selectedProperty());
        shouldFilterProperty.bind(applyFilterButton.pressedProperty());

        // startDate binding
        startDatePicker.valueProperty().addListener((ol, oldValue, newValue) -> {

            // startDate not after endDate
            if (endDatePicker.getValue() != null) {
                if (newValue != null && endDatePicker.getValue().toEpochDay() < newValue.toEpochDay()) {
                    startDatePicker.setValue(endDatePicker.getValue());
                }
            }
            if (startDatePicker.getValue() != null) {
                startDateProperty.setValue(startDatePicker.getValue().atStartOfDay(ZoneOffset.UTC));
            }
            if (newValue == null) {
                startDateProperty.setValue(null);
            }
        });
        startDatePicker.setEditable(false);

        // endDate binding
        endDatePicker.valueProperty().addListener((ol, oldValue, newValue) -> {

            // endDate not before startDate
            if (startDatePicker.getValue() != null) {
                if (newValue != null && startDatePicker.getValue().toEpochDay() > newValue.toEpochDay()) {
                    endDatePicker.setValue(startDatePicker.getValue());
                }
            }
            if (endDatePicker.getValue() != null) {
                endDateProperty.setValue(endDatePicker.getValue().atStartOfDay(ZoneOffset.UTC));
            }
            if (newValue == null) {
                endDateProperty.setValue(null);
            }
        });
        endDatePicker.setEditable(false);
    }

    /**
     * Resets the filters completely and lets the parent know that it should filter.
     */
    public void resetFilter() {
        searchStringInput.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        showArchivedToggle.selectedProperty().set(false);

        shouldFilterProperty.unbind();
        shouldFilterProperty.setValue(true);
        shouldFilterProperty.setValue(false);
        shouldFilterProperty.bind(applyFilterButton.pressedProperty());
    }

    /**
     * Applies the filter on hitting enter in the searchstring textfield.
     */
    public void applyFiltersOnEnter() {
        shouldFilterProperty.unbind();
        shouldFilterProperty.setValue(true);
        shouldFilterProperty.setValue(false);
        shouldFilterProperty.bind(applyFilterButton.pressedProperty());
    }

    /**
     * Enables the archived filter.
     */
    public void enableArchivedFilter() {
        showArchivedToggle.setVisible(true);
    }
}
