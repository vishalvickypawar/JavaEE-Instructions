------------------------
MessageDrivenBeans.java
------------------------


// NOTE: If you're running JDK 14+, you could run into such an Exception when invoking message-driven beans
// ClassNotFoundException: java.security.acl.Group


// Application Server Setup.



		// go to the terminal and cd to the bin directory of the application server

		// 		cd $JBOSS_HOME/bin 

		// run the following command to get access to JMS Configuration 

		// 		./standalone.sh --server-config=standalone-full.xml


		// in the wildfly console head to configuration tab. Under Subsystems select Messaging ActiveMQ

		// 	here under category select Server > Default > Destinations

		// 		Click on the view button next to the Destination

		// 			a new console opens up here we can setup the JMS Queues and Topics. 
		// 			On the left menu select the JMS Queue. 
  //                   A list of existing queues will appear with the default DLQ and ExpiryQueue. 
  //                   Add a new queue by clicing the "Add" button.

		// 			Give name to the queue 		Name 	: LoonyQueue
		// 			Add the entries 			Entries : java:jboss/exported/jms/queue/LoonyQueue, java:/jms/queue/LoonyQueue


		// 			Click Add and the LoonyQueue is added to Queue list.


//  From a separate terminal/shell add a new application user. 
	// Run the add user script

cd $JBOSS_HOME/bin

./add-user.sh

	// Type a for Application User 
	// When prompted for the username, type in: messagingapp
	// Choose to activate the user
	// Set the password to something you will remember, e.g. loonycorn1@



	// open the file application-roles.properties in the wildfly/standalone/configuration 
cd $JBOSS_HOME/standalone/configuration

nano application-roles.properties

	// there will be a messagingapp entry in the last line. Change and set 

	messagingapp=guest 


// We continue using the client and server apps as before, though you can clear out the old sources
// for convenience. We create a message processor (reader) bean on the server side and deploy it to Wildfly
// Then we publish a message from the client and confirm that the message reader has received it


// Create a consumer MessageDrivenBean to read the messages that reads the 
// messages posted to the LoonyQueue
// Let's call this class MessageReaderMDB and create it in the com.loonycorn package of the server project

package com.loonycorn;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(
                propertyName = "destination",
                propertyValue = "LoonyQueue"),
        @ActivationConfigProperty(
                propertyName = "destinationType",
                propertyValue = "javax.jms.Queue")
})
public class MessageReaderMDB implements MessageListener {

    public void onMessage(Message message) {

        TextMessage textMessage = (TextMessage) message;

        try {
            System.out.println("Message received: " + textMessage.getText());

        } catch (JMSException e) {

            System.out.println(
                    "Error while trying to consume messages: " + e.getMessage());
        }
    }
}



// Deploy the mdb to wildfly

mvn clean
mvn wildfly:undeploy

mvn install wildfly:deploy

/// Head to the Wildfly logs. You should see a message like this:
        Started message driven bean 'MessageReaderMDB' with 'activemq-ra.rar' resource adapter


// Create a producer 

// Head to the client project. You can clear out the old source files for convenience (not necessary)
// In the com.loonycorn package, create a new source called MessageGenerator.java

package com.loonycorn;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;

public class MessageGenerator {

    public static void main(String[] args) throws NamingException, JMSException {

        final Hashtable<String,String> jndiProperties = new Hashtable<>();


        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
        Context context = new InitialContext(jndiProperties);

        ConnectionFactory cf = (ConnectionFactory) context.lookup("jms/RemoteConnectionFactory");
        System.out.println("Found connection factory jms/RemoteConnectionFactory in JNDI");
        Destination dest = (Destination) context.lookup("jms/queue/LoonyQueue");

        System.out.println("Found destination queue/MDBQueue in JNDI");
        Connection connection = cf.createConnection("messagingapp","loonycorn1@");

        Session session = connection.createSession(
                false, Session.AUTO_ACKNOWLEDGE);
        MessageProducer publisher = session.createProducer(dest);

        connection.start();

        TextMessage message = session.createTextMessage("Hello message consumer!");
        publisher.send(message);

        connection.close();

    }
}

// Run the above program


// Head to the Wildfly server logs - as the producer sends the message on the LoonyQueue the server logs 
		
		15:00:11,970 INFO  [stdout] (Thread-3 (ActiveMQ-client-global-threads)) Message received: Hello message consumer!


// REMINDER: If you're running JDK 14+, you could run into such an Exception when invoking message-driven beans
// ClassNotFoundException: java.security.acl.Group


// Send multiple messages from the MessageGenerator
// After the call to connection.start(); instead of one message, send 3

        // Code above is the same as before

        connection.start();

        TextMessage message = session.createTextMessage("Hello again!");
        publisher.send(message);

        message = session.createTextMessage("Just thought I'd check in for a second time.");
        publisher.send(message);

        message = session.createTextMessage("Goodbye.");
        publisher.send(message);

        connection.close();


// Re-run the program and head to the wildfly logs - all messages have been received and logged.












