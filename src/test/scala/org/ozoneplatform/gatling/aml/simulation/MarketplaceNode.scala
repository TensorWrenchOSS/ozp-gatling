package org.ozoneplatform.gatling.aml.simulation

import org.ozoneplatform.gatling.aml.feeder.{FeederHelpers, Feeders}
import io.gatling.core.Predef._
import org.ozoneplatform.gatling.aml.action.MarketplaceActions._
import org.ozoneplatform.gatling.aml.feeder.FeederUtils._
import bootstrap._
import scala.concurrent.duration._
import org.ozoneplatform.gatling.aml.builder.SearchBuilder
import org.ozoneplatform.gatling.aml.action.ActionHelpers._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ChainBuilder
import scala.util.Random

class MarketplaceNode extends Simulation {

  // val userLoops = getScenarioUserCount.toInt
  // val rampPeriod = getRampPeriod.toInt

  // val termSearch: ActionBuilder = new SearchBuilder().searchTerm("${queryString}").maxResults("24").search

  // val searchForListings = {
  //   val searchChain = getSearchChain(termSearch)

  //   feed(Feeders.wordListFeeder(propertyName = "queryString"))
  //   .exec(searchChain)
  // }

  // def getSearchChain(searchAction: ActionBuilder): ChainBuilder = exec(searchAction)

  // val basicUserScenario = scenario("Basic Marketplace Performance Scenario")
  //   .repeat(50) {
  //     pause(1 seconds, 5 seconds)
  //     .exec(searchForListings)
  //   }

  // setUp(
  //   basicUserScenario.inject(
  //     ramp(userLoops users) over (rampPeriod seconds)
  //   )
  // ).protocols(restHttpProtocol)
}
