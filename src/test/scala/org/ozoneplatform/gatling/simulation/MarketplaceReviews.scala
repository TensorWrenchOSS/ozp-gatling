package org.ozoneplatform.gatling.simulation

import org.ozoneplatform.gatling.feeder.Feeders
import io.gatling.core.Predef._
import org.ozoneplatform.gatling.action.MarketplaceActions._
import org.ozoneplatform.gatling.action.Helpers._
import scala.concurrent.duration._
import org.ozoneplatform.gatling.feeder.FeederUtils._
import bootstrap._
import play.api.libs.json.JsObject

class MarketplaceReviews extends Simulation {
  val rampPeriod = getRampPeriod
  val profilesAsJson = getStoreProfilesAsJsonString
  val userCount = getScenarioUserCount
  val reviewPercentage = getActionPercentage

  val searchAndReview = scenario("Search and review")
    .feed(Feeders.randomUserName(profilesAsJson))
    .repeat(10) {
      feed(Feeders.searchQuery)
        .exec(searchMarketplace)
        .pause(3 seconds)
        .repeat(3) {
          feed(Feeders.itemComment)
            .feed(Feeders.itemRating)
            .exec(getSearchItemAndDoAction(reviewServiceItem, reviewPercentage))
            .pause(3 seconds)
    }
  }

  setUp(
    searchAndReview.inject(ramp(userCount.toInt users) over (rampPeriod.toInt seconds))
  ).protocols(restHttpProtocol)

}
