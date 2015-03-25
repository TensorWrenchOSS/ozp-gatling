package org.ozoneplatform.gatling.aml.simulation

import org.ozoneplatform.gatling.aml.feeder.Feeders
import org.ozoneplatform.gatling.aml.feeder.FeederUtils._

import io.gatling.core.Predef._
import org.ozoneplatform.gatling.aml.action.MetaDataActions._
import org.ozoneplatform.gatling.aml.action.ActionHelpers._
import org.ozoneplatform.gatling.aml.builder.ListingBuilder
import bootstrap._
import scala.concurrent.duration._

class InitializeMetaData extends Simulation {
  val itemCount = getItemCount
  val adminCount = getAdminCount


  val initMetaData = scenario("Initializing meta data")
    .feed(Feeders.randomUserFeeder(adminCount, isAdmin =  true, propertyName = "adminUserName"))
    .feed(Feeders.wordFeeder(propertyName = "typeTitle"))
    .feed(Feeders.wordFeeder(propertyName = "categoryTitle"))
    .feed(Feeders.wordFeeder(propertyName = "agencyTitle"))
    .feed(Feeders.wordFeeder(propertyName = "agencyShortName"))
    .feed(Feeders.wordFeeder(propertyName = "contactTypeTitle"))
    .exec(createType)
    .exec(createCategory)
    .exec(createAgency)
    .exec(createContactType)

  setUp(
    initMetaData.inject(nothingFor(5 seconds), ramp(1 users) over (5 seconds))
  ).protocols(restHttpProtocol)
}