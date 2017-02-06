package taskcoordinator;

import java.util.*;

import com.google.common.base.MoreObjects;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

public class TaskCoordinator {
    private static final String ETA_PREFERENCE_STRATEGY = "KBETAPreferenceStrategy";

    private static final String BEST_FIT_STRATEGY = "KBBestFitStrategy";

    private static final String FILE_AFFINITY_STRATEGY = "KBFileAffinityStrategy";

    private int nextTaskId = 1;

    private int nextServerId = 1;

    private KieContainer kieContainer;

    @Before
    public void setUp() {
        KieServices kieServices = KieServices.Factory.get();
        kieContainer = kieServices.getKieClasspathContainer();
    }

    @Test
    public void differentETAs() {
        List<Server> servers = createServers(6);
        List<Task> tasks = createTasks(9);
        BidResponseParameters defaultParameters = newDefaultParameters();
        Map<ServerTask, BidResponse> bidResponses = createBidResponses(servers, tasks, defaultParameters);

        updateBidResponses(bidResponses,
                           servers.get(0),
                           tasks,
                           BidResponseParametersBuilder.from(defaultParameters).setTimeUntilComplete(900));
        updateBidResponses(bidResponses,
                           servers.get(1),
                           tasks,
                           BidResponseParametersBuilder.from(defaultParameters).setTimeUntilComplete(800));

        execute(servers, tasks, bidResponses);
    }

    @Test
    public void differentETAs_tasksDeferred() {
        List<Server> servers = createServers(6);
        List<Task> tasks = createTasks(9, 3);
        BidResponseParameters defaultParameters = newDefaultParameters();
        Map<ServerTask, BidResponse> bidResponses = createBidResponses(servers, tasks, defaultParameters);

        updateBidResponses(bidResponses,
                           servers.get(0),
                           tasks,
                           BidResponseParametersBuilder.from(defaultParameters).setTimeUntilComplete(900));
        updateBidResponses(bidResponses,
                           servers.get(1),
                           tasks,
                           BidResponseParametersBuilder.from(defaultParameters).setTimeUntilComplete(800));

        execute(servers, tasks, bidResponses);
    }

    @Test
    public void fileAffinitiesYes() {
        List<Server> servers = createServers(6);
        List<Task> tasks = createTasks(9);
        BidResponseParameters defaultParameters = newDefaultParameters();
        Map<ServerTask, BidResponse> bidResponses = createBidResponses(servers, tasks, defaultParameters);

        updateBidResponse(bidResponses,
                          servers.get(0),
                          tasks.get(0),
                          BidResponseParametersBuilder.from(defaultParameters).setFileAffinityYes(500));
        updateBidResponse(bidResponses,
                          servers.get(1),
                          tasks.get(1),
                          BidResponseParametersBuilder.from(defaultParameters).setFileAffinityYes(500));

        execute(servers, tasks, bidResponses);
    }

    @Test
    public void fileAffinitiesWaiting() {
        List<Server> servers = createServers(6);
        List<Task> tasks = createTasks(9);
        BidResponseParameters defaultParameters = newDefaultParameters();
        Map<ServerTask, BidResponse> bidResponses = createBidResponses(servers, tasks, defaultParameters);

        updateBidResponse(bidResponses,
                          servers.get(0),
                          tasks.get(0),
                          BidResponseParametersBuilder.from(defaultParameters).setFileAffinityWaiting());
        updateBidResponse(bidResponses,
                          servers.get(1),
                          tasks.get(1),
                          BidResponseParametersBuilder.from(defaultParameters).setFileAffinityWaiting());

        execute(servers, tasks, bidResponses);
    }

    @Test
    public void fileAffinitiesYes_tasksDeferred() {
        List<Server> servers = createServers(6);
        List<Task> tasks = createTasks(9, 3);
        BidResponseParameters defaultParameters = newDefaultParameters();
        Map<ServerTask, BidResponse> bidResponses = createBidResponses(servers, tasks, defaultParameters);

        updateBidResponse(bidResponses,
                          servers.get(0),
                          tasks.get(3),
                          BidResponseParametersBuilder.from(defaultParameters).setFileAffinityYes(500));
        updateBidResponse(bidResponses,
                          servers.get(1),
                          tasks.get(4),
                          BidResponseParametersBuilder.from(defaultParameters).setFileAffinityYes(400));

        execute(servers, tasks, bidResponses);
    }

    @Test
    public void fileAffinitiesWaiting_tasksDeferred() {
        List<Server> servers = createServers(6);
        List<Task> tasks = createTasks(9, 3);
        BidResponseParameters defaultParameters = newDefaultParameters();
        Map<ServerTask, BidResponse> bidResponses = createBidResponses(servers, tasks, defaultParameters);

        updateBidResponse(bidResponses,
                          servers.get(0),
                          tasks.get(3),
                          BidResponseParametersBuilder.from(defaultParameters).setFileAffinityWaiting());
        updateBidResponse(bidResponses,
                          servers.get(1),
                          tasks.get(4),
                          BidResponseParametersBuilder.from(defaultParameters).setFileAffinityWaiting());

        execute(servers, tasks, bidResponses);
    }

