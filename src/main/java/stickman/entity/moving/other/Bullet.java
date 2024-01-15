package stickman.entity.moving.other;

import stickman.entity.Entity;
import stickman.entity.moving.MovingEntity;
import stickman.entity.moving.MovingObject;
import stickman.oberver.CurrentScoreObserver;
import stickman.oberver.Observer;
import stickman.oberver.Subject;

import java.util.List;

/**
 * Bullet object that the player can shoot to kill slimes.
 */
public class Bullet extends MovingObject implements Projectile, Subject {
    /**
     * The x-velocity of all bullets.
     */
    public static final double BULLET_SPEED = 2;

    /**
     * The width of all bullets. (Static to help testing)
     */
    public static final double BULLET_WIDTH = 10;

    /**
     * The height of all bullets. (Static to help testing)
     */
    public static final double BULLET_HEIGHT = 10;
    private CurrentScoreObserver currentScoreObserver;
    private boolean left;

    /**
     * Constructs a bullet object.
     *
     * @param x    The x-coordinate
     * @param y    The y-coordinate
     * @param left Whether the bullet is moving left or right
     */
    public Bullet(double x, double y, boolean left) {
        super("bullet.png", x, y, BULLET_HEIGHT, BULLET_WIDTH, Layer.FOREGROUND);
        this.left = left;
        this.xVelocity = left ? -BULLET_SPEED : BULLET_SPEED;
        this.yVelocity = 0;
    }

    @Override
    public void tick(List<Entity> entities, double heroX, double floorHeight) {
        this.xPos += this.xVelocity;
        this.yPos += this.yVelocity;
    }


    public boolean isLeft() {
        return left;
    }


    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public void stop() {
        this.active = false;
    }

    @Override
    public void movingCollision(List<MovingEntity> movingEntities) {
        for (MovingEntity movingEntity : movingEntities) {
            if (movingEntity != this) {
                if (this.checkCollide(movingEntity) && movingEntity.isActive()) {
                    movingEntity.die();
                    notifyObservers();
                    this.stop();
                    return;
                }
            }
        }
    }

    @Override
    public void notifyObservers() {
        currentScoreObserver.update(100);
    }

    @Override
    public void attachObserver(Observer observer) {
        currentScoreObserver=(CurrentScoreObserver) observer;
    }

    @Override
    public MovingObject copy() {
        MovingObject same = new Bullet(xPos, yPos, isLeft());
        return same;
    }
}
