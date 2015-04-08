To run the marketplace gatling tests you will need JDK 7 and
Maven. This project has been tested with OpenJDK 1.7 and Maven 3.2.1 on CentOS
and Oracle JDK 1.7 and Maven 3.2.1 on Mac OS X.

Assuming Java and Maven are setup and on your path, the following Maven invocation will run the basic Marketplace Scenario (from the project root directory):

mvn clean test-compile gatling:execute -Dgatling.simulationClass=org.ozoneplatform.gatling.aml.simulation.MarketplaceBasicScenario -DuserCount=5000 -DscenarioUserCount=1400 -DrampPeriod=280 -DbaseUrl=http://localhost:8080/marketplace

Here is a breakdown of the command line properties:
-Dgatling.simulationClass=org.ozoneplatform.gatling.aml.simulation.MarketplaceBasicScenario -> The fully qualified class name of the scenario to run
-DuserCount=5000 -> The number of users in the Marketplace database (the scenario expects them to be in the form testUser1, testUser2, testUser3, etc)
-DscenarioUserCount=1400 -> The number of concurrent users to simulate
-DrampPeriod=280 -> The number of seconds over which all users will be logged in
-DbaseUrl=http://localhost:8080/marketplace -> The url of the marketplace instance you're testing


The AML team uses basic HTTP auth for performance testing. The configuration can be found in MPsecurityContext.xml in the following repo:

https://www.owfgoss.org/git/repos/marketplace-docker-bundle.git

Note that if you are using a version of the application built with grails 1.3.x (7.14 and older), you will want to use the file from the 7.14 tag (git checkout 7.14).

If needed, this project can be run disconnected from the internet, however you will first need all of the dependencies installed in your local maven repository. You can accomplish this
while still connected by running the following command from the project root directory:

mvn dependency:go-offline

After this is complete, all of the dependencies will be in your local maven repository ($HOME/.m2/repository by default). It should then be possible to run
the project in an environment where internet connectivity does not exist.

This project has one or more SNAPSHOT dependencies. If it is necessary to run the project while disconnected, you can greatly
speed up the dependency resolution step by running in offline mode (add --offline or -o to the maven invocation) which prevents
maven from attempting to look for updates to those dependencies.

Notes:
* Things to do before starting test
** Verify that the SESSION CONTROL is turned OFF


This test suite includes scenarios for generating test data.

To generate test users:

mvn clean test-compile gatling:execute -Dgatling.simulationClass=org.ozoneplatform.gatling.aml.simulation.InitializeMarketplaceUsers -DuserCount=5000 -DadminCount=500 -DbaseUrl=http://localhost:8080/marketplace
-Dgatling.simulationClass=org.ozoneplatform.gatling.aml.simulation.InitializeMarketplaceUsers -> The fully qualified class name of the scenario to run
-DuserCount=5000 -> The number of users to create (they will be created in the form testUser1, testUser2, testUser3, etc)
-DadminCount=500 -> The number of admins to create (they will be created in the form testAdmin1, testAdmin2, testAdmin3, etc)
-DbaseUrl=http://localhost:8080/marketplace -> The url of the marketplace instance you're testing


To generate the entire test scenario:

mvn clean test-compile gatling:execute -Dgatling.simulationClass=org.ozoneplatform.gatling.aml.simulation.InitializePerformanceData -DuserCount=5000 -DadminCount=100 -DitemCount=300 -DmetaDataCount=30 -DbaseUrl=http://192.168.37.16:8080/marketplace
-Dgatling.simulationClass=org.ozoneplatform.gatling.aml.simulation.InitializePerformanceData -> The fully qualified class of the scenario to run
-DuserCount=5000 -> The number of users to create (they will be created in the form testUser1, testUser2, testUser3, etc)
-DadminCount=100 -> The number of admins to create (they will be created in the form testAdmin1, testAdmin2, testAdmin3, etc)
-DitemCount=300 -> The number of listings to create in the store
-DmetaDataCount=30 -> The number of meta data objects to create in the store
-DbaseUrl=http://localhost:8080/marketplace -> The url of the marketplace instance you're testing

