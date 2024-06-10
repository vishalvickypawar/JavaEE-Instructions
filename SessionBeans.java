SessionBeans.java
------------------

1. Stateless Session Beans


// Update the BeanIntroInterface

Interface BeanIntroInterface.java

	import javax.ejb.Remote;

	@Remote
	public interface BeanIntroInterface {
	    String getMessage();
	    void setName(String name);
	}

// Modify the Bean implementation 

Class BeanIntroImplementation.java is as follows:

	import javax.annotation.Resource;
	import javax.ejb.SessionContext;
	import javax.ejb.Stateless;


	@Stateless
	public class BeanIntroImplementation implements BeanIntroInterface{

	    @Resource
	    private SessionContext context;

	    public String name;

	    @Override
	    public String getMessage() {
	        return String.format("Welcome %s, to the world of EJB!", name );
	    }

	    @Override
	    public void setName(String givenName) {
	        name = givenName;
	    }

	}

// Deploy the modified bean to Wildfly

mvn install wildfly:deploy




// On the client side:

// Add the re-built jar to the library in the project structure 

// In the same com.loonycorn package where the AccessClient was created, 
// create a new client source file called StatelessClientOne


// Class StatelessClientOne.java

package com.loonycorn;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Hashtable;

public class StatelessClientOne {

    public static void main(String[] args) throws Exception {

        invokeStatelessBean();
    }

    private static void invokeStatelessBean() throws Exception {

        BeanIntroInterface statelessBean = lookupRemoteStatelessBean();
        System.out.println("Obtained a remote stateless bean for invocation");

        statelessBean.setName("Loony Client One");
        String message = statelessBean.getMessage();
        System.out.println("Returned message: " + message);

    }


    private static BeanIntroInterface lookupRemoteStatelessBean() throws Exception {

        final Hashtable<String, String> jndiProperties = new Hashtable<>();

        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");

        final Context context = new InitialContext(jndiProperties);

        final String appName = "";
        final String moduleName = "ejb-intro-1.0";
        final String distinctName = "";
        final String beanName = BeanIntroImplementation.class.getSimpleName();
        final String viewClassName = BeanIntroInterface.class.getName();

        return (BeanIntroInterface) context.lookup("ejb:" + appName + "/"
                                                    + moduleName + "/" + distinctName + "/"
                                                    + beanName + "!" + viewClassName);
    }
}

// Run the program (hit the play button next to the class name or create a new run config)
// The message with the client name is returned

// On run the Result returned by the server is 
// RESULT: Welcome Loony Client One, to the world of EJB!

// In the same com.loonycorn package create a third client class called StatelessClientTwo


// Class StatelessClientTwo.java 	

package com.loonycorn;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Hashtable;

public class StatelessClientTwo {

    public static void main(String[] args) throws Exception {

        invokeStatelessBean();
    }

    private static void invokeStatelessBean() throws Exception {

        BeanIntroInterface statelessBean = lookupRemoteStatelessBean();
        System.out.println("Obtained a remote stateless bean for invocation");

        //statelessBean.setName("Loony Client Two");

        String message = statelessBean.getMessage();
        System.out.println("Returned message: " + message);

    }


    private static BeanIntroInterface lookupRemoteStatelessBean() throws Exception {

        final Hashtable<String, String> jndiProperties = new Hashtable<>();

        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");

        final Context context = new InitialContext(jndiProperties);

        final String appName = "";
        final String moduleName = "ejb-intro-1.0";
        final String distinctName = "";
        final String beanName = BeanIntroImplementation.class.getSimpleName();
        final String viewClassName = BeanIntroInterface.class.getName();

        return (BeanIntroInterface) context.lookup("ejb:" + appName + "/"
                                                    + moduleName + "/" + distinctName + "/"
                                                    + beanName + "!" + viewClassName
                                                    + "?stateless");
    }
}


// Run the program 
// If the value of "Loony Client Two" is not set then the result will show the 
// "Loony Client One" value as set by the Client One.

// RESULT: Welcome Loony Client One, to the world of EJB!

// We can verify that client-bean instances are pooled. 
// Stateless beans do not provide concurrent client access 
// and are not unique per client.


// Uncomment the setName call in StatelessClientTwo
		statelessBean.setName("Loony Client Two"); 

// Re-run StatelessClientTwo
// RESULT: Welcome Loony Client Two, to the world of EJB!




