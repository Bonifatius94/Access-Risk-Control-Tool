package ui.custom.controls;

import com.jfoenix.controls.JFXButton;

import data.entities.IDataEntity;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

import extensions.ResourceBundleHelper;

import java.util.ResourceBundle;
import java.util.function.Function;

import javafx.event.ActionEvent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;

public class DisableArchiveButtonCell<S> extends TableCell<S, JFXButton> {

    private final JFXButton actionButton;
    private ResourceBundle bundle = ResourceBundleHelper.getInstance().getLanguageBundle();

    /**
     * Creates a new ButtonCell with the given icon and the function to call.
     * @param function the funcion to call
     */
    public DisableArchiveButtonCell(Function<S, S> function) {
        this.getStyleClass().add("action-button-table-cell");

        MaterialDesignIconView view = new MaterialDesignIconView(MaterialDesignIcon.DELETE);
        this.actionButton = new JFXButton(null, view);
        this.actionButton.setMinSize(30, 30);
        this.actionButton.setTooltip(new Tooltip(bundle.getString("archive")));

        this.actionButton.setOnAction((ActionEvent e) -> {
            function.apply(getCurrentItem());
        });
    }

    public S getCurrentItem() {
        return (S) getTableView().getItems().get(getIndex());
    }

    public static <S> Callback<TableColumn<S, JFXButton>, TableCell<S, JFXButton>> forTableColumn(Function<S, S> function) {
        return param -> new DisableDeleteButtonCell<>(function);
    }

    @Override
    public void updateItem(JFXButton item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
        } else {

            if (((IDataEntity) getCurrentItem()).isArchived()) {
                actionButton.setDisable(true);
            }

            setGraphic(actionButton);
        }
    }
}

