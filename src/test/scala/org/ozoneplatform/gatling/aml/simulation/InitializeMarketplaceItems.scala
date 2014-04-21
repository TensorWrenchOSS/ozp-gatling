package org.ozoneplatform.gatling.aml.simulation

import org.ozoneplatform.gatling.aml.feeder.Feeders
import org.ozoneplatform.gatling.aml.feeder.FeederUtils._

import io.gatling.core.Predef._
import org.ozoneplatform.gatling.aml.action.MarketplaceActions._
import org.ozoneplatform.gatling.aml.action.ActionHelpers._
import org.ozoneplatform.gatling.aml.builder.ServiceItemBuilder
import bootstrap._
import scala.concurrent.duration._

class InitializeMarketplaceItems extends Simulation {
  val itemCount = getItemCount
  val profiles = getObjectDataAsJson(PROFILE_PATH)
  val itemTypes = getObjectDataAsJson(TYPES_PATH)
  val contactTypes = getObjectDataAsJson(CONTACT_TYPE_PATH)
  val categories = getObjectDataAsJson(CATEGORY_PATH)

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
    .feed(Feeders.selectUserNameFeeder(profiles))
    .feed(Feeders.selectAdminUserFeeder(profiles, "adminUserName"))
    .feed(Feeders.randomObjectIdFromJson(itemTypes, "typesId"))
    .feed(Feeders.randomObjectIdFromJson(contactTypes, "contactTypeId"))
    .feed(Feeders.randomObjectIdFromJson(categories, "categoryId"))
    .feed(Feeders.emailFeeder("contactEmail"))
    .feed(Feeders.wordListFeeder(maxSize = 2, propertyName = "contactName"))
    .exec(createServiceItem("${userName}"))
    .exec(submitServiceItem)
    .exec((session: Session) => { session.remove("gatling.http.cookies") }) //logout the user, so we can log in the admin
    .exec(approveServiceItem)

  setUp(
    initServiceItems.inject(nothingFor(5 seconds), ramp(itemCount.toInt users) over (itemCount.toInt seconds))
  ).protocols(restHttpProtocol)
}
