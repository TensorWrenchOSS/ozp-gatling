package org.ozoneplatform.gatling.aml.simulation

import org.ozoneplatform.gatling.aml.feeder.Feeders
import org.ozoneplatform.gatling.aml.feeder.FeederUtils._

import io.gatling.core.Predef._
import org.ozoneplatform.gatling.aml.action.MarketplaceActions._
import org.ozoneplatform.gatling.aml.action.ActionHelpers._
import org.ozoneplatform.gatling.aml.builder.ListingBuilder
import bootstrap._
import scala.concurrent.duration._

class InitializeMarketplaceItems extends Simulation {
  val itemCount = getItemCount
  val adminCount = getAdminCount
  val userCount = getUserCount
  val itemType = getObjectDataAsJson(TYPE_PATH)
  val itemAgency = getObjectDataAsJson(AGENCY_PATH)
  val contactTypes = getObjectDataAsJson(CONTACT_TYPE_PATH)
  val itemCategory = getObjectDataAsJson(CATEGORY_PATH)

  val submitListing = exec((session: Session) => {
    val item = session("listing").as[String]
    val itemJson = new ListingBuilder(item).submit().toString

    session.set("listing", itemJson)
  }).exec(modifyListing("${userName}"))

  val orgApproveListing = exec((session: Session) => {
    val item = session("listing").as[String]
    val itemJson = new ListingBuilder(item).orgApprove().toString

    session.set("listing", itemJson)
  }).exec(modifyListing("${adminUserName}"))

  val approveListing = exec((session: Session) => {
    val item = session("listing").as[String]
    val itemJson = new ListingBuilder(item).approve().toString

    session.set("listing", itemJson)
  }).exec(modifyListing("${adminUserName}"))

  val initServiceItems = scenario("Initializing" + itemCount + " listings.")
    .feed(Feeders.blurbFeeder(3000, "itemDescription"))
    .feed(Feeders.blurbFeeder(100, "itemDescriptionShort"))
    .feed(Feeders.blurbFeeder(2, "itemVersionName"))
    .feed(Feeders.blurbFeeder(500, "itemRequirements"))
    //.feed(Feeders).smallIconId
    //.feed(Feeders).largeIconId
    //.feed(Feeders).bannerIconId
    //.feed(Feeders).featuredBannerIconId
    //.feed(Feeders).screenshots
    .feed(Feeders.wordListFeeder(propertyName = "itemTitle"))
    .feed(Feeders.blurbFeeder(100, "itemWhatIsNew"))
    .feed(Feeders.randomUserFeeder(userCount))
    .feed(Feeders.randomUserFeeder(adminCount, isAdmin =  true, propertyName = "adminUserName"))
    .feed(Feeders.randomObjectTitleFromJson(itemType, "itemType"))
    .feed(Feeders.randomObjectTitleFromJson(itemAgency, "itemAgency"))
    .feed(Feeders.randomObjectTitleFromJson(contactTypes, "itemContactType"))
    .feed(Feeders.randomObjectTitleFromJson(itemCategory, "itemCategory"))
    .feed(Feeders.emailFeeder("contactEmail"))
    .feed(Feeders.wordListFeeder(maxSize = 2, propertyName = "contactName"))
    .feed(Feeders.blurbFeeder(15, "itemTag"))
    .exec(createListing("${userName}"))
   // .exec(submitListing)
  //  .exec((session: Session) => { session.remove("gatling.http.cookies") }) //logout the user, so we can log in the admin
  //  .exec(orgApproveListing)
  //  .exec(approveListing)

  setUp(
    initServiceItems.inject(nothingFor(5 seconds), ramp(itemCount.toInt users) over (itemCount.toInt seconds))
  ).protocols(restHttpProtocol)
}
