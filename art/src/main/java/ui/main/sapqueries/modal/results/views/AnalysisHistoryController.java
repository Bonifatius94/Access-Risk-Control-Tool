package ui.main.sapqueries.modal.results.views;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import data.entities.AccessPattern;
import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;

import data.entities.Whitelist;
import extensions.ResourceBundleHelper;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import ui.AppComponents;

public class AnalysisHistoryController {

    @FXML
    private LineChart<String, Integer> queriesHistoryChart;

    @FXML
    private CategoryAxis chartX;

    @FXML
    private NumberAxis chartY;

    @FXML
    private VBox additionalDataBox;

    @FXML
    private JFXComboBox<AccessPattern> data0Box;

    @FXML
    private JFXComboBox<AccessPattern> data1Box;

    @FXML
    private JFXComboBox<AccessPattern> data2Box;

    @FXML
    private JFXDatePicker startDatePicker;

    @FXML
    private JFXDatePicker endDatePicker;

    @FXML
    private JFXCheckBox showArchived;

    ObservableList<AccessPattern> allEntries;
    private List<CriticalAccessQuery> relatedQueries;
    private CriticalAccessQuery query;

    private XYChart.Series<String, Integer> additionalSeries0;
    private XYChart.Series<String, Integer> additionalSeries1;
    private XYChart.Series<String, Integer> additionalSeries2;

    private ResourceBundle bundle = ResourceBundleHelper.getInstance().getLanguageBundle();

    /**
     * Gives the controller the result query.
     *
     * @param query the result query
     */
    public void giveData(CriticalAccessQuery query) throws Exception {

        this.query = query;

        allEntries = FXCollections.observableArrayList();

        createChart();

        initializeAdditionalDataBox();

        initializeDatePickers();
    }

    /**
     * Creates the history chart.
     */
    private void createChart() throws Exception {

        // y axis properties
        chartY.setAutoRanging(false);
        chartY.setLowerBound(0);
        chartY.setTickUnit(5);

        // x axis properties
        chartX.setAutoRanging(true);
        chartX.setAnimated(false);

        updateChart();
    }

