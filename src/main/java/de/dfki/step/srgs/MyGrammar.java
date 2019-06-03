package de.dfki.step.srgs;

public class MyGrammar {

    public static GrammarManager create() {
        GrammarManager grammarManager = new GrammarManager();


        Rule greetingRule = new Rule("greet")
                .add(new OneOf()
                        .add(new Item("Hi Max")
                                .add(Tag.intent("greetings")))
                        .add(new Item("Hello Max")
                                .add(Tag.intent("greetings")))
                );
        grammarManager.addRule(greetingRule);

        //added because otherwise "hello" would be understood all the time and would be mapped to the greeting rule
        Rule garbageRule = new Rule("greet_other")
                .add(new OneOf()
                        .add(new Item("Hi"))
                        .add(new Item("Hello"))
                );
        grammarManager.addRule(greetingRule);
        grammarManager.addRule(garbageRule);

        return grammarManager;
    }
}

