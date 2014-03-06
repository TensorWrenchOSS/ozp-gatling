package org.ozoneplatform.gatling.simulation

import io.gatling.core.Predef._
import org.ozoneplatform.gatling.action.MarketplaceActions._
import org.ozoneplatform.gatling.feeder.{Feeders, FeederUtils}
import scala.concurrent.duration._
import org.ozoneplatform.gatling.action.Helpers._

class MarketplaceTagging extends Simulation {

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

  val tagARandomItem = scenario("Tag a random service item.")
    .feed(Feeders.adminUser(adminCount))
    .feed(Feeders.itemTag(tagCount))
    .feed(Feeders.randomServiceItemId)
    .exec(tagServiceItem)


  setUp(
    initUsers.inject(ramp(adminCount.toInt users) over (adminCount.toInt seconds)),
    initServiceItems.inject(nothingFor(adminCount.toInt seconds), nothingFor(5 seconds), ramp(itemCount.toInt users) over (itemCount.toInt seconds)),
    tagARandomItem.inject(nothingFor(itemCount.toInt seconds), nothingFor(5 seconds), ramp(1 user) over (5 seconds), ramp(1000 users) over (1000 seconds))
  ).protocols(restHttpProtocol)



   /*




   val baseURL = FeederUtils.getBaseUrl
  val corpus = FeederUtils.getTextCorpus
  val storeItems = FeederUtils.getStoreItemsAsJsonString
  val adminCount = FeederUtils.getAdminCount

  val httpProtocol = http
    .baseURL(baseURL)
    .acceptHeader("application/json")

  val create_item_headers = Map("""Content-Type""" -> """application/json""")

  val scn = scenario("Marketplace Scenario 2")
    .feed(Feeders.itemDescription(corpus))
    .feed(Feeders.itemJson(storeItems))
    .feed(Feeders.adminUser(adminCount))
    .exec((session: Session) => {
      val item = session("itemJson").as[JsObject]
      val itemId = (item \ "id").as[Long]
      val description = session("itemDescription").as[String]

      session.set("editedItem", new ServiceItemBuilder(item).description(description).toString()).set("editedItemId", itemId)
    })
    .exec(http("edit service item")
      .put("api/serviceItem/" + "${editedItemId}")
      .headers(create_item_headers)
      .body(StringBody("${editedItem}"))
      .basicAuth("${adminUser}","""password"""))*/



}
