package org.ozoneplatform.gatling.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import org.ozoneplatform.gatling.feeder.Feeders
import org.ozoneplatform.gatling.feeder.FeederUtils
import org.ozoneplatform.gatling.builder.ServiceItemBuilder
import play.api.libs.json.JsObject

class MarketplaceScenario4 extends Simulation {

  val baseURL = FeederUtils.getBaseUrl
  val dictionary = FeederUtils.getDictionaryWords
  val storeItems = FeederUtils.getStoreItemsAsJsonString
  val adminCount = FeederUtils.getAdminCount

  val httpProtocol = http
    .baseURL(baseURL)
    .acceptHeader("application/json")

  val create_item_headers = Map("""Content-Type""" -> """application/json""")

  val scn = scenario("create a tag")
    .feed(Feeders.itemJson(storeItems))
    .feed(Feeders.adminUser(adminCount))
    .feed(Feeders.itemTag(dictionary))
    .exec((session: Session) => {
      val item = session("itemJson").as[JsObject]
      val itemId = (item \ "id").as[Long]

      session.set("editedItemId", itemId)
    })
    .exec(http("tag the service item")
      .post("api/serviceItem/" + "${editedItemId}" + "/tag")
      .headers(create_item_headers)
      .basicAuth("{$adminUser}","""password""")
      .body(StringBody("{\"title\": \"" +  "{$itemTag}" + "\"}")))

  setUp(scn.inject(ramp(10 users) over (10 seconds))).protocols(httpProtocol)
}
