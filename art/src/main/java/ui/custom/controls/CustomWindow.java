package ui.custom.controls;

import com.jfoenix.controls.JFXButton;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/*
 * sources:
 * ========
 * dragging undecorated JavaFX window: https://stackoverflow.com/questions/18173956/how-to-drag-undecorated-window
 */

// TODO: add support for a custom app icon at the left
// TODO: improve style of the headline label (e.g. by implementing the css style for 'windowLabel')

/**
 * <p>
 * This class implements a custom window header with custom JPhoenix JavaFX style.
 * It allows implicit usage in FXML as VBox layout container and automatically handles minimize / maximize / close window events.
 * The window header can can be get / set by getter / setter of this class (instead of stage).
 * </p>
 * <p>
 * Code example for usage in FXML:
 * <br>&lt;CustomWindow ... &gt;
 * <br>&lt;!-- insert your FXML code here --&gt;
 * <br>&lt;/CustomWindow&gt;
 * </p>
 */
@SuppressWarnings("unused")
public class CustomWindow extends VBox {

    // ========================================
    //               constructor
    // ========================================

    public CustomWindow() {

        initCustomWindowHeader();
        initEventHandlers();
    }

    // ========================================
    //                 members
    // ========================================

    private HBox hbHeaderContainer;
    private Label lblHeadline;
    private JFXButton btnMinimize;
    private JFXButton btnMaximize;
    private JFXButton btnClose;

    private double xOffset = 0;
    private double yOffset = 0;

    // ========================================
    //             init controls
    // ========================================

    private void initCustomWindowHeader() {

        // init label for headline
        lblHeadline = new Label();
        lblHeadline.getStyleClass().addAll("windowLabel");
        lblHeadline.setPadding(new Insets(2, 0, 0, 4));

        // init region filler to render headline bound to the left and buttons bound to the right
        final Region regFiller = new Region();
        HBox.setHgrow(regFiller, Priority.ALWAYS);

        // init minimize button
        btnMinimize = new JFXButton(null, new MaterialDesignIconView(MaterialDesignIcon.WINDOW_MINIMIZE));
        btnMinimize.setMnemonicParsing(false);
        btnMinimize.getStyleClass().addAll("windowButton");
        btnMinimize.setPrefWidth(20);

        // init maximize button
        btnMaximize = new JFXButton(null, new MaterialDesignIconView(MaterialDesignIcon.WINDOW_MAXIMIZE));
        btnMaximize.setMnemonicParsing(false);
        btnMaximize.getStyleClass().addAll("windowButton");
        btnMaximize.setPrefWidth(20);

        // init close button
        btnClose = new JFXButton(null, new MaterialDesignIconView(MaterialDesignIcon.CLOSE));
        btnClose.setMnemonicParsing(false);
        btnClose.getStyleClass().addAll("windowButton", "closeWindowButton");
        btnClose.setPrefWidth(20);

        // init container with horizontal orientation and apply children to it
        hbHeaderContainer = new HBox();
        hbHeaderContainer.getChildren().addAll(lblHeadline, regFiller, btnMinimize, btnMaximize, btnClose);

        // apply container to the top region of the border pane
        super.getChildren().addAll(hbHeaderContainer);
    }

    private void initEventHandlers() {

        btnMinimize.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                // get the current stage and minimize it
                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                stage.setIconified(true);
            }
        });

        btnMaximize.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                // get the current stage and maximize / resize it
                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                stage.setMaximized(!stage.isMaximized());
            }
        });

        btnClose.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                // get the current stage and close it
                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                stage.close();
            }
        });

        hbHeaderContainer.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                // get the current stage and manage horizontal / vertical offset while dragging
                Stage stage = (Stage) ((Parent) event.getSource()).getScene().getWindow();
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            }
        });

        hbHeaderContainer.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                // get the current stage and apply the new location (offset) to it
                Stage stage = (Stage) ((Parent)  event.getSource()).getScene().getWindow();
                stage.setX(event.getScreenX() + xOffset);
                stage.setY(event.getScreenY() + yOffset);
            }
        });

        hbHeaderContainer.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if (event.getClickCount() == 2) {

                    Stage stage = (Stage) ((Parent) event.getSource()).getScene().getWindow();
                    stage.setMaximized(!stage.isMaximized());
                }
            }
        });
    }

    public void initStage(Stage stage) {

        // remove OS dependent window frame
        stage.initStyle(StageStyle.UNDECORATED);
    }

    // ========================================
    //            getters / setters
    // ========================================

    public String getTitle() {
        return lblHeadline.getText();
    }

    public void setTitle(String title) {
        lblHeadline.setText(title);
    }

}
