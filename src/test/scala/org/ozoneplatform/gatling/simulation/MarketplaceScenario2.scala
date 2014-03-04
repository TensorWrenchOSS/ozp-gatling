package org.ozoneplatform.gatling.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import org.ozoneplatform.gatling.feeder.Feeders
import org.ozoneplatform.gatling.feeder.FeederUtils
import org.ozoneplatform.gatling.builder.ServiceItemBuilder
import play.api.libs.json.JsObject

class MarketplaceScenario2 extends Simulation {

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
      .basicAuth("${adminUser}","""password"""))

  setUp(scn.inject(ramp(10 users) over (10 seconds))).protocols(httpProtocol)
}
