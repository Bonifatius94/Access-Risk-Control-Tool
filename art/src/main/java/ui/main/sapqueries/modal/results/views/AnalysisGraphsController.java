package ui.main.sapqueries.modal.results.views;

import com.jfoenix.controls.JFXComboBox;

import data.entities.CriticalAccessQuery;

import extensions.ResourceBundleHelper;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import settings.UserSettingsHelper;

public class AnalysisGraphsController {

    @FXML
    private BarChart<String, Integer> barChart;

    @FXML
    private CategoryAxis chartX;

    @FXML
    private NumberAxis chartY;

    @FXML
    private JFXComboBox<String> chartDataChooser;

    private CriticalAccessQuery query;
    private ResourceBundle bundle = ResourceBundleHelper.getInstance().getLanguageBundle();

    /**
     * Gives the controller the result query.
     *
     * @param query the result query
     */
    public void giveResultQuery(CriticalAccessQuery query) throws Exception {

        this.query = query;

        initializeChartChoosers();

        createChart();
    }

    private void initializeChartChoosers() {
        chartDataChooser.getItems().setAll(bundle.getString("pattern"), bundle.getString("username"));
        chartDataChooser.getSelectionModel().select(0);

        chartDataChooser.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    updateChart();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
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

        barChart.setLegendVisible(false);

        updateChart();
    }

    /**
     * Updates the chart's main series.
     */
    private void updateChart() throws Exception {

        // if there was data, clear it
        if (barChart.getData() != null) {
            barChart.getData().clear();
        }

        Map<String, Integer> itemsXCount;

        if (chartDataChooser.getSelectionModel().getSelectedItem().equals(bundle.getString("pattern"))) {

            // group by usecase id
            itemsXCount =
                query.getEntries().stream().map(x -> x.getAccessPattern().getUsecaseId())
                    .collect(Collectors.toMap(x -> x, x -> 1, Integer::sum));

            barChart.setTitle(bundle.getString("usecaseIdViolations"));

        } else {

            // group by username
            itemsXCount =
                query.getEntries().stream().map(x -> x.getUsername())
                    .collect(Collectors.toMap(x -> x, x -> 1, Integer::sum));

            barChart.setTitle(bundle.getString("usernameViolations"));
        }

        XYChart.Series<String, Integer> mainSeries = new XYChart.Series<>();

        // calculate maximum of entries for upper bound (round up to nearest 10)
        int maximum = itemsXCount.values().stream().max(Integer::compareTo).get();
        int upperBound = ((maximum + 10) / 10) * 10;
        chartY.setUpperBound(upperBound);

        int all = 0;
        for (Map.Entry<String, Integer> entry : itemsXCount.entrySet()) {
            all += entry.getValue();
        }

        // compute average
        int average = all / itemsXCount.size();

        for (Map.Entry<String, Integer> entry : itemsXCount.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).collect(Collectors.toList())) {
            XYChart.Data<String, Integer> data = createData(entry.getKey(), entry.getValue(), average);

            mainSeries.getData().add(data);
        }

