EJBPersistence.java
--------------------

// Download and install mysql and workbench. 
// After installation create a new DB schema in the workbench using this command 
		
		CREATE DATABASE companydb; 

		USE companydb;

		// There will be no tables in the DB except potentially some system-generated ones
		SHOW TABLES;


// Download and Extract MySQL Connector/J

		// Download link: https://dev.mysql.com/downloads/connector/j/ 
		// choose and select the operating system based on individuals device. 
		// current version at recording time is Connector/J 8.0.25

		// the downloads page is default for a windows download

		// for Mac choose the Platform independent option.


// Setup MySQL JDBC Driver as a Wildfly Module.

// 	we need to first create a module package directory (com/mysql/main) 
// 	for MySQL JDBC driver under $JBOSS_HOME/modules/system/layers/base 
// 	(alternatively, $WILDFLY_HOME/modules/system/layers/base)

// Bring up your shell/terminal and cd into this directory

cd $JBOSS_HOME/modules/system/layers/base

// View the contents of the directory
ls -n
	
// 	Create the com/mysql/main directory - you may need to supply your admin credentials
sudo mkdir -p $JBOSS_HOME/modules/system/layers/base/com/mysql/main

// cd into the directory
cd $JBOSS_HOME/modules/system/layers/base/com/mysql/main


// 	Inside the mysql-connector-java-8.0.25.tar.gz/ directory that we extracted, there is a jar file by the name mysql-connector-java-8.0.25.jar. This jar file contains the required classes for the MySQL JDBC driver.

// 	Copy the jar inside the mysql-connector-java-8.0.25.tar.gz/ directory that we extracted 
// by the name mysql-connector-java-8.0.25.jar to $JBOSS_HOME/modules/system/layers/base/com/mysql/main/ 		directory.

// 	Create a module descriptor in the same directory

$ sudo vim module.xml

	paste the following to the newly created file

<module xmlns="urn:jboss:module:1.5" name="com.mysql">
	<resources>
	    <resource-root path="mysql-connector-java-8.0.25.jar" />
	</resources>
	<dependencies>
	    <module name="javax.api"/>
	    <module name="javax.transaction.api"/>
	</dependencies>
</module>

 :wq to save and exit the file.

// start/restart the Wildfly server so that the MySQL module is available.
// From a different terminal/shell window
cd $JBOSS_HOME/bin

./standalone.sh --server-config=standalone-full.xml



# Step : Configure MySQL JDBC Driver

	open the management console of wildfly at localhost:9990/ 

	go to Configuration -> Subsystems -> Datasources & Drivers -> JDBC Drivers

		Click on the ‘+’ icon and enter the following details.

			Driver Name : mysql

			Driver Module Name : com.mysql

			Driver Class Name : com.mysql.cj.jdbc.Driver
			
			Driver Datasource Class Name :

			Driver XA Datasource Class Name : com.mysql.cj.jdbc.MysqlXADataSource


	Add and the JDBC Driver should be available. 




# Step : set up a data source and to verify that our MySQL JDBC driver is properly working.

	go to Configuration -> Subsystems -> Datasources & Drivers -> Datasources

	Click on the ‘+’ icon and create a new non-XA data source.

	let the values be default. Only change the Connection URL to set the database one is working on and the port number if different port is being used.

		JNDI Name : java:/MySqlDS
		Driver Name : mysql
		Connection URL : jdbc:mysql://localhost:3306/companydb  // only change the name of the db one is connecting to. 

		when prompted give the username and password used while setting up the MySql in Step 1. e.g.

			username: root
			password: password123 (or whatever credentials you have set for MySQL)

Click on Test Connection button and if everything is configured properly you should get 
		
		test connection successful.


# Step 6: Return to IntelliJ, create a new Maven project using the maven-archetype-quickstart archetype


// Once the project is created, remove the test directory and the App.java source in the main folder
// Replace the POM file contents with this

