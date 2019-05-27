package de.dfki.step.dialog;

import de.dfki.step.core.InputComponent;
import de.dfki.step.core.Token;
import de.dfki.step.core.TokenComponent;
import de.dfki.step.fusion.FusionComponent;
import de.dfki.step.core.CoordinationComponent;
import de.dfki.step.fusion.InputNode;
import de.dfki.step.kb.DataEntry;
import de.dfki.step.kb.DataStore;
import de.dfki.step.output.PresentationComponent;
import de.dfki.step.rengine.RuleComponent;
import de.dfki.step.resolution.ResolutionComponent;


import de.dfki.step.taskmodel.RootTask;
import de.dfki.step.taskmodel.Task;
import de.dfki.step.tm.MyDataEntry;
import de.dfki.step.tm.TaskLearningComponent;
import de.dfki.step.util.Vector3;
import de.dfki.step.web.SpeechRecognitionClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MyTaskDialog extends Dialog {
    private static final Logger log = LoggerFactory.getLogger(MyTaskDialog.class);
    private final String app = "learning";

    public MyTaskDialog() {

        DataStore<Object> ds = createDataStore();
        createGrammarComponent();

       //get all objects in the environment from datastore
       List<DataEntry> physicalObjects = ds.primaryIds()
                .distinct()
                .map(id -> new MyDataEntry(ds, id))
                .filter(d -> d.isPhysicalEntity())
                .collect(Collectors.toList());
        //get all humans from datastore
        Collection<DataEntry> humans = ds.primaryIds()
                .distinct()
                .map(id -> new MyDataEntry(ds, id))
                .filter(d -> d.isHuman())
                .collect(Collectors.toList());

        //use ResolutionComponent
        var resc = new ResolutionComponent();
        resc.setPhysicalEntitySupplier(() -> physicalObjects);
        this.addComponent(resc);
        setPriority(resc.getId(), 250);

        System.out.println(resc.getTokens().toString());

    /*    InputComponent ic = retrieveComponent(InputComponent.class);
        ic.setTimeout(Duration.ofSeconds(4));
*/
        //custom component where rules for basic task learning are defined
        TaskLearningComponent trc = new TaskLearningComponent(ds);
        this.addComponent(trc);

        FusionComponent fc = retrieveComponent(FusionComponent.class);
        //forward all input tokens that already have an intent
        InputNode intentNode = new InputNode(t -> t.has("intent"));
        fc.addFusionNode("intent_forward", intentNode, match -> {
            return match.getTokens().iterator().next();
        });
    }

    protected DataStore createDataStore() {
        DataStore<Object> ds = new DataStore();
        ds.checkMutability(true);

        MyDataEntry human1 = new MyDataEntry(ds, "w1");
        human1.setVisualFocus("box3");
        human1.setPosition(new Vector3(0, 0, 0));
        human1.setHuman(true);
        human1.save();


        MyDataEntry robot1 = new MyDataEntry(ds, "r1");
        robot1.setPosition(new Vector3(2.0, 2.0, 2.0));
        robot1.save();

        MyDataEntry box1 = new MyDataEntry(ds, "box1");
        box1.setColors(List.of("green"));
        box1.setSize("small");
        box1.setPosition(new Vector3(1.0, 1.0, 1.0));
        box1.set("entity_type", "box");
        box1.setPhysicalEntity(true);
        box1.save();

        MyDataEntry box2 = new MyDataEntry(ds, "box2");
        box2.setColors(List.of("blue"));
        box2.setSize("small");
        box2.setPosition(new Vector3(-1.0, -1.0, -1.0));
        box2.set("entity_type", "box");
        box2.setPhysicalEntity(true);
        box2.save();

        MyDataEntry box3 = new MyDataEntry(ds, "box3");
        box3.setColors(List.of("red"));
        box3.setSize("large");
        box3.setPosition(new Vector3(2.0, 3.0, 2.0));
        box3.set("entity_type", "box");
        box3.setPhysicalEntity(true);
        box3.save();

        return ds;
    }

    protected void createGrammarComponent() {
        // speech-recognition-service of the step-dp
       // GrammarManager gm = MyGrammar.create();
     //   Grammar grammar = gm.createGrammar();
        SpeechRecognitionClient src = new SpeechRecognitionClient(app, "172.16.68.129", 9696, (token) -> {
            Optional<Map> semantic = token.get(Map.class, "semantic");
            if (!semantic.isPresent()) {
                return;
            }
            Token processedToken = new Token((Map<String, Object>) semantic.get());
            System.out.println("Grammar-Token: " + processedToken.toString());

            // add token to ic
            InputComponent ic = retrieveComponent(InputComponent.class);
            ic.addToken(processedToken);
        });


        File file = new File(getClass().getClassLoader().getResource("tasklearning.xml").getFile());
        Reader fileReader = null;
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader bufReader = new BufferedReader(fileReader);

        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            line = bufReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while( line != null){
            sb.append(line).append("\n");
            try {
                line = bufReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String xml2String = sb.toString();
        System.out.println("XML to String using BufferedReader : ");
        System.out.println(xml2String);

        try {
            bufReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        String grammarStr = sb.toString();
        src.setGrammar("main", grammarStr);
        src.initGrammar();
        src.init();
    }

}

