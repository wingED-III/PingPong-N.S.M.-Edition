package svanimpe.pong.ui;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import svanimpe.pong.HighScore;

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
        message.setText("High Score\n\n");

        Text info = new Text("press spacebar to back to main menu");
        info.boundsInLocalProperty().addListener(observable ->
        {
            info.setTranslateX((WIDTH - info.getBoundsInLocal().getWidth()) / 2);
            info.setTranslateY(HEIGHT - TEXT_MARGIN_TOP_BOTTOM - info.getBoundsInLocal().getHeight());
        });
        info.getStyleClass().add("info");
        setPrefSize(WIDTH, HEIGHT);
        getChildren().addAll(message, info);
        getStyleClass().add("screen");
        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                Back.run();
            }
        });
    }

    private Runnable Back = () -> {
    };

    public void setOnBack(Runnable Back) {
        this.Back = Back;
    }
    public void showHighscore(){
        HighScore[] scores = HighScore.getHighScores();
        Text detailsText = new Text();
        StringBuilder details = new StringBuilder();
        for (int i = 0; i < scores.length; i++) {
            details.append(i + ". " + scores[i].getName() + "\t" + scores[i].getScore() + "\n");
        }
        detailsText.getStyleClass().add("endText");
        detailsText.setText(details.toString());
        getChildren().addAll(detailsText);
    }
}
