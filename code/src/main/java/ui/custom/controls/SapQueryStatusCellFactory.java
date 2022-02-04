package ui.custom.controls;

import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

import extensions.ResourceBundleHelper;

import java.util.ResourceBundle;
import java.util.Set;

import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;

public class SapQueryStatusCellFactory implements Callback<TableColumn<CriticalAccessQuery, Set<CriticalAccessEntry>>, TableCell<CriticalAccessQuery, Set<CriticalAccessEntry>>> {
    private ResourceBundle bundle = ResourceBundleHelper.getInstance().getLanguageBundle();

    /**
     * Sets the graphic of the cell as a label with the right icon whether CriticalAccessEntries are present.
     * @param param the params
     * @return the formatted cell
     */
    @Override
    public TableCell<CriticalAccessQuery, Set<CriticalAccessEntry>> call(TableColumn<CriticalAccessQuery, Set<CriticalAccessEntry>> param) {
        TableCell<CriticalAccessQuery, Set<CriticalAccessEntry>> cell = new TableCell<CriticalAccessQuery, Set<CriticalAccessEntry>>() {

            protected void updateItem(Set<CriticalAccessEntry> items, boolean empty) {

                // display nothing if the row is empty
                if (empty || items == null) {

                    // nothing to display
                    setText("");
                    setGraphic(null);

                } else {

                    // add the icon
                    MaterialDesignIconView iconView = new MaterialDesignIconView();

                    // wrapper label for showing a tooltip
                    Label wrapper = new Label();
                    wrapper.setGraphic(iconView);

                    if (items.size() > 0) {

                        // CriticalAccessEntries present
                        iconView.setIcon(MaterialDesignIcon.CLOSE);
                        wrapper.setTooltip(new Tooltip(bundle.getString("criticalAccessCount") + " " + items.size()));
                        iconView.setStyle("-fx-font-size: 2em; -fx-fill: -fx-error");

                    } else {

                        // no CriticalAccessEntries
                        iconView.setIcon(MaterialDesignIcon.CHECK);
                        wrapper.setTooltip(new Tooltip(bundle.getString("noCriticalAccess")));
                        iconView.setStyle("-fx-font-size: 2em; -fx-fill: -fx-success");

                    }

                    setGraphic(wrapper);

                }
            }
        };
        return cell;
    }
}
