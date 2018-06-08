package ui.custom.controls;

import com.jfoenix.controls.JFXButton;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
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

    /**
     * This constructor initializes the custom window controls and binds the required event handlers.
     */
    public CustomWindow() {

        initCustomWindowHeader();
        initEventHandlers();
    }

    // ========================================
    //                 members
    // ========================================

    private Stage stage;

    private HBox hbHeaderContainer;
    private Label lblHeadline;
    private JFXButton btnMinimize;
    private JFXButton btnMaximize;
    private JFXButton btnClose;

    private double horizontalOffset = 0;
    private double verticalOffset = 0;

    /**
     * CanResize: Everything is working. Resizing is fully allowed.
     * CanMinimize: Miximize button is disabled and resize arrows at the border of the window, too. Minimize is still working as usual.
     * NoResize: Window size cannot be changed by user and only close button is available.
     */
    public enum WindowState { CanResize, CanMinimize, NoResize }

    private final ObjectProperty<WindowState> windowStateProperty = new SimpleObjectProperty<>(this, "windowState", WindowState.CanResize);

    // ========================================
    //             init controls
    // ========================================

    /**
     * <p>
     * This method prepares the overloaded stage for the usage in combination with this undecorated window.
     * </p>
     * <p>
     * Remark: The scene needs to be applied to the given stage before calling this method. Otherwise there will be a NullPointerException.
     * This method must be called once per window (not more or less).
     * </p>
     *
     * @param stage the stage to be prepared for the usage with this custom window
     */
    public void initStage(Stage stage) {

        this.stage = stage;

        // remove OS dependent window frame
        stage.initStyle(StageStyle.UNDECORATED);

        // customize controls according to window state
        applyWindowState();

        // allow stage to resize in undecorated mode (only if resize is allowed)
        if (getWindowState() == WindowState.CanResize) {
            new ResizeHelper().addResizeListener(stage);
        }

        // choose maximize button icon according to stage.isMaximized()
        updateMaximizeButton(stage);
    }

    /**
     * Initializes the custom window header.
     */
    private void initCustomWindowHeader() {

        // add all controls
        initControlsForCanMaximizeMode();

        // add padding to window to fix resizing bug
        HBox pane = new HBox();
        pane.setPadding(new Insets(4));
        HBox.setHgrow(hbHeaderContainer, Priority.ALWAYS);
        pane.getChildren().addAll(hbHeaderContainer);

        // used for styling
        pane.getStyleClass().add("custom-window-header");

        // apply container to the vbox pane
        super.getChildren().addAll(pane);
    }

    /**
     * Initializes the controls for the CanMaximize-Mode in which all buttons are present.
     */
    private void initControlsForCanMaximizeMode() {

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
    }

    /**
     * Applies the current windowState.
     */
    private void applyWindowState() {

        if (getWindowState() == WindowState.CanMinimize) {

            // disable maximize button
            btnMaximize.setDisable(true);

        } else if (getWindowState() == WindowState.NoResize) {

            // remove minimize and maximize button
            btnMinimize.setVisible(false);
            btnMaximize.setVisible(false);
        }
    }

    /**
     * Initializes the event handlers needed for the buttons to work and for the resizing handlers.
     */
    private void initEventHandlers() {

        btnMinimize.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                // minimize the current stage
                stage.setIconified(true);
            }
        });

        btnMaximize.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                // maximize / resize the current stage
                stage.setMaximized(!stage.isMaximized());
                updateMaximizeButton(stage);
            }
        });

        btnClose.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                // close current stage
                stage.close();
            }
        });

        hbHeaderContainer.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                // manage horizontal / vertical offset while dragging
                horizontalOffset = stage.getX() - event.getScreenX();
                verticalOffset = stage.getY() - event.getScreenY();
            }
        });

        hbHeaderContainer.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                // apply the new location (offset)
                stage.setX(event.getScreenX() + horizontalOffset);
                stage.setY(event.getScreenY() + verticalOffset);
            }
        });

        hbHeaderContainer.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if (getWindowState() == WindowState.CanResize && event.getClickCount() == 2) {
                    stage.setMaximized(!stage.isMaximized());
                    updateMaximizeButton(stage);
                }
            }
        });
    }

    private void updateMaximizeButton(Stage stage) {

        MaterialDesignIcon icon = stage.isMaximized() ? MaterialDesignIcon.CHECKBOX_MULTIPLE_BLANK_OUTLINE : MaterialDesignIcon.WINDOW_MAXIMIZE;
        btnMaximize.setGraphic(new MaterialDesignIconView(icon));
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

    public WindowState windowStateProperty() {
        return windowStateProperty.get();
    }

    public WindowState getWindowState() {
        return windowStateProperty.get();
    }

    /**
     * Sets the windowState accordingly.
     * @param newState the new window state
     */
    public void setWindowState(WindowState newState) {

        // set window state property
        WindowState oldState = this.windowStateProperty.get();
        this.windowStateProperty.set(newState);
    }

}