    @Test
    public void fileAffinities() {
        List<Server> servers = createServers(6);
        List<Task> tasks = createTasks(9);
        BidResponseParameters defaultParameters = newDefaultParameters();
        Map<ServerTask, BidResponse> bidResponses = createBidResponses(servers, tasks, defaultParameters);

        updateBidResponse(bidResponses,
                          servers.get(0),
                          tasks.get(0),
                          BidResponseParametersBuilder.from(defaultParameters).setFileAffinityYes(500));
        updateBidResponse(bidResponses,
                          servers.get(1),
                          tasks.get(1),
                          BidResponseParametersBuilder.from(defaultParameters).setFileAffinityYes(500));
        updateBidResponse(bidResponses,
                          servers.get(2),
                          tasks.get(2),
                          BidResponseParametersBuilder.from(defaultParameters).setFileAffinityWaiting());
        updateBidResponse(bidResponses,
                          servers.get(3),
                          tasks.get(3),
                          BidResponseParametersBuilder.from(defaultParameters).setFileAffinityWaiting());

        execute(servers, tasks, bidResponses);
    }

    @Test
    public void bestFit() {
        List<Server> servers = createServers(6);
        List<Task> tasks = createTasks(9);
        BidResponseParameters defaultParameters = newDefaultParameters();
        Map<ServerTask, BidResponse> bidResponses = createBidResponses(servers, tasks, defaultParameters);

        updateBidResponses(bidResponses,
                           servers.get(0),
                           tasks,
                           BidResponseParametersBuilder.from(defaultParameters).setTempSpaceRemaining(2048));
        updateBidResponses(bidResponses,
                           servers.get(1),
                           tasks,
                           BidResponseParametersBuilder.from(defaultParameters).setTempSpaceRemaining(1024));
        updateBidResponses(bidResponses,
                           servers.get(2),
                           tasks,
                           BidResponseParametersBuilder.from(defaultParameters).setTimeUntilStart(1000));
        updateBidResponses(bidResponses,
                           servers.get(3),
                           tasks,
                           BidResponseParametersBuilder.from(defaultParameters).setTimeUntilStart(1500));
        updateBidResponses(bidResponses,
                           servers.get(4),
                           tasks,
                           BidResponseParametersBuilder.from(defaultParameters)
                                                       .setTimeUntilStart(800)
                                                       .setHasWaitingTask(true));

        execute(servers, tasks, bidResponses);
    }

    private void execute(List<Server> servers,
                         List<Task> tasks,
                         Map<ServerTask, BidResponse> bidResponses) {
        execute(ETA_PREFERENCE_STRATEGY, servers, tasks, bidResponses.values());
        execute(BEST_FIT_STRATEGY, servers, tasks, bidResponses.values());
        execute(FILE_AFFINITY_STRATEGY, servers, tasks, bidResponses.values());
    }

    private void execute(String strategyKS,
                         Collection<Server> servers,
                         Collection<Task> tasks,
                         Collection<BidResponse> bidResponses) {
        System.out.println("============================================================");
        System.out.println(strategyKS);
        System.out.println("============================================================");

        tasks.forEach(task -> {
            task.setServer(null);
            task.setEta(null);
            task.setWaiting(null);
            task.setRule(null);
        });

        Tasks deferredTasks = new Tasks(tasks, true);
        Tasks otherTasks = new Tasks(tasks, false);
        StatelessKieSession kieSession = kieContainer.getKieBase(strategyKS).newStatelessKieSession();
        Collection<Object> elements = new LinkedList<>();
        elements.addAll(servers);
        elements.addAll(bidResponses);
        elements.add(deferredTasks);
        elements.add(otherTasks);
        kieSession.execute(elements);

        for (Task task : tasks) {
            System.out.println(task + " => " + task.getServer());
        }
    }

    private List<Server> createServers(int nbServers) {
        System.out.printf("nbServers = %d%n", nbServers);
        List<Server> servers = new LinkedList<>();
        for (int i = 0; i < nbServers; i++) {
            servers.add(new Server(nextServerId++));
        }
        return servers;
    }

    private List<Task> createTasks(int nbTasks) {
        System.out.printf("nbTasks = %d%n", nbTasks);
        return createTasks(nbTasks, 0);
    }

    private BidResponseParameters newDefaultParameters() {
        BidResponseParameters defaultParameters = new BidResponseParameters(0,
                                                                            1000,
                                                                            false,
                                                                            "no",
                                                                            4L * 1024 * 1024 * 1024,
                                                                            5);
        System.out.println("defaultParameters = " + defaultParameters);
        return defaultParameters;
    }

