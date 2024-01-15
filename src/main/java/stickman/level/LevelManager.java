package stickman.level;

import stickman.oberver.CurrentScoreObserver;
import stickman.memento.GameMemento;
import stickman.prototype.EntitySpawner;
import stickman.entity.*;
import stickman.entity.moving.MovingEntity;
import stickman.entity.moving.enemy.Slime;
import stickman.entity.moving.other.Bullet;
import stickman.entity.moving.other.Projectile;
import stickman.entity.moving.player.Controllable;
import stickman.entity.moving.player.StickMan;
import stickman.entity.still.Flag;
import stickman.entity.still.Mushroom;
import stickman.model.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the Level interface. Manages the running of
 * the level and all the entities within it.
 */
public class LevelManager implements Level {




    /**
     * The player character.
     */
    private long levelTime;
    private int lives;
    private Controllable hero;
    private Mushroom mushroom;
    private boolean heroWin = false;
    private CurrentScoreObserver currentScoreObserver;

    /**
     * A list of all the entities in the level.
     */
    private List<Entity> entities;

    /**
     * A list of all the moving entities in the level.
     */
    private List<MovingEntity> movingEntities;

    /**
     * A list of all the entities that can interact with the player.
     */
    private List<Interactable> interactables;

    /**
     * A list of all the projectiles (bullets) in the level.
     */
    private List<Projectile> projectiles;

    /**
     * The height of the level.
     */
    private double height;

    /**
     * The width of the level.
     */
    private double width;

    /**
     * The height of the floor in the level.
     */
    private double floorHeight;

    /**
     * Whether the entities should update, or the player has reached the flag.
     */
    private boolean active;

    /**
     * The name of the file the level is from.
     */
    private String filename;

    /**
     * The GameEngine the level is running inside of.
     */
    private GameEngine model;
    private double target_time;
    private long startTime = new Date().getTime();//获取当前时间

    private long currentTime;

    /**
     * Creates a new LevelManager object.
     *
     * @param model          The GameEngine the level is in
     * @param filename       The file the level is based off of
     * @param height         The height of the level
     * @param width          The width of the level
     * @param floorHeight    The height of the floor
     * @param heroX          The starting x of the hero
     * @param heroSize       The size of the hero
     * @param entities       The list of entities in the level
     * @param movingEntities The list of moving entities in the level
     * @param interactables  The list of entities that can interact with the hero in the level
     */
    public LevelManager(GameEngine model, String filename, double height, double width, double floorHeight, double heroX, String heroSize, List<Entity> entities, List<MovingEntity> movingEntities, List<Interactable> interactables, double target_time, int lives) {
        this.model = model;
        this.filename = filename;
        this.height = height;
        this.width = width;
        this.floorHeight = floorHeight;
        this.entities = entities;
        this.movingEntities = movingEntities;
        this.interactables = interactables;
        this.target_time = target_time;
        this.lives = lives;
        this.projectiles = new ArrayList<>();

        // Create new hero
        this.hero = new StickMan(heroX, floorHeight, heroSize, this, lives);
        this.movingEntities.add(this.hero);

        // Ensure entities has all entities (including moving ones)
        this.entities.addAll(movingEntities);
        this.entities = new ArrayList<>(new HashSet<>(entities));

        this.active = true;

    }
    public void setCurrentScoreObserver(CurrentScoreObserver observer) {
        currentScoreObserver = observer;
    }


    @Override
    public List<Entity> getEntities() {
        return this.entities;
    }

    @Override
    public double getHeight() {
        return this.height;
    }

    @Override
    public double getWidth() {
        return this.width;
    }

    @Override
    public void tick() {
        if (!active) {
            return;
        }

        for (MovingEntity entity : this.movingEntities) {
            entity.tick(this.entities, this.hero.getXPos(), this.floorHeight);
        }

        this.manageCollisions();

        // Remove inactive entities
        this.clearOutInactive();
    }

    /**
     * Removes inactive entities from all the lists.
     */
    private void clearOutInactive() {
        this.entities.removeIf(x -> !x.isActive());
        this.movingEntities.removeIf(x -> !this.entities.contains(x));
        this.interactables.removeIf(x -> !this.entities.contains(x));
        this.projectiles.removeIf(x -> !this.entities.contains(x));
    }

    /**
     * Calls interact methods on interactables and projectiles.
     */
    private void manageCollisions() {

        if (!entities.contains(this.hero)) {
            return;
        }

        // Collision between hero and other entity
        for (Interactable interactable : this.interactables) {
            if (interactable.checkCollide(hero)) {
                interactable.interact(hero);
            }
        }

        // Collision between bullet and moving entity (not hero)
        for (Projectile projectile : this.projectiles) {
            projectile.movingCollision(this.movingEntities.stream().filter(x -> x != hero).collect(Collectors.toList()));
        }

        // Collision between bullet and other entity
        for (Projectile projectile : this.projectiles) {
            projectile.staticCollision(this.entities.stream().filter(x -> x != hero).collect(Collectors.toList()));
        }
    }

