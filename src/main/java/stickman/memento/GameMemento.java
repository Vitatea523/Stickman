package stickman.memento;


import stickman.entity.Entity;
import stickman.entity.moving.player.Controllable;
import stickman.entity.moving.player.StickMan;

import java.util.List;

public class GameMemento {
    private Controllable hero;
    private List<Entity> entities;

    private long levelTime;
    private double targetTime;
    private int currentScore;
    private int previousScore;
    private int lives;
    private long currentTime;
    private long startTime;

    public GameMemento(StickMan hero, List<Entity> entities, long currentTime, long startTime, long levelTime, double targetTime,
                       int currentScore, int previousScore, int lives) {
        this.hero = hero;
        this.entities = entities;
        this.levelTime = levelTime;
        this.currentTime = currentTime;
        this.startTime = startTime;
        this.targetTime = targetTime;
        this.currentScore = currentScore;
        this.previousScore = previousScore;
        this.lives = lives;
    }

    public Controllable getHero() {
        return hero;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public long getLevelTime() {
        return levelTime;
    }

    public double getTargetTime() {
        return targetTime;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public int getPreviousScore() {
        return previousScore;
    }

    public int getLives() {
        return lives;
    }
}
