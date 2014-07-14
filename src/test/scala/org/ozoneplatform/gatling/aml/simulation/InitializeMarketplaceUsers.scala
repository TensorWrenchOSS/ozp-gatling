package org.ozoneplatform.gatling.aml.simulation

import io.gatling.core.Predef._
import org.ozoneplatform.gatling.aml.feeder.FeederUtils._
import org.ozoneplatform.gatling.aml.feeder.Feeders
import org.ozoneplatform.gatling.aml.action.MarketplaceActions._
import org.ozoneplatform.gatling.aml.action.ActionHelpers._
import scala.concurrent.duration._

class InitializeMarketplaceUsers extends Simulation {
  val adminCount = getAdminCount.toInt
  val userCount = getUserCount.toInt

  val initAdminUsers = scenario("Initializing " + adminCount + " admin users.")
    .feed(Feeders.userFeeder(adminCount, isAdmin =  true))
    .exec(createUser)

  val initUsers = scenario("Initializing " + userCount + " non-admin users")
    .feed(Feeders.userFeeder(userCount))
    .exec(createUser)

  setUp(
    initAdminUsers.inject(ramp(adminCount users) over (adminCount seconds)),
    initUsers.inject(nothingFor(adminCount seconds), ramp(userCount users) over (userCount seconds))
  ).protocols(restHttpProtocol)
}
