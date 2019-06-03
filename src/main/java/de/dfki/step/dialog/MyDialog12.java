package de.dfki.step.dialog;

import de.dfki.step.core.InputComponent;
import de.dfki.step.core.Token;
import de.dfki.step.core.TokenComponent;
import de.dfki.step.output.PresentationComponent;
import de.dfki.step.rengine.RuleComponent;
import de.dfki.step.srgs.Grammar;
import de.dfki.step.srgs.GrammarManager;
import de.dfki.step.srgs.MyGrammar;
import de.dfki.step.web.SpeechRecognitionClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;


/**
 * Rule based-dialog for simple request-response for greetings with Speech Recognition and TTS
 */
public class MyDialog12 extends Dialog {
    private static final Logger log = LoggerFactory.getLogger(MyDialog12.class);
    private final String app = "hello_world";
    //change host to ip where Speech Recognizer is running
    private final String host = "172.16.68.129";

    public MyDialog12() {
        // rules are registered in the RuleSystemComponent
        RuleComponent rsc = retrieveComponent(RuleComponent.class);
        // tokens are a way to pass data from e.g. the input into the dialog
        TokenComponent tc = retrieveComponent(TokenComponent.class);
        // component which handles the output (TTS in our case)
        PresentationComponent pc = retrieveComponent(PresentationComponent.class);

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

            pc.present(PresentationComponent.simpleTTS("Welcome"));
        });

        //call speech-recognition-service of the step-dp
        createGrammarComponent();

    }

    protected void createGrammarComponent() {
        GrammarManager gm = MyGrammar.create();
        Grammar grammar = gm.createGrammar();
        SpeechRecognitionClient src = new SpeechRecognitionClient(app, host, 9696, (token) -> {
           //get result from the speech recognizer
            Optional<Map> semantic = token.get(Map.class, "semantic");
            if (!semantic.isPresent()) {
                return;
            }

            Token processedToken = new Token((Map<String, Object>) semantic.get());

            // add token to input component
            InputComponent ic = retrieveComponent(InputComponent.class);
            ic.addToken(processedToken);
        });
        String grammarStr = grammar.toString();
        System.out.println("grammar: " + grammarStr);
        src.setGrammar("main", grammarStr);
        src.initGrammar();
        src.init();
    }
}

