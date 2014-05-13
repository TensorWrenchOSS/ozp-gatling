package org.ozoneplatform.gatling.aml.action

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.ozoneplatform.gatling.aml.builder.{ContactBuilder, ServiceItemBuilder}
import io.gatling.core.action.builder.ActionBuilder
import play.api.libs.json.{JsObject, Json}
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.request.builder.HttpRequestWithParamsBuilder
import scala.concurrent.duration._

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
    .check(bodyString.saveAs("serviceItem"),
           bodyString.transform(results => Json.parse(results) \ "id").saveAs("serviceItemId"))

  def modifyServiceItem(userName: String): ActionBuilder = http("Modify a service item")
    .put("api/serviceItem/" + "${serviceItemId}")
    .headers(ActionHelpers.restApiHeaders)
    .body(StringBody("${serviceItem}"))
    .basicAuth(userName, "password")
    .check(bodyString.saveAs("serviceItem"),
           bodyString.transform(results => Json.parse(results) \ "id").saveAs("serviceItemId"))

  def tagServiceItem: ActionBuilder = http("Tag a service item")
    .post("api/serviceItem/" + "${serviceItemId}" + "/tag")
    .headers(ActionHelpers.restApiHeaders)
    .body(StringBody("[{\"title\": \"" +  "${itemTag}" + "\"}]"))
    .basicAuth("${userName}", "password")

  def reviewServiceItem: ActionBuilder = http("Post a review on a serviceItem")
    //This is the legacy form based request which is still used in the quick view
    .post("itemComment/saveItemComment")
    //This is the REST API which is not being used yet
    //.post("api/serviceItem/" + "${serviceItemId}" + "/itemComment")
    //.headers(ActionHelpers.restApiHeaders)
    //This is the request body for the REST API - not in use yet
    //.body(StringBody("{\"text\": \"" + "${itemComment}" + "\", \"rate\": " + "${itemRating}" + "}"))
    //The following queryParams can be removed when we switch to the REST API
    .queryParam("commentTextInput", "${itemComment}")
    .queryParam("serviceItemId", "${serviceItemId}")
    .queryParam("text", "${itemComment}")
    .queryParam("userRate", "${itemRating}")
    .queryParam("newUserRating", "${itemRating}")
    .queryParam("accessAlertShown", "true")
    .basicAuth("${userName}", "password")

  def getAffiliatedMarketplaces: ActionBuilder =
    http("Get affiliated marketplaces")
      .get("affiliatedMarketplace/listAsJSON")
      .headers(ActionHelpers.restApiHeaders)
      .queryParam("active", "true")
      .queryParam("accessAlertShown", "true")
      .basicAuth("${userName}", "password")

  def goToShoppePage: ChainBuilder =
    group("Go to the Shoppe Page") {
      exec(http("Request for Shoppe Page")
        .get("/")
        .queryParam("accessAlertShown", "true")
        .basicAuth("${userName}", "password"))
      .exec(getConfig)
    }

  def setSearchResultUI: ActionBuilder =
    http("Set the Search Result UI")
      .post("search/setResultUiViewSettings")
      .headers(ActionHelpers.setUIRelatedHeaders)
      .param("accessAlertShown", "true")
      .param("viewGridOrList", "grid")
      .basicAuth("${userName}", "password")

  def addToOwf: ActionBuilder =
    http("Add Listing to Owf")
      .post("relationship/getOWFRequiredItems")
      .headers(ActionHelpers.owfRelatedHeaders)
      .param("accessAlertShown", "true")
      .param("id", "${serviceItemId}")
      .basicAuth("${userName}", "password")

  def getConfig: ActionBuilder =
    http("Request config.js")
      .get("config.js")
      .headers(ActionHelpers.configHeaders)
      .basicAuth("${userName}", "password")

  /**
   * Gets search results from the session, chooses the first item and after a pause (for the user to "think"), performs
   * the passed in chain. Note that the tail of the search results is inserted back into the session, so subsequent calls
   * to this method between searches will perform the chain on the next search result and so on.
   *
   * @param chain
   * @return
   */
  def getSearchItemAndDoChain(chain: ChainBuilder): ChainBuilder =
    doIf((session: Session) => session("searchResults").as[List[JsObject]].size > 0) {
      exec((session: Session) => {
        val results = session("searchResults").as[List[JsObject]]
        val firstResult = results.head
        val itemId = firstResult \ "id"

        session.set("serviceItemId", itemId).set("searchResults", results.tail)
      })
        .group("Quick View") {
        serviceItemGroupUser
      }
        .pause(30 seconds, 1 minutes) //pause to review the item
        .exec(chain)
    }

  def serviceItemGroupUser: ChainBuilder =
    exec(getServiceItem)
      .exec(getRequiredItems)
      .exec(getRequiringItems)
      .exec(getItemComments)
      .exec(getItemTags)

  def getServiceItem: ActionBuilder =
    http("Request a service item")
      .get("public/serviceItem/" + "${serviceItemId}")
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
      .get("api/serviceItem/" + "${serviceItemId}" + "/requiredServiceItems")
      .queryParam("accessAlertShown", "true")
      .headers(ActionHelpers.restApiHeaders)
      .basicAuth("${userName}", "password")

  def getRequiringItems: ActionBuilder =
    http("Get Requiring Items")
      .get("api/serviceItem/" + "${serviceItemId}" + "/requiringServiceItems")
      .queryParam("accessAlertShown", "true")
      .headers(ActionHelpers.restApiHeaders)
      .basicAuth("${userName}", "password")

  def getScoreCardResponses: ActionBuilder =
    http("Get Scorecard Responses")
      .get("scoreCardItemResponse/scoreCardResponsesByServiceItem/" + "${serviceItemId}")
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

  def createAdminTypeBase(url: String): HttpRequestWithParamsBuilder =
    http("Manage Admin Type: " + url)
      .post(url)
      .headers(ActionHelpers.adminTypeHeaders)
      .basicAuth("${adminUserName}", "password")
      .queryParam("accessAlertShown", "true")

  def customFieldDefinitionBase: HttpRequestWithParamsBuilder =
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
