package ui.main;

import javafx.fxml.FXML;
import ui.custom.controls.CustomWindow;

public class MainController {

    @FXML
    private CustomWindow mainWindow;

    /**
     * Sets the CustomWindow as the main window.
     */
    @FXML
    public void initialize() {
        mainWindow.setMainWindow(true);
    }
}
