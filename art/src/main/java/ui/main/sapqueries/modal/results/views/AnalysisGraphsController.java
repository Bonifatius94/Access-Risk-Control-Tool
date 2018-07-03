package ui.main.sapqueries.modal.results.views;

import data.entities.CriticalAccessQuery;

import extensions.ResourceBundleHelper;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import ui.AppComponents;


public class AnalysisGraphsController {

    @FXML
    private LineChart<String, Integer> queriesHistoryChart;

    @FXML
    private CategoryAxis chartX;

    @FXML
    private NumberAxis chartY;

    private List<CriticalAccessQuery> relatedQueries;

    private ResourceBundle bundle = ResourceBundleHelper.getInstance().getLanguageBundle();

    /**
     * Gives the controller the result query.
     *
     * @param query the result query
     */
    public void giveResultQuery(CriticalAccessQuery query) throws Exception {

        // get the related queries
        relatedQueries = AppComponents.getInstance().getDbContext().getRelatedSapQueries(query, false);

        if (relatedQueries.size() > 1) {
            createChart();
        }
    }

    private void createChart() throws Exception {

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
            mainSeries.getData().add(new XYChart.Data<>(query.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy\n   HH:mm")), query.getEntries().size()));
        }

        queriesHistoryChart.getData().add(mainSeries);
    }
}
