package ui;

import excel.AuthorizationPattern;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

import java.util.HashMap;
import java.util.List;

public class ConfigEditorController {

    @FXML
    private TextField tfUsecaseID;
    private StringProperty usecaseID = new SimpleStringProperty("");

    @FXML
    private TextArea taDescription;
    private StringProperty description = new SimpleStringProperty("");

    @FXML
    private TitledPane tpConfigDetails;

    @FXML
    private TableView tblConfigs;
    private ObservableList<AuthorizationPattern> patterns = FXCollections.observableArrayList();

    private enum DetailViewType {
        ComplexPattern,
        SimplePattern,
        SimpleProfile
    }

    private static final HashMap<DetailViewType, String> detailViewResources = new HashMap<>();
    static
    {
        // init hash map
        detailViewResources.put(DetailViewType.SimpleProfile, "");
        detailViewResources.put(DetailViewType.SimplePattern, "");
        detailViewResources.put(DetailViewType.ComplexPattern, "");
    }

    @SuppressWarnings("unchecked")
    private void initialize() {

        // TODO: set default detail view

        // init table view elements
        tblConfigs.setItems(patterns);

        // bind selection changed listener
        tblConfigs.getSelectionModel().selectedItemProperty().addListener(this::onSelectionChanged);
    }

    public void loadConfig(List<AuthorizationPattern> patterns) {

        // load items into table view
        this.patterns.clear();
        this.patterns.addAll(patterns);

        // select first record => init detail view with first record
        tblConfigs.getSelectionModel().select(0);
    }

    private void onSelectionChanged(ObservableValue<Object> observableValue, Object oldSelection, Object newSelection) {

        Object selectedItem = tblConfigs.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {

            AuthorizationPattern pattern = (AuthorizationPattern) selectedItem;
            updateSelectedDetailView(pattern);
        }
    }

    private void updateSelectedDetailView(AuthorizationPattern pattern) {

        // TODO:
    }

}
