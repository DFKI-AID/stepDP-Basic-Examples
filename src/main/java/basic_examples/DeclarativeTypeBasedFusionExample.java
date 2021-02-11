package basic_examples;

import java.time.Duration;

import de.dfki.step.blackboard.Rule;
import de.dfki.step.blackboard.Token;
import de.dfki.step.blackboard.conditions.PatternCondition;
import de.dfki.step.blackboard.patterns.Pattern;
import de.dfki.step.blackboard.patterns.PatternBuilder;
import de.dfki.step.blackboard.rules.DeclarativeTypeBasedFusionRule;
import de.dfki.step.blackboard.rules.SimpleRule;
import de.dfki.step.dialog.Dialog;
import de.dfki.step.kb.IKBObject;
import de.dfki.step.kb.KnowledgeBase;
import de.dfki.step.kb.semantic.PropReference;
import de.dfki.step.kb.semantic.PropString;
import de.dfki.step.kb.semantic.Type;

public class DeclarativeTypeBasedFusionExample extends Dialog {

    public DeclarativeTypeBasedFusionExample() throws Exception {

    	// define the relevant types for the example
    	extendSemanticTree();

 		// rule 0: fuse BringIntent with PhysicalObject
 		Pattern p1 = new PatternBuilder("BringIntent", this.getKB()).build();
 		Pattern p2 = new PatternBuilder("PhysicalObject", this.getKB()).build();
 		Type resultType = this.getKB().getType("BringObject");
 		long fusionInterval = Duration.ofMinutes(10).toMillis();
		
		Rule rule = new DeclarativeTypeBasedFusionRule(p1, p2, resultType, fusionInterval);
		rule.setName("BringObjectFusionRule");
 		this.getBlackboard().addRule(rule);

		// rule 1: match BringObject (fusion result) and do sth with it
		Pattern p = new PatternBuilder("BringObject", this.getKB()).build();
	
		rule = new SimpleRule(tokens -> {
			Token t = tokens[0];
			IKBObject object = t.getResolvedReference("object");
			if (object == null) {
				System.out.println("Which object should I bring to you?");
			    return;
			}
			IKBObject intent = t.getResolvedReference("intent");
			if (!intent.isSet("recipientName")) {
				System.out.println("Please tell me your name.");
			    return;
			}
			String name = intent.getString("recipientName");
			System.out.println("Here is your " + object.getType().getName() + ", " + name + ".");
		});
		rule.setCondition(new PatternCondition(p));
		rule.setName("BringRule");
 		this.getBlackboard().addRule(rule);
	}

    private void extendSemanticTree() throws Exception {
		KnowledgeBase kb = this.getKB();
		
		// Physical Object Type
		Type physObj = new Type("PhysicalObject", kb);
		kb.addType(physObj);

		// Food Types
		Type food = new Type("Food", kb);
		food.addInheritance(kb.getType("PhysicalObject"));
		kb.addType(food);
		Type pizza = new Type("Pizza", kb);
		pizza.addProperty(new PropString("sort", kb));
		pizza.addInheritance(kb.getType("Food"));
		kb.addType(pizza);
		Type spaghetti = new Type("Spaghetti", kb);
		spaghetti.addInheritance(kb.getType("Food"));
		kb.addType(spaghetti);

		// Intent Types
		kb.addType(new Type("Intent", kb));
		Type bringIntent = new Type("BringIntent", kb);
		bringIntent.addInheritance(kb.getType("Intent"));
		bringIntent.addProperty(new PropString("recipientName", kb));
		kb.addType(bringIntent);

		// Fusion Result Types
		Type bringObject = new Type("BringObject", kb);
		bringObject.addProperty(new PropReference("object", kb, kb.getType("PhysicalObject")));
		bringObject.addProperty(new PropReference("intent", kb, kb.getType("BringIntent")));
		kb.addType(bringObject);
    }

}