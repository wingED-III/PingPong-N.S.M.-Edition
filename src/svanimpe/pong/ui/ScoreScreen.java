package svanimpe.pong.ui;

import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import static svanimpe.pong.Constants.HEIGHT;
import static svanimpe.pong.Constants.TEXT_MARGIN_TOP_BOTTOM;
import static svanimpe.pong.Constants.WIDTH;

public class ScoreScreen extends Pane {
    private Runnable start = () -> {
    };

    public void setStart(Runnable start) {
        this.start = start;
    }

    private final Text message = new Text();

    public ScoreScreen() {
        message.boundsInLocalProperty().addListener(observable ->
        {
            message.setTranslateX((WIDTH - message.getBoundsInLocal().getWidth()) / 2);
            message.setTranslateY(TEXT_MARGIN_TOP_BOTTOM);
        });
        message.getStyleClass().add("endText");
        message.setText("High Score\n\n" +
                "P1......\n" +
                "P2.......\n" +
                "P3......\n" +
                "P4......\n" +
                "P5.......");
        setPrefSize(WIDTH, HEIGHT);
        getChildren().add(message);
        getStyleClass().add("screen");
    }
}
