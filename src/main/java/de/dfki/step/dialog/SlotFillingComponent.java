package de.dfki.step.dialog;

import de.dfki.step.core.Component;
import de.dfki.step.core.ComponentManager;
import de.dfki.step.core.Token;
import de.dfki.step.core.TokenComponent;
import de.dfki.step.kb.Entity;
import de.dfki.step.output.PresentationComponent;
import de.dfki.step.rengine.RuleComponent;



public class SlotFillingComponent implements Component {
    private RuleComponent rsc;
    private TokenComponent tc;
    private PresentationComponent pc;
    private Entity frameInfo;

    SlotFillingComponent(Entity frameInfo) {
        this.frameInfo = frameInfo;
    }

    @Override
    public void init(ComponentManager cm) {
        rsc = cm.retrieveComponent(RuleComponent.class);
        tc = cm.retrieveComponent(TokenComponent.class);
        pc = cm.retrieveComponent(PresentationComponent.class);

    }

    @Override
    public void deinit() {

    }

    @Override
    public void update() {
        for(String key: frameInfo.attributes.keySet()) {
            for(Token token: tc.getTokens()) {
                if(token.has(key)) {
                    //frameInfo.attributes.get(key) = new AttributeValue(key, token.get(key).get(), frameInfo.);
                }
            }

        }

    }

    @Override
    public Object createSnapshot() {
        return null;
    }

    @Override
    public void loadSnapshot(Object snapshot) {

    }
}
