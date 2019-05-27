package de.dfki.step.tm;


import de.dfki.step.taskmodel.AtomicTask;

public class GrabDropTask extends AtomicTask {

    private String entityId;

    public GrabDropTask(String name, String entityId) {
        super(name);
        this.entityId = entityId;
    }

    @Override
    public void execute() {
        if(super.getName().equals("grab")) {
            System.out.println("I will grab the " + entityId);
        }else if(super.getName().equals("drop")) {
            System.out.println("I will drop the " + entityId);
        }

    }
}
