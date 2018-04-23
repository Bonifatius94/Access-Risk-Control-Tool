package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tools.tracing.TraceOut;

import java.util.ResourceBundle;

public class MainController {

    @FXML
    protected void btnShowLogin(ActionEvent e) throws Exception {

        TraceOut.enter();

        Stage stage = new Stage();
        ResourceBundle bundle = ResourceBundle.getBundle("lang");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginView.fxml"), bundle);
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.setTitle("My modal window");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Node)e.getSource()).getScene().getWindow());
        stage.showAndWait();

        // evaluate dialog results
        // TODO: implement logic

        TraceOut.leave();
    }

}
