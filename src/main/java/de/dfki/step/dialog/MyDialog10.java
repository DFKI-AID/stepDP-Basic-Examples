package de.dfki.step.dialog;

import de.dfki.step.core.Token;
import de.dfki.step.core.TokenComponent;
import de.dfki.step.rengine.RuleComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


/**
 * Rule based-dialog for simple request-response for greetings
 */
public class MyDialog10 extends Dialog {
    private static final Logger log = LoggerFactory.getLogger(MyDialog10.class);

    public MyDialog10() {
        // rules are registered in the RuleSystemComponent
        RuleComponent rsc = retrieveComponent(RuleComponent.class);
        // tokens are a way to pass data from e.g. the input into the dialog
        TokenComponent tc = retrieveComponent(TokenComponent.class);

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

            System.out.println("Hello!");
        });

    }
}