2. Stateful Beans


// Back to the server-side app

// In the BeanIntroImplementation class, add this import
import javax.ejb.Stateful;

// Change the @Stateless to @Stateful

	@Stateful
	public class BeanIntroImplementation implements BeanIntroInterface{

	    // Rest of the class remains the same

	}

// The interface BeanIntroInterface remains the same



mvn clean 					// to clean the target folder
mvn wildfly:undeploy 		// to remove the version deployed on the server

// View the Wildfly logs. You should see an undeploy message

// Back to the IDE, deploy the modified bean
mvn install wildfly:deploy  	// to create jar and deploy it to the server

// From the Wildfly logs, you will see this JNDI binding
ejb:/ejb-intro-1.0/BeanIntroImplementation!com.loonycorn.BeanIntroInterface?stateful


// On the client side: 
//add the jar to the library in the project structure 

// In the com.loonycorn package, create a new class StatefulClientOne

package com.loonycorn;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Hashtable;

public class StatefulClientOne {

    public static void main(String[] args) throws Exception {

        invokeStatefulBean();
    }

    private static void invokeStatefulBean() throws Exception {

        BeanIntroInterface statefulRemoteBean = lookupRemoteStatefulBean();
        System.out.println("Obtained a remote stateful bean for invocation");

        statefulRemoteBean.setName("Stateful Client One");
        String message = statefulRemoteBean.getMessage();
        System.out.println("Returned message: " + message);

    }
    private static BeanIntroInterface lookupRemoteStatefulBean() throws Exception {

        final Hashtable<String, String> jndiProperties = new Hashtable<>();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");

        final Context context = new InitialContext(jndiProperties);

        final String appName = "";
        final String moduleName = "ejb-intro-1.0";
        final String distinctName = "";
        final String beanName = BeanIntroImplementation.class.getSimpleName();
        final String viewClassName = BeanIntroInterface.class.getName();

        return (BeanIntroInterface) context.lookup("ejb:" + appName + "/"
                                                    + moduleName + "/" + distinctName + "/"
                                                    + beanName + "!" + viewClassName
                                                    + "?stateful");
    }

}


// Run StatefulClientOne

// Obtained a remote stateful bean for invocation
// Returned message: Welcome Stateful Client One, to the world of EJB!


// Class StatefulClientTwo.java

package com.loonycorn;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Hashtable;

public class StatefulClientTwo {

    public static void main(String[] args) throws Exception {

        invokeStatefulBean();
    }

    private static void invokeStatefulBean() throws Exception {

        BeanIntroInterface statefulRemoteBean = lookupRemoteStatefulBean();
        System.out.println("Obtained a remote stateful bean for invocation");

        //statefulRemoteBean.setName("Stateful Client Two");
        String message = statefulRemoteBean.getMessage();
        System.out.println("Returned message: " + message);

    }
    private static BeanIntroInterface lookupRemoteStatefulBean() throws Exception {

        final Hashtable<String, String> jndiProperties = new Hashtable<>();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");

        final Context context = new InitialContext(jndiProperties);

        final String appName = "";
        final String moduleName = "ejb-intro-1.0";
        final String distinctName = "";
        final String beanName = BeanIntroImplementation.class.getSimpleName();
        final String viewClassName = BeanIntroInterface.class.getName();

        return (BeanIntroInterface) context.lookup("ejb:" + appName + "/"
									                + moduleName + "/" + distinctName + "/"
									                + beanName + "!" + viewClassName
									                + "?stateful");
    }

}



// On Run 

// Returned message: Welcome null, to the world of EJB!


// We have verified that client-bean instances are 1:1. 
// Stateless beans do not provide concurrent client access BUT ARE unique per client


//Rerun the StatefulClientTwo.java by assigning the name. 
// Uncomment the line to set name. 
		statefulRemoteBean.setName("Stateful Client Two");



// On rerun 

// Returned message: Welcome Stateful Client Two, to the world of EJB!





3. Singleton Beans

// Do not remove any of the previously created classes
// We'll just add a new singleton bean

// Reference:
// https://www.baeldung.com/java-ee-singleton-session-bean



// Interface PetShopBeanInterface

package com.loonycorn;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface PetShopBeanInterface {

    List<String> getBreeds(String petType);
    void setBreeds(String petType, List<String> breeds);
}





