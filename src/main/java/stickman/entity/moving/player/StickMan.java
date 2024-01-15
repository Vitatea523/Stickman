package stickman.entity.moving.player;

import stickman.entity.Entity;
import stickman.entity.moving.MovingEntity;
import stickman.entity.moving.MovingObject;
import stickman.level.Level;

import java.util.List;

/**
 * The player controlled character.
 */
public class StickMan extends MovingObject implements Controllable {
    private int lives;
    /**
     * The maximum speed the player can move horizontally.
     */
    public static final double HORIZONTAL_SPEED = 1.8;

    /**
     * The height of the player when normal sized.
     */
    public static final double NORMAL_HEIGHT = 40;

    /**
     * The width of the player when normal sized.
     */
    public static final double NORMAL_WIDTH = 24;

    /**
     * The height of the player when large.
     */
    public static final double LARGE_HEIGHT = 60;

    /**
     * The width of the player when large.
     */
    public static final double LARGE_WIDTH = 36;


    /**
     * The possible sizes the player can be.
     */
    private enum Size {
        NORMAL,
        LARGE
    }


    /**
     * The current size of the player.
     */
    private Size size;

    /**
     * The level the player exists within.
     */
    private Level level;

    /**
     * Whether the player is pressing left.
     */
    private boolean left;

    /**
     * Whether the player is pressing right.
     */
    private boolean right;

    /**
     * Whether the hero has upgraded.
     */
    private boolean upgraded;

    /**
     * Whether the sprite is facing left.
     */
    private boolean leftFacing;
    private String sameSize;


    /**
     * Creates a new StickMan object.
     *
     * @param x     The x-coordinate.
     * @param y     The y-coordinate.
     * @param size  The size of the player.
     * @param level The level the player exists within.
     */
    public StickMan(double x, double y, String size, Level level, int lives) {
        super("ch_stand1.png", x, y, 0, 0, Layer.FOREGROUND);
        this.sameSize = size;
        this.level = level;
        this.upgraded = false;
        this.leftFacing = false;
        this.lives = lives;
        if (size.equals("normal")) {
            this.size = Size.NORMAL;
            this.width = NORMAL_WIDTH;
            this.height = NORMAL_HEIGHT;
        } else if (size.equals("large")) {
            this.size = Size.LARGE;
            this.width = LARGE_WIDTH;
            this.height = LARGE_HEIGHT;
        }

        this.yPos -= this.height;
    }

    @Override
    public void tick(List<Entity> entities, double heroX, double floorHeight) {

        this.gravity(entities, floorHeight);
        this.yPos += this.yVelocity;
        this.horizontalMove(entities);
        this.xPos += this.xVelocity;

    }


    /**
     * Updates x-velocity based on the proximity of other entities.
     *
     * @param entities The other entities in the scene
     */
    private void horizontalMove(List<Entity> entities) {
        if (left) {
            this.xVelocity = Math.max(-HORIZONTAL_SPEED, -horizontalRaycast(true, entities, 0));
        } else if (right) {
            this.xVelocity = Math.min(HORIZONTAL_SPEED, horizontalRaycast(false, entities, level.getWidth()));
        } else {
            this.xVelocity = 0;
        }
    }

    @Override
    public boolean moveLeft() {
        this.left = true;
        this.right = false;
        faceLeft();
        return horizontalRaycast(true, this.level.getEntities(), 0) > 0;
    }

    @Override
    public boolean moveRight() {
        this.right = true;
        this.left = false;
        faceRight();
        return horizontalRaycast(false, this.level.getEntities(), 0) > 0;
    }

    @Override
    public boolean jump() {
        if (canJump()) {
            this.yVelocity = -4;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the player is able to jump.
     *
     * @return Whether the player can jump
     */
    private boolean canJumpTemp = false;

    private boolean canJump() {
        canJumpTemp = verticalRaycast(false, this.level.getEntities(), this.level.getFloorHeight()) < 0.05;
        return canJumpTemp;
    }

    @Override
    public boolean stop() {
        this.xVelocity = 0;
        this.left = false;
        this.right = false;
        return true;
    }

    @Override
    public void upgrade() {
        this.upgraded = true;
    }

    @Override
    public void die() {

        this.active = false;

        if (this.level != null) {
            this.level.reset();
        }
    }


    @Override
    public boolean isLeftFacing() {
        return this.leftFacing;
    }

    @Override
    public boolean upgraded() {
        return this.upgraded;
    }

    @Override
    public void win() {//change here
        this.level.win();
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    /**
     * Turns the player left and updates the sprite.
     */
    private void faceLeft() {
        this.leftFacing = true;
        this.imagePath = "ch_stand4.png";
    }

    /**
     * Turns the player right and updates the sprite.
     */
    private void faceRight() {
        this.leftFacing = false;
        this.imagePath = "ch_stand1.png";
    }

    public int getLives() {
        return lives;
    }

    public Size getSize() {
        return size;
    }

    @Override
    public Controllable copy() {

        Controllable copy = new StickMan(xPos, yPos, sameSize, level, level.getLives());
        copy.setYPos();
        copy.setCanJumpTemp(canJumpTemp);
        copy.setLeft(left);
        copy.setRight(right);
        copy.setUpgraded(upgraded);
        copy.setYVelocity(yVelocity);
        copy.stop();
        return copy;
    }
    public void setYPos() {
        yPos += this.height;
    }

    public void setUpgraded(boolean upgraded) {
        this.upgraded = upgraded;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void setCanJumpTemp(boolean canJumpTemp) {
        this.canJumpTemp = canJumpTemp;
    }




}
