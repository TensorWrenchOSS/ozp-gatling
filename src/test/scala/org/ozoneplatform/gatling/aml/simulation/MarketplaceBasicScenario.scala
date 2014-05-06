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
    .pause(30 seconds)
    //.exec(reviewServiceItem)

  val tagChain = feed(Feeders.wordFeeder(words = tagList, propertyName = "itemTag"))
    .pause(5 seconds)
    //.exec(tagServiceItem)

  val basicUserScenario = scenario("Basic Marketplace Performance Scenario")
    .feed(Feeders.randomUserFeeder(userCount))
    .repeat(10) {
      exec(getConfig)
        .randomSwitch(52.0 -> browseForListings, 48.0 -> searchForListings)
        .pause(10 seconds)
        .repeat(5) {
          exec(getSearchItemAndDoActions(randomSwitch(2.0 -> reviewChain, 4.0 -> tagChain)))
        }
    }

  setUp(
    basicUserScenario.inject(rampUsers(userLoops.toInt).over(rampPeriod.toInt))
  ).protocols(restHttpProtocol)
}
