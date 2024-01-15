package stickman.oberver;

import stickman.entity.still.Mushroom;
import stickman.model.GameManager;

public class CurrentScoreObserver implements Observer{
    private GameManager model;
    private Mushroom mushroom;
    private int currentScore;
    private double targetTime;
    private long currentTime;
    private int previousScore;
    private boolean firstTime=true;
    private int count;

    public CurrentScoreObserver(GameManager model){
        this.model=model;
        model.attachObserver(this);
        mushroom=model.getCurrentLevel().getMushroom();
        currentScore=0;
        targetTime=model.getCurrentLevel().getTargetTime();
        previousScore=0;
    }

    @Override
    public void update(int num) {
        if(num!=0){
            count+=num;
            currentScore+=num;
        }
        setCurrentScore(model.getCurrentLevel().getLevelTime());
        getCurrentScore();
    }
    public int getCurrentScore(){
        return currentScore;
    }
    public void setCurrentScore(long time){
        if(time==0&&firstTime){
            firstTime=false;
        }
        if(time==0&&!firstTime&&!model.getReset()){
            previousScore+=currentScore;
            //model.setReset(false);
        }
        if(time==0&&!firstTime){
            targetTime=model.getCurrentLevel().getTargetTime();
            currentTime=0;
            currentScore=0;
        }
        if(time>currentTime){
            if(currentTime<targetTime){
                currentScore+=1;
            }else if(time>targetTime){
                currentScore-=1;
            }currentTime=time;
        }


    }
    public double getTotalScore(){
        return previousScore+currentScore;
    }

    public double getTargetTime() {
        return targetTime;
    }

    public int getPreviousScore() {
        return previousScore;
    }

    public void setPreviousScore(int previousScore) {
        this.previousScore = previousScore;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    public void setTargetTime(double targetTime) {
        this.targetTime = targetTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }
}
