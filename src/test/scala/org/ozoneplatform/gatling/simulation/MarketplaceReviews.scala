package org.ozoneplatform.gatling.simulation

import org.ozoneplatform.gatling.feeder.{Feeders, FeederUtils}
import io.gatling.core.Predef._
import org.ozoneplatform.gatling.action.MarketplaceActions._
import org.ozoneplatform.gatling.action.Helpers._

class MarketplaceReviews extends Simulation {

  val adminCount = FeederUtils.getAdminCount
  val tagCount = FeederUtils.getTagCount
  val itemCount = FeederUtils.getItemCount

  val initUsers = scenario("Initializing " + adminCount + " admin users.")
    .feed(Feeders.adminLoop(adminCount))
    .exec(createUser)

  val initServiceItems = scenario("Initializing" + itemCount + " service items.")
    .feed(Feeders.adminUser(adminCount))
    .feed(Feeders.itemDescription)
    .feed(Feeders.itemTitle)
    .exec(createServiceItem)

  val reviewARandomItem = scenario("Review a random service item.")
    .feed(Feeders.adminUser(adminCount))
    .feed(Feeders.itemComment)
    .feed(Feeders.randomServiceItemId)
    .feed(Feeders.itemRating)
    .exec(reviewServiceItem)


  setUp(
    initUsers.inject(ramp(adminCount.toInt users) over (adminCount.toInt seconds)),
    initServiceItems.inject(nothingFor(adminCount.toInt seconds), nothingFor(5 seconds), ramp(itemCount.toInt users) over (itemCount.toInt seconds)),
    reviewARandomItem.inject(nothingFor(itemCount.toInt seconds), nothingFor(5 seconds), ramp(1 user) over (5 seconds), ramp(1000 users) over (1000 seconds))
  ).protocols(restHttpProtocol)
}
