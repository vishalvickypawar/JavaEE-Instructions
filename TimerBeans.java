TimerBeans
-----------------

// Pom creation remains the same as previous.

// Note the timerBeans do not work with stateful sessions.


# Step 1: Create a programmatic single action TimerBean


// A timer which expires after a set duration in seconds  
// Once a timer is created, a task is kicked off after a specified duration or at a specified time

// First create an interface for the Timer Bean
// On the server side, create a class TimerInterfaceRemote in the com.loonycorn package

package com.loonycorn;

import javax.ejb.Remote;

@Remote
public interface TimerInterfaceRemote {

    public void createTimer(long milliseconds);
}


// Implement the timer interface using a TimerBean class (also in the com.loonycorn package)

package com.loonycorn;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import java.util.Date;

@Stateless
public class TimerBean implements TimerInterfaceRemote {

    @Resource
    private SessionContext context;

    public void createTimer(long duration) {
        context.getTimerService().createTimer(duration, "Created new programmatic timer");
    }

    @Timeout
    public void timeOutHandler(Timer timer) {

        System.out.println("Timer service has begun at: [" + (new Date()).toString() + "]" );
        System.out.println("Timer message : " + timer.getInfo());
        timer.cancel();
    }
}




// Deploy the bean in Wildfly

mvn clean
mvn wildfly:undeploy

mvn install wildfly:deploy

// Head to the Wildfly logs to confirm that the TimerBean has been deployed


// Create the client 

package com.loonycorn;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Date;
import java.util.Hashtable;

public class TimerClient {
    public static void main(String[] args) throws Exception {

        invokeStatelessBean();

    }

    private static void invokeStatelessBean() throws NamingException {

        TimerInterfaceRemote timerBean = lookupRemoteStatelessCounter();
        System.out.println("Obtained a remote timer bean for invocation");

        System.out.println("About to submit a timed job at: [" + (new Date()).toString() + "]" );

        timerBean.createTimer(20000);

        System.out.println("Client program has ended at: [" + (new Date()).toString() + "]");

    }

    private static TimerInterfaceRemote lookupRemoteStatelessCounter() throws NamingException {

        final Hashtable<String, String> jndiProperties = new Hashtable<>();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");

        final Context context = new InitialContext(jndiProperties);

        final String appName = "";
        final String moduleName = "ejb-intro-1.0";
        final String distinctName = "";
        final String beanName = TimerBean.class.getSimpleName();
        final String viewClassName = TimerInterfaceRemote.class.getName();

        return (TimerInterfaceRemote) context.lookup("ejb:" + appName + "/" 
                                                    + moduleName + "/" + distinctName + "/" 
                                                    + beanName + "!" + viewClassName);
    }
}

// Run the program - the client sends a timer request at time t
// After approximately t+20 seconds, messages like this will appear in the Wildfly logs:

09:43:54,314 INFO  [stdout] (EJB default - 1) Timer service has begun at: [Thu May 27 09:43:54 IST 2021]
09:43:54,314 INFO  [stdout] (EJB default - 1) Timer message : Created new programmatic timer





//  A timer which expires at a set date and time

// Modify the timer interface to use a Date for createTimer

package com.loonycorn;

import javax.ejb.Remote;
import java.util.Date;

@Remote
public interface TimerInterfaceRemote {

    public void createTimer(Date date);
}


// Change the createTimer method in the TimerBean
// The whole class is included here, but only the 4 lines for createTimer are different

package com.loonycorn;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import java.util.Date;

@Stateless
public class TimerBean implements TimerInterfaceRemote {

    @Resource
    private SessionContext context;

    public void createTimer(Date date) {
        context.getTimerService().createTimer(date,
                                    String.format("Scheduled task set to run at ", date));
    }

    @Timeout
    public void timeOutHandler(Timer timer) {

        System.out.println("Timer service has begun at: [" + (new Date()).toString() + "]" );
        System.out.println("Timer service message : " + timer.getInfo());
        timer.cancel();
    }
}

// Deploy the bean in Wildfly

mvn clean
mvn wildfly:undeploy

