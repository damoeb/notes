package org.notes.core.messaging;

import org.apache.log4j.Logger;
import org.notes.common.exceptions.NotesException;
import org.notes.core.domain.EventType;
import org.notes.core.domain.PostProcessEvent;
import org.notes.core.services.SearchServiceRemote;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * Created by damoeb on 4/4/14.
 */
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
public class PostProcessorMessageBean implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(PostProcessorMessageBean.class);

    @EJB
    private SearchServiceRemote searchService;

    @Override
    public void onMessage(Message message) {
        try {

            ObjectMessage obj = (ObjectMessage) message;

            PostProcessEvent event = (PostProcessEvent) obj.getObject();

            // todo handle event types

            if (EventType.INDEX == event.getType()) {
                searchService.index(event.getDocuments());
            }

            if (EventType.UN_INDEX == event.getType()) {
                searchService.deleteFromIndex(event.getDocuments());
            }

        } catch (JMSException | NotesException e) {
            LOGGER.error(e);
        }
    }

}
