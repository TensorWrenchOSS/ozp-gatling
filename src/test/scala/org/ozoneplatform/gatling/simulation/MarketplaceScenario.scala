package org.ozoneplatform.gatling.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import org.ozoneplatform.gatling.feeder.Feeders
import org.ozoneplatform.gatling.feeder.FeederUtils
import org.ozoneplatform.gatling.builder.ServiceItemBuilder

class MarketplaceScenario extends Simulation {
  val baseURL = FeederUtils.getBaseUrl
  val dictionary: Array[String] = FeederUtils.getDictionaryWords
  val corpus: String = FeederUtils.getTextCorpus
  val storeItems: String = FeederUtils.getStoreItemsAsJsonString
  val adminCount = FeederUtils.getAdminCount

  val httpProtocol = http
    .baseURL(baseURL)
    .acceptHeader("application/json")

  val create_item_headers = Map("""Content-Type""" -> """application/json""")

  val scn = scenario("Marketplace Scenario")
    .feed(Feeders.itemTitle(dictionary))
    .feed(Feeders.itemDescription(corpus))
    .feed(Feeders.adminUser(adminCount))
    .exec(http("create service item")
      .post("""api/serviceItem""")
      .headers(create_item_headers)
      .body(StringBody(new ServiceItemBuilder()
                           .title("${itemTitle}")
                           .description("${itemDescription}")
                           .toString()))
      .basicAuth("${adminUser}","""password"""))

  setUp(scn.inject(ramp(10 users) over (10 seconds))).protocols(httpProtocol)
}
