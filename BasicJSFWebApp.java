// eg:ecommerce 
// Right click html file and click on 'view page source'

// CRUD Operations with JSF
// https://developer.okta.com/blog/2019/09/27/build-crud-app-jsf



/* =============

    @ManagedBean is deprecated: 
    https://stackoverflow.com/questions/4347374/backing-beans-managedbean-or-cdi-beans-named

    Using CDI with JSF requires TomEE or some extra installations with Tomcat:
    https://balusc.omnifaces.org/2013/10/how-to-install-cdi-in-tomcat.html

*/

/* ########################################
            Installation and Set Up
######################################## */

// First go to terminal and confirm java and maven versions
// In this course we use java 11 (the latest one supported by TomEE Web Server)

java -version

// Download maven from below link

https://maven.apache.org/download.cgi

// Then upzip and add this in Library folder
// Then come to terminal and add following command

nano .bash_profile
// Then bash profile will open and paste the following command

export MAVEN="/Library/apache-maven-3.8.1"
export PATH="$MAVEN/bin:$PATH"

// Then quit from the bash profile and run following command in terminal

source .bash_profile

// Then check the maven version once more

mvn --version

// The Maven version and the Java version it points to are displayed


/* Download TomEE version 9
    Download page: https://tomee.apache.org/download.html

    Make sure to download TomEE Webprofile ZIP for TomEE 9

    Unpack TomEE in a directory on your file system (e.g. ~/tools/servers/)

    The TomEE folder will be:
    ~/tools/servers/apache-tomee-webprofile-9.0.0-M7

*/

// Ensure there is nothing running on port 8080
lsof -nP -iTCP -sTCP:LISTEN | grep 8080

// If there is something running on the port, the PID will show up after running the above command
// Copy that PID and run this:
kill <pid>

// You may also need to check for port 8005 and kill any running process
lsof -nP -iTCP -sTCP:LISTEN | grep 8080

// cd into the TomEE bin folder
cd ~/tools/servers/apache-tomee-webprofile-9.0.0-M7/bin

// View the contents
ls -n

// Make sure you have permissions to run the TomEE scripts
chmod +x startup.sh 

chmod +x catalina.sh

// Run TomEE
./startup.sh && tail -f ../logs/catalina.out

// Navigate to this URL on your browser to confirm TomEE is running
    // http://localhost:8080/

// Leave the terminal/shell window running as we'll need to monitor it later


/* ########################################
        Building a Basic JSF Web App
######################################## */


// Let's start making the maven project

// Go to terminal and write following command to go to the projects folder
// This assumes a workspace directory called ~/projects/skillsoft already exists

cd ~/projects/skillsoft

// Next create a maven project called jsf-project

mvn archetype:generate \
-DgroupId=com.skillsoft \
-DartifactId=jsf-project \
-DarchetypeArtifactId=maven-archetype-webapp \
-DinteractiveMode=false

// Next list out the folder then we can see the new project which we have created

ls -l

// Now the project has created, let's open intellij and open the project there
// Go to file -> project structure -> Modules
// In the sources -> language level -> Change the version to 11
// Now we're all set to build a JSF App


// Add dependencies in pom.xml (Refer pom.xml)
// You may need to reload your project using the Maven reload/refresh option

// We need to be careful when defining the POM and ensure there is only a single JSF 
// implementation which can be applied
// https://fix.code-error.com/running-a-simple-jsf-webapp-on-tomee-9-0-plus-cannot-start-properly-due-to-undefined-component-type-jakarta-faces-viewroot/

// In the src/main folder of the project, create a subfolder called java
// Within the java folder, create a package called com.skillsoft

// Add HelloBean.java within the com.skillsoft package (Refer HelloBean.java)
// Add index.xhtml inside the main/webapp directory (Refer index.xhtml)

// A quick guide to XHTML: https://www.javatpoint.com/what-is-xhtml


// Add web.xml within the webapp/WEB-INF folder (Refer web_v1.xml)
// Run the code

// For that we have to create the war file

// Bring up the terminal from your IDE (e.g. IntelliJ) and build and package your app
mvn clean package

// Copy the war file from target folder to Apache TomEE webapps
cp target/jsf-project.war ~/tools/servers/apache-tomee-webprofile-9.0.0-M7/webapps/

