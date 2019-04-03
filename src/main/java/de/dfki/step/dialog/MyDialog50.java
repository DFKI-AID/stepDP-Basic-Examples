package de.dfki.step.dialog;

import de.dfki.step.core.InputComponent;
import de.dfki.step.core.Schema;
import de.dfki.step.core.Token;
import de.dfki.step.core.TokenComponent;
import de.dfki.step.fusion.FusionComponent;
import de.dfki.step.fusion.InputNode;
import de.dfki.step.fusion.ParallelNode;
import de.dfki.step.rengine.CoordinationComponent;
import de.dfki.step.rengine.RuleSystemComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;


/**
 * Example that shows some functionality of {@link de.dfki.step.fusion.FusionComponent}
 */
public class MyDialog50 extends Dialog {
    private static final Logger log = LoggerFactory.getLogger(MyDialog50.class);

    public MyDialog50() {
        RuleSystemComponent rsc = retrieveComponent(RuleSystemComponent.class);
        TokenComponent tc = retrieveComponent(TokenComponent.class);
        FusionComponent fc = retrieveComponent(FusionComponent.class);
        CoordinationComponent cc = retrieveComponent(CoordinationComponent.class);
        this.initFusion(fc);
        // Unimodal inputs are stored in the InputComponent.
        // Your external sensor should add tokens via ic.addTokens(...)
        // Tokens are removed if they are consumed, or if they are too old.
        InputComponent ic = retrieveComponent(InputComponent.class);
        ic.setTimeout(Duration.ofSeconds(4));




        rsc.addRule("rotateModel", () -> {
            tc.getTokens().stream()
                    .filter(t -> t.payloadEquals("intent", "rotate_model"))
                    .filter(t -> t.get("direction", String.class).isPresent())
                    .findAny()
                    .ifPresent(t -> {
                        cc.add(() -> {
                            log.info("rotating: {}", t.get("direction", String.class).get());
                        }).attachOrigin(t);

                    });
        });


        // here we simulate the focus from an external component
        var fsc = new FocusSimulationComponent();
        this.addComponent(fsc);
        // set the priority such that it will executed before the input component
        setPriority(fsc.getId(), getPriority(ic.getId()) -1);
    }

    /**
     * Create fusion rule that combines gestures and focus into 'rotate_model' intents.
     * Scenario: User sees a 3D model on a screen and wants to rotate it with a gesture.
     *
     * Creates a fusion rule that combines speech and focus into 'rotate_model' intents.
     * @param fc
     */
    public void initFusion(FusionComponent fc) {
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
    }
}

