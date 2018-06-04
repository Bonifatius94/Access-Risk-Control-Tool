package ui.custom.controls;

import com.jfoenix.controls.JFXButton;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

import java.util.concurrent.locks.Condition;

import javafx.scene.control.TableCell;

public class ButtonCell extends TableCell<Condition, Condition> {

    @Override
    public void updateItem(Condition obj, boolean empty) {
        super.updateItem(obj, empty);
        if (empty) {
            setText(null);
            getChildren().removeAll();
        } else {
            getChildren().add(new JFXButton(null, new MaterialDesignIconView(MaterialDesignIcon.DELETE)));
        }
    }
}