    private List<Task> createTasks(int nbTasks, int nbDeferred) {
        List<Task> tasks = new LinkedList<>();
        for (int i = 0; i < nbTasks; i++) {
            Task task = new Task(nextTaskId++);
            if (i < nbDeferred) {
                task.setDeferred(true);
            }
            tasks.add(task);
        }
        return tasks;
    }

    private Map<ServerTask, BidResponse> createBidResponses(Collection<Server> servers,
                                                            Collection<Task> tasks,
                                                            BidResponseParameters bidResponseParameters) {
        Map<ServerTask, BidResponse> bidResponses = new HashMap<>();
        for (Server server : servers) {
            for (Task task : tasks) {
                BidResponse bidResponse = newBidResponse(server, task, bidResponseParameters);
                bidResponses.put(new ServerTask(server, task), bidResponse);
            }
        }
        return bidResponses;
    }

    private void updateBidResponse(Map<ServerTask, BidResponse> bidResponses,
                                   Server server,
                                   Task task,
                                   BidResponseParametersBuilder bidResponseParametersBuilder) {
        System.out.println("Task{id="
                           + task.getId()
                           + "}, Server{"
                           + server.getId()
                           + "} = "
                           + bidResponseParametersBuilder);
        BidResponse bidResponse = newBidResponse(server, task, bidResponseParametersBuilder.get());
        bidResponses.put(new ServerTask(server, task), bidResponse);
    }

    private void updateBidResponses(Map<ServerTask, BidResponse> bidResponses,
                                    Server server,
                                    Collection<Task> tasks,
                                    BidResponseParametersBuilder bidResponseParametersBuilder) {
        for (Task task : tasks) {
            System.out.println("Task{id="
                               + task.getId()
                               + "}, Server{"
                               + server.getId()
                               + "} = "
                               + bidResponseParametersBuilder);
            BidResponse bidResponse = newBidResponse(server, task, bidResponseParametersBuilder.get());
            bidResponses.put(new ServerTask(server, task), bidResponse);
        }
    }

