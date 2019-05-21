package de.dfki.step.taskmodel;


import de.dfki.step.output.PresentationComponent;

public class MoveTask extends AtomicTask {

    private String location;

    public MoveTask(String name, String locationId) {
        super(name);
        this.location = locationId;
    }

    @Override
    public void execute() {
        System.out.println("I will move to " + location);
    }
}
