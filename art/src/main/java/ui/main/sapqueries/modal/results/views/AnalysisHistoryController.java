package ui.main.sapqueries.modal.results.views;

import com.jfoenix.controls.JFXComboBox;
import data.entities.AccessPattern;
import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;

import data.entities.Whitelist;
import extensions.ResourceBundleHelper;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

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
    private JFXComboBox<CriticalAccessEntry> data0Box;

    @FXML
    private JFXComboBox<CriticalAccessEntry> data1Box;

    @FXML
    private JFXComboBox<CriticalAccessEntry> data2Box;

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
    public void giveData(CriticalAccessQuery query, List<CriticalAccessQuery> relatedQueries) {

        this.query = query;

        // get the related queries
        this.relatedQueries = relatedQueries;

        if (relatedQueries.size() > 1) {
            createChart();
        }

        initializeAdditionalDataBox();
    }

    /**
     * Creates the history chart.
     */
    private void createChart() {

        // calculate maximum of entries for upper bound
        int maximum = relatedQueries.stream().map(x -> x.getEntries().size()).max(Integer::compareTo).get();
        maximum = ((maximum + 10) / 10) * 10;

        chartY.setAutoRanging(false);
        chartY.setUpperBound(maximum);
        chartY.setLowerBound(0);
        chartY.setTickUnit(5);

        chartX.setAutoRanging(true);
        chartX.setAnimated(false);

        XYChart.Series<String, Integer> mainSeries = new XYChart.Series<>();
        mainSeries.setName(bundle.getString("numberOfViolations"));

        for (CriticalAccessQuery query : relatedQueries) {
            mainSeries.getData().add(new XYChart.Data<>(query.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy\n    HH:mm")), query.getEntries().size()));
        }

        queriesHistoryChart.getData().add(mainSeries);

        showValuesOnHover();

    }

    /**
     * Browsing through the Data and applying ToolTip.
     */
    private void showValuesOnHover() {
        for (XYChart.Series<String, Integer> s : queriesHistoryChart.getData()) {
            for (XYChart.Data<String, Integer> d : s.getData()) {
                Tooltip.install(d.getNode(), new Tooltip(bundle.getString("violations") + " " + d.getYValue()));

                // style current query differently
                if (d.getXValue().trim().equals(query.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy\n    HH:mm")))) {
                    d.getNode().getStyleClass().add("current-node");
                }
            }
        }
    }

    /**
     * Initialize the additional data boxes.
     */
    private void initializeAdditionalDataBox() {

        data0Box.setConverter(new CriticalAccessEntryStringConverter());
        data1Box.setConverter(new CriticalAccessEntryStringConverter());
        data2Box.setConverter(new CriticalAccessEntryStringConverter());

        List<CriticalAccessEntry> allEntries = new ArrayList<>();

        for (CriticalAccessQuery query : relatedQueries) {
            allEntries.addAll(query.getEntries());
        }

        allEntries = allEntries.stream().distinct().collect(Collectors.toList());

        data0Box.getItems().addAll(allEntries);
        data1Box.getItems().addAll(allEntries);
        data2Box.getItems().addAll(allEntries);

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
    private void addSeries0ToChart(CriticalAccessEntry entry) {

        queriesHistoryChart.getData().remove(additionalSeries0);

        additionalSeries0 = new XYChart.Series<>();
        additionalSeries0.setName(bundle.getString("numberOfViolations"));

        for (CriticalAccessQuery query : relatedQueries) {

            int result = (int) query.getEntries().stream().map(x -> x.getAccessPattern().getUsecaseId()).filter(y -> y.equals(entry.getAccessPattern().getUsecaseId())).count();

            additionalSeries0.getData().add(new XYChart.Data<>(query.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy\n    HH:mm")), result));
        }

        queriesHistoryChart.getData().add(additionalSeries0);
    }

    /**
     * Add the series1 to the chart.
     */
    private void addSeries1ToChart(CriticalAccessEntry entry) {

        queriesHistoryChart.getData().remove(additionalSeries1);


        additionalSeries1 = new XYChart.Series<>();
        additionalSeries1.setName(bundle.getString("numberOfViolations"));

        for (CriticalAccessQuery query : relatedQueries) {

            int result = (int) query.getEntries().stream().map(x -> x.getAccessPattern().getUsecaseId()).filter(y -> y.equals(entry.getAccessPattern().getUsecaseId())).count();

            additionalSeries1.getData().add(new XYChart.Data<>(query.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy\n    HH:mm")), result));
        }

        queriesHistoryChart.getData().add(additionalSeries1);
    }

    /**
     * Add the series2 to the chart.
     */
    private void addSeries2ToChart(CriticalAccessEntry entry) {

        queriesHistoryChart.getData().remove(additionalSeries2);


        additionalSeries2 = new XYChart.Series<>();
        additionalSeries2.setName(bundle.getString("numberOfViolations"));

        for (CriticalAccessQuery query : relatedQueries) {

            int result = (int) query.getEntries().stream().map(x -> x.getAccessPattern().getUsecaseId()).filter(y -> y.equals(entry.getAccessPattern().getUsecaseId())).count();

            additionalSeries2.getData().add(new XYChart.Data<>(query.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy\n    HH:mm")), result));
        }

        queriesHistoryChart.getData().add(additionalSeries2);
    }


    /**
     * WhitelistStringConverter for the whitelistChooser ComboBox.
     */
    private class CriticalAccessEntryStringConverter extends StringConverter<CriticalAccessEntry> {
        private Map<String, CriticalAccessEntry> map = new HashMap<>();

        @Override
        public String toString(CriticalAccessEntry criticalAccessEntry) {
            if (criticalAccessEntry == null) {
                return "";
            }
            String str = criticalAccessEntry.getAccessPattern().getUsecaseId();
            map.put(str, criticalAccessEntry);
            return str;
        }

        @Override
        public CriticalAccessEntry fromString(String string) {
            if (!map.containsKey(string)) {
                return null;
            }
            return map.get(string);
        }
    }
}
