package org.ozoneplatform.gatling.simulation

import org.ozoneplatform.gatling.feeder.{Feeders, FeederUtils}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.ozoneplatform.gatling.action.MarketplaceActions._
import org.ozoneplatform.gatling.action.Helpers._
import scala.concurrent.duration._
import org.ozoneplatform.gatling.action.Helpers
import scala.collection.mutable.ArrayBuffer
import org.ozoneplatform.gatling.builder.ServiceItemBuilder
import bootstrap._
import org.ozoneplatform.gatling.feeder.FeederUtils._

class InitializeMarketplaceItems extends Simulation {
  val itemCount = FeederUtils.getItemCount
  val profilesAsJson = getStoreProfilesAsJsonString

  val submitServiceItem = exec((session: Session) => {
    val item = session("serviceItem").as[String]
    val itemJson = new ServiceItemBuilder(item).submit().toString

    session.set("serviceItem", itemJson)
  }).exec(modifyServiceItem("${userName}"))

  val approveServiceItem = exec((session: Session) => {
    val item = session("serviceItem").as[String]
    val itemJson = new ServiceItemBuilder(item).approve().toString

    session.set("serviceItem", itemJson)
  }).exec(modifyServiceItem("${adminUserName}"))

  val initServiceItems = scenario("Initializing" + itemCount + " service items.")
    .feed(Feeders.itemDescription)
    .feed(Feeders.itemTitle)
    .feed(Feeders.randomUserName(profilesAsJson))
    .feed(Feeders.randomAdminUserName(profilesAsJson))
    .exec(createServiceItem("${userName}"))
    .exec(submitServiceItem)
    .exec((session: Session) => { session.remove("gatling.http.cookies") })
    .exec(approveServiceItem)

  setUp(
    initServiceItems.inject(nothingFor(30 seconds), ramp(itemCount.toInt users) over (itemCount.toInt seconds))
  ).protocols(restHttpProtocol)
}
