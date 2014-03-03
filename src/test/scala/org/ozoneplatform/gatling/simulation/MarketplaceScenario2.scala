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
  val dictionary: Array[String] = FeederUtils.getDictionaryWords
  val corpus: String = FeederUtils.getTextCorpus
  val storeItems: String = FeederUtils.getStoreItemsAsJsonString

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

      session.set("editedItem", new ServiceItemBuilder(item).description("${itemDescription}").toString()).set("editedItemId", itemId)
    })
    .exec(http("edit service item")
      .put("api/serviceItem/" + "${editedItemId}")
      .headers(create_item_headers)
      .body(StringBody("${editedItem}"))
      .basicAuth("{$adminUser}","""password"""))

  setUp(scn.inject(ramp(10 users) over (10 seconds))).protocols(httpProtocol)
}
