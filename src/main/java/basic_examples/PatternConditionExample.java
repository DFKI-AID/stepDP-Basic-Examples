package basic_examples;

import de.dfki.step.blackboard.Rule;
import de.dfki.step.blackboard.Token;
import de.dfki.step.blackboard.conditions.PatternCondition;
import de.dfki.step.blackboard.patterns.Pattern;
import de.dfki.step.blackboard.patterns.PatternBuilder;
import de.dfki.step.blackboard.rules.SimpleRule;
import de.dfki.step.dialog.Dialog;
import de.dfki.step.kb.IKBObject;
import de.dfki.step.kb.KnowledgeBase;
import de.dfki.step.kb.semantic.PropBool;
import de.dfki.step.kb.semantic.PropReference;
import de.dfki.step.kb.semantic.PropString;
import de.dfki.step.kb.semantic.Type;

public class PatternConditionExample extends Dialog {

    public PatternConditionExample() throws Exception {

    	// define the relevant types for the example
    	extendSemanticTree();
		
		// this tag helps us to group the rules that we will define
 		String tag = "BringRule";
 		
		// rule 1: match BringIntent(object:Food, recipientName:String)
		PatternBuilder builder = new PatternBuilder("BringIntent", this.getKB());
		builder.hasNonNullProperties("recipientName")
		   	   .addPatternForProperty("object")
					.hasType("Food")
			   .endPropertyPattern();
		Pattern p = builder.build();
		
		Rule rule = new SimpleRule(tokens -> {
			Token intent = tokens[0];
			// a token should only trigger one of the rules with the tag
			// "BringRule", so we add the tag to the token's ignoreRuleTags
			intent.getIgnoreRuleTags().add(tag);
			String name = intent.getString("recipientName");
			System.out.println("Here you go! Enjoy your meal, " + name + ".");
		});
		rule.setCondition(new PatternCondition(p));
		rule.setName("BringFoodRule");
		rule.getTags().add(tag);
 		this.getBlackboard().addRule(rule);
 		
		// rule 2: match BringIntent(object:Drink, recipientName:String)
	    builder = new PatternBuilder("BringIntent", this.getKB());
		builder.hasNonNullProperties("recipientName")
			   .addPatternForProperty("object")
					.hasType("Drink")
			   .endPropertyPattern();
		p = builder.build();
		
		rule = new SimpleRule(tokens -> {
			Token intent = tokens[0];
			String name = intent.getString("recipientName");
			System.out.println("Here you go! Enjoy your drink, " + name + ".");
			intent.getIgnoreRuleTags().add(tag);
		});
		rule.setCondition(new PatternCondition(p));
		rule.setName("BringDrinkRule");
		rule.getTags().add(tag);
 		this.getBlackboard().addRule(rule);

		// rule 3: match any BringIntent that does not match the other more specific rules
 		// the order in which the rules are added should be from most specific to least specific
 		// to ensure that a token triggers the most specific matching rule
		p = new PatternBuilder("BringIntent", this.getKB()).build();
	
		rule = new SimpleRule(tokens -> {
			Token intent = tokens[0];
			intent.getIgnoreRuleTags().add(tag);
			IKBObject object = intent.getResolvedReference("object");
			if (object == null) {
				System.out.println("Which object should I bring to you?");
			    return;
			}
			if (!intent.isSet("recipientName")) {
				System.out.println("Please tell me your name.");
			    return;
			}
			String name = intent.getString("recipientName");
			System.out.println("Here you go, " + name + ".");
		});
		rule.setCondition(new PatternCondition(p));
		rule.setName("GeneralBringRule");
		rule.getTags().add(tag);
 		this.getBlackboard().addRule(rule);
	}

    private void extendSemanticTree() throws Exception {
		KnowledgeBase kb = this.getKB();
		
		// physical object type
		kb.addType(new Type("PhysicalObject", kb));

		// food types
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
		
		// drink types
		Type drink = new Type("Drink", kb);
		drink.addProperty(new PropBool("withIce", kb));
		drink.addInheritance(kb.getType("PhysicalObject"));
		kb.addType(drink);
		Type water = new Type("Water", kb);
		water.addProperty(new PropBool("carbonated", kb));
		water.addInheritance(kb.getType("Drink"));
		kb.addType(water);
		Type beer = new Type("Beer", kb);
		beer.addInheritance(kb.getType("Drink"));
		kb.addType(beer);

		// other physical objects
		Type knife = new Type("Knife", kb);
		knife.addInheritance(kb.getType("PhysicalObject"));
		kb.addType(knife);

		// intent types
		Type bringIntent = new Type("BringIntent", kb);
		bringIntent.addProperty(new PropReference("object", kb, kb.getType("PhysicalObject")));
		bringIntent.addProperty(new PropString("recipientName", kb));
		kb.addType(bringIntent);
    }

}