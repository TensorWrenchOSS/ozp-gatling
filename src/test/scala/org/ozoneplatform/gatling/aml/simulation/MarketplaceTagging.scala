package org.ozoneplatform.gatling.aml.simulation

import io.gatling.core.Predef._
import org.ozoneplatform.gatling.aml.action.MarketplaceActions._
import org.ozoneplatform.gatling.aml.feeder.FeederHelpers
import org.ozoneplatform.gatling.aml.feeder.Feeders
import org.ozoneplatform.gatling.aml.feeder.FeederUtils
import org.ozoneplatform.gatling.aml.action.ActionHelpers._
import bootstrap._
import scala.concurrent.duration._

class MarketplaceTagging extends Simulation {
  val rampPeriod = FeederUtils.getRampPeriod
  val profilesAsJson = FeederUtils.getStoreProfilesAsJsonString
  val userLoops = FeederUtils.getScenarioUserCount
  val tagCount = FeederUtils.getTagCount
  val tagPercentage = FeederUtils.getActionPercentage
  val tagList = FeederHelpers.randWordSet(count = tagCount)

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
