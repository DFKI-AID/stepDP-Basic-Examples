package de.dfki.step.dialog;

import de.dfki.step.core.*;
import de.dfki.step.kb.DataStore;
import de.dfki.step.output.PresentationComponent;
import de.dfki.step.rengine.RuleComponent;
import de.dfki.step.taskmodel.RootTask;
import de.dfki.step.taskmodel.Task;
import de.dfki.step.tm.GrabDropTask;
import de.dfki.step.tm.MoveTask;
import de.dfki.step.tm.MyDataEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/* one single rule to pick up an object is created */
public class MyDialog60Component implements Component {


    private RuleComponent rsc;
    private TokenComponent tc;
    private PresentationComponent pc;
    private MetaFactory mf;
    private Map<String, Object> map = new HashMap<>();


    public void createPickUpRule() {
        rsc.addRule("pick_up", () -> {
            // check for tokens with the intent 'pick up'
            Optional<Token> token = tc.getTokens().stream()
                    .filter(t -> t.payloadEquals("intent", "pick_up"))
                    .findFirst();
            if (!token.isPresent()) {
                return;
            }

            Token intent = token.get();

            if(!intent.get("slots").isPresent()) {
                pc.present(PresentationComponent.simpleTTS("What should I pick up?"));

                //specify rule does not need to be activated?
                mf.specifyRule("specify_pick_up", (specification) -> {
                    pc.present(PresentationComponent.simpleTTS("Ok, I pick up " + specification));
                });

            }else {
                Map<String, Object> slotinfo = (Map) intent.get("slots").get();
                if(slotinfo.containsKey("resolved") && slotinfo.get("resolved").equals(true) && !((List<Map<String, Object>>) slotinfo.get("resolved_candidates")).isEmpty()) {
                    //check if exactly one resolved candidate has been found
                    if(((List<Map<String,Object>>) slotinfo.get("resolved_candidates")).size() == 1) {
                        String entity = (String) ((List<Map<String,Object>>)slotinfo.get("resolved_candidates")).get(0).get("res_id");
                        pc.present(PresentationComponent.simpleTTS("Ok, I pick up " + entity));
                        return;
                    }

                    String entity = null;
                    double conf = 0.0;
                    //check for entity with highest confidence
                    for(Map<String, Object> slotCandidate: (List<Map<String, Object>>) slotinfo.get("resolved_candidates")) {
                        if((Double) slotCandidate.get("res_conf") > conf) {
                            entity = (String) slotCandidate.get("res_id");
                        }
                    }

                    if(entity != null) {
                        pc.present(PresentationComponent.simpleTTS("I found several possible objects: Should I pick up " + entity + "?"));
                        String finalEntity = entity;
                        mf.createInformAnswer("inform_answer", () -> {
                            pc.present(PresentationComponent.simpleTTS("Ok, I pick up " + finalEntity));
                        }, () -> {
                            pc.present(PresentationComponent.simpleTTS("What should I pick up?"));
                        });


                    }
                }
            }

        });

    }



    @Override
    public void init(ComponentManager cm) {
        rsc = cm.retrieveComponent(RuleComponent.class);
        tc = cm.retrieveComponent(TokenComponent.class);
        pc = cm.retrieveComponent(PresentationComponent.class);
        mf = new MetaFactory(cm);

        createPickUpRule();

    }

    @Override
    public void deinit() {

    }

    @Override
    public void update() {

    }

    @Override
    public Object createSnapshot() {
        Map<String, Object> mapCopy = new HashMap<>();
        mapCopy.putAll(this.map);
        return mapCopy;
    }

    @Override
    public void loadSnapshot(Object snapshot) {
        this.map = new HashMap<>();
        this.map.putAll((Map) snapshot);

    }
}
