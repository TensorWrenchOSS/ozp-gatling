package org.ozoneplatform.gatling.simulation

import org.ozoneplatform.gatling.feeder.Feeders._
import org.ozoneplatform.gatling.feeder.FeederUtils._
import io.gatling.http.Predef._
import io.gatling.core.Predef._
import scala.concurrent.duration._
import org.ozoneplatform.gatling.builder.ServiceItemBuilder

class InitializeMarketplace extends Simulation {

  val baseURL = getBaseUrl
  val corpus = getTextCorpus
  val dictionary = getWordsDistro
  val adminCount = getAdminCount
  val itemCount = getItemCount

  val httpProtocol = http
    .baseURL(baseURL)
    .acceptHeader("application/json")

  val create_item_headers = Map("""Content-Type""" -> """application/json""")

  val initUsers = scenario("Initializing " + adminCount + " admin users.")
    .feed(adminUserLoop(adminCount))
    .exec(http("Login and create profile by making a request to REST API")
      .get("api/serviceItem")
      .headers(create_item_headers)
      .basicAuth("${loopedAdminUser}", "password"))

  val initItems = scenario("Creating " + itemCount + " service items.")
    .feed(itemTitle(dictionary))
    .feed(itemDescription(corpus))
    .feed(adminUser(adminCount))
    .exec(http("create service item")
      .post("""api/serviceItem""")
      .headers(create_item_headers)
      .body(StringBody(new ServiceItemBuilder()
        .title("${itemTitle}")
        .description("${itemDescription}")
        .toString()))
      .basicAuth("${adminUser}","""password"""))

  setUp(initUsers.inject(ramp(adminCount.toInt users) over (adminCount.toInt seconds)),
        initItems.inject(ramp(itemCount.toInt users) over (itemCount.toInt seconds)))
    .protocols(httpProtocol)
}
