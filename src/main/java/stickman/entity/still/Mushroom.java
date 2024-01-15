package stickman.entity.still;

import stickman.entity.GameObject;
import stickman.entity.moving.player.Controllable;
import stickman.entity.Interactable;
import stickman.oberver.CurrentScoreObserver;
import stickman.oberver.Observer;
import stickman.oberver.Subject;

/**
 * Mushroom object that the player can pick up to get the ability to shoot.
 */
public class Mushroom extends GameObject implements Interactable , Subject {
    private CurrentScoreObserver currentScoreObserver;

    /**
     * Creates a new Mushroom object.
     * @param xPos The x-coordinate
     * @param yPos The y-coordinate
     */
    public Mushroom(double xPos, double yPos) {
        super("mushroom_mario.png", xPos, yPos, 20, 20, Layer.FOREGROUND);
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public void interact(Controllable hero) {
        notifyObservers();
        if (this.active) {
            this.active = false;
            hero.upgrade();
        }
    }

    @Override
    public void notifyObservers() {
        currentScoreObserver.update(50);
    }

    @Override
    public void attachObserver(Observer observer){
        currentScoreObserver=(CurrentScoreObserver) observer;
    }



    @Override
    public Mushroom copy() {
        return new Mushroom(xPos,yPos) ;
    }
}
