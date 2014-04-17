package org.ozoneplatform.gatling.simulation

import io.gatling.core.Predef._
import org.ozoneplatform.gatling.feeder.{FeederUtils, Feeders}
import org.ozoneplatform.gatling.action.MarketplaceActions._
import org.ozoneplatform.gatling.action.ActionHelpers._
import scala.concurrent.duration._

class InitializeMarketplaceUsers extends Simulation {
  val adminCount = FeederUtils.getAdminCount
  val userCount = FeederUtils.getUserCount

  val initAdminUsers = scenario("Initializing " + adminCount + " admin users.")
    .feed(Feeders.generateUserNameFeeder(isAdmin = true))
    .exec(createUser)

  val initUsers = scenario("Initializing " + userCount + " non-admin users")
    .feed(Feeders.generateUserNameFeeder())
    .exec(createUser)

  setUp(
    initAdminUsers.inject(atOnce(1 user), nothingFor(5 seconds), ramp((adminCount - 1) users) over ((adminCount -1) seconds)),
    initUsers.inject(nothingFor(30 seconds), ramp(userCount.toInt users) over (userCount.toInt seconds))
  ).protocols(restHttpProtocol)
}
