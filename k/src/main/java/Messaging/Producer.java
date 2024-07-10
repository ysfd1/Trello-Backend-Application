package Messaging;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
@Startup
@Singleton 
public class Producer {
	// Injecting the queue
    @Resource(mappedName = "java:/jms/queue/DLQ")
    private Queue myTrelloQueue;
    
    @Inject
    private JMSContext context;
    
    public void sendMessage(String msg) {
        try {
            JMSProducer producer = context.createProducer();
            producer.send(myTrelloQueue, msg);

        } catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    public void sendMessages(List<String> msgs) {
        try {
            JMSProducer producer = context.createProducer();
            for(int i=0;i<msgs.size();i++)
            producer.send(myTrelloQueue, msgs.get(i));
        } catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
}
