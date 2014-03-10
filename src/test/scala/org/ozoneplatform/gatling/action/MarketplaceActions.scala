package org.ozoneplatform.gatling.action

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.ozoneplatform.gatling.builder.ServiceItemBuilder
import io.gatling.core.action.builder.ActionBuilder
import play.api.libs.json.{JsObject, Json}

object MarketplaceActions {
  def createUser: ActionBuilder = http("Login and create a profile by making a simple request")
    .get("api/serviceItem")
    .headers(Helpers.restApiHeaders)
    .basicAuth("${userName}", "password")

  def createServiceItem(userName: String): ActionBuilder = http("Create a service item")
    .post("api/serviceItem")
    .headers(Helpers.restApiHeaders)
    .body(StringBody(new ServiceItemBuilder()
      .title("${itemTitle}")
      .description("${itemDescription}")
      .toString()))
    .basicAuth(userName, "password")
    .check(jsonPath("$").saveAs("serviceItem"), jsonPath("$.id").saveAs("serviceItemId"))

  def modifyServiceItem(userName: String): ActionBuilder = http("Modify a service item")
    .put("api/serviceItem/" + "${serviceItemId}")
    .headers(Helpers.restApiHeaders)
    .body(StringBody("${serviceItem}"))
    .basicAuth(userName, "password")
    .check(jsonPath("$").saveAs("serviceItem"), jsonPath("$.id").saveAs("serviceItemId"))

  def tagServiceItem: ActionBuilder = http("Tag a service item")
    .post("api/serviceItem/" + "${serviceItemId}" + "/tag")
    .headers(Helpers.restApiHeaders)
    .body(StringBody("{\"title\": \"" +  "${itemTag}" + "\"}"))
    .basicAuth("${userName}", "password")
    //TODO: handle tag that already exists on the item
    .check(status.in(List(201,400)))

  def reviewServiceItem: ActionBuilder = http("Post a review on a serviceItem")
    .post("api/serviceItem/" + "${serviceItemId}" + "/itemComment")
    .headers(Helpers.restApiHeaders)
    .body(StringBody("{\"text\": \"" + "${itemComment}" + "\", \"rate\": " + "${itemRating}" + "}"))
    .basicAuth("${userName}", "password")

  def searchMarketplace: ActionBuilder = http("Make a search request")
    .get("public/search")
    .queryParam("queryString", "${queryString}")
    .basicAuth("${userName}", "password")
    .check(jsonPath("$").transform(_.map(jsonString => {
      val results = Json.parse(jsonString)
      (results \ "data").as[List[JsObject]]
    })).saveAs("searchResults"))


  def getServiceItem: ActionBuilder = http("Request a service item")
    .get("api/serviceItem/" + "${serviceItemId}")
    .headers(Helpers.restApiHeaders)
    .basicAuth("${userName}", "password")
}
