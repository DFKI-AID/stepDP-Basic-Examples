package de.dfki.step.dialog;

import de.dfki.step.core.Schema;
import de.dfki.step.core.Token;
import de.dfki.step.core.TokenComponent;
import de.dfki.step.rengine.RuleComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


/**
 * Confidence is adapted for accepting greetings intent
 */
public class MyDialog11 extends Dialog {
    private static final Logger log = LoggerFactory.getLogger(MyDialog11.class);
    private RuleComponent rsc;
    private TokenComponent tc;

    public MyDialog11() {
        rsc = retrieveComponent(RuleComponent.class);
        tc = retrieveComponent(TokenComponent.class);

        createGreetingsRule(0.5);

    }

    private void createGreetingsRule(double minConfidence) {
        log.info("Creating greetings rule with a minimal confidence of {}", minConfidence);
        //build a schema to for easier matching the input
        // this is optional and can be done in various ways.
        // it is just nice to define the beforehand
        // Note: the descriptions are optional and may be helpful for debugging or user feedback
        Schema greetingsSchema = Schema.builder()
                .describe("we need a greetings intent")
                .add(t -> t.payloadEquals("intent", "greetings"))
                .describe(String.format("A minimal confidence of %1$,.2f is required", minConfidence))
                .add(t -> t.get("confidence", Double.class).orElse(1.0) > minConfidence)
                .build();


        //this will also overwrite any rule with the same name
        rsc.addRule("greetingsRule", () -> {
            // the code here is executed once for every iteration
            // here we look for an intent. think of the token object as an arbitrary data structure like json
            Optional<Token> optGreetings = tc.getTokens().stream()
                    .filter(t -> t.payloadEquals("intent", "greetings"))
                    .findAny();

            if(!optGreetings.isPresent()) {
                //do nothing, because no token with our desired intent was found
                return;
            }

            Token greetings = optGreetings.get();
            //use 1.0 as confidence if not set and ensure that is in the range of [0,1]
            double confidence = greetings.get("confidence", Double.class)
                    .map(c -> Math.max(0.0, c))
                    .map(c -> Math.min(1.0, c))
                    .orElse(1.0);

            if(confidence < minConfidence) {
                // the confidence is not sufficient to trigger a greetings
                // we adjust the rule by lowering the min confidence
                double newMinConfidence = Math.min(0.2, minConfidence - 0.1);
                createGreetingsRule(newMinConfidence);
                return;
            } else {
                // we adjust the rule by increasing the necessary confidence to trigger it again
                double newMinConfidence = Math.max(0.7, minConfidence + 0.1);
                createGreetingsRule(newMinConfidence);

                System.out.println("Hello!");
            }

        });



    }
}

