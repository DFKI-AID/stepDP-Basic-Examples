package de.dfki.step.dialog;

import de.dfki.step.core.InputComponent;
import de.dfki.step.core.Token;
import de.dfki.step.core.TokenComponent;
import de.dfki.step.fusion.FusionComponent;
import de.dfki.step.core.CoordinationComponent;
import de.dfki.step.kb.DataStore;
import de.dfki.step.output.PresentationComponent;
import de.dfki.step.rengine.RuleComponent;
import de.dfki.step.resolution.ResolutionComponent;
import de.dfki.step.taskmodel.*;
import de.dfki.step.util.Vector3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class MyTaskDialog extends Dialog {
    private static final Logger log = LoggerFactory.getLogger(MyTaskDialog.class);

    public MyTaskDialog() {
        DataStore ds = new DataStore();
        ds.checkMutability(true);

        MyDataEntry human1 = new MyDataEntry(ds, "w1");
        human1.setVisualFocus("box3");
        human1.setPosition(new Vector3(0, 0, 0));
        human1.save();

        MyDataEntry robot1 = new MyDataEntry(ds, "r1");
        robot1.setPosition(new Vector3(2.0, 2.0, 2.0));
        robot1.save();

        MyDataEntry box1 = new MyDataEntry(ds, "box1");
        box1.setColors(List.of("red"));
        box1.setSize("small");
        box1.setPosition(new Vector3(1.0, 1.0, 1.0));

        MyDataEntry box2 = new MyDataEntry(ds, "box2");
        box2.setColors(List.of("blue"));
        box2.setSize("small");
        box2.setPosition(new Vector3(-1.0, -1.0, -1.0));

        MyDataEntry box3 = new MyDataEntry(ds, "box3");
        box3.setColors(List.of("red"));
        box3.setSize("large");
        box3.setPosition(new Vector3(2.0, 3.0, 2.0));



        RuleComponent rc = retrieveComponent(RuleComponent.class);
        TokenComponent tc = retrieveComponent(TokenComponent.class);
        FusionComponent fc = retrieveComponent(FusionComponent.class);
        PresentationComponent pc = retrieveComponent(PresentationComponent.class);
        CoordinationComponent cc = retrieveComponent(CoordinationComponent.class);
        ResolutionComponent resc = retrieveComponent(ResolutionComponent.class);

      //  MetaFactory mf = new MetaFactory();

        //this.initFusion(fc);
        // Unimodal inputs are stored in the InputComponent.
        // Your external sensor should add tokens via ic.addTokens(...)
        // Tokens are removed if they are consumed, or if they are too old.
        InputComponent ic = retrieveComponent(InputComponent.class);
        ic.setTimeout(Duration.ofSeconds(4));



        // here we simulate the focus from an external component
    /*    var fsc = new FocusSimulationComponent();
        this.addComponent(fsc);
        // set the priority such that it will executed before the input component
        setPriority(fsc.getId(), getPriority(ic.getId()) -1);
*/
        var trc = new TaskLearningComponent(ds, robot1);
        this.addComponent(trc);

        rc.addRule("executeTask", () -> {
            // check for tokens with the intent 'startTask'
            Optional<Token> token = tc.getTokens().stream()
                    .filter(t -> t.payloadEquals("intent", "executeTask"))
                    .findFirst();
            if (!token.isPresent()) {
                return;
            }

            Token executeToken = token.get();
            if (!executeToken.get("taskname").isPresent()) {
                cc.add("executeTask", () -> {
                    if (robot1.getTasks().isEmpty()) {
                        String taskname =  ((RootTask) robot1.getTasks().get(0)).getName();
                        robot1.getTasks().get(0).execute();
                        pc.present(PresentationComponent.simpleTTS("Okay I will execute the task: " + taskname));
                    }
                }).attachOrigin(executeToken);
            }else {
                cc.add("executeTask_else", () -> {
                    Task eTask = null;
                    for(Task t: robot1.getTasks()) {
                        if(t instanceof RootTask) {
                            if(((RootTask) t).getName().equals(executeToken.get("taskname").orElse(""))) {
                               eTask = t;
                               break;
                            }
                        }
                    }
                    if(eTask != null) {
                        eTask.execute();
                        pc.present(PresentationComponent.simpleTTS("Okay I will execute the task: " + executeToken.get("taskname").orElse("")));
                    }
                }).attachOrigin(executeToken);



            }

        });




    }





    /**
     * Create fusion rule that combines gestures and focus into 'rotate_model' intents.
     * Scenario: User sees a 3D model on a screen and wants to rotate it with a gesture.
     *
     * Creates a fusion rule that combines speech and focus into 'rotate_model' intents.
     * @param fc
     */
   /* public void initFusion(FusionComponent fc) {
        // looking for up and down gesture
        Schema gestureSchema = Schema.builder()
                .add(t -> t.payloadEqualsOneOf("gesture", "down", "up")).build();
        InputNode gestureNode = new InputNode(gestureSchema);

        // check if focus is on billboard
        InputNode focusNode = new InputNode(t -> t.payloadEquals("focus", "billboard"));

        //build a node for multimodal input: This waits
        ParallelNode node = new ParallelNode()
                .add(gestureNode)
                .add(focusNode);

        fc.addFusionNode("control_3d_model_g+f", node, match -> {
            //this function is triggered if the fusion module finds a match in the input history
            //it will create an intent and forward it to the dialog core.

            // It is important that the intent includes an origin, confidence and intent itself
            // To merge the origin from all inputs and get the average confidence from all inputs
            // we use a helper function here.
            Token intent = FusionComponent.defaultIntent(match, "rotate_model");

            // We set additional data that is required by our rules
            intent = intent.add("direction", match.getToken(gestureNode).get().get("gesture", String.class).get());

            //add this point we have something like
            //  token:
            //      intent = control_3d_model
            //      confidence = 0.4
            //      origin = [ a, b]
            //      direction = up


            return intent;
        });

        // speech + focus
        InputNode speechNode = new InputNode(t ->
                t.payloadEquals("intent", "rotate")
                        && t.payloadEqualsOneOf("direction", "left", "right", "down", "up")
        );
        ParallelNode speechAndFocus = new ParallelNode()
                .add(speechNode)
                .add(focusNode);
        fc.addFusionNode("control_3d_model_s+f", speechAndFocus, match -> {
            Token intent = FusionComponent.defaultIntent(match, "rotate_model");
            intent = intent.add("direction", match.getToken(speechNode).get().get("direction", String.class).get());
            return intent;
        });

        //how to say that entity-type of speech and pointing is the same,
        // if pointing has more than one candidate -> fuse them separately,
        // how from object detection -> 3 boxes in area to concrete object ids...
        InputNode speechNodeTake = new InputNode(t -> t.payloadEquals("intent", "grab") && t.payloadEquals("slot_type", "entity"));
        InputNode pointingNode = new InputNode(t -> t.has("pointing"));

        ParallelNode parallelNode = new ParallelNode()
                .add(speechNodeTake)
                .add(pointingNode);
        fc.addFusionNode("grab", parallelNode, match -> {
            if(!match.getToken(speechNodeTake).get().get("entity_type").equals(match.getToken(pointingNode).get().get("pointing"))) {
                return  null;
            }

            Token intent = FusionComponent.defaultIntent(match, "grab");
            intent = intent.addAll(match.getToken(speechNodeTake).get().getPayload());
            return intent;
        });



    }*/
}