mvn install wildfly:deploy


// Client class

// In the TimerClient, add this import
import java.text.SimpleDateFormat;

// Just replace the call to 
timerBean.createTimer(20000);

// with these lines - note to change the date and time so that it runs in the near future for you
SimpleDateFormat formatter =
                new SimpleDateFormat("MM/dd/yyyy 'at' HH:mm");
        Date date = formatter.parse("05/27/2021 at 10:47");
        timerBean.createTimer(date);


// Here is the full class for reference

package com.loonycorn;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

public class TimerClient {
    public static void main(String[] args) throws Exception {

        invokeStatelessBean();

    }

    private static void invokeStatelessBean() throws NamingException, ParseException {

        TimerInterfaceRemote timerBean = lookupRemoteStatelessCounter();
        System.out.println("Obtained a remote timer bean for invocation");

        System.out.println("About to submit a timed job at: [" + (new Date()).toString() + "]" );

        SimpleDateFormat formatter =
                new SimpleDateFormat("MM/dd/yyyy 'at' HH:mm");
        Date date = formatter.parse("05/27/2021 at 10:47");
        timerBean.createTimer(date);

        System.out.println("Client program has ended at: [" + (new Date()).toString() + "]");

    }

    private static TimerInterfaceRemote lookupRemoteStatelessCounter() throws NamingException {

        final Hashtable<String, String> jndiProperties = new Hashtable<>();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");

        final Context context = new InitialContext(jndiProperties);

        final String appName = "";
        final String moduleName = "ejb-intro-1.0";
        final String distinctName = "";
        final String beanName = TimerBean.class.getSimpleName();
        final String viewClassName = TimerInterfaceRemote.class.getName();

        return (TimerInterfaceRemote) context.lookup("ejb:" + appName + "/" 
                                                    + moduleName + "/" + distinctName + "/" 
                                                    + beanName + "!" + viewClassName);
    }
}




// Schedule a timer

// At the server side, modify the Timer interface to accept a ScheduleExpression

package com.loonycorn;

import javax.ejb.Remote;
import javax.ejb.ScheduleExpression;

@Remote
public interface TimerInterfaceRemote {

    public void createCalendarTimer(ScheduleExpression schedule);
}


// Modify the TimerBean to accept a ScheduleExpression when creating a timer
// This will set the timer service to run exactly 3 times for a given ScheduleExpression


Class: TimerBean.class

package com.loonycorn;

import javax.annotation.Resource;
import javax.ejb.*;
import java.util.Date;

@Stateless
public class TimerBean implements TimerInterfaceRemote {

    int counter = 1;
    static final int limit = 3;

    @Resource
    private SessionContext context;

    public void createCalendarTimer(ScheduleExpression schedule) {
        context.getTimerService().createCalendarTimer(schedule,
                                    new TimerConfig("Timer scheduled.",false));
    }

    @Timeout
    public void timeOutHandler(Timer timer) {

        System.out.println("Timer service has begun at: [" + (new Date()).toString() + "]" );
        System.out.println("Timer service message : " + timer.getInfo());
        System.out.println("Counter: " + counter);

        counter++;

        if(counter > limit) {
            System.out.println("Canceling the timer service at: [" + (new Date()).toString() + "]" );
            timer.cancel();
        }
    }
}





// Deploy the bean in Wildfly

mvn clean
mvn wildfly:undeploy

mvn install wildfly:deploy




Client class 

// In the TimerClient, add this import
import javax.ejb.ScheduleExpression;

// Just replace these lines
		SimpleDateFormat formatter =
                new SimpleDateFormat("MM/dd/yyyy 'at' HH:mm");
        Date date = formatter.parse("05/27/2021 at 10:47");
        timerBean.createTimer(date);

// with these lines - note to change the schedule details so that it runs in the near future for you
		ScheduleExpression schedule = new ScheduleExpression();
        schedule.dayOfWeek("Thu");
        schedule.hour("11-12, 18");
        schedule.minute("*/2");
        schedule.second("30");
        timerBean.createCalendarTimer(schedule);


// Here is the full class for reference


package com.loonycorn;

