package taskscheduler;

import java.util.LinkedHashMap;
import java.util.Map;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

public class TaskScheduler {
    public static void main(String[] args) {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.getKieClasspathContainer();
        KieSession kieSession = kieContainer.newKieSession("TaskSchedulerKS");
//        kieSession.addEventListener(new DebugAgendaEventListener());
//        kieSession.addEventListener(new DebugRuleRuntimeEventListener());

        Map<Object, FactHandle> objects = new LinkedHashMap<>();

        Clock clock = new Clock();
        insert(kieSession, clock, objects);

        Disk disk = new Disk(500);
        insert(kieSession, disk, objects);

        Cpu cpu = new Cpu(4);
        insert(kieSession, cpu, objects);

        // Step 1
        Task task1 = new Task("Task1");
        task1.setStatus(Task.Status.NEW);
        task1.setCpuNeeded(1);
        task1.setDiskNeeded(100);
        insert(kieSession, task1, objects);
        // => rank 0

        Task task2 = new Task("Task2");
        task2.setStatus(Task.Status.NEW);
        task2.setCpuNeeded(1);
        task2.setDiskNeeded(200);
        insert(kieSession, task2, objects);
        // => rank 1

        Task task3 = new Task("Task3");
        task3.setStatus(Task.Status.NEW);
        task3.setCpuNeeded(2);
        task3.setDiskNeeded(400);
        task3.setPriority(10);
        insert(kieSession, task3, objects);
        // => rank 0

        Task task4 = new Task("Task4");
        task4.setStatus(Task.Status.NEW);
        task4.setCpuNeeded(1);
        task4.setDiskNeeded(200);
        insert(kieSession, task4, objects);
        // => rank 1

        kieSession.fireAllRules();

        for (Object object : objects.keySet()) {
            System.out.println(object);
        }

        // Step 2
        Task task5 = new Task("task5");
        task5.setStatus(Task.Status.NEW);
        task5.setCpuNeeded(1);
        task5.setDiskNeeded(100);
        insert(kieSession, task5, objects);
        // => rank 2


        Task task6 = new Task("task6");
        task6.setStatus(Task.Status.NEW);
        task6.setCpuNeeded(1);
        task6.setDiskNeeded(100);
        insert(kieSession, task6, objects);
        // => rank 2

        kieSession.fireAllRules();

        for (Object object : objects.keySet()) {
            System.out.println(object);
        }

        // Step 3
        Task task7 = new Task("task5");
        task7.setStatus(Task.Status.NEW);
        task7.setCpuNeeded(1);
        task7.setDiskNeeded(100);
        task7.setPriority(10);
        insert(kieSession, task7, objects);
        // => rank 0?

//        Task task1 = new Task("task1");
//        task1.setCpuNeeded(1);
//        task1.setDiskNeeded(10);
//        insert(kieSession, task1, objects);
//
//        Task task2 = new Task("task2");
//        task2.setCpuNeeded(2);
//        task2.setDiskNeeded(100);
//        insert(kieSession, task2, objects);
//
//        Task task3 = new Task("task3");
//        task3.setPriority(10);
//        task3.setCpuNeeded(1);
//        task3.setDiskNeeded(100);
//        insert(kieSession, task3, objects);
//
//        Task task4 = new Task("task4");
//        task4.setActualRank(0);
//        task4.setPriority(0);
//        task4.setCpuNeeded(1);
//        task4.setDiskNeeded(100);
//        insert(kieSession, task4, objects);
//
//        Task task5 = new Task("task5");
//        task5.setActualRank(10);
//        task5.setPriority(10);
//        task5.setCpuNeeded(1);
//        task5.setDiskNeeded(100);
//        insert(kieSession, task5, objects);

//        kieSession.fireAllRules();
//
//        for (Object object : objects.keySet()) {
//            System.out.println(object);
//        }

//        task1.setStatus(Task.Status.COMPLETED);
//        kieSession.update(objects.get(task1), task1);
//
//        task3.setStatus(Task.Status.COMPLETED);
//        kieSession.update(objects.get(task3), task3);
//
//        kieSession.fireAllRules();
//
//        for (Object object : objects.keySet()) {
//            System.out.println(object);
//        }
    }

    private static void insert(KieSession kieSession, Object object, Map<Object, FactHandle> objects) {
        FactHandle factHandle = kieSession.insert(object);
        objects.put(object, factHandle);
    }
}
