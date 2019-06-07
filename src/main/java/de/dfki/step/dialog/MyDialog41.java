package de.dfki.step.dialog;

import de.dfki.step.core.ComponentManager;
import de.dfki.step.core.CoordinationComponent;
import de.dfki.step.core.Token;
import de.dfki.step.core.TokenComponent;
import de.dfki.step.rengine.RuleComponent;
import de.dfki.step.sc.SimpleStateBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.*;


/**
 */
public class MyDialog41 extends Dialog {
    private static final Logger log = LoggerFactory.getLogger(MyDialog41.class);
    private PizzaBehavior pizzaBehavior;

    public MyDialog41() {
        RuleComponent rsc = retrieveComponent(RuleComponent.class);
        TokenComponent tc = retrieveComponent(TokenComponent.class);
        try {
            pizzaBehavior = new PizzaBehavior();
            addComponent(pizzaBehavior);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }






    }

    public static class PizzaBehavior extends SimpleStateBehavior {
        public PizzaBehavior() throws URISyntaxException {
            // path inside the resources folder
            super("/sc/MyDialog40");
            orders = new ArrayList<>();

        }

        List<String> orders ;


        @Override
        public void init(ComponentManager cm) {
            super.init(cm);
            RuleComponent rsc = cm.retrieveComponent(RuleComponent.class);
            TokenComponent tc = cm.retrieveComponent(TokenComponent.class);
            CoordinationComponent cc = cm.retrieveComponent(CoordinationComponent.class);



            rsc.addRule("incomingCall", () -> {
                tc.getTokens().stream()
                        .forEach(t-> System.out.println(t.toString()));
                Optional<Token> token = tc.getTokens().stream()
                        .filter(t ->
                                t.payloadEquals("intent", "hungry")
                                        || t.payloadEquals("intent", "wants_pizza")
                        )
                        .findAny();

                if(!token.isPresent())
                {
                    return;
                }

                getStateHandler().fire("incoming_call");
            });



            rsc.addRule("placeOrder1",() -> {
                Optional<Token> token = tc.getTokens().stream()
                        .filter(t ->
                                t.payloadEquals("intent", "place_order")
                                        || t.payloadEquals("intent", "order"))
                        .findAny();

                if(!token.isPresent()){
                    return;
                }
                cc.add(()-> {
                    System.out.println("place_order1");
                    token.get().get("orders", List.class).ifPresent(o -> orders.addAll(o));
                    getStateHandler().fire("place_order");
                }).attachOrigin(token.get());
            } );

            rsc.addRule("placeOrder2",() -> {
                Optional<Token> token = tc.getTokens().stream()
                        .filter(t ->
                                t.payloadEquals("intent", "place_order")
                                        || t.payloadEquals("intent", "order"))
                        .findAny();

                if(!token.isPresent()){
                    return;
                }

                cc.add(()->{
                    System.out.println("place_order2");
                    token.get().get("orders",List.class).ifPresent(o->orders.addAll(o));
                    getStateHandler().fire("place_order");
                }).attachOrigin(token.get());


            } );

            rsc.addRule("finishOrder",()->{
                Optional<Token> token = tc.getTokens().stream()
                        .filter(t ->
                                t.payloadEqualsOneOf("intent","done_ordering",
                                        "finish_order", "done"))
                        .findAny();
                if(!token.isPresent()){
                    return;
                }

                getStateHandler().fire("finish_order");
            });


            rsc.addRule("modifyOrder",()->{
                Optional<Token> token = tc.getTokens().stream()
                        .filter(t ->
                                t.payloadEqualsOneOf("intent","modify_order",
                                        "not_done_ordering", "change"))
                        .findAny();
                if(!token.isPresent()){
                    return;
                }

                getStateHandler().fire("modify_order");
            });

            rsc.addRule("confirmOrder",()->{
                Optional<Token> token = tc.getTokens().stream()
                        .filter(t ->
                                t.payloadEqualsOneOf("intent","confirm_order",
                                        "happy_with_order", "agree"))
                        .findAny();
                if(!token.isPresent()){
                    return;
                }

                getStateHandler().fire("confirm_order");
            });
        }

        public void greetCustomer() {
            System.out.println("WELCOME TO OUR AWESOME PIZZA PLACE!!!");
        }

        public void takeOrder() {System.out.println("WHAT WILL YOU BE HAVING TODAY?"); }

        public void takeNextOrder(){
            System.out.println("What would you like to order");
        }

        public void ackOrder() {System.out.println("Copy that. Anything else?"); }

        public void repeatOrder() {
            System.out.println("IS YOUR ORDER CORRECT? " + orders.stream()
            .reduce((x,y)->x + " " + y)
            );

        }

        public void sayGoodBye() {System.out.println("THANK YOU FOR ORDERING. YOUR PIZZA WILL ARRIVE " +
                "IN 30 MINUTES"); }



        @Override
        public Set<String> getActiveRules(String state) {
            if(Objects.equals(state, "Waiting")) {
                return Set.of("incomingCall");
            }
            if(Objects.equals(state, "Confirm")) {
                return Set.of("confirmOrder", "modifyOrder");
            }
            if(Objects.equals(state, "OrderPlacement")) {
                return Set.of("placeOrder", "finishOrder");
            }


            //log.warn("no rules for state {}", state);
            return Collections.EMPTY_SET;
        }
    }
}

