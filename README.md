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

This project has one or more SNAPSHOT dependencies. If it is necessary to run the project while disconnected, you can greatly
speed up the dependency resolution step by running in offline mode (add --offline or -o to the maven invocation).

Notes:
* Things to do before starting test
** Verify that the SESSION CONTROL is turned OFF
