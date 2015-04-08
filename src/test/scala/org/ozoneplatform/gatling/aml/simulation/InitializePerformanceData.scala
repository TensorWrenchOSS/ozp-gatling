package org.ozoneplatform.gatling.aml.simulation

import io.gatling.core.Predef._
import org.ozoneplatform.gatling.aml.action.MetaDataActions._
import org.ozoneplatform.gatling.aml.action.ActionHelpers._
import org.ozoneplatform.gatling.aml.builder.ListingBuilder
import org.ozoneplatform.gatling.aml.feeder.FeederUtils._
import org.ozoneplatform.gatling.aml.feeder.Feeders
import org.ozoneplatform.gatling.aml.action.MarketplaceActions._
import org.ozoneplatform.gatling.aml.action.ActionHelpers._
import bootstrap._
import scala.concurrent.duration._

class InitializePerformanceData extends Simulation {
  val itemCount = getItemCount.toInt
  val userCount = getUserCount.toInt
  val adminCount = getAdminCount.toInt
  val metaDataCount = getMetaDataCount.toInt
  val itemType = getObjectDataAsJson(TYPE_PATH)
  val itemAgency = getObjectDataAsJson(AGENCY_PATH)
  val contactTypes = getObjectDataAsJson(CONTACT_TYPE_PATH)
  val itemCategory = getObjectDataAsJson(CATEGORY_PATH)

  val initMetaData = scenario("Initializing " + metaDataCount + " meta data")
    .feed(Feeders.randomUserFeeder(adminCount, isAdmin =  true, propertyName = "adminUserName"))
    .feed(Feeders.wordFeeder(propertyName = "typeTitle"))
    .feed(Feeders.wordFeeder(propertyName = "categoryTitle"))
    .feed(Feeders.wordFeeder(propertyName = "agencyTitle"))
    .feed(Feeders.wordFeeder(propertyName = "agencyShortName"))
    .feed(Feeders.wordFeeder(propertyName = "contactTypeTitle"))
    .exec(createType)
    .exec(createCategory)
    .exec(createAgency)
    .exec(createContactType)

  val initAdminUsers = scenario("Initializing " + adminCount + " admin users.")
    .feed(Feeders.userFeeder(adminCount, isAdmin =  true))
    .exec(createUser)

  val initUsers = scenario("Initializing " + userCount + " non-admin users")
    .feed(Feeders.userFeeder(userCount))
    .exec(createUser)

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

  val initServiceItems = scenario("Initializing " + itemCount + " listings.")
    .feed(Feeders.blurbFeeder(3000, "itemDescription"))
    .feed(Feeders.blurbFeeder(100, "itemDescriptionShort"))
    .feed(Feeders.blurbFeeder(2, "itemVersionName"))
    .feed(Feeders.blurbFeeder(500, "itemRequirements"))
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
    .repeat(6) {
      exec(createImage("${userName}"))
    }
    .exec(createListing("${userName}"))
    .exec(submitListing)
    .exec((session: Session) => { session.remove("gatling.http.cookies") }) //logout the user, so we can log in the admin
    .exec(orgApproveListing)
    .exec(approveListing)

  setUp(
    initAdminUsers.inject(ramp(adminCount users) over (adminCount seconds)),
    initUsers.inject(nothingFor(adminCount seconds), ramp(userCount users) over (userCount seconds)),
    initMetaData.inject(nothingFor((adminCount + userCount) seconds), ramp(metaDataCount users) over (metaDataCount seconds)),
    initServiceItems.inject(nothingFor((adminCount + userCount + metaDataCount) seconds), ramp(itemCount users) over (itemCount seconds))
  ).protocols(restHttpProtocol)
}