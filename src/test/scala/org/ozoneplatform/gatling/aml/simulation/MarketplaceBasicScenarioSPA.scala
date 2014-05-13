package org.ozoneplatform.gatling.aml.simulation

import org.ozoneplatform.gatling.aml.feeder.{FeederHelpers, Feeders}
import io.gatling.core.Predef._
import org.ozoneplatform.gatling.aml.action.MarketplaceActions._
import org.ozoneplatform.gatling.aml.feeder.FeederUtils._
import scala.concurrent.duration._
import org.ozoneplatform.gatling.aml.builder.SearchBuilder
import org.ozoneplatform.gatling.aml.action.ActionHelpers._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ChainBuilder
import scala.util.Random

class MarketplaceBasicScenarioSPA extends Simulation {
  val userCount = getUserCount
  val userLoops = getScenarioUserCount
  val rampPeriod = getRampPeriod

  val allListings: ActionBuilder = new SearchBuilder().allListings().maxResults(24).search
  val newArrivals: ActionBuilder = new SearchBuilder().newArrivals().maxResults(24).search
  val highestRate: ActionBuilder = new SearchBuilder().highestRated().maxResults(24).search
  val termSearch: ActionBuilder = new SearchBuilder().searchTerm("${queryString}").maxResults(24).search

  val browseForListings = {
    val searchAction = Random.shuffle(Array(allListings, newArrivals, highestRate).toList).head
    val searchChain = getSearchChain(searchAction)

    exec(searchChain).exec(getFilterChain(searchChain))
  }

  val searchForListings = {
    val searchChain = getSearchChain(termSearch)

    feed(Feeders.wordListFeeder(propertyName = "queryString"))
    .exec(searchChain)
    .exec(getFilterChain(searchChain))
  }

  def getSearchChain(searchAction: ActionBuilder): ChainBuilder = exec(searchAction)

  //filter search results up to 3 times
  //TODO: handle choosing an actual filter to apply - for now, just repeat the same search
  def getFilterChain(searchChain: ChainBuilder): ChainBuilder = {
    randomSwitch(47.0 ->
      pause(1 seconds, 5 seconds)
      .exec(searchChain)
      .randomSwitch(33.0 ->
        pause(1 seconds, 5 seconds)
        .exec(searchChain)
        .randomSwitch(33.0 ->
          pause(1 seconds, 5 seconds)
          .exec(searchChain))))
  }

  val reviewChain = {
    feed(Feeders.blurbFeeder(propertyName = "itemComment"))
    .feed(Feeders.itemRatingFeeder())
    .pause(1 minutes, 3 minutes) //pause to compose the review
    .exec(reviewServiceItem)
  }

  val tagChain = {
    feed(Feeders.wordFeeder(propertyName = "itemTag"))
    .pause(3 seconds, 8 seconds) //pause to choose tag
    .exec(tagServiceItem)
  }

  val basicUserScenario = scenario("Basic Marketplace Performance Scenario")
    .feed(Feeders.randomUserFeeder(userCount))
    .exec(goToDiscoveryPage)
    .repeat(10) {
      pause(1 seconds, 5 seconds) //pause to choose search method/query
      .randomSwitch(52.0 -> browseForListings, 48.0 -> searchForListings)
      .repeat(5) {
        pause(1 seconds, 5 seconds) //pause to scan and choose search result
        .exec(getSearchItemAndDoChain(randomSwitch(0.25 -> reviewChain, 0.5 -> tagChain, 0.5 -> exec(addToOwf))))
      }
    }

  setUp(
    basicUserScenario.inject(
      rampUsers(30).over(30),
      nothingFor(120),
      rampUsers(userLoops).over(rampPeriod))
  ).protocols(restHttpProtocol)
}
