package basic_examples;

import de.dfki.step.blackboard.Rule;
import de.dfki.step.blackboard.Token;
import de.dfki.step.blackboard.conditions.PatternCondition;
import de.dfki.step.blackboard.patterns.Pattern;
import de.dfki.step.blackboard.patterns.PatternBuilder;
import de.dfki.step.blackboard.rules.SimpleRule;
import de.dfki.step.dialog.Dialog;
import de.dfki.step.kb.semantic.PropString;
import de.dfki.step.kb.semantic.Type;

public class HelloWorldExample extends Dialog {

    public HelloWorldExample() throws Exception {
		Type greetingIntent = new Type("GreetingIntent", this.getKB());
		greetingIntent.addProperty(new PropString("userName", this.getKB()));
		this.getKB().addType(greetingIntent);
		
        Rule greetingRule = new SimpleRule(tokens -> {
        	Token t = tokens[0];
        	if (!t.isSet("userName")) {
        		System.out.println("Hello! What's your name?");
        	} else {
            	String userName = t.getString("userName");
                System.out.println("Hello, nice to meet you, " + userName + "!");
        	}
        }, "GreetingRule");
        Pattern p = new PatternBuilder("GreetingIntent", this.getKB()).build();
        greetingRule.setCondition(new PatternCondition(p));
        this.getBlackboard().addRule(greetingRule);
    }

}