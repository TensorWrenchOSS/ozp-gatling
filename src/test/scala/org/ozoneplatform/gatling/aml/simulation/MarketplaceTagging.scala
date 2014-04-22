package org.ozoneplatform.gatling.aml.simulation

import io.gatling.core.Predef._
import org.ozoneplatform.gatling.aml.action.MarketplaceActions._
import org.ozoneplatform.gatling.aml.feeder.FeederHelpers
import org.ozoneplatform.gatling.aml.feeder.Feeders
import org.ozoneplatform.gatling.aml.feeder.FeederUtils._
import org.ozoneplatform.gatling.aml.action.ActionHelpers._
import bootstrap._
import scala.concurrent.duration._
import org.ozoneplatform.gatling.aml.builder.SearchBuilder

class MarketplaceTagging extends Simulation {
  val rampPeriod = getRampPeriod
  val profilesAsJson = getObjectDataAsJson(PROFILE_PATH)
  val userLoops = getScenarioUserCount
  val tagCount = getTagCount
  val tagPercentage = getActionPercentage
  val tagList = FeederHelpers.randWordSet(count = tagCount)

  val searchAndTag = scenario("Search and review")
    .feed(Feeders.selectUserNameFeeder(profilesAsJson))
    .repeat(10) {
      feed(Feeders.wordListFeeder(propertyName = "queryString"))
        .exec(new SearchBuilder()
          .basicAuth("${userName}", "password")
          .searchTerm("${queryString}")
          .search)
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
