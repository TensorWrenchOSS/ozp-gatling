package org.ozoneplatform.gatling.aml.action

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.ozoneplatform.gatling.aml.builder.{CategoryBuilder, AgencyBuilder, ListingBuilder, TypeBuilder}
import io.gatling.core.action.builder.ActionBuilder
import play.api.libs.json.{JsObject, Json}
import bootstrap._
import io.gatling.core.structure.ChainBuilder
import scala.concurrent.duration._
import io.gatling.http.request.builder.PostHttpRequestBuilder

object MetaDataActions {
  def createType: ActionBuilder = http("Create a type in the system")
    .post("api/type")
    .headers(ActionHelpers.restApiHeaders)
    .body(StringBody(new TypeBuilder()
      .title("${typeTitle}")
      .toString()))
    .basicAuth("testAdmin1", "password")

  def createCategory: ActionBuilder = http("Create a category in the system")
    .post("api/category")
    .headers(ActionHelpers.restApiHeaders)
    .body(StringBody(new CategoryBuilder()
      .title("${categoryTitle}")
      .toString()))
    .basicAuth("testAdmin1", "password")


  def createAgency: ActionBuilder = http("Create an agency in the system")
    .post("api/agency")
    .headers(ActionHelpers.restApiHeaders)
    .body(StringBody(new AgencyBuilder()
      .title("${agencyTitle")
      .shortName("${agencyShortName")
      .toString()))
    .basicAuth("testAdmin1", "password")
}
