package org.notes.search.messaging;

import org.apache.log4j.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * Created by damoeb on 4/4/14.
 */
//@MessageDriven(mappedName="java:jboss/exported/jms/queue/test", activationConfig =  {
//        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
////        @ActivationConfigProperty(propertyName = "useJNDI", propertyValue = "true"),
////        @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:jboss/exported/jms/topic/test"),
////        java:jboss/exported/jms/topic/test
//        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
//})
@MessageDriven(
        name = "IndexMDB",
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationType",
                        propertyValue = "javax.jms.Queue"),

                @ActivationConfigProperty(propertyName = "connectionFactory",
                        propertyValue = "java:/ConnectionFactory"),

                @ActivationConfigProperty(propertyName = "destination",
                        propertyValue = "java:jboss/exported/jms/queue/test")
        }
)
public class IndexDocumentMessageBean implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(IndexDocumentMessageBean.class);


    @Override
    public void onMessage(Message message) {
        try {
            LOGGER.info("consume " + ((TextMessage) message).getText());

        } catch (JMSException e) {
            LOGGER.error(e);
        }
    }

}