// Bring up the Apache TomEE terminal/shell window
// You should see messages such as this one
30-Jun-2021 14:59:23.279 INFO [Catalina-utility-2] org.apache.myfaces.webapp.StartupServletContextListener.contextInitialized MyFaces Core has started, it took [840] ms.
30-Jun-2021 14:59:23.331 INFO [Catalina-utility-2] jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke Deployment of web application archive [/Users/kishan/tools/tomcat/apache-tomee-webprofile-9.0.0-M7/webapps/jsf-project.war] has finished in [1,587] ms

// Access index.xhtml from the browser
http://localhost:8080/jsf-project/index.xhtml

// Then you will be able to see only the xhtml code

// To get the message we have to make change in the web.xml (Refer web_v2.xml)
  <welcome-file-list>
    <welcome-file>index.xhtml</welcome-file>
  </welcome-file-list>

// Package and deploy the app again
mvn clean package

cp target/jsf-project.war ~/tools/servers/apache-tomee-webprofile-9.0.0-M7/webapps/

// Access index.xhtml from the browser - 
http://localhost:8080/jsf-project/

// The same Hello World message appears


// Right-click on the web page and select View Page Source
// You get an idea of how the page is constructed
// You may need to select the Line Wrap option to view everything clearly



/* ########################################
        Building an App while exploring JSF tags
######################################## */

// In the com.skillsoft package, create a new source file called MobilePhoneRepo.java
// Refer to MobilePhoneRepo.java for the source code

// In the webapp folder, created a resources sub-folder
// Inside resources, create two sub-folders - css and images

// Inside the CSS folder, create in styles.css

// Inside the images folder, copy over phones_home.jpg

// Edit the index.xhtml page and update with the new contents

/* The XHTML covers a few useful JSF tags:
    - CSS links
    - Image links
    - accessing primitive from a bean
    - accessing composite data from a bean and rendering in a table

*/

// Package and deploy the app
mvn clean package

cp target/jsf-project.war ~/tools/servers/apache-tomee-webprofile-9.0.0-M7/webapps/


// Navigate to http://localhost:8080/jsf-project/ in the browser

// The content is displayed as expected
// Click on the link to the image to navigate to the pexels.com page

// Right-click on the web page and select View Page Source
// You get an idea of how the page is constructed
// You may need to select the Line Wrap option to view everything clearly



/* ########################################
        Page Navigation
######################################## */

// We first explore implicit navigation

/* In the webapps folder, add two new files and paste in the code:
    - about.xhtml
    - phones.xhtml

    Modify index.html so that it contains a form and command buttons to navigate (refer v1)
*/

// Package and deploy the app
mvn clean package

cp target/jsf-project.war ~/tools/servers/apache-tomee-webprofile-9.0.0-M7/webapps/


// Navigate to http://localhost:8080/jsf-project/ in the browser

// Check the page source

// Click on each button to confirm that the about and phones page load
// Use the Back button in the browser to navigate back to the home page


// Building the same app with user-defined navigation set in a faces-config.xml file

// Modify index.xhtml (refer to v2) - the only detail which changes from the previous version
// is the action attribute for each commandButton
// To map each action to a page, we create a faces-config.xml file in the WEB-INF directory

/* 
    faces-config.xml was required in older versions of JSF but not from JSF 2+
    While not needed, it can still be useful:
    https://stackoverflow.com/questions/7583038/what-is-the-use-of-faces-config-xml-in-jsf-2
*/

// Package and deploy the app
mvn clean package

cp target/jsf-project.war ~/tools/servers/apache-tomee-webprofile-9.0.0-M7/webapps/

// Navigate to http://localhost:8080/jsf-project/ in the browser

// Check the page source

// Click on each button to confirm that the about and phones page load
// Use the Back button in the browser to navigate back to the home page



/* ########################################
        Using a ValueChangeListener
######################################## */

// Using valueChangeListener attribute

// Delete the faces-config.xml file created earlier

// Modify index.xhtml so that the action attributes for the command buttons use implicit navigation

