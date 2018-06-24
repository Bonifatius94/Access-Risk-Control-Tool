package ui.custom.controls;

import data.entities.AccessCondition;
import data.entities.AccessPattern;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

import java.util.ResourceBundle;
import java.util.Set;

import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;


public class ConditionTypeCellFactory implements Callback<TableColumn<AccessPattern, Set<AccessCondition>>, TableCell<AccessPattern, Set<AccessCondition>>> {

    private ResourceBundle bundle = ResourceBundle.getBundle("lang");

    /**
     * Sets the graphic of the cell as a label with the right icon (Pattern / Profile).
     * @param param the params
     * @return the formatted cell
     */
    @Override
    public TableCell<AccessPattern, Set<AccessCondition>> call(TableColumn<AccessPattern, Set<AccessCondition>> param) {
        TableCell<AccessPattern, Set<AccessCondition>> cell = new TableCell<AccessPattern, Set<AccessCondition>>() {
            protected void updateItem(Set<AccessCondition> items, boolean empty) {

                // display nothing if the row is empty, otherwise the item count
                if (empty || items == null) {

                    // nothing to display
                    setText("");

                } else {

                    // add the icon
                    MaterialDesignIconView iconView = new MaterialDesignIconView();
                    iconView.setStyle("-fx-font-size: 1.6em");

                    // wrapper label for showing a tooltip
                    Label wrapper = new Label();
                    wrapper.setGraphic(iconView);

                    if (items.stream().findFirst().get().getProfileCondition() == null) {

                        // pattern
                        iconView.setIcon(MaterialDesignIcon.VIEW_GRID);
                        wrapper.setTooltip(new Tooltip(bundle.getString("patternCondition")));

                    } else {

                        // profile
                        iconView.setIcon(MaterialDesignIcon.ACCOUNT_BOX_OUTLINE);
                        wrapper.setTooltip(new Tooltip(bundle.getString("profileCondition")));

                    }

                    setGraphic(wrapper);

                }
            }
        };
        return cell;
    }
}
