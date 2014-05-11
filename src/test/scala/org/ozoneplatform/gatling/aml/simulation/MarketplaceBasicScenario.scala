package org.ozoneplatform.gatling.aml.simulation

import org.ozoneplatform.gatling.aml.feeder.{FeederHelpers, Feeders}
import io.gatling.core.Predef._
import org.ozoneplatform.gatling.aml.action.MarketplaceActions._
import org.ozoneplatform.gatling.aml.feeder.FeederUtils._
import scala.concurrent.duration._
import org.ozoneplatform.gatling.aml.builder.SearchBuilder
import org.ozoneplatform.gatling.aml.action.ActionHelpers._

class MarketplaceBasicScenario extends Simulation {
  val userCount = getUserCount
  val userLoops = getScenarioUserCount
  val rampPeriod = getRampPeriod

  val tagList = FeederHelpers.randWordSet(count = 500)

  val browseForListings = randomSwitch(
    33.3 -> exec(new SearchBuilder().allListings().maxResults(24).search),
    33.3 -> exec(new SearchBuilder().newArrivals().maxResults(24).search),
    33.3 -> exec(new SearchBuilder().highestRated().maxResults(24).search)
  )

  val searchForListings = feed(Feeders.wordListFeeder(propertyName = "queryString"))
    .exec(new SearchBuilder()
      .searchTerm("${queryString}")
      .maxResults(24)
      .search)

  val reviewChain = feed(Feeders.blurbFeeder(propertyName = "itemComment"))
    .feed(Feeders.itemRatingFeeder())
    .pause(1 minutes, 3 minutes) //pause to compose the review
    .exec(reviewServiceItem)

  val tagChain = feed(Feeders.wordFeeder(propertyName = "itemTag"))
    .pause(3 seconds, 8 seconds) //pause to choose tag
    .exec(tagServiceItem)

  val basicUserScenario = scenario("Basic Marketplace Performance Scenario")
    .feed(Feeders.randomUserFeeder(userCount))
    .repeat(10) {
      exec(getConfig)
        .pause(3 seconds, 12 seconds) //pause to choose search method/query
        .randomSwitch(52.0 -> browseForListings, 48.0 -> searchForListings)
        .repeat(5) {
          pause(3 seconds, 12 seconds) //pause to scan and choose search result
          .exec(getSearchItemAndDoChain(randomSwitch(0.25 -> reviewChain, 0.5 -> tagChain, 0.5 -> exec(addToOwf))))
        }
    }

  setUp(
    basicUserScenario.inject(rampUsers(userLoops).over(rampPeriod))
  ).protocols(restHttpProtocol)
}
