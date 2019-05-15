package de.dfki.step.dialog;

import de.dfki.step.core.Component;
import de.dfki.step.core.ComponentManager;
import de.dfki.step.core.InputComponent;
import de.dfki.step.core.Token;

public class FocusSimulationComponent implements Component {
    private InputComponent ic;
    private String focus = "billboard";

    @Override
    public void init(ComponentManager cm) {
        ic = cm.retrieveComponent(InputComponent.class);
    }

    @Override
    public void deinit() {

    }

    @Override
    public void update() {
        //volatile token will be removed after each iteration
        ic.addVolatileToken(new Token().add("focus", getFocus()));
    }

    @Override
    public Object createSnapshot() {
        return null;
    }

    @Override
    public void loadSnapshot(Object snapshot) {

    }

    public synchronized String getFocus() {
        return focus;
    }

    public synchronized void setFocus(String focus) {
        this.focus = focus;
    }
}
