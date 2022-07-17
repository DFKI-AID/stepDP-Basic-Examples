package basic_examples;

import de.dfki.step.blackboard.IToken;
import de.dfki.step.blackboard.Rule;
import de.dfki.step.blackboard.conditions.PatternCondition;
import de.dfki.step.blackboard.patterns.Pattern;
import de.dfki.step.blackboard.patterns.PatternBuilder;
import de.dfki.step.blackboard.rules.SimpleRule;
import de.dfki.step.dialog.Dialog;
import de.dfki.step.kb.IKBObject;
import de.dfki.step.kb.graph.Graph;
import de.dfki.step.kb.semantic.PropString;
import de.dfki.step.kb.semantic.Type;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

public class GraphExampleType2 extends Dialog {

    public GraphExampleType2() throws Exception {

        // define the relevant types for the example

        Type addGraph = new Type("AddGraph", this.getKB());
        addGraph.addProperty(new PropString("graphName", this.getKB()));
        this.getKB().addType(addGraph);


        Type addEdge = new Type("AddEdge", this.getKB());
        addEdge.addProperty(new PropString("graphName", this.getKB()));
        addEdge.addProperty(new PropString("childNode", this.getKB()));
        addEdge.addProperty(new PropString("parentNode", this.getKB()));
        addEdge.addProperty(new PropString("edgeLabel", this.getKB()));
        this.getKB().addType(addEdge);

        // DeleteEdge takes Edge UUID and deletes it from the graph
        Type deleteEdge = new Type("DeleteEdge", this.getKB());
        deleteEdge.addProperty(new PropString("graphName", this.getKB()));
        deleteEdge.addProperty(new PropString("edgeUUID", this.getKB()));
        this.getKB().addType(deleteEdge);

        // Saves all the edges in the graph in a json file
        Type saveEdges = new Type("SaveEdges", this.getKB());
        saveEdges.addProperty(new PropString("graphName", this.getKB()));
        this.getKB().addType(saveEdges);

        //loads all the edges from a json file to the graph
        Type loadEdges = new Type("LoadEdges", this.getKB());
        loadEdges.addProperty(new PropString("graphName", this.getKB()));
        this.getKB().addType(loadEdges);

        Type getAllEdges = new Type("getAllEdges", this.getKB());
        getAllEdges.addProperty(new PropString("graphName", this.getKB()));
        this.getKB().addType(getAllEdges);

        //gives all the unique nod
        Type getNodesBelow = new Type("GetNodesBelow", this.getKB());
        getNodesBelow.addProperty(new PropString("graphName", this.getKB()));
        getNodesBelow.addProperty(new PropString("nodeName", this.getKB()));
        this.getKB().addType(getNodesBelow);

        Rule newGraphRule = new SimpleRule(tokens -> {
            IToken t = tokens[0];
            String graphName = t.getString("graphName");
            Graph graph = new Graph();
            this.getBlackboard().addGraph(graphName, graph);
            System.out.println("Graph Added Successfully");
        }, "NewGraphRule");


        // takes child & parent node and  label and adds edge in graph
        Rule addRule = new SimpleRule(tokens -> {
            IToken token = tokens[0];
            Graph graph = this.getBlackboard().getGraph(token.getString("graphName"));
            try {
                Type T = new Type("default", this.getKB());
                IKBObject child = null;
                IKBObject parent = null;

                if (this.getKB().getInstance(token.getString("childNode")) != null)
                {
                    child = this.getKB().getInstance(token.getString("childNode"));
                }
                else
                {
                    child = this.getKB().createInstance(token.getString("childNode"), T);
                }
                if (this.getKB().getInstance(token.getString("parentNode")) != null)
                {
                    parent = this.getKB().getInstance(token.getString("parentNode"));
                }
                else
                {
                    parent = this.getKB().createInstance(token.getString("parentNode"), T);
                }

                UUID u1 = graph.createEdge(child, parent, token.getString("edgeLabel"));
                System.out.println("Edge Added Successfully");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }, "AddRule");

        // takes edge UUID which can be found here or in edges.json (after saving them) and deletes the edge
        Rule deleteRule = new SimpleRule(tokens -> {
            IToken token = tokens[0];
            Graph graph = this.getBlackboard().getGraph(token.getString("graphName"));
            graph.deleteEdge(UUID.fromString(token.getString("edgeUUID")));
            System.out.println("Edge deleted Successfully");
        }, "DeleteRule");

        //saves all edges of graph in a json file
        Rule saveRule = new SimpleRule(tokens -> {
            IToken token = tokens[0];
            Graph graph = this.getBlackboard().getGraph(token.getString("graphName"));
            try {
                graph.saveEdges("edges.json");
                System.out.println("Edges saved Successfully");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }, "SaveRule");

        //loads all edges from json file
        Rule loadRule = new SimpleRule(tokens -> {
            IToken token = tokens[0];
            Graph graph = this.getBlackboard().getGraph(token.getString("graphName"));
            try {
                graph.loadEdges("edges.json");
                System.out.println("Edges loaded successfully");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }, "LoadRule");

        // gives us all the edges in the graph
        Rule getRule = new SimpleRule(tokens -> {
            IToken token = tokens[0];
            Graph graph = this.getBlackboard().getGraph(token.getString("graphName"));
            System.out.println(graph.getAllEdges());
        }, "GetRule");

        //takes a node and gives us all the nodes below that node
        Rule nodesBelowRule = new SimpleRule(tokens -> {
            IToken token = tokens[0];
            Graph graph = this.getBlackboard().getGraph(token.getString("graphName"));
            IKBObject node = this.getKB().getInstance(token.getString("nodeName"));
            System.out.println (node.getUUID());

            System.out.println(graph.getNodesBelow(node));
        }, "NodesBelowRule");

        Pattern p1 = new PatternBuilder("AddGraph", this.getKB()).build();
        newGraphRule.setCondition(new PatternCondition(p1));
        this.getBlackboard().addRule(newGraphRule);

        Pattern p2 = new PatternBuilder("AddEdge", this.getKB()).build();
        addRule.setCondition(new PatternCondition(p2));
        this.getBlackboard().addRule(addRule);

        Pattern p3 = new PatternBuilder("DeleteEdge", this.getKB()).build();
        deleteRule.setCondition(new PatternCondition(p3));
        this.getBlackboard().addRule(deleteRule);

        Pattern p4 = new PatternBuilder("SaveEdges", this.getKB()).build();
        saveRule.setCondition(new PatternCondition(p4));
        this.getBlackboard().addRule(saveRule);

        Pattern p5 = new PatternBuilder("LoadEdges", this.getKB()).build();
        loadRule.setCondition(new PatternCondition(p5));
        this.getBlackboard().addRule(loadRule);

        Pattern p6 = new PatternBuilder("getAllEdges", this.getKB()).build();
        getRule.setCondition(new PatternCondition(p6));
        this.getBlackboard().addRule(getRule);

        Pattern p7 = new PatternBuilder("GetNodesBelow", this.getKB()).build();
        nodesBelowRule.setCondition(new PatternCondition(p7));
        this.getBlackboard().addRule(nodesBelowRule);
    }
}
