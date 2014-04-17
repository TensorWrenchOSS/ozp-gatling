package org.ozoneplatform.gatling.aml.simulation

import org.ozoneplatform.gatling.aml.feeder.{Feeders, FeederHelpers}
import org.ozoneplatform.gatling.aml.feeder.FeederUtils

import io.gatling.core.Predef._
import org.ozoneplatform.gatling.aml.action.MarketplaceActions._
import org.ozoneplatform.gatling.aml.action.ActionHelpers._
import org.ozoneplatform.gatling.aml.builder.ServiceItemBuilder
import bootstrap._
import scala.concurrent.duration._

class InitializeMarketplaceItems extends Simulation {
  val itemCount = 5;//FeederUtils.getItemCount
  val profilesAsJson = FeederUtils.getStoreProfilesAsJsonString

  println(profilesAsJson)

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
