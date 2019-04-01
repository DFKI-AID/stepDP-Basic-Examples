package de.dfki.step.dialog;

import de.dfki.step.core.TokenComponent;
import de.dfki.step.rengine.RuleSystemComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Rule based-dialog for simple request-response
 */
public class MyDialog20 extends Dialog {
    private static final Logger log = LoggerFactory.getLogger(MyDialog20.class);

    public MyDialog20() {
        // rules are registered in the RuleSystemComponent
        RuleSystemComponent rsc = retrieveComponent(RuleSystemComponent.class);
        // tokens are a way to pass data from e.g. the input into the dialog
        TokenComponent tc = retrieveComponent(TokenComponent.class);

        //Here we create two rules, whereby only one is active at a time.
        //If one of the rules fire, it will deactivate itself and enable the other rule
//
//        rsc.addRule("rule1", () -> {
//            // the code here is executed once for every iteration
//            // here we look for an intent. think of the token object as an arbitrary data structure like json
//            tc.getTokens().stream()
//                    .filter(t -> t.payloadEquals("intent", ))
//
//        });

    }
}

