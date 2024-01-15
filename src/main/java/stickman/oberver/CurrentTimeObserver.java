package stickman.oberver;

import stickman.model.GameManager;

public class CurrentTimeObserver implements Observer{
    private long currentTime=0;
    private GameManager model;
    public CurrentTimeObserver(GameManager model){
        this.model=model;
        model.attachObserver(this);


    }

    public long getCurrentTime() {
        return model.getCurrentLevel().getLevelTime();
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    public void update(int num) {
        setCurrentTime(getCurrentTime());
    }
}