<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.loonycorn</groupId>
    <artifactId>EJBPersistence</artifactId>
    <version>1.0</version>
    <packaging>ejb</packaging>
    <name>EJBPersistence</name>


    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <endorsed.dir>${project.build.directory}/endorsed</endorsed.dir>
        <maven.compiler.source>9</maven.compiler.source>
        <maven.compiler.target>9</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-core</artifactId>
            <version>5.4.31.Final</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.25</version>
        </dependency>
        <dependency>
            <groupId>javax.xml.bind</groupId>
            <artifactId>jaxb-api</artifactId>
            <version>2.3.1</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.eclipse.persistence/org.eclipse.persistence.jpa -->
        <dependency>
            <groupId>javax</groupId>
            <artifactId>javaee-api</artifactId>
            <version>8.0.1</version>
            <scope>provided</scope>
        </dependency>

    </dependencies>

    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.wildfly.plugins</groupId>
                    <artifactId>wildfly-maven-plugin</artifactId>
                    <version>2.0.1.Final</version>
                    <configuration>
                        <hostname>127.0.0.1</hostname>
                        <port>9990</port>
                    </configuration>
                </plugin>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-ejb-plugin</artifactId>
                    <version>3.1.0</version>
                    <configuration>
                        <ejbVersion>3.2</ejbVersion>
                    </configuration>
                </plugin>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-dependency-plugin</artifactId>
                    <version>3.1.2</version>
                    <executions>
                        <execution>
                            <phase>validate</phase>
                            <goals>
                                <goal>copy</goal>
                            </goals>
                            <configuration>
                                <outputDirectory>${endorsed.dir}</outputDirectory>
                                <silent>true</silent>
                                <artifactItems>
                                    <artifactItem>
                                        <groupId>javax</groupId>
                                        <artifactId>javaee-endorsed-api</artifactId>
                                        <version>7.0</version>
                                        <type>jar</type>
                                    </artifactItem>
                                </artifactItems>
                            </configuration>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
</project>

# Step 7: Create a resources folder in src/main
			
			add a META-INF folder under resources.
			create a new persistence.xml file and add the following code 

			<?xml version="1.0" encoding="UTF-8"?>
			<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence"
			             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="2.2"
			             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence
			             http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd">
			    <!-- Define persistence unit -->
			    <persistence-unit name="companyPU">
			        <provider>org.hibernate.ejb.HibernatePersistence</provider>
			        <jta-data-source>java:/MySqlDS</jta-data-source>
			        <class>org.example.Book</class>
			        <properties>
			            <property name="javax.persistence.schema-generation.database.action"
			                      value="drop-and-create"/>
			            <property name="hibernate.show_sql"
			                      value="true"/>
			            <property name="hibernate.format_sql" value="true"/>
			        </properties>
			    </persistence-unit>
			</persistence>




# Step : Create the classes to be deployed in EJB container.

Class Employee.java (create this in the package com.loonycorn)

package com.loonycorn;

import javax.persistence.Entity;

