package org.ozoneplatform.gatling.simulation

import io.gatling.core.Predef._
import org.ozoneplatform.gatling.action.MarketplaceActions._
import org.ozoneplatform.gatling.action.ActionHelpers._
import scala.concurrent.duration._
import org.ozoneplatform.gatling.feeder.FeederUtils._
import org.ozoneplatform.gatling.feeder.Feeders._

class InitializeAdminTypes extends Simulation {
  val profilesAsJson = getStoreProfilesAsJsonString

  val initCustomFields = scenario("Initializing Custom Fields")
    .feed(wordFeeder(propertyName = "cfName"))
    .feed(wordFeeder(propertyName = "cfLabel"))
    .feed(selectAdminUserFeeder(profilesAsJson, "adminUserName"))
    .exec(createTextCustomField)
    .feed(wordFeeder(propertyName = "cfName"))
    .feed(wordFeeder(propertyName = "cfLabel"))
    .exec(createTextAreaCustomField)
    .feed(wordFeeder(propertyName = "cfName"))
    .feed(wordFeeder(propertyName = "cfLabel"))
    .feed(wordFeeder(propertyName = "cfOption0"))
    .feed(wordFeeder(propertyName = "cfOption1"))
    .feed(wordFeeder(propertyName = "cfOption2"))
    .feed(wordFeeder(propertyName = "cfOption3"))
    .feed(wordFeeder(propertyName = "cfOption4"))
    .feed(wordFeeder(propertyName = "cfOption5"))
    .feed(wordFeeder(propertyName = "cfOption6"))
    .exec(createDropDownCustomField)
    .feed(wordFeeder(propertyName = "contactTypeTitle"))
    .exec(createContactType)


  setUp(
    initCustomFields.inject(nothingFor(5 seconds), ramp(5 users) over (5 seconds))
  ).protocols(restHttpProtocol)
}
