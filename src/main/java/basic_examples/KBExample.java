package basic_examples;

import de.dfki.step.blackboard.Rule;
import de.dfki.step.blackboard.IToken;
import de.dfki.step.blackboard.conditions.PatternCondition;
import de.dfki.step.blackboard.patterns.Pattern;
import de.dfki.step.blackboard.patterns.PatternBuilder;
import de.dfki.step.blackboard.rules.SimpleRule;
import de.dfki.step.dialog.Dialog;
import de.dfki.step.kb.IKBObject;
import de.dfki.step.kb.IKBObjectWriteable;
import de.dfki.step.kb.semantic.PropBool;
import de.dfki.step.kb.semantic.PropInt;
import de.dfki.step.kb.semantic.PropReference;
import de.dfki.step.kb.semantic.PropString;
import de.dfki.step.kb.semantic.Type;

public class KBExample extends Dialog {

    public KBExample() throws Exception {
        extendSemanticTree();
        createKBObjects();

        // give information about the tv models
        Rule infoRule = new SimpleRule(tokens -> {
            IToken tv = tokens[0];
            System.out.print("This is " + tv.getString("modelName") + ". ");
            System.out.print("Its size is " + tv.getInteger("size") + " inch. ");
            System.out.println("It costs " + tv.getInteger("price") + " euros.");
        }, "InfoRule");
        Pattern p1 = new PatternBuilder("TV", this.getKB()).build();
        infoRule.setCondition(new PatternCondition(p1));
        this.getBlackboard().addRule(infoRule);

		// take an order from the user
        Rule orderRule = new SimpleRule(tokens -> {
        	IToken t = tokens[0];
        	IKBObject tv = t.getResolvedReference("tv");
        	if (tv == null) {
        		System.out.println("Which tv do you want to order?");
        	} else {
                System.out.println("Okay, we'll send you " + tv.getString("modelName") + "!");
        	}
        }, "OrderRule");
        Pattern p2 = new PatternBuilder("OrderIntent", this.getKB()).build();
        orderRule.setCondition(new PatternCondition(p2));
        this.getBlackboard().addRule(orderRule);
    }

    private void extendSemanticTree() throws Exception {
        Type tv = new Type("TV", this.getKB());
        tv.addProperty(new PropString("modelName", this.getKB()));
        tv.addProperty(new PropInt("price", this.getKB()));
        tv.addProperty(new PropInt("size", this.getKB()));
        this.getKB().addType(tv);

        // user intents
        Type orderIntent = new Type("OrderIntent", this.getKB());
        orderIntent.addProperty(new PropReference("tv", this.getKB(), this.getKB().getType("TV")));
        this.getKB().addType(orderIntent);
    }

    private void createKBObjects() throws Exception {
        IKBObjectWriteable tv1 = this.getKB().createInstance("tv1", this.getKB().getType("TV"));
        tv1.setString("modelName", "Model A");
        tv1.setInteger("price", 1500);
        tv1.setInteger("size", 65);

        IKBObjectWriteable tv2 = this.getKB().createInstance("tv2", this.getKB().getType("TV"));
        tv2.setString("modelName", "Model B");
        tv2.setInteger("price", 1000);
        tv2.setInteger("size", 55);
    }

}