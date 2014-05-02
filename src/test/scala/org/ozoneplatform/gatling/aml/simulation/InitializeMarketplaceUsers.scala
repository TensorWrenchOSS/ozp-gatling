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
    .feed(Feeders.randomUserFeeder(adminCount, isAdmin =  true))
    .exec(createUser)

  val initUsers = scenario("Initializing " + userCount + " non-admin users")
    .feed(Feeders.randomUserFeeder(userCount))
    .exec(createUser)

  setUp(
    initAdminUsers.inject(atOnce(1 user), nothingFor(5 seconds), ramp((adminCount - 1) users) over ((adminCount -1) seconds)),
    initUsers.inject(nothingFor(30 seconds), ramp(userCount.toInt users) over (userCount.toInt seconds))
  ).protocols(restHttpProtocol)
}
