package svanimpe.pong.ui;

import javafx.beans.binding.Bindings;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import svanimpe.pong.Game;
import svanimpe.pong.HighScore;

import static svanimpe.pong.Constants.*;

public class EndScreen extends Pane {
    private Runnable onRestart = () -> {
    }; /* Do nothing for now. */

    public void setOnRestart(Runnable onRestart) {
        this.onRestart = onRestart;
    }

    private final Text header = new Text();

    public void setScore(int playerScore, int p2Score) {
        if (playerScore == WINNING_SCORE) {
            header.getStyleClass().remove("header2");
            header.getStyleClass().remove("endText");
            header.getStyleClass().add("header");
            header.setText("p1 win");
        } else if (p2Score == WINNING_SCORE) {
            header.getStyleClass().remove("header");
            header.getStyleClass().remove("endText");
            header.getStyleClass().add("header2");
            header.setText("p2 win");
        } else
        {
            header.getStyleClass().add("endText");
            header.setText("leaving so soon?");
        }
    }

    Game game;
    final Text nameText = new Text("Enter name: ");
    final StringBuilder name = new StringBuilder("");

    public EndScreen(Game game) {
        this.game = game;
        header.boundsInLocalProperty().addListener(observable ->
        {
            header.setTranslateX((WIDTH - header.getBoundsInLocal().getWidth()) / 2); /* Centered. */
            header.setTranslateY(TEXT_MARGIN_TOP_BOTTOM);
        });


        nameText.boundsInLocalProperty().addListener(observable -> {
            nameText.setTranslateX((WIDTH - 200 - nameText.getBoundsInLocal().getWidth()) / 2);
            nameText.setTranslateY(header.getY() + 150);
        });
        nameText.getStyleClass().add("info");

        Text info = new Text("press space to restart\npress escape to back to main menu");
        info.boundsInLocalProperty().addListener(observable ->
        {
            info.setTranslateX((WIDTH - info.getBoundsInLocal().getWidth()) / 2); /* Centered. */
            info.setTranslateY(HEIGHT - TEXT_MARGIN_TOP_BOTTOM - info.getBoundsInLocal().getHeight());
        });
        info.getStyleClass().add("info");

        setPrefSize(WIDTH, HEIGHT);
        getChildren().addAll(header, info);
        getStyleClass().add("screen");

        setOnKeyPressed(event ->
        {
            if (event.getCode() == KeyCode.SPACE) {
                onRestart.run();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                Back.run();
            }
            if (soloWin()) {
                if (event.getCode().isLetterKey()) {
                    if (name.length() < 6)
                        name.append(event.getText());
                    nameText.setText("Enter name: " + name);
                } else if (event.getCode() == KeyCode.BACK_SPACE) {
                    if (name.length() > 0) {
                        name.deleteCharAt(name.length() - 1);
                        nameText.setText("Enter name: " + name);
                    }
                } else if (event.getCode() == KeyCode.ENTER) {
                    if (name.length() == 0)
                        name.append("Unknown");
                    HighScore.addHighScore(new HighScore(WINNING_SCORE - game.getOpponent().getScore(), name.toString()));
                    Back.run();
                }
            }
        });
    }

    private Runnable Back = () -> {
    };

    public void setOnBack(Runnable Back) {
        this.Back = Back;
    }

    private String getName(String name, String input, Text x) {
        name += input;
        x.setText(name);
        return name;
    }

    public void setNameText() {
        nameText.setText("Enter name:");
        if (name.length() > 0)
            name.delete(0, name.length() - 1);
        if (!soloWin()) {
            getChildren().remove(nameText);
        } else {
            getChildren().add(nameText);
        }
    }

    private boolean soloWin() {
        return !game.getIs2p() && game.getPlayer().getScore() == WINNING_SCORE;
    }
}