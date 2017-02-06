package taskscheduler;

import com.google.common.base.MoreObjects;

public class Clock {
    private int queue = 0;

    public int getQueue() {
        return queue;
    }

    public void setQueue(int queue) {
        this.queue = queue;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("queue", queue)
                          .toString();
    }
}
