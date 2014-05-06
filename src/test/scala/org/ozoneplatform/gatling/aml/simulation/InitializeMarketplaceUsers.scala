package org.ozoneplatform.gatling.aml.simulation

import io.gatling.core.Predef._
import org.ozoneplatform.gatling.aml.feeder.FeederUtils._
import org.ozoneplatform.gatling.aml.feeder.Feeders
import org.ozoneplatform.gatling.aml.action.MarketplaceActions._
import org.ozoneplatform.gatling.aml.action.ActionHelpers._
import scala.concurrent.duration._

class InitializeMarketplaceUsers extends Simulation {
  val adminCount = getAdminCount
  val userCount = getUserCount

  val initAdminUsers = scenario("Initializing " + adminCount + " admin users.")
    .feed(Feeders.userFeeder(adminCount, isAdmin =  true))
    .exec(createUser)

  val initUsers = scenario("Initializing " + userCount + " non-admin users")
    .feed(Feeders.userFeeder(userCount))
    .exec(createUser)

  setUp(
    initAdminUsers.inject(rampUsers(adminCount.toInt).over(adminCount.toInt)),
    initUsers.inject(nothingFor(30 seconds), rampUsers(userCount.toInt).over(userCount.toInt))
  ).protocols(restHttpProtocol)
}
