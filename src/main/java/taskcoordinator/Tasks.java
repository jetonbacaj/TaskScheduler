package taskcoordinator;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Tasks {
    private static final Predicate<Task> PREVIOUSLY_DEFERRED = Task::isDeferred;

    private final Collection<Task> tasks;

    private final boolean deferred;

    public Tasks(Collection<Task> tasks, boolean deferred) {
        Predicate<Task> predicate = PREVIOUSLY_DEFERRED;
        if (!deferred) {
            predicate = predicate.negate();
        }
        this.tasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        this.deferred = deferred;
    }

    public Collection<Task> getTasks() {
        return tasks;
    }

    public boolean isDeferred() {
        return deferred;
    }
}
