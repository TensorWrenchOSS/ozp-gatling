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
  val userLoops = getScenarioUserCount
  val tagCount = getTagCount
  val tagPercentage = getActionPercentage

  val searchAndTag = scenario("Search and review")
    .feed(Feeders.randomUserName(profilesAsJson))
    .repeat(10) {
      feed(Feeders.searchQuery)
        .group("Search Page") {
          searchChain
        }
        .pause(3 seconds)
        .repeat(3) {
          feed(Feeders.itemTag(tagCount))
            .exec(getSearchItemAndDoAction(tagServiceItem, tagPercentage))
            .pause(3 seconds)
        }
    }

  setUp(
    searchAndTag.inject(ramp(userLoops.toInt users) over (rampPeriod.toInt seconds))
  ).protocols(restHttpProtocol)
}
