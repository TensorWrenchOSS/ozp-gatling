package org.ozoneplatform.gatling.simulation

import io.gatling.core.Predef._
import org.ozoneplatform.gatling.action.MarketplaceActions._
import org.ozoneplatform.gatling.feeder.Feeders
import scala.concurrent.duration._
import org.ozoneplatform.gatling.action.Helpers._
import org.ozoneplatform.gatling.feeder.FeederUtils._
import play.api.libs.json.JsObject
import bootstrap._

class MarketplaceTagging extends Simulation {
  val rampPeriod = getRampPeriod
  val profilesAsJson = getStoreProfilesAsJsonString
  val userCount = getScenarioUserCount

  val searchAndTagFirstItem = exec(searchMarketplace)
    .pause(1 second)
    .doIf(session => session("searchResults").as[List[JsObject]].size > 0) {
      exec((session: Session) => {
        val firstResult = session("searchResults").as[List[JsObject]].head
        val itemId = firstResult \ "id"

        session.set("serviceItemId", itemId)
      })
      .exec(getServiceItem)
      .pause(1)
      .exec(tagServiceItem)
    }

  val searchAndTag = scenario("Search and review")
    .feed(Feeders.randomUserName(profilesAsJson))
    .repeat(10) {
      feed(Feeders.searchQuery)
      .feed(Feeders.itemTag(200))
      .exec(searchAndTagFirstItem)
    }

  setUp(
    searchAndTag.inject(ramp(userCount.toInt users) over (rampPeriod.toInt seconds))
  ).protocols(restHttpProtocol)
}