// Modify MobilePhoneRepo.java in the com.skillsoft package
/* The changes are:
    - There is a new import statement for ValueChangeEvent
    - There is a new field called selectedPhone
    - There 3 new methods - getter and setter for selectedPhone plus selectedPhoneChanged
*/

// Modify phones.xhtml to use a drop-down menu based on the phones in the phone repo
// This selectedMenu is within a form - this is needed for data to be submitted

// Package and deploy the app again
mvn clean package

cp target/jsf-project.war ~/tools/servers/apache-tomee-webprofile-9.0.0-M7/webapps/


// Navigate to http://localhost:8080/jsf-project/ in the browser and click on View Phones
// Select various phones from the select (drop-down) menu
// The price of the phone gets updated each time

// Right-click on the web page and select View Page Source
// You get an idea of how the page is constructed
// You may need to select the Line Wrap option to view everything clearly



/* ########################################
        Using an ActionListener
######################################## */

/*
    Modify MobilePhoneRepo.java to include a viewDate field. The changes are:
        - java.util.Date is added as an import
        - there is a new viewDate member variable
        - 3 new methods are added - getter and setter for viewDate plus an updateViewDate method
*/


// Modify index.xhtml to include a 3rd button. Essentially, these are the lines added

                <h:commandButton value="Phone Table"
                                 action="phonetable.xhtml"
                                 actionListener="#{PhoneRepo.updateViewDate}">
                </h:commandButton>

// So the action is performed by calling a method before the new page is loaded

// Create a new phonetable.xhtml file in the webapps folder - this accesses the viewDate


// Package and deploy the app again
mvn clean package

cp target/jsf-project.war ~/tools/servers/apache-tomee-webprofile-9.0.0-M7/webapps/


// Navigate to http://localhost:8080/jsf-project/ in the browser
// Right-click on the web page and select View Page Source
// You get an idea of how the page is constructed
// You may need to select the Line Wrap option to view everything clearly

// Click on the Phone Table button and you will see the viewDate accessed and printed

// Navigate to the home page by hitting the back button
// Click on Phone Table again - a new value of viewDate is created and published





/* ########################################
        Form Validation
######################################## */

/* Create a new Customer.java file in com.skillsoft
    This includes fields, standard getters and setters, plus two other methods:
        - an email validator
        - a method which simulates the storing of data
    This bean is also SessionScoped since we'll be using it to store data which will
    be accessed across pages (request/response cycles)

*/

/* Inside webapps, create the following files:
    - customer.xhtml
    - confirm.xhtml
    - regsuccess.xhtml

    customer.xhtml is a registration form with built-in validators
    Upon submitting the form, the user will be directed to a confirm.xhtml page
    Upon confirming, the regsuccess.xhtml page should load

*/

// Modify index.xhtml to include a button to the customer registration page


// Package and deploy the app again
mvn clean package

cp target/jsf-project.war ~/tools/servers/apache-tomee-webprofile-9.0.0-M7/webapps/


// Navigate to http://localhost:8080/jsf-project/ in the browser
// Right click and View Page Source

/* Fill in some details, but make sure many of the validators will raise an exceptions
    - put in just one character for the name
    - do not make a selection for the gender
    - use the wrong format for the date
    - do not include an @ in the email address
    - specify a phone number containing fewer than 10 characters

    When the submit button is hit, a whole bunch of validation errors pop up

    Right-click on the page and View Page Source

    Fill in valid details in the form. E.g.
        Alice
        Female
        04-24-1992
        Single
        101 Main Street
        alice@loonycorn.com
        +15550002233

    Submit the form - you should be taken to the confirm page

    Confirm the details. The success page should load.
    Keep in mind that this information is accessible here only because the bean is SessionScoped
    To make data accessible across pages using a RequestScoped bean, we'll need to use
    something called a SessionMap

    Head to the TomEE server logs. You should see a message like:

    WARNING [http-nio-8080-exec-35] org.apache.myfaces.lifecycle.RenderResponseExecutor.execute There are some unhandled FacesMessages, this means not every FacesMessage had a chance to be rendered.
    These unhandled FacesMessages are: 
    - Registration information has been stored.
*/



/* ########################################
        Database setup
######################################## */


// install mysql
// install workbench

// Add following dependancy to pom.xml

    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>8.0.25</version>
    </dependency>