// Class PetShopBeanImplementation
// The clients will need to manage concurrency - no guarantee is provided by the server

package com.loonycorn;


import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class PetShopBeanImplementation implements PetShopBeanInterface{

    private final Map<String, List<String>> petTypeMap = new HashMap<String, List<String>>();

    @PostConstruct
    public void initialize() {

        List<String> dogBreeds = new ArrayList<String>();
        dogBreeds.add("Golden Retriever");
        dogBreeds.add("Labrador");
        dogBreeds.add("German Shepherd");
        dogBreeds.add("Bulldog");
        dogBreeds.add("Beagle");

        petTypeMap.put("Dog", dogBreeds);
    }

    @Override
    public List<String> getBreeds(String petType) {
        return petTypeMap.get(petType);
    }

    @Override
    public void setBreeds(String petType, List<String> breeds) {
        petTypeMap.put(petType, breeds);
    }

}

// Deploy the updated bean

mvn clean
mvn wildfly:undeploy 
mvn install wildfly:deploy 

// Head to the Wildfly logs to confirm the deployment
// You'll observe that both the old BeanIntroImplementation and the 
// PetShopBeanImplementations are now available 


/// Head to the client-side app

// in the com.loonycorn package, create a new bean called PetShopClientBean


package com.loonycorn;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class PetShopClientBean {

    public static void main(String[] args) throws NamingException {
        final Hashtable<String, String> jndiProperties = new Hashtable<>();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");

        final Context context = new InitialContext(jndiProperties);

        final String appName = "";
        final String moduleName = "ejb-intro-1.0";
        final String distinctName = "";
        final String beanName = PetShopBeanImplementation.class.getSimpleName();
        final String viewClassName = PetShopBeanInterface.class.getName();

        String jndi = "ejb:" + appName + "/"
                        + moduleName + "/" + distinctName + "/"
                        + beanName + "!" + viewClassName;

        PetShopBeanInterface petTypeBean = (PetShopBeanInterface) context.lookup(jndi);
        List<String> petTypes = petTypeBean.getBreeds("Dog");
        System.out.println("Dog breeds: " + petTypes);

        String[] catTypes = { "Siamese", "Persian", "Maine Coon", "Ragdoll", "Bengal" };

        petTypeBean.setBreeds(
                "Cat", Arrays.asList(catTypes));

        petTypes = petTypeBean.getBreeds("Cat");
        System.out.println("Cat breeds: " + petTypes);
    }
}


// Run the program - the dog breeds are returned and so are the cat breeds

// Dog breeds: [Golden Retriever, Labrador, German Shepherd, Bulldog, Beagle]
// Cat breeds: [Siamese, Persian, Maine Coon, Ragdoll, Bengal]


// Explicitly set concurrency at the server side
// Back to the server app, change the PetShopBeanImplementation class

// Note that the only changes below are:
// Change to @ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
// Add dogBreeds.add("Indie"); in the initialize method
// Add @Lock(LockType.READ) next to the getBreeds() method
// Add @Lock(LockType.WRITE) next to setBreeds()



package com.loonycorn;


import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class PetShopBeanImplementation implements PetShopBeanInterface{

    private final Map<String, List<String>> petTypeMap = new HashMap<String, List<String>>();

    @PostConstruct
    public void initialize() {

        List<String> dogBreeds = new ArrayList<String>();
        dogBreeds.add("Golden Retriever");
        dogBreeds.add("Labrador");
        dogBreeds.add("German Shepherd");
        dogBreeds.add("Bulldog");
        dogBreeds.add("Beagle");
        dogBreeds.add("Indie");

        petTypeMap.put("Dog", dogBreeds);
    }

    @Lock(LockType.READ)
    @Override
    public List<String> getBreeds(String petType) {
        return petTypeMap.get(petType);
    }

    @Lock(LockType.WRITE)
    @Override
    public void setBreeds(String petType, List<String> breeds) {
        petTypeMap.put(petType, breeds);
    }

}



// Deploy the updated bean

mvn clean
mvn wildfly:undeploy 
mvn install wildfly:deploy   

// Head to the client - no change required there

// Run the client app - the output is:
// Dog breeds: [Golden Retriever, Labrador, German Shepherd, Bulldog, Beagle, Indie]
// Cat breeds: [Siamese, Persian, Maine Coon, Ragdoll, Bengal]




