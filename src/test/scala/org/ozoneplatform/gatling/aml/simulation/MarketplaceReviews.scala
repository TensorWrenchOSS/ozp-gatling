package org.ozoneplatform.gatling.aml.simulation

import org.ozoneplatform.gatling.aml.feeder.Feeders
import io.gatling.core.Predef._
import org.ozoneplatform.gatling.aml.action.MarketplaceActions._
import org.ozoneplatform.gatling.aml.action.ActionHelpers._
import org.ozoneplatform.gatling.aml.feeder.FeederUtils._
import bootstrap._
import scala.concurrent.duration._

class MarketplaceReviews extends Simulation {
  val rampPeriod = getRampPeriod
  val profilesAsJson = getObjectDataAsJson(PROFILE_PATH)
  val userCount = getScenarioUserCount
  val reviewPercentage = getActionPercentage

  val searchAndReview = scenario("Search and review")
    .feed(Feeders.selectUserNameFeeder(profilesAsJson))
    .repeat(10) {
      feed(Feeders.wordListFeeder(propertyName = "queryString"))
        .group("Search Page") {
          searchChain
        }
        .pause(3 seconds)
        .repeat(3) {
          feed(Feeders.blurbFeeder(propertyName = "itemComment"))
            .feed(Feeders.itemRatingFeeder())
            .exec(getSearchItemAndDoAction(reviewServiceItem, reviewPercentage))
            .pause(3 seconds)
    }
  }

  setUp(
    searchAndReview.inject(ramp(userCount.toInt users) over (rampPeriod.toInt seconds))
  ).protocols(restHttpProtocol)

}