import javax.ejb.ScheduleExpression;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

public class TimerClient {
    public static void main(String[] args) throws Exception {

        invokeStatelessBean();

    }

    private static void invokeStatelessBean() throws NamingException, ParseException {

        TimerInterfaceRemote timerBean = lookupRemoteStatelessCounter();
        System.out.println("Obtained a remote timer bean for invocation");

        System.out.println("About to submit a timed job at: [" + (new Date()).toString() + "]" );

        ScheduleExpression schedule = new ScheduleExpression();
        schedule.dayOfWeek("Thu");
        schedule.hour("11-12, 18");
        schedule.minute("*/2");
        schedule.second("30");
        timerBean.createCalendarTimer(schedule);

        System.out.println("Client program has ended at: [" + (new Date()).toString() + "]");

    }

    private static TimerInterfaceRemote lookupRemoteStatelessCounter() throws NamingException {

        final Hashtable<String, String> jndiProperties = new Hashtable<>();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");

        final Context context = new InitialContext(jndiProperties);

        final String appName = "";
        final String moduleName = "ejb-intro-1.0";
        final String distinctName = "";
        final String beanName = TimerBean.class.getSimpleName();
        final String viewClassName = TimerInterfaceRemote.class.getName();

        return (TimerInterfaceRemote) context.lookup("ejb:" + appName + "/" 
                                                    + moduleName + "/" + distinctName + "/" 
                                                    + beanName + "!" + viewClassName);
    }
}

// Run the program - the service gets scheduled, and is set to run at the 30-second mark every 2 minutes
// It will also run 3 times before being canceled, so it could take 6-8 minutes for all 3 runs

// Head to the Wildfly logs - remain there for 6-8 minutes until all runs have completed
// This is what the output might look like:
11:26:30,006 INFO  [stdout] (EJB default - 1) Timer service has begun at: [Thu May 27 11:26:30 IST 2021]
11:26:30,007 INFO  [stdout] (EJB default - 1) Timer service message : Timer scheduled.
11:26:30,013 INFO  [stdout] (EJB default - 1) Counter: 1
11:28:30,008 INFO  [stdout] (EJB default - 1) Timer service has begun at: [Thu May 27 11:28:30 IST 2021]
11:28:30,009 INFO  [stdout] (EJB default - 1) Timer service message : Timer scheduled.
11:28:30,010 INFO  [stdout] (EJB default - 1) Counter: 2
11:30:30,010 INFO  [stdout] (EJB default - 1) Timer service has begun at: [Thu May 27 11:30:30 IST 2021]
11:30:30,012 INFO  [stdout] (EJB default - 1) Timer service message : Timer scheduled.
11:30:30,012 INFO  [stdout] (EJB default - 1) Counter: 3
11:30:30,014 INFO  [stdout] (EJB default - 1) Canceling the timer service at: [Thu May 27 11:30:30 IST 2021]





# Automatic timers these need not be invoked by a client. 

// Modify the TimerBean on the server side
// In the @Schedule annotation, note the dayOfMonth - set this to a valid value so that it runs 
// in the near future for you (e.g. "3rd Wed", "1st Mon" etc.)

package com.loonycorn;

import javax.annotation.Resource;
import javax.ejb.*;
import java.util.Date;

@Stateless
public class TimerBean{

    int counter = 1;
    static final int limit = 3;

    @Schedule(minute = "*/1", hour = "*", dayOfMonth = "4th Thu", second = "10, 40",
                persistent = false, info = "Scheduled job")
    public void timeOutHandler(Timer timer) {

        System.out.println("Timer service has begun at: [" + (new Date()).toString() + "]" );
        System.out.println("Timer service message : " + timer.getInfo());
        System.out.println("Counter: " + counter);

        counter++;

        if(counter > limit) {
            System.out.println("Canceling the timer service at: [" + (new Date()).toString() + "]" );
            timer.cancel();
        }
    }
}


// Deploy the bean
mvn install wildfly:deploy

// The job runs 3 times on the 20 and 40 second mark and then terminates
// Check the Wildfly logs, an output like this should be visible


