package stickman.model;

import stickman.oberver.CurrentScoreObserver;
import stickman.oberver.CurrentTimeObserver;
import stickman.oberver.Observer;
import stickman.oberver.Subject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import stickman.memento.GameCareTaker;
import stickman.memento.GameMemento;
import stickman.level.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of GameEngine. Manages the running of the game.
 */
public class GameManager implements GameEngine , Subject {

    private List<Observer> observers=new ArrayList<>();
    private int lives;
    private boolean reset=false;
    private boolean transaction;
    private GameCareTaker gameCareTaker=new GameCareTaker();
    private int currentScore;
    private long currentTime;

    /**
     * The current level
     */
    private Level level;


    /**
     * List of all level files
     */
    private List<String> levelFileNames;
    public int count=0;
    /**
     * Creates a GameManager object.
     * @param levels The config file containing the names of all the levels
     */
    public GameManager(String levels) {
        this.levelFileNames = this.readConfigFile(levels);

        this.level = LevelBuilderImpl.generateFromFile(levelFileNames.get(count), this);
        if(levelFileNames.size()>1){
            transaction=true;
        }
        lives= level.getLives();

    }

    @Override
    public Level getCurrentLevel() {
        return this.level;
    }

    @Override
    public boolean jump() {
        return this.level.jump();
    }

    @Override
    public boolean moveLeft() {
        return this.level.moveLeft();
    }

    @Override
    public boolean moveRight() {
        return this.level.moveRight();
    }

    @Override
    public boolean stopMoving() {
        return this.level.stopMoving();
    }

    @Override
    public void tick() {
        this.level.tick();
        notifyObservers();
    }

    @Override
    public void shoot() {
        this.level.shoot();
    }

    @Override
    public void reset() {
        if(level.getHeroWin()){
            return;
        }
        reset=true;
        this.level = LevelBuilderImpl.generateFromFile(levelFileNames.get(count), this);
        lives--;
    }

    /**
     * Retrieves the list of level filenames from a config file
     * @param config The config file
     * @return The list of level names
     */
    @SuppressWarnings("unchecked")
    private List<String> readConfigFile(String config) {

        List<String> res = new ArrayList<String>();

        JSONParser parser = new JSONParser();

        try {

            Reader reader = new FileReader(config);

            JSONObject object = (JSONObject) parser.parse(reader);

            JSONArray levelFiles = (JSONArray) object.get("levelFiles");

            Iterator<String> iterator = (Iterator<String>) levelFiles.iterator();

            // Get level file names
            while (iterator.hasNext()) {
                String file = iterator.next();
                res.add("levels/" + file);
            }

        } catch (IOException e) {
            System.exit(10);
            return null;
        } catch (ParseException e) {
            return null;
        }

        return res;
    }

    public boolean isTransaction() {
        return transaction;
    }

    public void level_Transition(){
        if((count+1)<levelFileNames.size()){
            count++;
            this.level=LevelBuilderImpl.generateFromFile(levelFileNames.get(count), this);
            reset=false;
            if((count+1)==levelFileNames.size()){
               transaction=false;
            }else{

                transaction=true;
            }
        }
    }

    @Override
    public void notifyObservers() {
        for(Observer o:observers){
            o.update(0);
            if(o instanceof CurrentScoreObserver){
                currentScore=((CurrentScoreObserver) o).getCurrentScore();
            }
            if(o instanceof CurrentTimeObserver){
                currentTime=((CurrentTimeObserver) o).getCurrentTime();
            }
        }
    }
    public boolean getReset(){
        return reset;
    }
    public void setReset(boolean reset){
        this.reset=reset;
    }

    @Override
    public void attachObserver(Observer observer) {
        observers.add(observer);
    }


    public int  getLives(){
        return lives;
    }
    public void setLives(int lives){
        this.lives=lives;
    }
    private int countTemp;
    @Override
    public void save() {
        countTemp=count;
        gameCareTaker.setGameMemento(level.saveGame());
    }

    @Override
    public void load() {
        GameMemento gameMemento=gameCareTaker.getGameMemento();
        level.loadGame(gameMemento);
        gameCareTaker.setGameMemento(level.saveGame());
        count=countTemp;
    }
}