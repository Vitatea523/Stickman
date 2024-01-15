1. Type 'gradle build' and 'gradle run' to run my application.
2. Level Transition, Score and Time, and Save and Load
3. Design pattern:
a.Observer：In stickman.observer
Subject：Subject
ConcreteSubject: GameManager Bullet Mushroom
Observer：Observer 
ConcreteObserver：CurrentTimeObserver CurrentScoreObserver

b.Memento：In stickman.memento
Memento: GameMemento
Originator: Level
Caretaker: GameCareTaker

c.Prototype：In stickman.prototype
Prototype:EntitySpawner
ConcretePrototype:All entity classes implements entity
Client:Level

4.
Press 'S' to save and press 'Q' to load.
5.
The design pattern details I wrote in the report, and I have split the UML graph to show
the dependency.

