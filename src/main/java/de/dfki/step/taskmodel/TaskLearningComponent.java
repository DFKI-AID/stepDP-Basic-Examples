package de.dfki.step.taskmodel;

import de.dfki.step.core.*;
import de.dfki.step.dialog.MetaFactory;
import de.dfki.step.kb.DataStore;
import de.dfki.step.output.PresentationComponent;
import de.dfki.step.core.CoordinationComponent;
import de.dfki.step.rengine.RuleComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.*;

public class TaskLearningComponent implements Component {

    private String currentTaskId;
    private RootTask currentRootTask;
    private Task currentTask;
    private RuleComponent rsc;
    private TokenComponent tc;
    private PresentationComponent pc;
    private CoordinationComponent rc;
    private MetaFactory mf;
    private Map<String, Object> map = new HashMap<>();
    private DataStore ds;
    private MyDataEntry robot;

    private static final Logger log = LoggerFactory.getLogger(TaskLearningComponent.class);

    public TaskLearningComponent(DataStore ds, MyDataEntry robot) {
        this.ds = ds;
        this.robot = robot;
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
                    mf.specifyRule("specify_taskname", (specification) -> {
                       // rc.add("startTask", () -> {
                        currentRootTask = new RootTask(specification);
                        pc.present(PresentationComponent.simpleTTS("Sure, tell me what I have to do"));
                        log.info("start_task: {}", specification);
                        //});
                    });
                }).attachOrigin(startTaskToken);
            }else {
                rc.add("startTask_else", () -> {
                    currentRootTask = new RootTask((String) startTaskToken.get("taskname").get());
                    pc.present(PresentationComponent.simpleTTS("Sure, tell me what I have to do"));
                    log.info("start_task: {}", startTaskToken.get("taskname", String.class).get());
                }).attachOrigin(startTaskToken);

                map.put("startTask", startTaskToken);
                map.put("rootTask", currentRootTask);

            }

        });

    }

    public void createFinishTaskRule() {
        rsc.addRule("finishTask", () -> {
            // check for tokens with the intent 'finishTask'
            Optional<Token> token = tc.getTokens().stream()
                    .filter(t -> t.payloadEquals("intent", "finishTask"))
                    .findFirst();
            if (!token.isPresent()) {
                return;
            }

            Token intent = token.get();
            if (!intent.get("taskname").isPresent()) { //assume currentTask -> maybe ask question??
                List<Task> tasks = robot.getTasks();
                tasks.add(currentRootTask);
                robot.setTasks(tasks);
                robot.save();

            }else {
                if(intent.get("taskname").get().equals(currentRootTask.getName())) {
                    List<Task> tasks = robot.getTasks();
                    ArrayList<Task> newList = new ArrayList<>();
                    newList.addAll(tasks);
                    newList.add(currentRootTask);
                    robot.setTasks(newList);
                    robot.save();
                    pc.present(PresentationComponent.simpleTTS("Okay, I successfully learnt a new task."));
                }else {
                    pc.present(PresentationComponent.simpleTTS("Currently I am learning the " + currentRootTask.getName() + " task. Is learning for this task completed?"));
                    mf.createInformAnswer("confirmfinishedTask", () -> {
                        List<Task> tasks = robot.getTasks();
                        tasks.add(currentRootTask);
                        robot.setTasks(tasks);
                        robot.save();
                        pc.present(PresentationComponent.simpleTTS("Okay, I successfully learnt a new task."));
                    }, () -> {
                        pc.present(PresentationComponent.simpleTTS("You need to finish or abort the current task learning first."));
                    });
                }
            }

        });

    }

    public void createMoveRule() {
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
                       // String objectId = specification; // object as return type would be better
                        //check if is valid location?
                        currentRootTask.addTask(new MoveTask("move", specification));
                        log.info("move_to: {}", specification);

                });}).attachOrigin(moveToken);

            }else {
                rc.add(() -> {
                    String locId = (String) moveToken.get("location").get();
                    currentRootTask.addTask(new MoveTask("move", locId));
                    log.info("move_to: {}", moveToken.get("location", String.class).get());
                }).attachOrigin(moveToken);

            }


            // consume the token (subsequent rules won't see the token)
         //   sys.removeToken(token.get());
        });

        // set the priority of the rule.
      //  rs.setPriority("addMove", 20);
     //   tagSystem.addTag("addMove", "TaskLearning");

    }

    public void createGrabDropRule() {

        rsc.addRule("addGrabDrop", () -> {
            // check for tokens with the intent 'greetings'
            Optional<Token> token = tc.getTokens().stream()
                    .filter(t -> t.payloadEquals("intent", "addAtomicAction"))
                    .filter(t -> t.payloadEquals("action_name", "grab") || t.payloadEquals("action_name", "drop"))
                    .findFirst();
            if (!token.isPresent()) {
                return;
            }

            Token intent = token.get();
            final boolean isDrop = intent.get("type").equals("drop");


            if(!intent.get("object").isPresent()) {
                if(isDrop) {
                    pc.present(PresentationComponent.simpleTTS("What should I drop?"));
                }else {
                    pc.present(PresentationComponent.simpleTTS("What should I grab?"));
                }
                //specify rule does not need to be activated?
                mf.specifyRule("specify_addGrabDrop", (specification) -> {
                    if(isDrop) {
                        currentRootTask.addTask(new GrabDropTask("drop", specification));
                        log.info("drop: {}", specification);
                    }else {
                        currentRootTask.addTask(new GrabDropTask("grab", specification));
                        log.info("grab: {}", specification);
                    }

                });

            }else {
                if(isDrop) {
                    currentRootTask.addTask(new GrabDropTask("drop", (String) intent.get("object").get()));
                    log.info("drop: {}", intent.get("object", String.class).get());
                }else {
                    currentRootTask.addTask(new GrabDropTask("grab", (String) intent.get("object").get()));
                    log.info("grab: {}", intent.get("object", String.class).get());
                }
            }

        });


    }


    @Override
    public void init(ComponentManager cm) {
        rsc = cm.retrieveComponent(RuleComponent.class);
        tc = cm.retrieveComponent(TokenComponent.class);
        pc = cm.retrieveComponent(PresentationComponent.class);
        rc = cm.retrieveComponent(CoordinationComponent.class);
        mf = new MetaFactory(cm);

        createStartTaskRule();
        createFinishTaskRule();
        createMoveRule();
        createGrabDropRule();
    }

    @Override
    public void deinit() {

    }

    @Override
    public void update() {

    }

    @Override
    public Object createSnapshot() {
        Map<String, Object> mapCopy = new HashMap<>();
        mapCopy.putAll(this.map);
        return mapCopy;
    }

    @Override
    public void loadSnapshot(Object snapshot) {
        this.map = new HashMap<>();
        this.map.putAll((Map) snapshot);

    }
}
