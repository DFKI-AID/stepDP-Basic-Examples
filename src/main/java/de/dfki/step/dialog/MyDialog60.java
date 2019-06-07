package de.dfki.step.dialog;

import de.dfki.step.core.InputComponent;
import de.dfki.step.core.Token;
import de.dfki.step.kb.DataEntry;
import de.dfki.step.kb.DataStore;
import de.dfki.step.resolution.ResolutionComponent;
import de.dfki.step.tm.MyDataEntry;
import de.dfki.step.util.Vector3;
import de.dfki.step.web.SpeechRecognitionClient;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/* This example show how to use the Datastore and the Resolution Component is used
 * also see MyDialog60Component */
public class MyDialog60 extends Dialog {

    private final String app = "resolution_test";
    private final String host = "172.16.68.129";


    public MyDialog60() {

        DataStore<Object> ds = createDataStore();
        createGrammarComponent();

        //get all objects in the environment from datastore
        List<DataEntry> physicalObjects = ds.primaryIds()
                .distinct()
                .map(id -> new MyDataEntry(ds, id))
                .filter(d -> d.isPhysicalEntity())
                .collect(Collectors.toList());


        //use ResolutionComponent
        var resc = new ResolutionComponent();
        resc.setPhysicalEntitySupplier(() -> physicalObjects);
        this.addComponent(resc);
        setPriority(resc.getId(), 250);


        MyDialog60Component dcomponent = new MyDialog60Component();
        this.addComponent(dcomponent);


    }



    protected DataStore createDataStore() {
        DataStore<Object> ds = new DataStore();
        ds.checkMutability(true);


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
        box3.setColors(List.of("green"));
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
        SpeechRecognitionClient src = new SpeechRecognitionClient(app, host, 9696, (token) -> {
            Optional<Map> semantic = token.get(Map.class, "semantic");
            if (!semantic.isPresent()) {
                return;
            }
            Token processedToken = new Token((Map<String, Object>) semantic.get());

            // add token to ic
            InputComponent ic = retrieveComponent(InputComponent.class);
            ic.addToken(processedToken);
        });


        File file = new File(getClass().getClassLoader().getResource("dialog60.xml").getFile());
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
