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

  val searchAndReviewFirstItem = exec(searchMarketplace)
    .pause(1 second)
    .doIf(session => session("searchResults").as[List[JsObject]].size > 0) {
      exec((session: Session) => {
        val firstResult = session("searchResults").as[List[JsObject]].head
        val itemId = firstResult \ "id"

        session.set("serviceItemId", itemId)
      })
      .exec(getServiceItem)
      .pause(1)
      .exec(reviewServiceItem)
    }

  val searchAndReview = scenario("Search and review")
    .feed(Feeders.randomUserName(profilesAsJson))
    .repeat(10) {
      feed(Feeders.searchQuery)
      .feed(Feeders.itemComment)
      .feed(Feeders.itemRating)
      .exec(searchAndReviewFirstItem)
    }

  setUp(
    searchAndReview.inject(ramp(userCount.toInt users) over (rampPeriod.toInt seconds))
  ).protocols(restHttpProtocol)

}
