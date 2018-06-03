package ui.custom.controls;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

// TODO: rework this snippet (divide logic of monster methods into smaller parts, etc.)
// snippet source: https://stackoverflow.com/questions/19455059/allow-user-to-resize-an-undecorated-stage

@SuppressWarnings("unused")
public class ResizeHelper {

    /**
     * This method applies the resize logic to the given undecorated stage.
     *
     * @param stage the undecorated stage to apply the resizing logic to
     */
    public void addResizeListener(Stage stage) {

        ResizeListener resizeListener = new ResizeListener(stage);
        ObservableList<Node> children = stage.getScene().getRoot().getChildrenUnmodifiable();
        children.forEach(x -> addListenerDeeply(x, resizeListener));
    }

    private void addListenerDeeply(Node node, EventHandler<MouseEvent> listener) {

        node.addEventHandler(MouseEvent.MOUSE_MOVED, listener);
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, listener);
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, listener);
        node.addEventHandler(MouseEvent.MOUSE_EXITED, listener);
        node.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, listener);

        if (node instanceof Parent) {

            Parent parent = (Parent) node;
            ObservableList<Node> children = parent.getChildrenUnmodifiable();

            for (Node child : children) {
                addListenerDeeply(child, listener);
            }
        }
    }

    private class ResizeListener implements EventHandler<MouseEvent> {

        private Stage stage;
        private Cursor cursorEvent = Cursor.DEFAULT;
        private int border = 4;
        private double startX = 0;
        private double startY = 0;

        ResizeListener(Stage stage) {

            this.stage = stage;

            stage.getScene().addEventHandler(MouseEvent.MOUSE_MOVED, this);
            stage.getScene().addEventHandler(MouseEvent.MOUSE_PRESSED, this);
            stage.getScene().addEventHandler(MouseEvent.MOUSE_DRAGGED, this);
            stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED, this);
            stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, this);
        }

        @Override
        public void handle(MouseEvent mouseEvent) {

            EventType<? extends MouseEvent> mouseEventType = mouseEvent.getEventType();
            Scene scene = stage.getScene();

            double mouseEventX = mouseEvent.getSceneX();
            double mouseEventY = mouseEvent.getSceneY();
            double sceneWidth = scene.getWidth();
            double sceneHeight = scene.getHeight();

            if (MouseEvent.MOUSE_MOVED.equals(mouseEventType)) {

                if (mouseEventX < border && mouseEventY < border) {
                    cursorEvent = Cursor.NW_RESIZE;
                } else if (mouseEventX < border && mouseEventY > sceneHeight - border) {
                    cursorEvent = Cursor.SW_RESIZE;
                } else if (mouseEventX > sceneWidth - border && mouseEventY < border) {
                    cursorEvent = Cursor.NE_RESIZE;
                } else if (mouseEventX > sceneWidth - border && mouseEventY > sceneHeight - border) {
                    cursorEvent = Cursor.SE_RESIZE;
                } else if (mouseEventX < border) {
                    cursorEvent = Cursor.W_RESIZE;
                } else if (mouseEventX > sceneWidth - border) {
                    cursorEvent = Cursor.E_RESIZE;
                } else if (mouseEventY < border) {
                    cursorEvent = Cursor.N_RESIZE;
                } else if (mouseEventY > sceneHeight - border) {
                    cursorEvent = Cursor.S_RESIZE;
                } else {
                    cursorEvent = Cursor.DEFAULT;
                }

                scene.setCursor(cursorEvent);

            } else if (MouseEvent.MOUSE_EXITED.equals(mouseEventType) || MouseEvent.MOUSE_EXITED_TARGET.equals(mouseEventType)) {
                scene.setCursor(Cursor.DEFAULT);
            } else if (MouseEvent.MOUSE_PRESSED.equals(mouseEventType)) {

                startX = stage.getWidth() - mouseEventX;
                startY = stage.getHeight() - mouseEventY;

            } else if (MouseEvent.MOUSE_DRAGGED.equals(mouseEventType)) {

                if (!Cursor.DEFAULT.equals(cursorEvent)) {

                    if (!Cursor.W_RESIZE.equals(cursorEvent) && !Cursor.E_RESIZE.equals(cursorEvent)) {

                        double minHeight = stage.getMinHeight() > (border * 2) ? stage.getMinHeight() : (border * 2);

                        if (Cursor.NW_RESIZE.equals(cursorEvent) || Cursor.N_RESIZE.equals(cursorEvent) || Cursor.NE_RESIZE.equals(cursorEvent)) {

                            if (stage.getHeight() > minHeight || mouseEventY < 0) {

                                stage.setHeight(stage.getY() - mouseEvent.getScreenY() + stage.getHeight());
                                stage.setY(mouseEvent.getScreenY());
                            }

                        } else {

                            if (stage.getHeight() > minHeight || mouseEventY + startY - stage.getHeight() > 0) {
                                stage.setHeight(mouseEventY + startY);
                            }
                        }
                    }

                    if (!Cursor.N_RESIZE.equals(cursorEvent) && !Cursor.S_RESIZE.equals(cursorEvent)) {

                        double minWidth = stage.getMinWidth() > (border * 2) ? stage.getMinWidth() : (border * 2);

                        if (Cursor.NW_RESIZE.equals(cursorEvent) || Cursor.W_RESIZE.equals(cursorEvent) || Cursor.SW_RESIZE.equals(cursorEvent)) {

                            if (stage.getWidth() > minWidth || mouseEventX < 0) {

                                stage.setWidth(stage.getX() - mouseEvent.getScreenX() + stage.getWidth());
                                stage.setX(mouseEvent.getScreenX());
                            }

                        } else {

                            if (stage.getWidth() > minWidth || mouseEventX + startX - stage.getWidth() > 0) {
                                stage.setWidth(mouseEventX + startX);
                            }
                        }
                    }
                }
            }
        }

    }

}
