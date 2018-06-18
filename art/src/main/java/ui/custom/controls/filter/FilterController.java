package ui.custom.controls.filter;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;

import java.time.LocalDate;

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

    @FXML
    private JFXButton resetFilterButton;


    public SimpleObjectProperty<LocalDate> startDateProperty;
    public SimpleObjectProperty<LocalDate> endDateProperty;
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
        startDateProperty.bind(startDatePicker.valueProperty());
        endDateProperty.bind(endDatePicker.valueProperty());
        searchStringProperty.bind(searchStringInput.textProperty());
        showArchivedProperty.bind(showArchivedToggle.selectedProperty());
        shouldFilterProperty.bind(applyFilterButton.pressedProperty());
    }

    /**
     * Resets the filters completely.
     */
    public void resetFilter() {
        searchStringInput.setText("");
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        showArchivedToggle.selectedProperty().set(false);
    }
}