    private BidResponse newBidResponse(Server server,
                                       Task task,
                                       BidResponseParameters bidResponseParameters) {
        BidResponse bidResponse = new BidResponse(server, task);
        bidResponse.setTimeUntilStart(bidResponseParameters.getTimeUntilStart());
        bidResponse.setTimeUntilComplete(bidResponseParameters.getTimeUntilComplete());
        bidResponse.setHasWaitingTask(bidResponseParameters.getHasWaitingTask());
        bidResponse.setFileAffinity(bidResponseParameters.getFileAffinity());
        bidResponse.setTempSpaceRemaining(bidResponseParameters.getTempSpaceRemaining());
        bidResponse.setWorkersRemaining(bidResponseParameters.getWorkersRemaining());
        return bidResponse;
    }

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "WeakerAccess", "unused"})
    private static class BidResponseParametersBuilder {
        private final BidResponseParameters bidResponseParameters;

        private Optional<Integer> timeUntilStart = Optional.empty();

        private Optional<Integer> timeUntilComplete = Optional.empty();

        private Optional<Boolean> hasWaitingTask = Optional.empty();

        private Optional<String> fileAffinity = Optional.empty();

        private Optional<Long> tempSpaceRemaining = Optional.empty();

        private Optional<Integer> workersRemaining = Optional.empty();

        private BidResponseParametersBuilder(BidResponseParameters bidResponseParameters) {
            this.bidResponseParameters = bidResponseParameters;
        }

        public static BidResponseParametersBuilder from(BidResponseParameters bidResponseParameters) {
            return new BidResponseParametersBuilder(bidResponseParameters);
        }

        public BidResponseParametersBuilder setTimeUntilStart(int timeUntilStart) {
            this.timeUntilStart = Optional.of(timeUntilStart);
            return this;
        }

        public BidResponseParametersBuilder setTimeUntilComplete(int timeUntilComplete) {
            this.timeUntilComplete = Optional.of(timeUntilComplete);
            return this;
        }

        public BidResponseParametersBuilder setHasWaitingTask(boolean hasWaitingTask) {
            this.hasWaitingTask = Optional.of(hasWaitingTask);
            return this;
        }

        public BidResponseParametersBuilder setFileAffinity(String fileAffinity) {
            this.fileAffinity = Optional.of(fileAffinity);
            return this;
        }

        public BidResponseParametersBuilder setFileAffinityYes(int timeUntilStart) {
            this.fileAffinity = Optional.of("yes");
            this.timeUntilStart = Optional.of((timeUntilStart));
            return this;
        }

        public BidResponseParametersBuilder setFileAffinityWaiting() {
            this.fileAffinity = Optional.of("waiting");
            return this;
        }

        public BidResponseParametersBuilder setTempSpaceRemaining(long tempSpaceRemaining) {
            this.tempSpaceRemaining = Optional.of(tempSpaceRemaining);
            return this;
        }

        public BidResponseParametersBuilder setWorkersRemaining(int workersRemaining) {
            this.workersRemaining = Optional.of(workersRemaining);
            return this;
        }

        public BidResponseParameters get() {
            return new BidResponseParameters(timeUntilStart.orElse(bidResponseParameters.getTimeUntilStart()),
                                             timeUntilComplete.orElse(bidResponseParameters.getTimeUntilComplete()),
                                             hasWaitingTask.orElse(bidResponseParameters.getHasWaitingTask()),
                                             fileAffinity.orElse(bidResponseParameters.getFileAffinity()),
                                             tempSpaceRemaining.orElse(bidResponseParameters.getTempSpaceRemaining()),
                                             workersRemaining.orElse(bidResponseParameters.getWorkersRemaining()));
        }

        @Override
        public String toString() {
            MoreObjects.ToStringHelper toStringHelper = MoreObjects.toStringHelper(this);
            if (timeUntilStart.isPresent()) {
                toStringHelper.add("timeUntilStart", timeUntilStart.get());
            }
            if (timeUntilComplete.isPresent()) {
                toStringHelper.add("timeUntilComplete", timeUntilComplete.get());
            }
            if (hasWaitingTask.isPresent()) {
                toStringHelper.add("hasWaitingTask", hasWaitingTask.get());
            }
            if (fileAffinity.isPresent()) {
                toStringHelper.add("fileAffinity", fileAffinity.get());
            }
            if (tempSpaceRemaining.isPresent()) {
                toStringHelper.add("tempSpaceRemaining", tempSpaceRemaining.get());
            }
            if (workersRemaining.isPresent()) {
                toStringHelper.add("workersRemaining", workersRemaining.get());
            }
            return toStringHelper.toString();
        }
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    private static class BidResponseParameters {
        private int timeUntilStart;

        private int timeUntilComplete;

        private boolean hasWaitingTask;

        private String fileAffinity;

        private long tempSpaceRemaining;

        private int workersRemaining;

        public BidResponseParameters(int timeUntilStart,
                                     int timeUntilComplete,
                                     boolean hasWaitingTask,
                                     String fileAffinity,
                                     long tempSpaceRemaining,
                                     int workersRemaining) {
            this.timeUntilStart = timeUntilStart;
            this.timeUntilComplete = timeUntilComplete;
            this.hasWaitingTask = hasWaitingTask;
            this.fileAffinity = fileAffinity;
            this.tempSpaceRemaining = tempSpaceRemaining;
            this.workersRemaining = workersRemaining;
        }

        public int getTimeUntilStart() {
            return timeUntilStart;
        }

        public void setTimeUntilStart(int timeUntilStart) {
            this.timeUntilStart = timeUntilStart;
        }

        public int getTimeUntilComplete() {
            return timeUntilComplete;
        }

        public void setTimeUntilComplete(int timeUntilComplete) {
            this.timeUntilComplete = timeUntilComplete;
        }

        public boolean getHasWaitingTask() {
            return hasWaitingTask;
        }

        public void setHasWaitingTask(boolean hasWaitingTask) {
            this.hasWaitingTask = hasWaitingTask;
        }

        public String getFileAffinity() {
            return fileAffinity;
        }

        public void setFileAffinity(String fileAffinity) {
            this.fileAffinity = fileAffinity;
        }

        public long getTempSpaceRemaining() {
            return tempSpaceRemaining;
        }

        public void setTempSpaceRemaining(long tempSpaceRemaining) {
            this.tempSpaceRemaining = tempSpaceRemaining;
        }

        public int getWorkersRemaining() {
            return workersRemaining;
        }

        public void setWorkersRemaining(int workersRemaining) {
            this.workersRemaining = workersRemaining;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                              .add("timeUntilStart", timeUntilStart)
                              .add("timeUntilComplete", timeUntilComplete)
                              .add("hasWaitingTask", hasWaitingTask)
                              .add("fileAffinity", fileAffinity)
                              .add("tempSpaceRemaining", tempSpaceRemaining)
                              .add("workersRemaining", workersRemaining)
                              .toString();
        }
    }

    @SuppressWarnings("WeakerAccess")
    private static class ServerTask {
        private final Server server;

        private final Task task;

        public ServerTask(Server server, Task task) {
            this.server = server;
            this.task = task;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ServerTask serverTask = (ServerTask) o;
            return Objects.equals(server, serverTask.server) && Objects.equals(task, serverTask.task);
        }

        @Override
        public int hashCode() {
            return Objects.hash(server, task);
        }
    }
}
