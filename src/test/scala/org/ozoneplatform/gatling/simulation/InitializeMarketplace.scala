package org.ozoneplatform.gatling.simulation

import org.ozoneplatform.gatling.feeder.{Feeders, FeederUtils}
import io.gatling.core.Predef._
import org.ozoneplatform.gatling.action.MarketplaceActions._
import org.ozoneplatform.gatling.action.Helpers._
import scala.concurrent.duration._

class InitializeMarketplace extends Simulation {
  val adminCount = FeederUtils.getAdminCount
  val userCount = FeederUtils.getUserCount
  val itemCount = FeederUtils.getItemCount

  val initAdminUsers = scenario("Initializing " + adminCount + " admin users.")
    .feed(Feeders.adminLoop(adminCount))
    .exec(createUser)

  val initUsers = scenario("Initializing " + userCount + " non-admin users")
    .feed(Feeders.userLoop(userCount))
    .exec(createUser)

  val initServiceItems = scenario("Initializing" + itemCount + " service items.")
    .feed(Feeders.adminUser(adminCount))
    .feed(Feeders.itemDescription)
    .feed(Feeders.itemTitle)
    .exec(createServiceItem)

  setUp(
    initAdminUsers.inject(ramp(adminCount.toInt users) over (adminCount.toInt seconds)),
    initUsers.inject(ramp(adminCount.toInt users) over (adminCount.toInt seconds)),
    initServiceItems.inject(ramp(itemCount.toInt users) over (itemCount.toInt seconds))
  ).protocols(restHttpProtocol)
}
