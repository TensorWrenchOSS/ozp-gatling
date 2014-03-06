package org.ozoneplatform.gatling.action

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.ozoneplatform.gatling.builder.ServiceItemBuilder
import io.gatling.core.action.builder.ActionBuilder

object MarketplaceActions {
  def createUser: ActionBuilder = http("Login and create a profile by making a simple request")
    .get("api/serviceItem")
    .headers(Helpers.restApiHeaders)
    .basicAuth("${userName}", "password")

  def createServiceItem: ActionBuilder = http("Create a service item")
    .post("api/serviceItem")
    .headers(Helpers.restApiHeaders)
    .body(StringBody(new ServiceItemBuilder()
      .title("${itemTitle}")
      .description("${itemDescription}")
      .toString()))
    .basicAuth("${userName}", "password")

  def tagServiceItem: ActionBuilder = http("Tag a service item")
    .post("api/serviceItem/" + "${serviceItemId}" + "/tag")
    .headers(Helpers.restApiHeaders)
    .body(StringBody("{\"title\": \"" +  "${itemTag}" + "\"}"))
    .basicAuth("${userName}", "password")

  def reviewServiceItem: ActionBuilder = http("Post a review on a serviceItem")
    .post("api/serviceItem" + "${serviceItemId}" + "/itemComment")
    .headers(Helpers.restApiHeaders)
    .body(StringBody("{\"text\": \"" + "${commentText}" + "\", \"rate\": " + "${itemRating}" + "}"))
    .basicAuth("$userNAme", "password")

}
