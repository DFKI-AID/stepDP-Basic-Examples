package basic_examples;

import de.dfki.step.blackboard.Rule;

import de.dfki.step.blackboard.conditions.PatternCondition;
import de.dfki.step.blackboard.patterns.Pattern;
import de.dfki.step.blackboard.patterns.PatternBuilder;
import de.dfki.step.blackboard.rules.SimpleRule;
import de.dfki.step.dialog.Dialog;
import de.dfki.step.kb.semantic.Type;
import de.dfki.step.rm.sc.SCRuleManager;

public class StateChartExample extends Dialog {

    public StateChartExample() throws Exception {
        this.getKB().addType(new Type("GreetingIntent", this.getKB()));
        Type goodbyeIntent = new Type("GoodbyeIntent", this.getKB());
        this.getKB().addType(goodbyeIntent);

        // add state chart manager to board
        this.getBlackboard().addStateChartManager("Greetings", StateChartExample.class.getResource("/greetings.scxml"));

        Rule greetingRule = new SimpleRule(tokens -> {
            System.out.println("Hello!");
            // this rule triggers the transition "hello" which changes the state to the "End" state
            this.getBlackboard().getStateChartManager("Greetings").fireTransition("hello");
        }, "GreetingRule");
        Pattern p = new PatternBuilder("GreetingIntent", this.getKB()).build();
        greetingRule.setCondition(new PatternCondition(p));
        // add a rule manager that automatically (de-)activates the rule based on the statechart
        // the rule is generally not active except for the "Start" state
        SCRuleManager man = this.getBlackboard().getStateChartManager("Greetings").getRuleAssignment(false, new String[] {"Start"});
        greetingRule.addRuleManager(man);
        this.getBlackboard().addRule(greetingRule);

        Rule goodbyeRule = new SimpleRule(tokens -> {
            System.out.println("Goodbye!");
            // this rule triggers the transition "goodbye" which changes the state back to
            // the "Start" state
            this.getBlackboard().getStateChartManager("Greetings").fireTransition("goodbye");
        }, "GoodbyeRule");
        p = new PatternBuilder("GoodbyeIntent", this.getKB()).build();
        goodbyeRule.setCondition(new PatternCondition(p));
        // add a rule manager that automatically (de-)activates the based on the statechart
        // the rule is generally not active except for the "End" state
        man = this.getBlackboard().getStateChartManager("Greetings").getRuleAssignment(false, new String[]{"End"});
        goodbyeRule.addRuleManager(man);
        this.getBlackboard().addRule(goodbyeRule);
    }

}