import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Employee implements Serializable {

    @Id
    private int id;

    private String name;
    private String position;
    private float salary;

    public Employee(){

    }

    public Employee(int id, String name, String position, float salary){
        this.id = id;
        this.name = name;
        this.position = position;
        this.salary = salary;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public float getSalary() {
        return salary;
    }
}



Interface: EJBPersistenceRemote.java (create in the package com.loonycorn)

package com.loonycorn;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface EJBPersistenceRemote {

    void addEmployee(Employee employee);

    List<Employee> getEmployees();
}



Class: EJBPersistenceBean.java

package com.loonycorn;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class EJBPersistenceBean implements EJBPersistenceRemote{
    public EJBPersistenceBean() {
    }

    @PersistenceContext(unitName="companyPU")
    private EntityManager entityManager;

    public void addEmployee(Employee employee) {

        entityManager.persist(employee);

    }

    @SuppressWarnings("unchecked")
    public List<Employee> getEmployees() {
        return entityManager.createQuery("From Employee").getResultList();
    }
}



// Deploy to server

	mvn install wildfly:deploy


// Check out the Wildfly server logs
// You should see the JNDI bindings with a message like:
JNDI bindings for session bean named 'EJBPersistenceBean' in deployment unit 'deployment "EJBPersistence-1.0.jar"'...

// followed by the JNDI bindings. Right after that, there will be a message along the lines of 
14:48:32,569 INFO  [org.jipijapa] (MSC service thread 1-8) JIPIORMV53020253: Second level cache enabled for EJBPersistence-1.0.jar#companyPU
14:48:32,737 INFO  [org.jboss.as.jpa] (ServerService Thread Pool -- 86) WFLYJPA0010: Starting Persistence Unit (phase 1 of 2) Service 'EJBPersistence-1.0.jar#companyPU'

// This will be followed by a number of Hibernate messages including
drop table if exists Employee ... create table Employee ...


// Head to MySQL Workbench to confirm the table creation - this should list Employee as a table
SHOW TABLES;

// View the CREATE TABLE statement for this Employee table
SHOW CREATE TABLE Employee;

// The output will have 2 columns - Table and Create Table
// Under Create Table, you should see the start of a CREATE TABLE statement
CREATE TABLE `Employee`...

// Right click on the cell containing this CREATE TABLE `Employee` statement and choose "Open Value in Viewer"
// You will now see the entire CREATE TABLE statement

// Check the table contents - it should be empty
SELECT * FROM Employee;



Client Side: Use the same client project as in the previous demos 

// Add the newly deployed EJBPersistence-1.0.jar to the classpath via the Project Structure

// Add this dependency to the client pom.xml

	<dependency>
      <groupId>org.hibernate</groupId>
      <artifactId>hibernate-core</artifactId>
      <version>5.4.31.Final</version>
    </dependency>


// For reference, here is the full pom.xml for the client app


<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.loonycorn</groupId>
  <artifactId>ejb-client</artifactId>
  <version>1.0-SNAPSHOT</version>

  <name>ejb-client</name>
  <url>http://www.loonycorn.com</url>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>9</maven.compiler.source>
    <maven.compiler.target>9</maven.compiler.target>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.wildfly</groupId>
      <artifactId>wildfly-client-all</artifactId>
      <version>23.0.1.Final</version>
    </dependency>
    <dependency>
      <groupId>org.hibernate</groupId>
      <artifactId>hibernate-core</artifactId>
      <version>5.4.31.Final</version>
    </dependency>
  </dependencies>

  <build>
    <pluginManagement><!-- lock down plugins versions to avoid using Maven defaults (may be moved to parent pom) -->
      <plugins>

      </plugins>
    </pluginManagement>
  </build>
</project>




Class: PersistenceClient (create in the com.loonycorn package)
// This will add 2 employees to the table

package com.loonycorn;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;
import java.util.List;

public class PersistenceClient {

    public static void main(String[] args) throws NamingException {

        final Hashtable<String, String> jndiProperties = new Hashtable<>();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");

        final Context context = new InitialContext(jndiProperties);

        final String appName = "";
        final String moduleName = "EJBPersistence-1.0";
        final String distinctName = "";
        final String beanName = EJBPersistenceBean.class.getSimpleName();
        final String viewClassName = EJBPersistenceRemote.class.getName();

        String jndi = "ejb:" + appName + "/"
                        + moduleName + "/" + distinctName + "/"
                        + beanName + "!" + viewClassName;

        EJBPersistenceRemote employeeBean = (EJBPersistenceRemote) context.lookup(jndi);

        Employee renee = new Employee(1234, "Renee Schneider",
                                "Devops Engineer", 100000);
        employeeBean.addEmployee(renee);

        Employee matt = new Employee(1255, "Matt Foster",
                "Systems Engineer", 90000);
        employeeBean.addEmployee(matt);

        List<Employee> employeeList = employeeBean.getEmployees();

        System.out.println("Employees(s) entered so far: " + employeeList.size());

        int i = 0;

        for (Employee employee:employeeList) {
            System.out.println("\n" + (i+1) + ". Name: " + employee.getName());
            System.out.println("Position: " + employee.getPosition());
            System.out.println("Salary: $" + employee.getSalary());
            i++;
        }
    }
}


// Run the program - the two employees should be returned


// Confirm that these employees have indeed been added to the table.
// Head to MySQL Workbench and run this query - the 2 employees should show up
SELECT * FROM Employee;

// Remove the table in preparation for the next demo
DROP TABLE Employee;




