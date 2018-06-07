package ui.custom.controls;

import java.util.function.Function;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class ButtonCell<S> extends TableCell<S, Button> {

    private final JFXButton actionButton;

    public ButtonCell(MaterialDesignIcon icon, Function<S, S> function) {
        this.getStyleClass().add("action-button-table-cell");

        MaterialDesignIconView view = new MaterialDesignIconView(icon);
        this.actionButton = new JFXButton(null, view);
        this.actionButton.setOnAction((ActionEvent e) -> {
            function.apply(getCurrentItem());
        });
    }

    public S getCurrentItem() {
        return (S) getTableView().getItems().get(getIndex());
    }

    public static <S> Callback<TableColumn<S, Button>, TableCell<S, Button>> forTableColumn(MaterialDesignIcon icon, Function< S, S> function) {
        return param -> new ButtonCell<>(icon, function);
    }

    @Override
    public void updateItem(Button item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(actionButton);
        }
    }
}