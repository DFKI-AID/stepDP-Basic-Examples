package de.dfki.step.dialog;

import de.dfki.step.core.*;
import de.dfki.step.kb.Entity;
import de.dfki.step.output.PresentationComponent;
import de.dfki.step.rengine.CoordinationComponent;
import de.dfki.step.rengine.RuleSystemComponent;


import java.util.Optional;
import java.util.Set;

/*public class Taskrules  implements Component {

    private String currentTaskId;
    private RootTask currentRootTask;
    private Task currentTask;
    private RuleSystemComponent rsc;
    private TokenComponent tc;
    private PresentationComponent pc;
    private CoordinationComponent rc;
    private MetaFactory mf;

    public Taskrules() {

    }


    public void createStartTaskRule() {

        rsc.addRule("startTask", () -> {
            // check for tokens with the intent 'startTask'
            Optional<Token> token = tc.getTokens().stream()
                    .filter(t -> t.payloadEquals("intent", "startTask"))
                    .findFirst();
            if (!token.isPresent()) {
                return;
            }

            Token startTaskToken = token.get();
            if (!startTaskToken.get("taskname").isPresent()) {
                rc.add("startTask", () -> {
                    pc.present(PresentationComponent.simpleTTS("What is the name of the new task?"));
                }).attachOrigin(startTaskToken);
               ;
            }else {
                rc.add("startTask", () -> {
                    currentRootTask = new RootTask((String) startTaskToken.get("taskname").get());
                    pc.present(PresentationComponent.simpleTTS("Sure, tell me what I have to do"));
                }).attachOrigin(startTaskToken);

                //todo add to snapshot

            }

            // consume the token (subsequent rules won't see the token)
          //  sys.removeToken(token.get());
          //  Set<String> rules = tagSystem.getTagged("CreateTask");
           // rules.forEach(r -> rsc.enable(r));
        });

        // set the priority of the rule.
       // rsc.setPriority("startTask", 20);
        //tagSystem.addTag("startTask", "TaskLearning");

    }

    public void createFinishTaskRule(Dialog dialog) {*/
  /*      var rs = dialog.getRuleSystem();
        var tagSystem = dialog.getTagSystem();

        rs.addRule("finishTask", (sys) -> {
            // check for tokens with the intent 'finishTask'
            Optional<Token> token = sys.getTokens().stream()
                    .filter(t -> t.payloadEquals("intent", "finishTask"))
                    .findFirst();
            if (!token.isPresent()) {
                return;
            }

            Token intent = token.get();
            if (!intent.get("taskname").isPresent()) { //assume currentTask -> maybe ask question??
                contentProvider.addEntity(currentRootTask);
            }else {
                if(intent.get("taskname").get().equals(currentRootTask.getName())) {
                    contentProvider.addEntity(currentRootTask);
                }else {
                    dialog.present(new PresentationRequest("Currently I am learning the " + currentRootTask.getName() + " task. Is learning for this task completed?"));
                }
            }

            // consume the token (subsequent rules won't see the token)
            sys.removeToken(token.get());
            Set<String> rules = tagSystem.getTagged("CreateTask");
            rules.forEach(r -> rs.disable(r));
        });


        // set the priority of the rule.
        rs.setPriority("finishTask", 20);
        tagSystem.addTag("finishTask", "TaskLearning");*/
/*
    }

    public void createMoveRule(Dialog dialog) {
        rsc.addRule("addMove", () -> {
            // check for tokens with the intent 'greetings'
            Optional<Token> token = tc.getTokens().stream()
                    .filter(t -> t.payloadEquals("intent", "addAtomicAction"))
                    .filter(t -> t.payloadEquals("action_name", "move"))
                    .findFirst();
            if (!token.isPresent()) {
                return;
            }

            Token moveToken = token.get();

            if(!moveToken.get("location").isPresent()) {
                rc.add("addMove", () -> {
                    pc.present(PresentationComponent.simpleTTS("Where do you want me to go?"));
                    //specify rule does not need to be activated?
                    mf.specifyRule("specify_addMove", (specification) -> {
                        String objectId = specification; // object as return type would be better
                        if(objectId.equals("the small workbench")) {
                            currentRootTask.addTask(new MoveTask("move", "teslocation"));
                        }
                });}).attachOrigin(moveToken);

            }else {
                rc.add(() ->   {
                    Entity loc = (Entity) moveToken.get("location").get();
                    currentRootTask.addTask(new MoveTask("move", loc));
                    rsc.removeRule("specify_addMove");}).attachOrigin(moveToken);

            }


            // consume the token (subsequent rules won't see the token)
         //   sys.removeToken(token.get());
        });

        // set the priority of the rule.
      //  rs.setPriority("addMove", 20);
     //   tagSystem.addTag("addMove", "TaskLearning");

    }

    public void createGrabDropRule(Dialog dialog) {
      /*  var rs = dialog.getRuleSystem();
        var tagSystem = dialog.getTagSystem();

        rs.addRule("addGrabDrop", (sys) -> {
            // check for tokens with the intent 'greetings'
            Optional<Token> token = sys.getTokens().stream()
                    .filter(t -> t.payloadEquals("intent", "addAtomicAction"))
                    .filter(t -> t.payloadEquals("type", "grab") || t.payloadEquals("type", "drop"))
                    .findFirst();
            if (!token.isPresent()) {
                return;
            }

            Token intent = token.get();
            final boolean isDrop = intent.get("type").equals("drop");


            if(!intent.get("object").isPresent()) {
                if(isDrop) {
                    dialog.present(new PresentationRequest("What should I drop?"));
                }else {
                    dialog.present(new PresentationRequest("What should I grab?"));
                }
                //specify rule does not need to be activated?
                MetaFactory.specifyRule(rs, "specify_addGrabDrop", (specification) -> {
                    String objectId = specification; // object as return type would be better
                    Optional<Object> action = intent.get("type");
                    rs.removeRule("specify_addGrabDrop");
                    if(isDrop) {
                        currentRootTask.addTask(new GrabDropTask("drop", objectId));
                    }else {
                        currentRootTask.addTask(new GrabDropTask("grab", objectId));
                    }

                });

            }else {
                if(isDrop) {
                    currentRootTask.addTask(new GrabDropTask("drop", (String) intent.get("object").get()));
                }else {
                    currentRootTask.addTask(new GrabDropTask("grab", (String) intent.get("object").get()));
                }
            }



            // consume the token (subsequent rules won't see the token)
            sys.removeToken(token.get());
        });

        // set the priority of the rule.
        rs.setPriority("addGrabDrop", 20);
        tagSystem.addTag("addGrabDrop", "TaskLearning");*/
/*
    }


    @Override
    public void init(ComponentManager cm) {
        rsc = cm.retrieveComponent(RuleSystemComponent.class);
        tc = cm.retrieveComponent(TokenComponent.class);
        pc = cm.retrieveComponent(PresentationComponent.class);
        rc = cm.retrieveComponent(CoordinationComponent.class);
        mf = new MetaFactory(cm);
    }

    @Override
    public void deinit() {

    }

    @Override
    public void update() {

    }

    @Override
    public Object createSnapshot() {
        return null;
    }

    @Override
    public void loadSnapshot(Object snapshot) {

    }
}
**/