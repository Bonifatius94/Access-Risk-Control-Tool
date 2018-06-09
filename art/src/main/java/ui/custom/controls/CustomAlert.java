package ui.custom.controls;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CustomAlert extends Alert {

    private CustomWindow window;

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
        getDialogPane().getStylesheets().add("/css/dark-theme.css");
        getDialogPane().getStylesheets().add("/css/custom-dialog.css");

        // add custom window as header
        window = new CustomWindow();
        window.setWindowState(CustomWindow.WindowState.NoButtons);
        window.initStage((Stage) getDialogPane().getScene().getWindow());

        // set window title
        window.setTitle(this.title);

        getDialogPane().setHeader(window);

        // initialize the buttons
        initButtons();
    }

    /**
     * Initializes the custom buttons with the given texts.
     * Uses default values when no buttonTexts are given.
     */
    private void initButtons() {
        // content text
        if (contentText != null) {
            getDialogPane().setContentText(contentText);
        }

        // initialize buttons
        if (getAlertType() == AlertType.CONFIRMATION) {

            initConfirmationType();

        } else if (getAlertType() == AlertType.INFORMATION || getAlertType() == AlertType.WARNING) {

            initOtherAlertTypes();

        } else {

            // TODO: remove this
            System.err.println("Only use CONFIRMATION, INFORMATION and WARNING as AlertType!");

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
