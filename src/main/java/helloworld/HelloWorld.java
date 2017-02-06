package helloworld;

import org.kie.api.KieServices;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class HelloWorld {
    public static void main(String[] args) {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.getKieClasspathContainer();
        KieSession kieSession = kieContainer.newKieSession("HelloWorldKS");
        kieSession.addEventListener( new DebugAgendaEventListener() );
        kieSession.addEventListener( new DebugRuleRuntimeEventListener() );

        Message message = new Message();
        message.setMessage("Hello World!");
        message.setStatus(Message.HELLO);
        kieSession.insert(message);

        kieSession.fireAllRules();
    }
}
