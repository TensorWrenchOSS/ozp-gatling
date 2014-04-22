package org.ozoneplatform.gatling.aml.action

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.ozoneplatform.gatling.aml.builder.{SearchBuilder, ContactBuilder, ServiceItemBuilder}
import io.gatling.core.action.builder.ActionBuilder
import play.api.libs.json.{JsObject, Json}
import bootstrap._
import io.gatling.core.structure.ChainBuilder
import scala.concurrent.duration._
import io.gatling.http.request.builder.PostHttpRequestBuilder

object MarketplaceActions {
  def createUser: ActionBuilder = http("Login and create a profile by making a simple request")
    .get("api/serviceItem")
    .headers(ActionHelpers.restApiHeaders)
    .basicAuth("${userName}", "password")

  def createServiceItem(userName: String): ActionBuilder = http("Create a service item")
    .post("api/serviceItem")
    .headers(ActionHelpers.restApiHeaders)
    .body(StringBody(new ServiceItemBuilder()
      .types("${typesId}")
      .title("${itemTitle}")
      .description("${itemDescription}")
      .addCategory("${categoryId}")
      .addContact(new ContactBuilder()
        .contactType("${contactTypeId}")
        .email("${contactEmail}")
        .securePhone("111-1111")
        .name("${contactName}"))
      .toString()))
    .basicAuth(userName, "password")
    .check(jsonPath("$").saveAs("serviceItem"), jsonPath("$.id").saveAs("serviceItemId"))

  def modifyServiceItem(userName: String): ActionBuilder = http("Modify a service item")
    .put("api/serviceItem/" + "${serviceItemId}")
    .headers(ActionHelpers.restApiHeaders)
    .body(StringBody("${serviceItem}"))
    .basicAuth(userName, "password")
    .check(jsonPath("$").saveAs("serviceItem"), jsonPath("$.id").saveAs("serviceItemId"))

  def tagServiceItem: ActionBuilder = http("Tag a service item")
    .post("api/serviceItem/" + "${serviceItemId}" + "/tag")
    .headers(ActionHelpers.restApiHeaders)
    .body(StringBody("{\"title\": \"" +  "${itemTag}" + "\"}"))
    .basicAuth("${userName}", "password")
    //TODO: handle tag that already exists on the item
    .check(status.in(List(201,400)))

  def reviewServiceItem: ActionBuilder = http("Post a review on a serviceItem")
    .post("api/serviceItem/" + "${serviceItemId}" + "/itemComment")
    .headers(ActionHelpers.restApiHeaders)
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

  def getConfig: ActionBuilder =
    http("Request config.js")
      .get("config.js")
      .headers(ActionHelpers.configHeaders)
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
  def getSearchItemAndDoAction(action: ActionBuilder, actionPercent: Int = 100, thinkFor: Int = 30): ChainBuilder =
    doIf(session => session("searchResults").as[List[JsObject]].size > 0) {
      exec((session: Session) => {
        val results = session("searchResults").as[List[JsObject]]
        val firstResult = results.head
        val itemId = firstResult \ "id"
  
        session.set("serviceItemId", itemId).set("searchResults", results.tail)
      })
      .group("Quick View") {
        serviceItemGroup
      }
      .pause(thinkFor seconds)
      .randomSwitch(actionPercent -> exec(action))
    }

  def getSearchItemAndDoActions(actions: ChainBuilder, thinkFor: Int = 30): ChainBuilder =
    doIf(session => session("searchResults").as[List[JsObject]].size > 0) {
      exec((session: Session) => {
        val results = session("searchResults").as[List[JsObject]]
        val firstResult = results.head
        val itemId = firstResult \ "id"

        session.set("serviceItemId", itemId).set("searchResults", results.tail)
      })
        .group("Quick View") {
        serviceItemGroup
      }
        .pause(thinkFor seconds)
        .exec(actions)
    }

  def serviceItemGroup: ChainBuilder =
    exec(getServiceItem)
      .exec(getItemActivities)
      .exec(getRequiredItems)
      .exec(getItemComments)
      .exec(getItemTags)

  def getServiceItem: ActionBuilder =
    http("Request a service item")
      .get("api/serviceItem/" + "${serviceItemId}")
      .headers(ActionHelpers.restApiHeaders)
      .basicAuth("${userName}", "password")

  def getItemActivities: ActionBuilder =
    http("Request Item Activities")
      .get("api/serviceItem/" + "${serviceItemId}" + "/activity")
      .queryParam("max", "24")
      .queryParam("offset", "0")
      .queryParam("sort", "activityDate")
      .queryParam("dir", "desc")
      .headers(ActionHelpers.restApiHeaders)
      .basicAuth("${userName}", "password")

  def getRequiredItems: ActionBuilder =
    http("Get Required Items")
      .get("public/serviceItem/getRequiredItems/" + "${serviceItemId}")
      .queryParam("accessAlertShown", "true")
      .headers(ActionHelpers.restApiHeaders)
      .basicAuth("${userName}", "password")

  def getItemComments: ActionBuilder =
    http("Get Item Comments")
      .get("itemComment/commentsByServiceItem/" + "${serviceItemId}")
      .queryParam("accessAlertShown", "true")
      .headers(ActionHelpers.restApiHeaders)
      .basicAuth("${userName}", "password")

  def getItemTags: ActionBuilder =
    http("Get Item Tags")
      .get("api/serviceItem/" + "${serviceItemId}" + "/tag")
      .headers(ActionHelpers.restApiHeaders)
      .basicAuth("${userName}", "password")

  def createAdminTypeBase(url: String): PostHttpRequestBuilder =
    http("Manage Admin Type: " + url)
      .post(url)
      .headers(ActionHelpers.adminTypeHeaders)
      .basicAuth("${adminUserName}", "password")
      .queryParam("accessAlertShown", "true")

  def customFieldDefinitionBase: PostHttpRequestBuilder =
    createAdminTypeBase("customFieldDefinition/save")
      .param("allTypes", "true")
      .param("section", "typeProperties")
      .param("isRequred", "false")
      .param("name", "${cfName}")
      .param("label", "${cfLabel}")

  def createTextCustomField: ActionBuilder = customFieldDefinitionBase.param("styleType", "TEXT")

  def createTextAreaCustomField: ActionBuilder = customFieldDefinitionBase.param("styleType", "TEXT_AREA")

  def createDropDownCustomField: ActionBuilder = customFieldDefinitionBase
    .param("styleType", "DROP_DOWN")
    .param("fieldValues[0].displayText", "${cfOption0}")
    .param("fieldValues[1].displayText", "${cfOption1}")
    .param("fieldValues[2].displayText", "${cfOption2}")
    .param("fieldValues[3].displayText", "${cfOption3}")
    .param("fieldValues[4].displayText", "${cfOption4}")
    .param("fieldValues[5].displayText", "${cfOption5}")
    .param("fieldValues[6].displayText", "${cfOption6}")

  def createContactType: ActionBuilder =
    createAdminTypeBase("contactType/save")
      .param("_required", "false")
      .param("title", "${contactTypeTitle}")

  def createCategory: ActionBuilder =
    createAdminTypeBase("category/save")
      .param("title", "${categoryTitle}")

}
