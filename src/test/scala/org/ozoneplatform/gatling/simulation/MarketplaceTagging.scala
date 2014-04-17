package org.ozoneplatform.gatling.simulation

import io.gatling.core.Predef._
import org.ozoneplatform.gatling.action.MarketplaceActions._
import org.ozoneplatform.gatling.feeder.Feeders
import org.ozoneplatform.gatling.feeder.FeederHelpers._
import org.ozoneplatform.gatling.action.ActionHelpers._
import org.ozoneplatform.gatling.feeder.FeederUtils._
import bootstrap._
import scala.concurrent.duration._

class MarketplaceTagging extends Simulation {
  val rampPeriod = getRampPeriod
  val profilesAsJson = getStoreProfilesAsJsonString
  val userLoops = getScenarioUserCount
  val tagCount = getTagCount
  val tagPercentage = getActionPercentage
  val tagList = randWordSet(count = tagCount)

  val searchAndTag = scenario("Search and review")
    .feed(Feeders.selectUserNameFeeder(profilesAsJson))
    .repeat(10) {
      feed(Feeders.wordListFeeder(propertyName = "queryString"))
        .group("Search Page") {
          searchChain
        }
        .pause(3 seconds)
        .repeat(3) {
          feed(Feeders.wordFeeder(words = tagList, propertyName = "itemTag"))
            .exec(getSearchItemAndDoAction(tagServiceItem, tagPercentage))
            .pause(3 seconds)
        }
    }

  setUp(
    searchAndTag.inject(ramp(userLoops.toInt users) over (rampPeriod.toInt seconds))
  ).protocols(restHttpProtocol)
}
