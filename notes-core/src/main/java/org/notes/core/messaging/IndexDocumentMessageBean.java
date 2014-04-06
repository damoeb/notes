package org.notes.core.messaging;

import org.apache.log4j.Logger;
import org.notes.common.domain.Document;
import org.notes.common.exceptions.NotesException;
import org.notes.core.services.SearchService;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
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
public class IndexDocumentMessageBean implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(IndexDocumentMessageBean.class);

    @Inject
    private SearchService searchService;

    @Override
    public void onMessage(Message message) {
        try {

            ObjectMessage obj = (ObjectMessage) message;

            searchService.index((Document) obj.getObject());

        } catch (JMSException | NotesException e) {
            LOGGER.error(e);
        }
    }

}
