package org.ozoneplatform.gatling.aml.simulation

import org.ozoneplatform.gatling.aml.feeder.{FeederHelpers, Feeders}
import io.gatling.core.Predef._
import org.ozoneplatform.gatling.aml.action.MarketplaceActions._
import org.ozoneplatform.gatling.aml.feeder.FeederUtils._
import bootstrap._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import org.ozoneplatform.gatling.aml.builder.SearchBuilder
import org.ozoneplatform.gatling.aml.action.ActionHelpers._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ChainBuilder
import scala.util.Random
import play.api.libs.json._

class MarketplaceIndex extends Simulation {

  val userLoops = getScenarioUserCount.toInt
  val rampPeriod = getRampPeriod.toInt

  val queryBody = JsObject(
    "query" -> JsObject(
      "bool" -> JsObject(
        "must" -> JsArray(
          JsObject(
            "query_string" -> JsObject(
              "default_field" -> JsString("_all") ::
              "query" -> JsString("${queryString}") :: Nil
            ) :: Nil
          ) ::
          JsObject(
            "query_string" -> JsObject(
              "default_field" -> JsString("serviceItem.approvalStatus") ::
              "query" -> JsString("Approved") :: Nil
            ) :: Nil
          ) :: Nil
        ) :: Nil
      ) :: Nil
    ) :: Nil
  )

  val termSearch = http("Make a search request")
    .post("marketplace/_search")
    .headers(searchHeaders)
    .body(StringBody(queryBody.toString()))
    .check(jsonPath("$").transform(_.map(jsonString => {
      val results = (Json.parse(jsonString) \ "hits" \ "hits").as[Array[JsObject]]
      results map (item => (item \ "_id").toString)
    })).saveAs("searchResults"))

  val basicUserScenario = scenario("Basic Marketplace Performance Scenario")
    .repeat(50) {
      pause(1 seconds, 5 seconds)
      .feed(Feeders.wordListFeeder(propertyName = "queryString"))
      .exec(termSearch)
    }

  setUp(
    basicUserScenario.inject(
      ramp(userLoops users) over (rampPeriod seconds)
    )
  ).protocols(restHttpProtocol)
}
