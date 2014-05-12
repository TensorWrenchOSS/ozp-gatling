package org.ozoneplatform.gatling.aml.simulation

import org.ozoneplatform.gatling.aml.feeder.{FeederHelpers, Feeders}
import io.gatling.core.Predef._
import org.ozoneplatform.gatling.aml.action.MarketplaceActions._
import org.ozoneplatform.gatling.aml.feeder.FeederUtils._
import scala.concurrent.duration._
import org.ozoneplatform.gatling.aml.builder.SearchBuilder
import org.ozoneplatform.gatling.aml.action.ActionHelpers._
import io.gatling.core.action.builder.ActionBuilder

class MarketplaceBasicScenario extends Simulation {
  val userCount = getUserCount
  val userLoops = getScenarioUserCount
  val rampPeriod = getRampPeriod

  val tagList = FeederHelpers.randWordSet(count = 500)

  def allListings: ActionBuilder = new SearchBuilder().allListings().maxResults(24).search
  def newArrivals: ActionBuilder = new SearchBuilder().newArrivals().maxResults(24).search
  def highestRate: ActionBuilder = new SearchBuilder().highestRated().maxResults(24).search
  def termSearch: ActionBuilder = new SearchBuilder().searchTerm("${queryString}").maxResults(24).search

  val browseForListings = randomSwitch(
    33.3 -> exec(allListings).exec(allListings),
    33.3 -> exec(newArrivals).exec(newArrivals),
    33.3 -> exec(highestRate).exec(highestRate)
  )

  val searchForListings = exec(termSearch).exec(termSearch) //there are two search requests issued per rendering of the search page

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
      .feed(Feeders.wordListFeeder(propertyName = "queryString"))
      .pause(3 seconds, 12 seconds) //pause to choose search method/query
      .randomSwitch(52.0 -> browseForListings, 48.0 -> searchForListings)
      .repeat(5) {
        pause(3 seconds, 12 seconds) //pause to scan and choose search result
        .randomSwitch(47.0 -> exec(termSearch)) //use a filter
        .randomSwitch(33.0 -> exec(termSearch)) //use a second filter
        .randomSwitch(33.0 -> exec(termSearch)) //use a third filter
        .exec(getSearchItemAndDoChain(randomSwitch(0.25 -> reviewChain, 0.5 -> tagChain, 0.5 -> exec(addToOwf))))
      }
    }

  setUp(
    basicUserScenario.inject(rampUsers(userLoops).over(rampPeriod))
  ).protocols(restHttpProtocol)
}
