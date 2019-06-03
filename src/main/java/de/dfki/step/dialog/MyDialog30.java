package de.dfki.step.dialog;

import de.dfki.step.core.ComponentManager;
import de.dfki.step.core.CoordinationComponent;
import de.dfki.step.core.TokenComponent;
import de.dfki.step.output.PresentationComponent;
import de.dfki.step.rengine.RuleComponent;
import de.dfki.step.sc.SimpleStateBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;


/**
 * Example for simple statechart-based dialog
 */
public class MyDialog30 extends Dialog {
    private static final Logger log = LoggerFactory.getLogger(MyDialog30.class);

    public MyDialog30() {

        try {
            GreetingBehavior greetingBehavior = new GreetingBehavior();
            addComponent(greetingBehavior);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }

    public static class GreetingBehavior extends SimpleStateBehavior {

        private RuleComponent rc;
        private TokenComponent tc;
        private CoordinationComponent cc;
        private PresentationComponent pc;

        public GreetingBehavior() throws URISyntaxException {
            super("/sc/greetings");
        }

        @Override
        public void init(ComponentManager cm) {
            super.init(cm);
            rc = cm.retrieveComponent(RuleComponent.class);
            tc = cm.retrieveComponent(TokenComponent.class);
            cc = cm.retrieveComponent(CoordinationComponent.class);
            pc = cm.retrieveComponent(PresentationComponent.class);

            handleGreetings();
        }




        @Override
        public Set<String> getActiveRules(String state) {
            //makes sure that rules are only activated in the given state
            if(Objects.equals(state, "Start")) {
                return Set.of("say_hello");
            }
            if(Objects.equals(state, "End")) {
                return Set.of("say_goodbye");
            }
            return Collections.EMPTY_SET;
        }



        public void handleGreetings() {

            rc.addRule("say_hello", () -> {
                tc.getTokens().stream()
                    .filter(t -> t.payloadEquals("intent", "greet"))
                    .forEach(t -> {
                        cc.add(() -> {
                            pc.present(PresentationComponent.simpleTTS("Hello! Nice to meet you."));
                            //triggers the transition to go into the other state
                            stateHandler.fire("hello");
                        }).attachOrigin(t);
                    });
            });

            rc.addRule("say_goodbye", () -> {
                tc.getTokens().stream()
                    .filter(t -> t.payloadEquals("intent", "bye"))
                    .forEach(t -> {
                        cc.add(() -> {
                            pc.present(PresentationComponent.simpleTTS("Goodbye! It was a pleasure to meet you."));
                            stateHandler.fire("goodbye");

                        }).attachOrigin(t);
                    });
            });

        }
    }
}

