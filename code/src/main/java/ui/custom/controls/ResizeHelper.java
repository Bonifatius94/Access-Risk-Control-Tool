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

// snippet source: https://stackoverflow.com/questions/19455059/allow-user-to-resize-an-undecorated-stage

@SuppressWarnings({"unused", "WeakerAccess"})
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
        private Cursor mouseCursor = Cursor.DEFAULT;
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

            double sceneX = mouseEvent.getSceneX();
            double sceneY = mouseEvent.getSceneY();
            double sceneWidth = scene.getWidth();
            double sceneHeight = scene.getHeight();

            if (MouseEvent.MOUSE_MOVED.equals(mouseEventType)) {

                if (sceneX < border && sceneY < border) {
                    mouseCursor = Cursor.NW_RESIZE;
                } else if (sceneX < border && sceneY > sceneHeight - border) {
                    mouseCursor = Cursor.SW_RESIZE;
                } else if (sceneX > sceneWidth - border && sceneY < border) {
                    mouseCursor = Cursor.NE_RESIZE;
                } else if (sceneX > sceneWidth - border && sceneY > sceneHeight - border) {
                    mouseCursor = Cursor.SE_RESIZE;
                } else if (sceneX < border) {
                    mouseCursor = Cursor.W_RESIZE;
                } else if (sceneX > sceneWidth - border) {
                    mouseCursor = Cursor.E_RESIZE;
                } else if (sceneY < border) {
                    mouseCursor = Cursor.N_RESIZE;
                } else if (sceneY > sceneHeight - border) {
                    mouseCursor = Cursor.S_RESIZE;
                } else {
                    mouseCursor = Cursor.DEFAULT;
                }

                scene.setCursor(mouseCursor);

            } else if (MouseEvent.MOUSE_EXITED.equals(mouseEventType) || MouseEvent.MOUSE_EXITED_TARGET.equals(mouseEventType)) {

                scene.setCursor(Cursor.DEFAULT);

            } else if (MouseEvent.MOUSE_PRESSED.equals(mouseEventType)) {

                startX = stage.getWidth() - sceneX;
                startY = stage.getHeight() - sceneY;

            } else if (MouseEvent.MOUSE_DRAGGED.equals(mouseEventType)) {

                if (!Cursor.DEFAULT.equals(mouseCursor)) {

                    if (!Cursor.W_RESIZE.equals(mouseCursor) && !Cursor.E_RESIZE.equals(mouseCursor)) {

                        double minHeight = stage.getMinHeight() > (border * 2) ? stage.getMinHeight() : (border * 2);

                        if (Cursor.NW_RESIZE.equals(mouseCursor) || Cursor.N_RESIZE.equals(mouseCursor) || Cursor.NE_RESIZE.equals(mouseCursor)) {

                            if (stage.getHeight() > minHeight || sceneY < 0) {

                                stage.setHeight(stage.getY() - mouseEvent.getScreenY() + stage.getHeight());
                                stage.setY(mouseEvent.getScreenY());
                            }

                        } else {

                            if (stage.getHeight() > minHeight || sceneY + startY - stage.getHeight() > 0) {
                                stage.setHeight(sceneY + startY);
                            }
                        }
                    }

                    if (!Cursor.N_RESIZE.equals(mouseCursor) && !Cursor.S_RESIZE.equals(mouseCursor)) {

                        double minWidth = stage.getMinWidth() > (border * 2) ? stage.getMinWidth() : (border * 2);

                        if (Cursor.NW_RESIZE.equals(mouseCursor) || Cursor.W_RESIZE.equals(mouseCursor) || Cursor.SW_RESIZE.equals(mouseCursor)) {

                            if (stage.getWidth() > minWidth || sceneX < 0) {

                                stage.setWidth(stage.getX() - mouseEvent.getScreenX() + stage.getWidth());
                                stage.setX(mouseEvent.getScreenX());
                            }

                        } else {

                            if (stage.getWidth() > minWidth || sceneX + startX - stage.getWidth() > 0) {
                                stage.setWidth(sceneX + startX);
                            }
                        }
                    }
                }
            }
        }

    }

}
