package ui.main.about;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;


public class AboutController {

    @FXML
    private WebView webView;

    /**
     * Initializes the controller (WebView).
     */
    @FXML
    public void initialize() {

        // open the exported dependencies.html in a webview to display it
        WebEngine webEngine = webView.getEngine();
        URL url = this.getClass().getResource("/dependencies/dependencies.html");

        webEngine.load(url.toString());

        // overwrite the default hyperlink behavior so that all links open in the default browser
        webEngine.documentProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                NodeList nodeList = newValue.getElementsByTagName("a");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    EventTarget eventTarget = (EventTarget) node;
                    eventTarget.addEventListener("click", new EventListener() {
                        @Override
                        public void handleEvent(Event evt) {
                            EventTarget target = evt.getCurrentTarget();
                            HTMLAnchorElement anchorElement = (HTMLAnchorElement) target;
                            String href = anchorElement.getHref();

                            //handle opening URL outside JavaFX WebView
                            try {
                                Desktop.getDesktop().browse(new URI(href));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            evt.preventDefault();
                        }
                    }, false);
                }
            }
        }));
    }
}
