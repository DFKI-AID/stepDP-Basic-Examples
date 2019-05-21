package de.dfki.step.resolution;

import de.dfki.step.core.Component;
import de.dfki.step.core.ComponentManager;
import de.dfki.step.core.InputComponent;
import de.dfki.step.core.Token;
import de.dfki.step.kb.Attribute;
import de.dfki.step.kb.AttributeValue;
import de.dfki.step.kb.DataEntry;
import de.dfki.step.kb.Entity;
import org.pcollections.PSequence;
import org.pcollections.PSet;
import org.pcollections.TreePVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ResolutionComponent implements Component {

    private static Logger log = LoggerFactory.getLogger(ResolutionComponent.class);
    private ComponentManager cm;
    private Supplier<Collection<DataEntry>> personSupplier;
    private Supplier<Collection<DataEntry>> physicalEntitySupplier;
    private Supplier<Collection<DataEntry>> sessionSupplier;
    private PSequence<ReferenceDistribution> distributions = TreePVector.empty();
    private final double RESOLUTION_CONFIDENCE = 0.1;


    public void setPersonSupplier(Supplier<Collection<DataEntry>> personSupplier) {
        this.personSupplier = personSupplier;
    }

    public void setPhysicalEntitySupplier(Supplier<Collection<DataEntry>> physicalEntitySupplier) {
        this.physicalEntitySupplier = physicalEntitySupplier;
    }

    public void setSessionSupplier(Supplier<Collection<DataEntry>> sessionSupplier) {
        this.sessionSupplier = sessionSupplier;
    }

    @Override
    public void init(ComponentManager cm) {
        this.cm = cm;
    }

    @Override
    public void deinit() {

    }

    public PSequence<ReferenceDistribution> getReferenceDistribution() {
        return distributions;
    }

    @Override
    public void update() {
        PSet<Token> tokens = cm.retrieveComponent(InputComponent.class).getTokens();
        for(Token token: tokens) {
            doResolution(token);
        }
    }


    public void doResolution(Token token) {
        System.out.println("in doResolution");
        ReferenceResolver rr = null;

        if(token.has("slots")) {
            List<Map<String,Object>> slots = (List<Map<String, Object>>) token.get("slots").get();
            for(Map<String, Object> slotinfo: slots) {
                //check if slot has already been resolved in previous iteration
                if(slotinfo.containsKey("resolved")) {
                    if((Boolean) slotinfo.get("resolved")) {
                        continue;
                    }
                }
                ReferenceDistribution distribution;
                if(slotinfo.containsKey("slot_type")) {
                    if(slotinfo.get("slot_type").equals("entity")) {
                        if(slotinfo.get("entity_type").equals("person")) {
                         //   rr = new PersonRR(personSupplier);
                        }else if(slotinfo.get("entity_type").equals("personal_pronoun")) {
                          //  rr = new PersonPronounRR(personSupplier, sessionSupplier);
                        }else {
                            rr = new ObjectRR(() -> physicalEntitySupplier.get().stream().filter(o -> o.get("entity_type").get().equals(slotinfo.get("entity_type"))).collect(Collectors.toList()));
                            if(slotinfo.containsKey("personal_pronoun")) {
                                ((ObjectRR) rr).setPronoun((String)slotinfo.get("personal_pronoun"));
                            }
                            if(slotinfo.containsKey("attributes")) {
                                Map<String, Object> attrMap = new HashMap<>();
                                List<Map<String, Object>> attributes = (List) slotinfo.get("attributes");
                                for(Map<String, Object> attr: attributes) {
                                    String attrKey = (String) attr.get("attribute_type");
                                    Object attrValue =  attr.get("attribute_value");
                                    attrMap.put(attrKey, attrValue);
                                }
                                ((ObjectRR) rr).setAttrMap(attrMap);

                            }
                        }
                    }else if(slotinfo.get("slot_type").equals("location")) {
                        //TODO
                    }
                }
                if(rr != null) {
                    distribution = rr.getReferences();
                    if(distribution != null) {
                        Map<String, Double> dCandidates = distribution.getConfidences();
                        slotinfo.put("resolved", true);
                        List<Map<String, Object>> candidates = new ArrayList<>();
                        for(Map.Entry entry: dCandidates.entrySet()) {
                            //only add candidated if probability is above threshold (e.g. 0.1)
                            if((Double) entry.getValue() > RESOLUTION_CONFIDENCE) {
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("res_id", entry.getKey());
                                map.put("res_conf", entry.getValue());
                                candidates.add(map);
                            }
                        }
                        slotinfo.put("resolved_candidates", candidates);
                    }else {
                        slotinfo.put("resolved", false);
                    }
                }else {
                    slotinfo.put("resolved", false);
                }
            }

            System.out.println("Token in Resolution: " + token.toString());
        }

    }

    @Override
    public Object createSnapshot() {
        return null;
    }

    @Override
    public void loadSnapshot(Object snapshot) {

    }

}
