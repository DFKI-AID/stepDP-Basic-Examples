package de.dfki.step.dialog;

import de.dfki.step.core.Component;
import de.dfki.step.core.ComponentManager;
import de.dfki.step.core.Token;
import de.dfki.step.core.TokenComponent;
import de.dfki.step.rengine.RuleSystemComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 */
public class MyDialog30 extends Dialog {
    private static final Logger log = LoggerFactory.getLogger(MyDialog30.class);

    public MyDialog30() {
        RuleSystemComponent rsc = retrieveComponent(RuleSystemComponent.class);
        TokenComponent tc = retrieveComponent(TokenComponent.class);
        ConfidenceAdapationComponent cac = new ConfidenceAdapationComponent();
        this.addComponent(cac);

        rsc.addRule("swipe_gesture", () -> {
            List<Token> gestureIntents = tc.getTokens().stream()
                    .filter(t -> t.payloadEquals("intent", "swipe_gesture"))
//                    .filter(t -> t.get("confidence", Double.class).orElse(0.0) > cac.getConfidence("swipe_gesture"))
                    .collect(Collectors.toList());

            for(Token gestureIntent : gestureIntents) {
                Double confidence = gestureIntent.get("confidence", Double.class).orElse(0.0);
                Double minConfidence = cac.getConfidence("swipe_gesture");
                if(confidence < minConfidence) {
                    log.info("Ignored {} because confidence was lower than {}", gestureIntent, minConfidence);
                    continue;
                }

                //handle swipe gesture
                System.out.println("swipe_gesture");
            }
        });
    }




    public static class ConfidenceAdapationComponent implements Component {
        private static Logger log = LoggerFactory.getLogger(ConfidenceAdapationComponent.class);
        // maps from arbitrary keys to confidences
        // keys could be 'intent name'
        private Map<String, Double> map = new HashMap<>();
        private Double defaultMinConfidence = 0.5;
        //If you use persistent data structure, you can avoid creating copies in the snapshot methods
//        private PMap<String, Double> map = HashTreePMap.empty();

        @Override
        public void init(ComponentManager cm) {
        }

        @Override
        public void deinit() {
        }

        @Override
        public void update() {
        }

        @Override
        public synchronized Object createSnapshot() {
            Map<String, Double> mapCopy = new HashMap<>();
            mapCopy.putAll(this.map);
            return mapCopy;
        }

        @Override
        public synchronized void loadSnapshot(Object snapshot) {
            this.map = new HashMap<>();
            this.map.putAll((Map) snapshot);
        }

        public synchronized void setConfidence(String key, Double minConfidence) {
            log.info("updating confidence for {}={}", key, minConfidence);
            this.map.put(key, minConfidence);
        }

        public synchronized Double getConfidence(String key) {
            Double confidence = map.get(key);
            if(confidence == null) {
                return defaultMinConfidence;
            }
            return confidence;
        }

    }
}

