package stickman.memento;

public class GameCareTaker {
    private GameMemento gameMemento;

    public GameMemento getGameMemento() {
        return gameMemento;
    }

    public void setGameMemento(GameMemento gameMemento) {
        this.gameMemento = gameMemento;
    }
}
