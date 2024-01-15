package stickman.view;

import stickman.oberver.CurrentScoreObserver;
import stickman.oberver.CurrentTimeObserver;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import stickman.entity.Entity;
import stickman.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The window the Game exists within.
 */
public class GameWindow {
    private Text currentScore;
    private Text previousScore;
    private Text lives;
    private Text targetTime;
    private Text currentTime;
    private CurrentScoreObserver currentScoreObserver;
    private CurrentTimeObserver currentTimeObserver;
    private Timeline timeline;
    private String totalScore;


    /**
     * The distance from the top/bottom the player can be before the camera follows.
     */
    private static final double VIEWPORT_MARGIN_VERTICAL = 130.0;

    /**
     * The distance from the left/right side the player can be before the camera follows.
     */
    private static final double VIEWPORT_MARGIN = 280.0;

    /**
     * The width of the screen.
     */
    private final int width;

    /**
     * The height of the screen.
     */
    private final int height;

    /**
     * The current running scene.
     */
    private Scene scene;

    /**
     * The pane of the window on which sprites are projected.
     */
    private Pane pane;

    /**
     * The GameEngine of the game.
     */
    private GameEngine model;

    /**
     * A list of all the entities' views in the Game.
     */
    private List<EntityView> entityViews;

    /**
     * The background of the scene.
     */
    private BackgroundDrawer backgroundDrawer;

    /**
     * The x-offset of the camera.
     */
    private double xViewportOffset = 0.0;

    /**
     * The y-offset of the camera.
     */
    private double yViewportOffset = 0.0;
    private int countTran = 0;

    /**
     * Creates a new GameWindow object.
     *
     * @param model  The GameEngine of the game
     * @param width  The width of the screen
     * @param height The height of the screen
     */
    public GameWindow(GameEngine model, int width, int height) {
        this.model = model;
        this.pane = new Pane();
        this.width = width;
        this.height = height;
        this.scene = new Scene(pane, width, height);
        currentScoreObserver = new CurrentScoreObserver((GameManager) model);
        currentTimeObserver = new CurrentTimeObserver((GameManager) model);

        this.entityViews = new ArrayList<>();
        this.previousScore = new Text("Previous score: " + currentScoreObserver.getPreviousScore());
        this.previousScore.setFont(Font.font("Chalkboard SE", FontPosture.ITALIC, 17.5));
        this.previousScore.setFill(Paint.valueOf("BLACK"));
        this.previousScore.setX(10);
        this.previousScore.setY(25);
        this.pane.getChildren().add(previousScore);

        this.currentScore = new Text("Current score: " + currentScoreObserver.getCurrentScore());
        this.currentScore.setFont(Font.font("Chalkboard SE", FontPosture.ITALIC, 17.5));
        this.currentScore.setFill(Paint.valueOf("BLACK"));
        this.currentScore.setX(10);
        this.currentScore.setY(50);
        this.pane.getChildren().add(currentScore);

        this.lives = new Text("Lives: " + model.getLives());
        this.lives.setFont(Font.font("Chalkboard SE", FontPosture.ITALIC, 17.5));
        this.lives.setFill(Paint.valueOf("BLACK"));
        this.lives.setX(10);
        this.lives.setY(75);
        this.pane.getChildren().add(lives);

        this.targetTime = new Text("targetTime: " + model.getCurrentLevel().getTargetTime());
        this.targetTime.setFont(Font.font("Chalkboard SE", FontPosture.ITALIC, 17.5));
        this.targetTime.setFill(Paint.valueOf("BLACK"));
        this.targetTime.setX(450);
        this.targetTime.setY(25);
        this.pane.getChildren().add(targetTime);

        this.currentTime = new Text("currentTime: " + currentTimeObserver.getCurrentTime());
        this.currentTime.setFont(Font.font("Chalkboard SE", FontPosture.ITALIC, 17.5));
        this.currentTime.setFill(Paint.valueOf("BLACK"));
        this.currentTime.setX(450);
        this.currentTime.setY(50);
        this.pane.getChildren().add(currentTime);

        KeyboardInputHandler keyboardInputHandler = new KeyboardInputHandler(model);
        scene.setOnKeyPressed(keyboardInputHandler::handlePressed);
        scene.setOnKeyReleased(keyboardInputHandler::handleReleased);
        this.backgroundDrawer = new BlockedBackground();
        backgroundDrawer.draw(model, pane);
    }

    private void setCurrentScore() {
        if (currentScoreObserver.getCurrentScore() < 0) {
            model.reset();
        }
        this.currentScore.setText("Current score: " + currentScoreObserver.getCurrentScore());
    }

