package svanimpe.pong.objects;

public class RandomObject extends GameObject {

    private double speed;

    public RandomObject(double speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public enum Movement {
        UP, DOWN, NONE;
    }

    private Movement movement = Movement.NONE;

    public Movement getMovement() {
        return movement;
    }

    public void setMovement(Movement movement) {
        this.movement = movement;
    }


    @Override
    public void update(double deltaTime) {
        setX(285);
        if (movement == Movement.DOWN) {
            setY(getY() + speed * deltaTime);

        } else if (movement == Movement.UP) {
            setY(getY() - speed * deltaTime);
        }
    }

}