    @Override
    public double getFloorHeight() {
        return this.floorHeight;
    }

    @Override
    public double getHeroX() {
        return this.hero.getXPos();
    }

    @Override
    public double getHeroY() {
        return this.hero.getYPos();
    }

    @Override
    public boolean jump() {
        if (!active) {
            return false;
        }
        return this.hero.jump();
    }

    @Override
    public boolean moveLeft() {
        if (!active) {
            return false;
        }
        return this.hero.moveLeft();
    }

    @Override
    public boolean moveRight() {
        if (!active) {
            return false;
        }
        return this.hero.moveRight();
    }

    @Override
    public boolean stopMoving() {
        if (!active) {
            return false;
        }
        return this.hero.stop();
    }

    @Override
    public void reset() {
        if (this.model != null) {
            this.model.reset();
        }
    }

    @Override
    public void shoot() {
        if (!this.hero.upgraded() || !active) {
            return;
        }
        double x = this.hero.getXPos() + this.hero.getWidth();

        if (this.hero.isLeftFacing()) {
            x = this.hero.getXPos();
        }
        Bullet bullet = new Bullet(x, this.hero.getYPos() + (2 * this.hero.getWidth() / 3), this.hero.isLeftFacing());
        bullet.attachObserver(currentScoreObserver);
        this.entities.add(bullet);
        this.movingEntities.add(bullet);
        this.projectiles.add(bullet);
    }

    @Override
    public String getSource() {
        return this.filename;
    }

    @Override
    public void win() {
        boolean b = model.isTransaction();
        if (!b) {
            this.active = false;
            heroWin = true;
        } else {
            model.level_Transition();
        }
    }

    public boolean getHeroWin() {
        return heroWin;
    }


    @Override
    public int getLives() {
        return hero.getLives();
    }

    @Override
    public double getTargetTime() {
        return target_time;
    }

    public void setTargetTime(double targetTime) {
        this.target_time = targetTime;
    }

    @Override
    public long getLevelTime() {
        currentTime = new Date().getTime();
        levelTime = (currentTime - startTime) / 1000;
        return levelTime;
    }

    public Mushroom getMushroom() {
        for (Entity e : entities) {
            if (e instanceof Mushroom) {
                mushroom = (Mushroom) e;
            }
        }
        return mushroom;
    }

    //originator
    public GameMemento saveGame() {
        List<Entity> entitiesCopy = new ArrayList<>();
        StickMan heroCopy = null;
        for (EntitySpawner entity : entities) {
            entitiesCopy.add((Entity) entity.copy());
        }
        long levelTimeCopy = getLevelTime();
        long currentTimeCopy = currentTime;
        long startTimeCopy = startTime;

        double targetTimeCopy = currentScoreObserver.getTargetTime();
        int currentScoreCopy = currentScoreObserver.getCurrentScore();
        int livesCopy = model.getLives();
        int previousScoreCopy = currentScoreObserver.getPreviousScore();
        return new GameMemento(heroCopy, entitiesCopy, currentTimeCopy
                , startTimeCopy, levelTimeCopy, targetTimeCopy, currentScoreCopy, previousScoreCopy, livesCopy);
    }

    public void loadGame(GameMemento gameMemento) {
        if (gameMemento == null) {
            return;
        }
        entities.clear();
        movingEntities.clear();
        interactables.clear();
        projectiles.clear();
        entities.addAll(gameMemento.getEntities());
        for (Entity entity : entities) {
            if (entity instanceof Mushroom) {
                interactables.add((Mushroom) entity);
            }
            if (entity instanceof Slime) {
                interactables.add((Slime) entity);
                movingEntities.add((Slime) entity);
            }
            if (entity instanceof Flag) {
                interactables.add((Interactable) entity);
            }
            if (entity instanceof Bullet) {
                movingEntities.add((Bullet) entity);
                projectiles.add((Bullet) entity);
            }
            if (entity instanceof StickMan) {

                this.hero = (StickMan) entity;
                movingEntities.add(this.hero);
            }
        }

        levelTime = (gameMemento.getLevelTime());
        startTime = currentTime - levelTime * 1000;
        currentScoreObserver.setCurrentTime(getLevelTime());
        setTargetTime(gameMemento.getTargetTime());
        currentScoreObserver.setTargetTime(target_time);
        currentScoreObserver.setCurrentScore(gameMemento.getCurrentScore());
        currentScoreObserver.setPreviousScore(gameMemento.getPreviousScore());
        model.setLives(gameMemento.getLives());
    }
}
