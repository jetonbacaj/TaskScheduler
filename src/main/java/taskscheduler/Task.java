package taskscheduler;

import com.google.common.base.MoreObjects;

public class Task {
    private final String name;

    private Status status = Status.NOT_SCHEDULED;

    private long diskNeeded = 0;

    private int cpuNeeded = 0;

    private int priority = 0;

    private int expectedRank = 0;

    private int actualRank = -1;


    public Task(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getDiskNeeded() {
        return diskNeeded;
    }

    public void setDiskNeeded(long diskNeeded) {
        this.diskNeeded = diskNeeded;
    }

    public int getCpuNeeded() {
        return cpuNeeded;
    }

    public void setCpuNeeded(int cpuNeeded) {
        this.cpuNeeded = cpuNeeded;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getExpectedRank() {
        return expectedRank;
    }

    public void setExpectedRank(int expectedRank) {
        this.expectedRank = expectedRank;
    }

    public int getActualRank() {
        return actualRank;
    }

    public void setActualRank(int actualRank) {
        this.actualRank = actualRank;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("name", name)
                          .add("status", status)
                          .add("diskNeeded", diskNeeded)
                          .add("cpuNeeded", cpuNeeded)
                          .add("priority", priority)
                          .add("expectedRank", expectedRank)
                          .add("actualRank", actualRank)
                          .toString();
    }

    public enum Status {
        NOT_SCHEDULED,
        NOT_STARTED,
        STARTED,
        ALLOCATED,
        COMPLETED,
        RUNNING,
        NEW,
        NOT_RANKED
    }
}
