package ui.custom.controls;

import extensions.ResourceBundleHelper;

import java.util.ResourceBundle;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import javafx.scene.control.Label;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import settings.UserSettingsHelper;
import ui.App;

public class CustomAlert extends Alert {

    private CustomWindow window;
    private ResourceBundle bundle = ResourceBundleHelper.getInstance().getLanguageBundle();

    private String title;
    private String contentText;
    private String okButtonText;
    private String cancelButtonText;

    /**
     * Creates a working custom alert with proper css styling.
     *
     * <p>Use like this:
     * CustomAlert alert = new CustomAlert(Alert.AlertType.CONFIRMATION, "Title", "This is a test message.");
     * if (alert.showAndWait().get() == ButtonType.OK) {
     * System.out.println("Ok button pressed.");
     * }
     *
     * @param alertType   the type of alert
     * @param title       the title of the window
     * @param contentText the text that the alert should contain
     */
    public CustomAlert(AlertType alertType, String title, String contentText) {
        super(alertType);
        this.title = title;
        this.contentText = contentText;
        this.okButtonText = bundle.getString("defaultOkButtonText");
        this.cancelButtonText = bundle.getString("defaultCancelButtonText");

        init();
    }

    /**
     * Creates a working custom alert with proper css styling.
     *
     * <p>Use like this:
     * CustomAlert alert = new CustomAlert(Alert.AlertType.CONFIRMATION, "Title", "This is a test message.", "Okay", "Nope");
     * if (alert.showAndWait().get() == ButtonType.OK) {
     * System.out.println("Ok button pressed.");
     * }
     *
     * @param alertType        the type of alert
     * @param title            the title of the window
     * @param contentText      the text that the alert should contain
     * @param okButtonText     the text of the ok button
     * @param cancelButtonText the text of the cancel button
     */
    public CustomAlert(AlertType alertType, String title, String contentText, String okButtonText, String cancelButtonText) {
        super(alertType);
        this.title = title;
        this.contentText = contentText;
        this.okButtonText = okButtonText;
        this.cancelButtonText = cancelButtonText;

        init();
    }

    /**
     * Creates a working custom alert with proper css styling.
     * Not recommended as it does not contain specific information!
     *
     * <p>Use like this:
     * CustomAlert alert = new CustomAlert(Alert.AlertType.CONFIRMATION);
     * if (alert.showAndWait().get() == ButtonType.OK) {
     * System.out.println("Ok button pressed.");
     * }
     *
     * @param alertType the type of alert
     */
    public CustomAlert(AlertType alertType) {
        super(alertType);

        // Default title
        this.title = "Alert";

        init();
    }

    /**
     * Initializes the window and adds styling.
     */
    public void init() {

        // initialize undecorated stage
        initStyle(StageStyle.UNDECORATED);
        getDialogPane().setGraphic(null);

        // add stylesheet
        try {
            getDialogPane().setStyle(new UserSettingsHelper().loadUserSettings().getDarkThemeCss());
        } catch (Exception e) {
            e.printStackTrace();
        }

        getDialogPane().getStylesheets().add("/css/main.css");
        getDialogPane().getStylesheets().add("/css/custom-dialog.css");

        Stage stage = (Stage) getDialogPane().getScene().getWindow();

        stage.setOnShown((event -> {
            updatePosition(stage);
        }));

        // add custom window as header
        window = new CustomWindow();
        window.setWindowState(CustomWindow.WindowState.NoButtons);
        window.initStage(stage);

        // set window title
        window.setTitle(this.title);

        getDialogPane().setHeader(window);

        // initialize content
        initContent();

        // initialize the buttons
        initButtons();
    }

    /**
     * Sets the correct position for the stage (center of owner).
     */
    private void updatePosition(Stage stage) {
        for (Screen screen : Screen.getScreens()) {
            Rectangle2D screenBounds = screen.getVisualBounds();

            if (screenBounds.contains(new Point2D(App.primaryStage.getX(), App.primaryStage.getY()))) {
                stage.setX(App.primaryStage.getX() + (App.primaryStage.getWidth() / 2) - (stage.getWidth() / 2));
                stage.setY(App.primaryStage.getY() + (App.primaryStage.getHeight() / 2) - (stage.getHeight() / 2));
            }

        }
    }

    /**
     * Creates a new label with the given text and adds it to the DialogPane.
     */
    private void initContent() {

        // set max width of the content
        getDialogPane().setMaxWidth(400);

        // content text
        if (contentText != null) {
            Label contentLabel = new Label(contentText);
            contentLabel.setStyle("-fx-font-size: 1.1em");
            contentLabel.setWrapText(true);
            getDialogPane().setContent(contentLabel);
        }
    }

    /**
     * Initializes the custom buttons with the given texts.
     * Uses default values when no buttonTexts are given.
     */
    private void initButtons() {

        // initialize buttons
        if (getAlertType() == AlertType.CONFIRMATION) {

            initConfirmationType();

        } else if (getAlertType() == AlertType.INFORMATION || getAlertType() == AlertType.WARNING) {

            initOtherAlertTypes();

        }
    }

    /**
     * Initialize the dialog for a ConfirmationType Alert.
     */
    private void initConfirmationType() {

        window.getStyleClass().add("confirmation");

        if (okButtonText != null) {

            ButtonType okButton = new ButtonType(this.okButtonText, ButtonBar.ButtonData.OK_DONE);
            getButtonTypes().set(0, okButton);

        }

        if (cancelButtonText != null) {

            ButtonType cancelButton = new ButtonType(this.cancelButtonText, ButtonBar.ButtonData.CANCEL_CLOSE);
            getButtonTypes().set(1, cancelButton);

            getDialogPane().lookupButton(cancelButton).getStyleClass().add("cancel-button");

        }
    }

    /**
     * Initialize the dialog for other types of Alert (Information and Warning).
     */
    private void initOtherAlertTypes() {

        ButtonType okButton = new ButtonType(this.okButtonText, ButtonBar.ButtonData.OK_DONE);

        if (okButtonText != null) {
            getButtonTypes().set(0, okButton);
        }

        if (getAlertType() == AlertType.WARNING) {

            window.getStyleClass().add("warning");
            getDialogPane().lookupButton(okButton).getStyleClass().add("cancel-button");

        } else {

            window.getStyleClass().add("information");

        }
    }
}
