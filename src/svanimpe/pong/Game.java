package svanimpe.pong;

import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.scene.media.AudioClip;
import svanimpe.pong.ai.DefaultAi;
import svanimpe.pong.ai.PaddleAi;
import svanimpe.pong.objects.Ball;
import svanimpe.pong.objects.Paddle;
import svanimpe.pong.objects.RandomObject;

import static svanimpe.pong.Constants.*;

public class Game {
    /* --- Construction and final properties --- */

    private static final Random random = new Random();

    private final int winningScore;

    public Game(int maxScore) {
        this.winningScore = maxScore;
        loop.start();
    }

    public int getWinningScore() {
        return winningScore;
    }

    int checker = 0;

    /* --- Game loop --- */

    /*
     * This is an implementation of a game loop using variable time steps. See the blog posts on
     * game loops in JavaFX for more information.
     */
    private class GameLoop extends AnimationTimer {
        private long previousTime = 0;

        @Override
        public void handle(long currentTime) {
            /*
             * If this is the first frame, simply record an initial time.
             */
            if (previousTime == 0) {
                previousTime = currentTime;
                return;
            }

            double secondsElapsed = (currentTime - previousTime) / 1_000_000_000.0; /* Convert nanoseconds to seconds. */

            /*
             * Avoid large time steps by imposing an upper bound.
             */
            if (secondsElapsed > 0.0333) {
                secondsElapsed = 0.0333;
            }

            updateGame(secondsElapsed);

            previousTime = currentTime;
        }
    }

    private final GameLoop loop = new GameLoop();

    /* --- State --- */

    public enum State {
        PLAYING, PAUSED, ENDED;
    }

    private State state = State.ENDED;

    public State getState() {
        return state;
    }

    private Runnable onGameEnd = () -> {
    }; /* Do nothing for now. */

    public void setOnGameEnd(Runnable onGameEnd) {
        this.onGameEnd = onGameEnd;
    }

    public void start() {
        player.setX(MARGIN_LEFT_RIGHT + GOAL_WIDTH - PADDLE_WIDTH); /* Aligned with the goal area. */
        player.setY((HEIGHT - PADDLE_HEIGHT) / 2); /* Centered. */

        opponent.setX(WIDTH - MARGIN_LEFT_RIGHT - GOAL_WIDTH); /* Aligned with the goal area. */
        opponent.setY((HEIGHT - PADDLE_HEIGHT) / 2); /* Centered. */

        player.setScore(0);
        opponent.setScore(0);

        player.setMovement(Paddle.Movement.NONE);
        opponent.setMovement(Paddle.Movement.NONE);

        launchBall();
        setIntitialPosOfRanOb();

        state = State.PLAYING;
    }

    public void pause() {
        if (state == State.PAUSED) {
            state = State.PLAYING;
        } else if (state == State.PLAYING) {
            state = State.PAUSED;
        }
    }

    public void forfeit() {
        player.setScore(0);
        opponent.setScore(0);
        state = State.ENDED;
        onGameEnd.run();
    }

    /* --- Ball --- */

    private final Ball ball = new Ball(BALL_MAX_SPEED);

    public Ball getBall() {
        return ball;
    }

    public void launchBall() {
        boolean towardsOpponent = random.nextBoolean();
        double initialAngle = PADDLE_SECTION_ANGLES[random.nextInt(2) + 1]; /* We don't use the steepest angle. */
        ball.setSpeed(towardsOpponent ? -BALL_INITIAL_SPEED : BALL_INITIAL_SPEED);
        ball.setAngle(towardsOpponent ? -initialAngle : initialAngle);
        ball.setX((WIDTH - BALL_SIZE) / 2); /* Centered. */
        ball.setY(MARGIN_TOP_BOTTOM);
    }

    /*randomObject*/
    public RandomObject r = new RandomObject();
    public RandomObject getR(){return r;}

    /*random position of objects ,not draw */
    public void launchRandomObject(){
        /*set the left-up corner position of rectangle*/
        r.setX(300);
        r.setY(Math.random()*280+1);
    }
    public void dontLaunchRandomObject(){
        /*set the left-up corner position of rectangle*/
        r.setX(1000);
        r.setY(1000);
    }
    public void setIntitialPosOfRanOb(){
        r.setX(1000);
        r.setY(1000);
    }


    /* --- Player --- */

    private final Paddle player = new Paddle(PLAYER_PADDLE_SPEED);

    public Paddle getPlayer() {
        return player;
    }


    /* --- Opponent --- */

    private final Paddle opponent = new Paddle(OPPONENT_PADDLE_SPEED);
    private final PaddleAi ai = new DefaultAi(opponent, this);

    public Paddle getOpponent() {
        return opponent;
    }

    /* --- Update --- */

    private void updateGame(double deltaTime) {
        if (state == State.PAUSED || state == State.ENDED) {
            return; /* This is necessary because the loop keeps running even when the game is paused or stopped. */
        }

        player.update(deltaTime);
        opponent.update(deltaTime);

        keepPaddleInBounds(player);
        keepPaddleInBounds(opponent);

        ball.update(deltaTime);
        r.update(deltaTime);

        checkWallCollision();
        checkPaddleOrEdgeCollision(player);
        checkPaddleOrEdgeCollision(opponent);

        checkRandomObjectCollision(r);
        if (!Is2p)
            ai.update(deltaTime);
    }

    /* --- Collision detection --- */

