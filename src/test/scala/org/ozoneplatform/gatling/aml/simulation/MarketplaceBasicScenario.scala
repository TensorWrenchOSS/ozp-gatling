package org.ozoneplatform.gatling.aml.simulation

import org.ozoneplatform.gatling.aml.feeder.{FeederHelpers, Feeders}
import io.gatling.core.Predef._
import org.ozoneplatform.gatling.aml.action.MarketplaceActions._
import org.ozoneplatform.gatling.aml.feeder.FeederUtils._
import bootstrap._
import scala.concurrent.duration._
import org.ozoneplatform.gatling.aml.builder.SearchBuilder
import org.ozoneplatform.gatling.aml.action.ActionHelpers._

class MarketplaceBasicScenario extends Simulation {
  val userCount = getUserCount
  val userLoops = getScenarioUserCount
  val rampPeriod = getRampPeriod

  val tagList = FeederHelpers.randWordSet(count = 500)

  val browseForListings = randomSwitch(
    exec(new SearchBuilder().allListings().search),
    exec(new SearchBuilder().newArrivals().search),
    exec(new SearchBuilder().highestRated().search)
  )

  val searchForListings = feed(Feeders.wordListFeeder(propertyName = "queryString"))
    .exec(new SearchBuilder()
      .searchTerm("${queryString}")
      .highestRated()
      .search)

  val reviewChain = feed(Feeders.blurbFeeder(propertyName = "itemComment"))
    .feed(Feeders.itemRatingFeeder())
    .pause(30 seconds)
    .exec(reviewServiceItem)

  val tagChain = feed(Feeders.wordFeeder(words = tagList, propertyName = "itemTag"))
    .pause(5 seconds)
    .exec(tagServiceItem)

  val basicUserScenario = scenario("Basic Marketplace Performance Scenario")
    .feed(Feeders.randomUserFeeder(userCount))
    .repeat(10) {
      exec(getConfig)
        .randomSwitch(52 -> browseForListings, 48 -> searchForListings)
        .pause(10 seconds)
        .repeat(5) {
          exec(getSearchItemAndDoActions(randomSwitch(2 -> reviewChain, 4 -> tagChain)))
        }
    }

  setUp(
    basicUserScenario.inject(ramp(userLoops.toInt users) over (rampPeriod.toInt seconds))
  ).protocols(restHttpProtocol)
}
