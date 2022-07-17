package basic_examples;

import de.dfki.step.blackboard.IToken;
import de.dfki.step.blackboard.Rule;
import de.dfki.step.blackboard.conditions.PatternCondition;
import de.dfki.step.blackboard.patterns.Pattern;
import de.dfki.step.blackboard.patterns.PatternBuilder;
import de.dfki.step.blackboard.rules.SimpleRule;
import de.dfki.step.dialog.Dialog;
import de.dfki.step.kb.IKBObject;
import de.dfki.step.kb.IKBObjectWriteable;
import de.dfki.step.kb.graph.Edge;
import de.dfki.step.kb.graph.Graph;
import de.dfki.step.kb.semantic.PropString;
import de.dfki.step.kb.semantic.Type;

public class GraphExample extends Dialog {

    public GraphExample() throws Exception {
        //create new Type
        Type concept = new Type("concept", this.getKB());
        concept.addProperty(new PropString("conceptName", this.getKB()));
        this.getKB().addType(concept);


        //create KB Objects (as Nodes)
        IKBObjectWriteable soundbar = this.getKB().createInstance("Soundbar", concept);
        soundbar.setString("conceptName", "Soundbar");

        IKBObjectWriteable tv = this.getKB().createInstance("TV", concept);
        tv.setString("conceptName", "TV");

        IKBObjectWriteable hdmi = this.getKB().createInstance("HDMI", concept);
        hdmi.setString("conceptName", "HDMI");

        IKBObjectWriteable signal = this.getKB().createInstance("Signal", concept);
        signal.setString("conceptName", "Signal");

        IKBObjectWriteable arcPort = this.getKB().createInstance("ARC-Port", concept);
        arcPort.setString("conceptName", "ARC-Port");

        IKBObjectWriteable dvbS = this.getKB().createInstance("DVB-S", concept);
        dvbS.setString("conceptName", "DVB-S");


        //create Graph Structure
        Graph knowledgeGraph = new Graph();
        knowledgeGraph.createEdge(soundbar, hdmi, "more complex");
        knowledgeGraph.createEdge(tv, hdmi, "more complex");
        knowledgeGraph.createEdge(tv, signal, "more complex");
        knowledgeGraph.createEdge(hdmi, arcPort, "more complex");
        knowledgeGraph.createEdge(signal, dvbS, "more complex");

        this.getBlackboard().addGraph("Knowledge Graph", knowledgeGraph);

        //rule: when one of the words is used add all the ones below to kb
        Rule addKeywordsRule = new SimpleRule(tokens -> {
            IToken t = tokens[0];
            IKBObject usedConcept = t.getResolvedReference("concept");
            if (usedConcept != null) {
                String conceptString = usedConcept.getName();

                //search for the used concept in all nodes in the graph
                for (Edge edge : knowledgeGraph.getAllEdges()) {
                    IKBObject parent = this.getKB().getInstanceWriteable(edge._parentUUID);
                    if (parent.getName().equals(conceptString)) {

                        //when it is found the customer knows all nodes below
                        for (IKBObject node : knowledgeGraph.getNodesBelow(parent)) {
                            System.out.println("It seems like you know " + node.getName() + ".");
                        }
                    }

                    IKBObject child = this.getKB().getInstanceWriteable(edge._childUUID);
                    if (child.getName().equals(conceptString)) {

                        //when it is found the customer knows all nodes below
                        for (IKBObject node : knowledgeGraph.getNodesBelow(child)) {
                            System.out.println("It seems like you know " + node.getName() + ".");
                        }
                    }
                }
            }
        }, "AddKeywordsRule");

        Pattern p1 = new PatternBuilder("concept", this.getKB()).build();
        addKeywordsRule.setCondition(new PatternCondition(p1));
        this.getBlackboard().addRule(addKeywordsRule);
    }
}