    /**
     * Updates the chart's main series.
     */
    private void updateChart() throws Exception {

        // if there was data, clear it
        if (queriesHistoryChart.getData() != null) {
            queriesHistoryChart.getData().clear();
        }

        // get the related queries
        List<CriticalAccessQuery> relatedQueries =
            AppComponents.getInstance().getDbContext()
            .getRelatedFilteredCriticalAccessQueries(query,
                showArchived.isSelected(),
                null,
                startDatePicker.getValue() != null ? startDatePicker.getValue().atStartOfDay(ZoneOffset.UTC) : null,
                endDatePicker.getValue() != null ? endDatePicker.getValue().atStartOfDay(ZoneOffset.UTC) : null,
                0);

        // sort by date
        relatedQueries = relatedQueries.stream().sorted(Comparator.comparing(CriticalAccessQuery::getCreatedAt)).collect(Collectors.toList());

        this.relatedQueries = relatedQueries;

        // fill the allEntries Collection
        List<AccessPattern> currentEntries = new ArrayList<>();
        for (CriticalAccessQuery query : relatedQueries) {
            currentEntries.addAll(query.getEntries().stream().map(x -> x.getAccessPattern()).collect(Collectors.toList()));
        }

        allEntries = FXCollections.observableList(currentEntries.stream().sorted(Comparator.comparing(AccessPattern::getUsecaseId)).distinct().collect(Collectors.toList()));

        // calculate maximum of entries for upper bound (round up to nearest 10)
        int maximum = relatedQueries.stream().map(x -> x.getEntries().size()).max(Integer::compareTo).get();
        int upperBound = ((maximum + 10) / 10) * 10;
        chartY.setUpperBound(upperBound);

        // if there are no violations (so also no patterns), don't show the additional data selection
        if (maximum == 0) {
            additionalDataBox.setVisible(false);
        } else {
            additionalDataBox.setVisible(true);
        }

        XYChart.Series<String, Integer> mainSeries = new XYChart.Series<>();
        mainSeries.setName(bundle.getString("all"));

        // test if the data to display is too much
        if (relatedQueries.size() > 8) {

            // display only date to use automatic clustering from the line chart
            for (CriticalAccessQuery query : relatedQueries) {
                mainSeries.getData().add(new XYChart.Data<>(query.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), query.getEntries().size()));
            }

        } else {

            // display date + time
            for (CriticalAccessQuery query : relatedQueries) {
                mainSeries.getData().add(new XYChart.Data<>(query.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy\n    HH:mm")), query.getEntries().size()));
            }

        }



        // add the main series to the chart
        queriesHistoryChart.getData().add(mainSeries);

        // add the additional series to the chart
        if (data0Box.getValue() != null) {
            addSeries0ToChart(data0Box.getValue());
        }
        if (data1Box.getValue() != null) {
            addSeries1ToChart(data1Box.getValue());
        }
        if (data2Box.getValue() != null) {
            addSeries2ToChart(data2Box.getValue());
        }

        // add the hover value display
        showValuesOnHover();
    }

    /**
     * Browsing through the Data and applying ToolTip.
     */
    private void showValuesOnHover() {
        for (XYChart.Series<String, Integer> s : queriesHistoryChart.getData()) {
            for (XYChart.Data<String, Integer> d : s.getData()) {
                // style current query node differently
                if (d.getXValue().trim().equals(query.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy\n    HH:mm")))
                    || d.getXValue().trim().equals(query.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))) {
                    d.getNode().getStyleClass().add("current-node");
                    Tooltip.install(d.getNode(), new Tooltip("Current Query\n" + bundle.getString("violations") + ": " + d.getYValue()));
                } else {
                    Tooltip.install(d.getNode(), new Tooltip(bundle.getString("violations") + ": " + d.getYValue()));
                }
            }
        }
    }

    /**
     * Initialize the additional data boxes.
     */
    private void initializeAdditionalDataBox() {

        data0Box.setConverter(new AccessPatternStringConverter());
        data1Box.setConverter(new AccessPatternStringConverter());
        data2Box.setConverter(new AccessPatternStringConverter());

        // add the patterns to the comboboxes
        data0Box.setItems(allEntries);
        data1Box.setItems(allEntries);
        data2Box.setItems(allEntries);

        // listen to combo box value changes and add the according series

        data0Box.valueProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                addSeries0ToChart(newValue);
            }
        }));

        data1Box.valueProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                addSeries1ToChart(newValue);
            }
        }));

        data2Box.valueProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                addSeries2ToChart(newValue);
            }
        }));
    }

    /**
     * Add the series0 to the chart.
     */
    private void addSeries0ToChart(AccessPattern pattern) {

        // remove old data
        queriesHistoryChart.getData().remove(additionalSeries0);

        additionalSeries0 = new XYChart.Series<>();
        additionalSeries0.setName(pattern.getUsecaseId());

        // test if the data to display is too much
        if (relatedQueries.size() > 8) {

            // create new series for the data and add it
            for (CriticalAccessQuery query : relatedQueries) {

                int result = (int) query.getEntries().stream().map(x -> x.getAccessPattern().getUsecaseId()).filter(y -> y.equals(pattern.getUsecaseId())).count();

                additionalSeries0.getData().add(new XYChart.Data<>(query.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), result));
            }

        } else {

            // display date + time
            for (CriticalAccessQuery query : relatedQueries) {

                int result = (int) query.getEntries().stream().map(x -> x.getAccessPattern().getUsecaseId()).filter(y -> y.equals(pattern.getUsecaseId())).count();

                additionalSeries0.getData().add(new XYChart.Data<>(query.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy\n    HH:mm")), result));
            }

        }

        queriesHistoryChart.getData().add(additionalSeries0);

        showValuesOnHover();
    }

    /**
     * Add the series1 to the chart.
     */
    private void addSeries1ToChart(AccessPattern pattern) {

        // remove old data
        queriesHistoryChart.getData().remove(additionalSeries1);

        // create new series for the data and add it
        additionalSeries1 = new XYChart.Series<>();
        additionalSeries1.setName(pattern.getUsecaseId());

        // test if the data to display is too much
        if (relatedQueries.size() > 8) {

            // create new series for the data and add it
            for (CriticalAccessQuery query : relatedQueries) {

                int result = (int) query.getEntries().stream().map(x -> x.getAccessPattern().getUsecaseId()).filter(y -> y.equals(pattern.getUsecaseId())).count();

                additionalSeries1.getData().add(new XYChart.Data<>(query.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), result));
            }

        } else {

            // display date + time
            for (CriticalAccessQuery query : relatedQueries) {

                int result = (int) query.getEntries().stream().map(x -> x.getAccessPattern().getUsecaseId()).filter(y -> y.equals(pattern.getUsecaseId())).count();

                additionalSeries1.getData().add(new XYChart.Data<>(query.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy\n    HH:mm")), result));
            }

        }

        queriesHistoryChart.getData().add(additionalSeries1);

        showValuesOnHover();
    }

    /**
     * Add the series2 to the chart.
     */
    private void addSeries2ToChart(AccessPattern pattern) {

        // remove old data
        queriesHistoryChart.getData().remove(additionalSeries2);

        // create new series for the data and add it
        additionalSeries2 = new XYChart.Series<>();
        additionalSeries2.setName(pattern.getUsecaseId());

        // test if the data to display is too much
        if (relatedQueries.size() > 8) {

            // create new series for the data and add it
            for (CriticalAccessQuery query : relatedQueries) {

                int result = (int) query.getEntries().stream().map(x -> x.getAccessPattern().getUsecaseId()).filter(y -> y.equals(pattern.getUsecaseId())).count();

                additionalSeries2.getData().add(new XYChart.Data<>(query.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), result));
            }

        } else {

            // display date + time
            for (CriticalAccessQuery query : relatedQueries) {

                int result = (int) query.getEntries().stream().map(x -> x.getAccessPattern().getUsecaseId()).filter(y -> y.equals(pattern.getUsecaseId())).count();

                additionalSeries2.getData().add(new XYChart.Data<>(query.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy\n    HH:mm")), result));
            }

        }

        queriesHistoryChart.getData().add(additionalSeries2);

        showValuesOnHover();
    }


    /**
     * AccessPatternStringConverter for the ComboBoxes.
     */
    private class AccessPatternStringConverter extends StringConverter<AccessPattern> {
        private Map<String, AccessPattern> map = new HashMap<>();

        @Override
        public String toString(AccessPattern accessPattern) {
            if (accessPattern == null) {
                return "";
            }
            String str = bundle.getString("pattern") + " " + accessPattern.getUsecaseId();
            map.put(str, accessPattern);
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

    /**
     * Resets Data0Box.
     */
    public void resetData0Box() {
        data0Box.setValue(null);

        // remove old data
        queriesHistoryChart.getData().remove(additionalSeries0);
    }

    /**
     * Resets Data1Box.
     */
    public void resetData1Box() {
        data1Box.setValue(null);

        // remove old data
        queriesHistoryChart.getData().remove(additionalSeries1);
    }

    /**
     * Resets Data2Box.
     */
    public void resetData2Box() {
        data2Box.setValue(null);

        // remove old data
        queriesHistoryChart.getData().remove(additionalSeries2);
    }

    private void initializeDatePickers() {
        // startDate binding
        startDatePicker.valueProperty().addListener((ol, oldValue, newValue) -> {

            // startDate not after endDate
            if (endDatePicker.getValue() != null) {
                if (newValue != null && endDatePicker.getValue().toEpochDay() < newValue.toEpochDay()) {
                    startDatePicker.setValue(endDatePicker.getValue());
                }
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
        });
        endDatePicker.setEditable(false);
    }

    public void applyFilters() throws Exception {
        updateChart();
    }

    public void resetFilters() throws Exception {
        showArchived.setSelected(false);

        startDatePicker.setValue(null);
        endDatePicker.setValue(null);

        updateChart();
    }
}
