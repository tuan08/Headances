package org.headvances.crawler.channel;

import java.io.Serializable;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class JMSChannelGateway  implements ChannelGateway {
	private static final Logger logger = LoggerFactory.getLogger(JMSChannelGateway.class);

	private Destination destination ;
	private JmsTemplate template ;	
	
	public void setDestination(Destination dest) { this.destination = dest ; }
	
	public void setJmsTemplate(JmsTemplate template) { this.template = template ; }
	
	public void send(final Serializable object) {
		template.send(destination, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage omessage = session.createObjectMessage() ;
				try {
	        omessage.setObject(object) ;
	        return omessage;
				} catch (Throwable e) {
					logger.error("Cannot schedule", e);
        	throw new RuntimeException("Cannot schedule", e) ;
        }
			}
		});
	}
}