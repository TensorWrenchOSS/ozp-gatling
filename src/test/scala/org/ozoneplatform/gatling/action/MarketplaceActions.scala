package org.ozoneplatform.gatling.action

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.ozoneplatform.gatling.builder.ServiceItemBuilder
import io.gatling.core.action.builder.ActionBuilder
import play.api.libs.json.{JsObject, Json}
import bootstrap._
import io.gatling.core.structure.ChainBuilder

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

  /**
   * Gets search results from the session, chooses the first item and after a pause (for the user to "think"), performs
   * the passed in action, with an optional parameter to perform it only a certain percentage of the time. Note that the
   * tail of the search results is inserted back into the session, so multiple calls of this method between searches
   * will subsequently perform the action on the next item in the list.
   *
   * @param action
   * @param actionPercent
   * @return
   */
  def getSearchItemAndDoAction(action: ActionBuilder, actionPercent: Int = 100): ChainBuilder =
    doIf(session => session("searchResults").as[List[JsObject]].size > 0) {
      exec((session: Session) => {
        val results = session("searchResults").as[List[JsObject]]
        val firstResult = results.head
        val itemId = firstResult \ "id"
  
        session.set("serviceItemId", itemId).set("searchResults", results.tail)
      })
      .exec(getServiceItem)
      .pause(30)
      .randomSwitch(actionPercent -> exec(action))
  }
}
