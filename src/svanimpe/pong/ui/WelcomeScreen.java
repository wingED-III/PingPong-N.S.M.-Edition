package svanimpe.pong.ui;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import svanimpe.pong.Game;

import static svanimpe.pong.Constants.*;

public class WelcomeScreen extends Pane {
    private Runnable onStart = () -> {
    }; /* Do nothing for now. */

    public void setOnStart(Runnable onStart) {
        this.onStart = onStart;
    }

    Game game;

    public WelcomeScreen(Game game) {
        this.game = game;
//        Text header = new Text("pong");
//        header.boundsInLocalProperty().addListener(observable ->
//        {
//            /*
//             * When using CSS, the width and height (with CSS applied) aren't available right away.
//             * Therefore, we listen for changes and update the position once the width and height
//             * are available.
//             */
//            header.setTranslateX((WIDTH - header.getBoundsInLocal().getWidth()) / 2); /* Centered. */
//            header.setTranslateY(TEXT_MARGIN_TOP_BOTTOM);
//        });
//        header.getStyleClass().add("header");

        Text info = new Text("use the arrow keys to move" +
                "\npress p to pause" +
                "\npress enter to start" + "" +
                "\npress spacebar for 2 players " +
                "\npress E to view high scores\n" +
                "\npress escape to quit");
        info.boundsInLocalProperty().addListener(observable ->
        {
            /*
             * When using CSS, the width and height (with CSS applied) aren't available right away.
             * Therefore, we listen for changes and update the position once the width and height
             * are available.
             */
            info.setTranslateX((WIDTH - info.getBoundsInLocal().getWidth()) / 2); /* Centered. */
            info.setTranslateY(HEIGHT - TEXT_MARGIN_TOP_BOTTOM - info.getBoundsInLocal().getHeight());
        });
        info.getStyleClass().add("info");

        setPrefSize(WIDTH, HEIGHT);
        getChildren().addAll(info);
        getStyleClass().add("Welscreen");

        setOnKeyPressed(event ->
        {
            if (event.getCode() == KeyCode.ENTER) {
                selected2(this.game, false);
                onStart.run();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                Platform.exit();
            } else if (event.getCode() == KeyCode.SPACE) {
                selected2(this.game, true);
                onStart.run();
            } else if (event.getCode() == KeyCode.E) {
                toScore.run();
            }
        });
    }

    private Runnable toScore = () -> {
    };

    public void setToScore(Runnable toScore) {
        this.toScore = toScore;
    }

    private void selected2(Game game, boolean bool) {
        game.setIs2p(bool);
    }
}