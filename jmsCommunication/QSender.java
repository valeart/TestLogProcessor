package jmsCommunication;

import javax.jms.*;

import commonClasses.IncorrectPropertyConfig;

import java.io.IOException;

public class QSender {
	private final QueueConnectionFactory factory;
	private final JmsConfigReader confReader; 
	
	private QueueConnection connection = null;
	private Queue queue = null;
	private QueueSession session = null;
	private QueueSender sender = null;
	
	public QSender(String configFile) throws IOException, IncorrectPropertyConfig, IncorrectJmsConfig {
		confReader = new JmsConfigReader(configFile);
		
        factory = new com.tibco.tibjms.TibjmsQueueConnectionFactory(confReader.GetParamValue("jmsServerURL"));
	}
	
	private void openConnection() throws JMSException {
		if(connection != null)
			return;
		
		if(confReader.GetParamValue("userName") != null)
			connection = factory.createQueueConnection(confReader.GetParamValue("userName"),confReader.GetParamValue("password"));
		else
			connection = factory.createQueueConnection();
			
		session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		queue = session.createQueue(confReader.GetParamValue("queueName"));
		sender = session.createSender(queue);
	}

	public void sendMessage(String messageBody) throws JMSException {      
		if(session == null || sender == null)
			openConnection();
		
		//Sending message          
		TextMessage message = session.createTextMessage(messageBody);
		int amount = Integer.parseInt(confReader.GetParamValue("repeat"));
		for(int i = 0; i < amount; i++){
			sender.send(message);          
		}
	}
	
	public void closeConnection() throws JMSException {
		if(connection != null){
			connection.close();
			connection = null;
			session = null;
			queue = null;
			sender = null;
		}
	}
}
