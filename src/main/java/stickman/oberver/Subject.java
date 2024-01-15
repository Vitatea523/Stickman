package stickman.oberver;

public interface Subject {
    void notifyObservers();
    void attachObserver(Observer observer);
}