    private void keepPaddleInBounds(Paddle paddle) {
        if (paddle.getY() < MARGIN_TOP_BOTTOM) {
            paddle.setY(MARGIN_TOP_BOTTOM);
        } else if (paddle.getY() + PADDLE_HEIGHT > HEIGHT - MARGIN_TOP_BOTTOM) {
            paddle.setY(HEIGHT - MARGIN_TOP_BOTTOM - PADDLE_HEIGHT);
        }
    }

    private void checkWallCollision() {
        boolean ballHitTopWall = ball.getY() < MARGIN_TOP_BOTTOM;
        boolean ballHitBottomWall = ball.getY() + BALL_SIZE > HEIGHT - MARGIN_TOP_BOTTOM;

        if (ballHitTopWall || ballHitBottomWall) {
            ball.setAngle(ball.getAngle() * -1);
            new AudioClip(Sounds.HIT_WALL).play();
        }

        if (ballHitTopWall) {
            ball.setY(MARGIN_TOP_BOTTOM);
        } else if (ballHitBottomWall) {
            ball.setY(HEIGHT - MARGIN_TOP_BOTTOM - BALL_SIZE);
        }
    }

    private void checkRandomObjectCollision(RandomObject randomObject){
        boolean ballHitRandomObject =ball.getX()+BALL_SIZE>randomObject.getX()&&ball.getX()<randomObject.getX()+RANDOMOBJECT_WIDTH
                &&ball.getY()+BALL_SIZE>randomObject.getY()&&ball.getY()<randomObject.getY()+RANDOMOBJECT_HEIGHT;
        if (ballHitRandomObject){
            dontLaunchRandomObject();
            for (int i = 0; i < RANDOMOBJECT_SECTIONS; i++) {
                boolean ballHitTCurrentSection = ball.getY() < randomObject.getY() + (i + 0.5) * RANDOMOBJECT_SECTION_HEIGHT;
                if (ballHitTCurrentSection) {
                    ball.setAngle(RANDOMOBJECT_SECTION_ANGLES[i] *(ball.getAngle()*-1)*-1);
                    ball.setSpeed(ball.getSpeed()*-1);
                    break; /* Found our match. */
                } else if (i == RANDOMOBJECT_SECTIONS - 1) { /* If we haven't found our match by now, it must be the last section. */
                    ball.setAngle(RANDOMOBJECT_SECTION_ANGLES[i]*(ball.getAngle()*-1)*-1 );
                    ball.setSpeed(ball.getSpeed()*-1);

                }
            }
            System.out.println("Hit randomObject"+ball.getAngle());


        }
    }

    private void checkPaddleOrEdgeCollision(Paddle paddle) {
        boolean ballHitEdge;
        if (paddle == player) {
            ballHitEdge = ball.getX() < MARGIN_LEFT_RIGHT + GOAL_WIDTH;
        } else {
            ballHitEdge = ball.getX() + BALL_SIZE > WIDTH - MARGIN_LEFT_RIGHT - GOAL_WIDTH;
        }
        if (!ballHitEdge) {
            return;
        }
        boolean ballHitPaddle = ball.getY() + BALL_SIZE > paddle.getY() && ball.getY() < paddle.getY() + PADDLE_HEIGHT;

        if (ballHitPaddle) {
            /*
             * Find out what section of the paddle was hit.
             */
            //System.out.println(paddle == opponent ? -1 : 1);
            System.out.println("Hit Paddle"+ball.getAngle());
            for (int i = 0; i < PADDLE_SECTIONS; i++) {
                boolean ballHitCurrentSection = ball.getY() < paddle.getY() + (i + 0.5) * PADDLE_SECTION_HEIGHT;
                if (ballHitCurrentSection) {
                    ball.setAngle(PADDLE_SECTION_ANGLES[i] * (paddle == opponent ? -1 : 1));
                    break; /* Found our match. */
                } else if (i == PADDLE_SECTIONS - 1) { /* If we haven't found our match by now, it must be the last section. */
                    ball.setAngle(PADDLE_SECTION_ANGLES[i]*(paddle == opponent ? -1 : 1));
                }
            }
            /*
             * Update and reposition the ball.
             */
            ball.setSpeed(ball.getSpeed() * BALL_SPEED_INCREASE);
            if (paddle == player) {
                checker++;
                ball.setX(MARGIN_LEFT_RIGHT + GOAL_WIDTH);
            } else {
                ball.setX(WIDTH - MARGIN_LEFT_RIGHT - GOAL_WIDTH - BALL_SIZE);
            }
            new AudioClip(Sounds.HIT_PADDLE).play();

            if (checker>=2){
                if (Math.random()>=0.0&&Math.random()<=0.5){
                    dontLaunchRandomObject();
                    checker=1;
                }
                else{
                    launchRandomObject();
                    checker=-5;
                }

            }

        } else {
            /*
             * Update the score.
             */
            if (paddle == opponent) {
                player.setScore(player.getScore() + 1);
                new AudioClip(Sounds.SCORE_PLAYER).play();
            } else {
                opponent.setScore(opponent.getScore() + 1);
                new AudioClip(Sounds.SCORE_OPPONENT).play();
            }

            /*
             * Check if the game has ended. If not, play another round.
             */
            if (player.getScore() == winningScore || opponent.getScore() == winningScore) {
                state = State.ENDED;
                onGameEnd.run();
            } else {
                launchBall();
                dontLaunchRandomObject();
                checker=0;
            }
        }
    }

    private boolean Is2p = false;

    public boolean getIs2p() {
        return Is2p;
    }

    public void setIs2p(boolean is2p) {
        Is2p = is2p;
    }

    public void setP2speed() {
        if (getIs2p())
            opponent.setSpeed(PLAYER_PADDLE_SPEED);
        else
            opponent.setSpeed(OPPONENT_PADDLE_SPEED);
    }
}