        // add the main series to the chart
        barChart.getData().add(mainSeries);
    }

    /**
     * Creates the data and adds a label with the value.
     */
    private XYChart.Data<String, Integer> createData(String key, int value, int average) {

        Label label = new Label("" + value);
        label.getStyleClass().add("bar-value");
        Group group = new Group(label);
        StackPane.setAlignment(group, Pos.BOTTOM_CENTER);
        StackPane.setMargin(group, new Insets(0, 0, 5, 0));

        StackPane node = new StackPane();
        node.getChildren().add(group);

        // color the nodes that are above the average
        if (value > average) {
            node.getStyleClass().add("warning-bar");
        }

        XYChart.Data<String, Integer> data = new XYChart.Data<>(key, value);
        data.setNode(node);

        return data;
    }

    /**
     * Makes a snapshot of the given chart and return a BufferedImage of it.
     */
    private BufferedImage graphToBufferedImage(BarChart chart) throws Exception {
        Scene scene = new Scene(new AnchorPane(chart));

        // add styles to the scene
        scene.getRoot().setStyle(new UserSettingsHelper().loadUserSettings().getDarkThemeCss());
        scene.getStylesheets().add("css/bar-export.css");

        // add main-root to the root so tooltip styling is not affected
        scene.getRoot().getStyleClass().add("main-root");

        WritableImage image = scene.snapshot(null);

        return SwingFXUtils.fromFXImage(image, null);
    }

    /**
     * Exports the graphs.
     */
    public List<BufferedImage> getExportedGraphs() throws Exception {

        List<BufferedImage> images = new ArrayList<>();

        images.add(exportUsecaseGraph());
        images.add(exportUsernameGraph());

        return images;
    }

    /**
     * Exports the UsecaseGraph as a BufferedImage.
     */
    @SuppressWarnings("all")
    private BufferedImage exportUsecaseGraph() throws Exception {

        // y axis properties
        NumberAxis numberAxis = new NumberAxis();
        numberAxis.setAutoRanging(false);
        numberAxis.setLowerBound(0);
        numberAxis.setTickUnit(5);
        numberAxis.setAnimated(false);

        // x axis properties
        CategoryAxis categoryAxis = new CategoryAxis();
        categoryAxis.setAutoRanging(true);
        categoryAxis.setAnimated(false);

        BarChart<String, Integer> chart = new BarChart(categoryAxis, numberAxis);

        chart.setLegendVisible(false);

        Map<String, Integer> itemsXCount;

        // group by usecase id
        itemsXCount =
            query.getEntries().stream().map(x -> x.getAccessPattern().getUsecaseId())
                .collect(Collectors.toMap(x -> x, x -> 1, Integer::sum));

        chart.setTitle(bundle.getString("usecaseIdViolations"));

        XYChart.Series<String, Integer> mainSeries = new XYChart.Series<>();

        // calculate maximum of entries for upper bound (round up to nearest 10)
        int maximum = itemsXCount.values().stream().max(Integer::compareTo).get();
        int upperBound = ((maximum + 10) / 10) * 10;
        numberAxis.setUpperBound(upperBound);

        int all = 0;
        for (Map.Entry<String, Integer> entry : itemsXCount.entrySet()) {
            all += entry.getValue();
        }

        // compute average
        int average = all / itemsXCount.size();

        for (Map.Entry<String, Integer> entry : itemsXCount.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).collect(Collectors.toList())) {
            XYChart.Data<String, Integer> data = createData(entry.getKey(), entry.getValue(), average);

            mainSeries.getData().add(data);
        }

        // add the main series to the chart
        chart.getData().add(mainSeries);

        // cap max chart size
        chart.setMaxWidth(800);

        return graphToBufferedImage(chart);
    }

    /**
     * Exports the UsernameGraph as a BufferedImage.
     */
    @SuppressWarnings("all")
    private BufferedImage exportUsernameGraph() throws Exception {

        // y axis properties
        NumberAxis numberAxis = new NumberAxis();
        numberAxis.setAutoRanging(false);
        numberAxis.setLowerBound(0);
        numberAxis.setTickUnit(5);
        numberAxis.setAnimated(false);

        // x axis properties
        CategoryAxis categoryAxis = new CategoryAxis();
        categoryAxis.setAutoRanging(true);
        categoryAxis.setAnimated(false);

        BarChart<Integer, String> chart = new BarChart(numberAxis, categoryAxis);

        chart.setLegendVisible(false);

        Map<String, Integer> itemsXCount;

        // group by username
        itemsXCount =
            query.getEntries().stream().map(x -> x.getUsername())
                .collect(Collectors.toMap(x -> x, x -> 1, Integer::sum));

        chart.setTitle(bundle.getString("usernameViolations"));

        // calculate maximum of entries for upper bound (round up to nearest 10)
        int maximum = itemsXCount.values().stream().max(Integer::compareTo).get();
        int upperBound = ((maximum + 10) / 10) * 10;
        numberAxis.setUpperBound(upperBound);

        int all = 0;
        for (Map.Entry<String, Integer> entry : itemsXCount.entrySet()) {
            all += entry.getValue();
        }

        XYChart.Series<Integer, String> mainSeries = new XYChart.Series<>();

        // compute average
        int average = all / itemsXCount.size();

        for (Map.Entry<String, Integer> entry : itemsXCount.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).collect(Collectors.toList())) {
            XYChart.Data<Integer, String> data = createDataInverted(entry.getKey(), entry.getValue(), average);

            mainSeries.getData().add(data);
        }

        // add the main series to the chart
        chart.getData().add(mainSeries);

        // cap max chart size
        chart.setPrefHeight(itemsXCount.size() * 40);
        chart.setMaxHeight(1000);

        return graphToBufferedImage(chart);
    }

    /**
     * Creates the data and adds a label with the value.
     */
    private XYChart.Data<Integer, String> createDataInverted(String key, int value, int average) {

        Label label = new Label("" + value);
        label.getStyleClass().add("bar-value");
        Group group = new Group(label);
        StackPane.setAlignment(group, Pos.CENTER_RIGHT);
        StackPane.setMargin(group, new Insets(0, 5, 0, 0));

        StackPane node = new StackPane();
        node.getChildren().add(group);

        // color the nodes that are above the average
        if (value > average) {
            node.getStyleClass().add("warning-bar");
        }

        XYChart.Data<Integer, String> data = new XYChart.Data<>(value, key);
        data.setNode(node);

        return data;
    }
}
