package org.ozoneplatform.gatling.simulation

import org.ozoneplatform.gatling.feeder.{Feeders, FeederUtils}
import io.gatling.core.Predef._
import org.ozoneplatform.gatling.action.MarketplaceActions._
import org.ozoneplatform.gatling.action.ActionHelpers._
import org.ozoneplatform.gatling.builder.ServiceItemBuilder
import bootstrap._
import org.ozoneplatform.gatling.feeder.FeederUtils._
import scala.concurrent.duration._

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
    .feed(Feeders.blurbFeeder(3000, "itemDescription"))
    .feed(Feeders.wordListFeeder(propertyName = "itemTitle"))
    .feed(Feeders.selectUserNameFeeder(profilesAsJson))
    .feed(Feeders.selectAdminUserFeeder(profilesAsJson, "adminUserName"))
    .exec(createServiceItem("${userName}"))
    .exec(submitServiceItem)
    .exec((session: Session) => { session.remove("gatling.http.cookies") })
    .exec(approveServiceItem)

  setUp(
    initServiceItems.inject(nothingFor(5 seconds), ramp(itemCount.toInt users) over (itemCount.toInt seconds))
  ).protocols(restHttpProtocol)
}