// Bring up a database client (e.g. MySQLWorkbench) and run the following queries

CREATE DATABASE Products;

USE Products;

CREATE TABLE phones (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    brandName VARCHAR(100),
    productName VARCHAR(50),
    os VARCHAR(20),
    storageCapacity INT,
    ram INT
);

SELECT * FROM phones;

INSERT INTO phones (brandName, productName, os, storageCapacity, ram)
    VALUES ('Zoflina', 'Z09', 'Android', 128, 4),
            ('Zoflina', 'Z11', 'Android', 256, 6),
            ('Diallonic', 'DS101', 'Symbian', 256, 8),
            ('Diallonic', 'DS102', 'Symbian', 256, 8),
            ('Diallonic', 'DL104', 'Android', 128, 4);

SELECT * FROM phones;

// The first goal is to view all of this phone data in our JSF app




/* ########################################
        Viewing DB Data
######################################## */

// Create a new class in the com.skillsoft package called MobilePhone

// To access instances of MobilePhone, we create a Bean
// Create a new class called MobilePhoneBean in com.skillsoft

// To view the phones in the database by invoking the getPhones method in MobilePhoneBean,
// create a view.xhtml file

// Modify index.xhtml - it now only includes 2 buttons

// Package and deploy the app again
mvn clean package

cp target/jsf-project.war ~/tools/servers/apache-tomee-webprofile-9.0.0-M7/webapps/

// Navigate to http://localhost:8080/jsf-project/ in the browser

// Click on View Phones
SELECT * FROM phones;

// The newly added phone should appear in the output



/* ########################################
        Add a New Row to the Database
######################################## */

/* Modify the MobilePhoneBean so that it now includes:
    - fields for the creation of a new phone entry in the database
    - getters and setters for these new fields
    - a save() method which carries out an INSERT query against the phone table

*/

// Create a new create.xhtml in the webapps folder - this is similar to the 
// registration form we created earlier but calls save() in the MobilePhoneBean upon submit

// Package and deploy the app again
mvn clean package

cp target/jsf-project.war ~/tools/servers/apache-tomee-webprofile-9.0.0-M7/webapps/

// Navigate to http://localhost:8080/jsf-project/ in the browser

// Click on Create Record - the create page should pop up

/*
    Fill in these details to create a new phone:
        Brand name: Sonical
        Product name: X99
        OS: Sonical
        Storage: 256
        RAM: 8

    Hit submit - you should be redirected to the views page and the new phone should appear

*/

// For a sanity check, head to the DB client (e.g. MySQL Workbench) and run this
SELECT * FROM phones;





/* ########################################
        Database Update
######################################## */


/*
    Modify the MobilePhoneBean. The changes are:
        - There is a new SessionMap instance (to transfer data between requests)
        - There are 2 new methods:
            - edit() creates a new MobilePhone instance and adds it to the session map
                and then redirects to an edit page
            - update() accepts the edited information and carries out a DB update
*/

// Modify view.xhtml to include an update and delete column for each phone in the data table
// These columns include delete and update buttons (delete will be implemented later)

// Create an edit.xhtml to edit the user

// Package and deploy the app again
mvn clean package

cp target/jsf-project.war ~/tools/servers/apache-tomee-webprofile-9.0.0-M7/webapps/

// Navigate to http://localhost:8080/jsf-project/ in the browser

/* Go to View Phones and edit one of the phones (let's say the newly added Sonical X99)
    - Change the OS from Sonical to Android
    - Change the RAM from 8 to 6
    - Hit Submit

    This will take you back to the View page where the updated info can be seen
*/

// For a sanity check, head to the DB client (e.g. MySQL Workbench) and run this
SELECT * FROM phones;





/* ########################################
        Database Delete
######################################## */


// Modify MobilePhoneBean to include a new delete method

mvn clean package

cp target/jsf-project.war ~/tools/servers/apache-tomee-webprofile-9.0.0-M7/webapps/

// Navigate to http://localhost:8080/jsf-project/view.xhtml in the browser

// Click on the delete button for one of the phones - it is gone
// Delete one more phone

// For a sanity check, head to the DB client (e.g. MySQL Workbench) and run this
SELECT * FROM phones;








