package de.dfki.step.tm;

import de.dfki.step.core.*;
import de.dfki.step.dialog.MetaFactory;
import de.dfki.step.kb.DataStore;
import de.dfki.step.output.PresentationComponent;
import de.dfki.step.core.CoordinationComponent;
import de.dfki.step.rengine.RuleComponent;
import de.dfki.step.taskmodel.RootTask;
import de.dfki.step.taskmodel.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.*;

public class TaskLearningComponent implements Component {

    private String currentTaskId;
    private RootTask currentRootTask = null;
    private Task currentTask;
    private RuleComponent rsc;
    private TokenComponent tc;
    private PresentationComponent pc;
    private CoordinationComponent rc;
    private MetaFactory mf;
    private Map<String, Object> map = new HashMap<>();
    private MyDataEntry robot;

    private static final Logger log = LoggerFactory.getLogger(TaskLearningComponent.class);

    public TaskLearningComponent(DataStore ds) {
        this.robot =  new MyDataEntry(ds, "r1");//(MyDataEntry) ds.get("r1").get();
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
            if(currentRootTask == null) {
                pc.present(PresentationComponent.simpleTTS("I am currently not learning a task."));
                return;
            }
            if (!intent.get("taskname").isPresent()) { //assume currentTask -> maybe ask question??
                List<Task> tasks = robot.getTasks();
                ArrayList<Task> newList = new ArrayList<>();
                newList.addAll(tasks);
                newList.add(currentRootTask);
                robot.setTasks(newList);
                robot.save();
                pc.present(PresentationComponent.simpleTTS("Okay, I successfully learnt a new task."));

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
                        ArrayList<Task> newList = new ArrayList<>();
                        newList.addAll(tasks);
                        newList.add(currentRootTask);
                        robot.setTasks(newList);
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

            if(currentRootTask == null) {
                pc.present(PresentationComponent.simpleTTS("I am currently not learning a task."));
                return;
            }

            if(!moveToken.get("slots").isPresent()) {
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
                Map<String, Object> slotinfo = (Map) moveToken.get("slots").get();
                if(slotinfo.containsKey("resolved") && slotinfo.get("resolved").equals(true) && !((List<Map<String, Object>>) slotinfo.get("resolved_candidates")).isEmpty()) {
                    String entity = null;
                    double conf = 0.0;
                    for(Map<String, Object> slotCandidate: (List<Map<String, Object>>) slotinfo.get("resolved_candidates")) {
                        if((Double) slotCandidate.get("res_conf") > conf) {
                            entity = (String) slotCandidate.get("res_id");
                        }
                    }
                    if(entity != null) {
                        currentRootTask.addTask(new MoveTask("move", entity));
                        log.info("move_to: {}", entity);
                    }
                }

                }).attachOrigin(moveToken);

            }

        });


    }

    public void createGrabDropRule() {
        System.out.println("tokens in grabdroprule: " + tc.getTokens().toString());

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
            boolean isDrop =  intent.get("action_name").get().equals("drop");

            if(currentRootTask == null) {
                pc.present(PresentationComponent.simpleTTS("I am currently not learning a task."));
                return;
            }

            if(!intent.get("slots").isPresent()) {

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
                Map<String, Object> slotinfo = (Map) intent.get("slots").get();
                if(slotinfo.containsKey("resolved") && slotinfo.get("resolved").equals(true) && !((List<Map<String, Object>>) slotinfo.get("resolved_candidates")).isEmpty()) {
                    String entity = null;
                    double conf = 0.0;
                    for(Map<String, Object> slotCandidate: (List<Map<String, Object>>) slotinfo.get("resolved_candidates")) {
                        if((Double) slotCandidate.get("res_conf") > conf) {
                            entity = (String) slotCandidate.get("res_id");
                        }
                    }

                    if(entity != null) {
                        if (isDrop) {
                            currentRootTask.addTask(new GrabDropTask("drop", entity));
                            log.info("drop: {}", entity);
                        } else {
                            currentRootTask.addTask(new GrabDropTask("grab", entity));
                            log.info("grab: {}", entity);
                        }
                    }
                }
            }

        });

    }

    public void createExecutionRule() {
        rsc.addRule("executeTask", () -> {
            // check for tokens with the intent 'startTask'
            Optional<Token> token = tc.getTokens().stream()
                    .filter(t -> t.payloadEquals("intent", "execute_task"))
                    .findFirst();
            if (!token.isPresent()) {
                return;
            }

            Token executeToken = token.get();
            if (!executeToken.get("taskname").isPresent()) {
                rc.add("executeTask", () -> {
                    if (!robot.getTasks().isEmpty()) {
                        String taskname =  ((RootTask) robot.getTasks().get(0)).getName();
                        robot.getTasks().get(0).execute();
                        pc.present(PresentationComponent.simpleTTS("Okay I will execute the task: " + taskname));

                    }
                }).attachOrigin(executeToken);
            }else {
                rc.add("executeTask_else", () -> {
                    Task eTask = null;
                    for(Task t: robot.getTasks()) {
                        if(t instanceof RootTask) {
                            if(((RootTask) t).getName().equals(executeToken.get("taskname").orElse(""))) {
                                eTask = t;
                                break;
                            }
                        }
                    }
                    if(eTask != null) {
                        eTask.execute();
                        pc.present(PresentationComponent.simpleTTS("Okay I will execute the task: " + executeToken.get("taskname").orElse("")));
                    }
                }).attachOrigin(executeToken);



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
        createExecutionRule();
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
