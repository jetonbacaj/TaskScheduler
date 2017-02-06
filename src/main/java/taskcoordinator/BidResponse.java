package taskcoordinator;

import com.google.common.base.MoreObjects;

public class BidResponse {
    private final Server server;

    private final Task task;

    private int timeUntilStart;

    private int timeUntilComplete;

    private boolean hasWaitingTask;

    private String fileAffinity;

    private long tempSpaceRemaining;

    private int workersRemaining;

    public BidResponse(Server server, Task task) {
        this.server = server;
        this.task = task;
    }

    public Server getServer() {
        return server;
    }

    public Task getTask() {
        return task;
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

    public boolean isHasWaitingTask() {
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
                          .add("server", server)
                          .add("task", task)
                          .toString();
    }
}
