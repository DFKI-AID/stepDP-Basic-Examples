package de.dfki.step.dialog;

import de.dfki.step.core.Token;
import de.dfki.step.core.TokenComponent;
import de.dfki.step.rengine.Rule;
import de.dfki.step.rengine.RuleSystemComponent;
import de.dfki.step.sc.SimpleStateBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.*;


/**
 */
public class MyDialog40 extends Dialog {
    private static final Logger log = LoggerFactory.getLogger(MyDialog40.class);
    private PizzaBehavior pizzaBehavior;

    public MyDialog40() {
        RuleSystemComponent rsc = retrieveComponent(RuleSystemComponent.class);
        TokenComponent tc = retrieveComponent(TokenComponent.class);
        try {
            pizzaBehavior = new PizzaBehavior();
            addComponent("pizza_behavior", pizzaBehavior);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        rsc.addRule("incomingCall", () -> {
            Optional<Token> token = tc.getTokens().stream()
                    .filter(t ->
                            t.payloadEquals("intent", "hungry")
                            || t.payloadEquals("intent", "wants_pizza")
                    )
                    .findAny();

            if(!token.isPresent())
            {
                return;
            }

            pizzaBehavior.getStateHandler().fire("incoming_call");
        });

        rsc.addRule("placeOrder", () -> {

        });

        List<String> waitingRules = List.of("incomingCall",  "two");

    }

    public static class PizzaBehavior extends SimpleStateBehavior {
        public PizzaBehavior() throws URISyntaxException {
            // path inside the resources folder
            super("/sc/MyDialog40");
        }

        public void greetCustomer() {
            System.out.println("WELCOME TO OUR AWESOME PIZZA PLACE!!!");
        }

        @Override
        public Set<String> getActiveRules(String state) {
            if(Objects.equals(state, "Waiting")) {
                List.of("incomingCall",  "two");
            }
            if(Objects.equals(state, "Confirm")) {
                List.of();
            }
            if(Objects.equals(state, "OrderPlacement")) {
                List.of();
            }
            if(Objects.equals(state, "Waiting")) {
                List.of();
            }

            //log.warn("no rules for state {}", state);
            return Collections.EMPTY_SET;
        }
    }
}

