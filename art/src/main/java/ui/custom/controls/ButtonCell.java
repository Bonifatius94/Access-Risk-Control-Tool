package ui.custom.controls;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

import java.util.function.Function;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;

import javax.tools.Tool;

public class ButtonCell<S> extends TableCell<S, JFXButton> {

    private final JFXButton actionButton;

    /**
     * Creates a new ButtonCell with the given icon and the function to call.
     * @param icon the icon to display
     * @param function the funcion to call
     */
    public ButtonCell(MaterialDesignIcon icon, Function<S, S> function) {
        this.getStyleClass().add("action-button-table-cell");

        MaterialDesignIconView view = new MaterialDesignIconView(icon);
        this.actionButton = new JFXButton(null, view);
        this.actionButton.setMinSize(30, 30);
        this.actionButton.setOnAction((ActionEvent e) -> {
            function.apply(getCurrentItem());
        });
    }

    /**
     * Creates a new ButtonCell with the given icon and the function to call.
     * @param icon the icon to display
     * @param function the funcion to call
     */
    public ButtonCell(MaterialDesignIcon icon, Function<S, S> function, String tooltip) {
        this.getStyleClass().add("action-button-table-cell");

        MaterialDesignIconView view = new MaterialDesignIconView(icon);
        this.actionButton = new JFXButton(null, view);
        this.actionButton.setMinSize(30, 30);
        this.actionButton.setTooltip(new Tooltip(tooltip));
        this.actionButton.setOnAction((ActionEvent e) -> {
            function.apply(getCurrentItem());
        });
    }

    public S getCurrentItem() {
        return (S) getTableView().getItems().get(getIndex());
    }

    public static <S> Callback<TableColumn<S, JFXButton>, TableCell<S, JFXButton>> forTableColumn(MaterialDesignIcon icon, Function<S, S> function) {
        return param -> new ButtonCell<>(icon, function);
    }

    public static <S> Callback<TableColumn<S, JFXButton>, TableCell<S, JFXButton>> forTableColumn(MaterialDesignIcon icon, String tooltip, Function<S, S> function) {
        return param -> new ButtonCell<>(icon, function, tooltip);
    }

    @Override
    public void updateItem(JFXButton item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(actionButton);
        }
    }
}