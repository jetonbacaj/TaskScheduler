package taskscheduler;

import com.google.common.base.MoreObjects;

public abstract class Resource {
    private long available;

    public Resource(long available) {
        this.available = available;
    }

    public long getAvailable() {
        return available;
    }

    public void setAvailable(long available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("available", available)
                          .toString();
    }
}