    private void setPreviousScore() {
        this.previousScore.setText("Previous score: " + currentScoreObserver.getPreviousScore());
    }

    private void setTargetTime() {
        this.targetTime.setText("Target Time: " + model.getCurrentLevel().getTargetTime());
    }

    private void setCurrentTime() {
        this.currentTime.setText("Current Time: " + currentTimeObserver.getCurrentTime());
    }

    private void setLives() {
        if (model.getLives() == 0) {
            drawGameOver();
        } else {
            this.lives.setText("Lives: " + model.getLives());
        }
    }

    /**
     * Returns the scene.
     *
     * @return The current scene
     */
    public Scene getScene() {
        return this.scene;
    }

    /**
     * Starts the game.
     */

    public void run() {
        timeline = new Timeline(new KeyFrame(Duration.millis(17),
                t -> this.draw()));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }



    /**
     * Draws the game (and updates it).
     */
    private void draw() {
        model.tick();
        model.getCurrentLevel().getMushroom().attachObserver(currentScoreObserver);
        model.getCurrentLevel().setCurrentScoreObserver(currentScoreObserver);

        setCurrentTime();
        setCurrentScore();
        setPreviousScore();
        setTargetTime();
        setLives();

        if (model.getCurrentLevel().getHeroWin()) {
            drawGameOver();
            timeline.stop();
        }

        List<Entity> entities = model.getCurrentLevel().getEntities();

        for (EntityView entityView : entityViews) {
            entityView.markForDelete();
        }

        double heroXPos = model.getCurrentLevel().getHeroX();
        heroXPos -= xViewportOffset;

        if (heroXPos < VIEWPORT_MARGIN) {
            if (xViewportOffset >= 0) { // Don't go further left than the start of the level
                xViewportOffset -= VIEWPORT_MARGIN - heroXPos;
                if (xViewportOffset < 0) {
                    xViewportOffset = 0;
                }
            }
        } else if (heroXPos > width - VIEWPORT_MARGIN) {
            xViewportOffset += heroXPos - (width - VIEWPORT_MARGIN);
        }

        double heroYPos = model.getCurrentLevel().getHeroY();
        heroYPos -= yViewportOffset;

        if (heroYPos < VIEWPORT_MARGIN_VERTICAL) {
            if (yViewportOffset >= 0) { // Don't go further up than the top of the level
                yViewportOffset -= VIEWPORT_MARGIN_VERTICAL - heroYPos;
            }
        } else if (heroYPos > height - VIEWPORT_MARGIN_VERTICAL) {
            yViewportOffset += heroYPos - (height - VIEWPORT_MARGIN_VERTICAL);
        }

        backgroundDrawer.update(xViewportOffset, yViewportOffset);

        for (Entity entity : entities) {
            boolean notFound = true;
            for (EntityView view : entityViews) {
                if (view.matchesEntity(entity)) {
                    notFound = false;
                    view.update(xViewportOffset, yViewportOffset);
                    break;
                }
            }
            if (notFound) {
                EntityView entityView = new EntityViewImpl(entity);
                entityViews.add(entityView);
                pane.getChildren().add(entityView.getNode());
            }
        }

        for (EntityView entityView : entityViews) {
            if (entityView.isMarkedForDelete()) {
                pane.getChildren().remove(entityView.getNode());
            }
        }
        entityViews.removeIf(EntityView::isMarkedForDelete);
    }

    public void drawGameOver() {
        pane.getChildren().clear();
        pane.getChildren().add(previousScore);
        scene.setOnKeyReleased(null);
        scene.setOnKeyPressed(null);
        Text gameOver = new Text();
        if (model.getCurrentLevel().getHeroWin()) {
            totalScore = Double.toString(currentScoreObserver.getTotalScore());
            gameOver.setText("Winner!" + "total score:" + totalScore);
        } else {
            gameOver.setText("Game Over!");
        }
        gameOver.setFont(Font.font("Chalkboard SE", FontPosture.ITALIC, 38));
        gameOver.setFill(Paint.valueOf("BLACK"));
        gameOver.setTextAlignment(TextAlignment.CENTER);
        gameOver.setLayoutX((width - gameOver.getLayoutBounds().getWidth()) / 2.0);
        gameOver.setLayoutY((height - gameOver.getLayoutBounds().getHeight()) / 2.0);
        pane.getChildren().add(gameOver);
        backgroundDrawer.draw(model, pane);
        timeline.stop();
    }

}
