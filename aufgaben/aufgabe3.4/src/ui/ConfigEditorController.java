package ui;

import excel.AuthorizationPattern;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;

import java.util.HashMap;

public class ConfigEditorController {

    @FXML
    private TitledPane tpConfigDetails;

    @FXML
    private TableView tblConfigs;

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

    private final ObservableList<AuthorizationPattern> patterns = FXCollections.observableArrayList();

    @SuppressWarnings("unchecked")
    private void initialize() {

        // TODO: set default detail view

        // init table view elements
        tblConfigs.setItems(patterns);

        // bind selection changed listener
        tblConfigs.getSelectionModel().selectedItemProperty().addListener(this::onSelectionChanged);
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
