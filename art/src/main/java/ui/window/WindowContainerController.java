package ui.window;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import tools.tracing.TraceOut;


public class WindowContainerController {

    @FXML
    protected void btnCloseAppClicked(ActionEvent event) {

        TraceOut.enter();

        System.exit(0);

        TraceOut.leave();
    }

    @FXML
    protected void btnMinimizeAppClicked(ActionEvent event) {

        TraceOut.enter();

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        // is stage minimizable into task bar. (true | false)
        stage.setIconified(true);

        TraceOut.leave();
    }
}

