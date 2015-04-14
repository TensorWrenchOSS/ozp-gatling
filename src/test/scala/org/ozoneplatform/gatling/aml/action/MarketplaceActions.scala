package org.ozoneplatform.gatling.aml.action

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.ozoneplatform.gatling.aml.builder.{SearchBuilder, ContactBuilder, ScreenshotBuilder, ListingBuilder}
import io.gatling.core.action.builder.ActionBuilder
import play.api.libs.json.{JsObject, Json}
import bootstrap._
import io.gatling.core.structure.ChainBuilder
import scala.concurrent.duration._
import io.gatling.http.request.builder.PostHttpRequestBuilder

object MarketplaceActions {

  def createUser: ActionBuilder = http("Login and create a profile by making a simple request")
    .get("api/profile/self")
    .headers(ActionHelpers.restApiHeaders)
    .basicAuth("${userName}", "password")

  def createImage(userName: String): ActionBuilder = http("Upload image")
    .post("api/image")
    .headers(ActionHelpers.imageHeaders)
    .body(ELFileBody("PlaceholderIconSmall.png"))
    .basicAuth(userName, "password") 
    .check(jsonPath("$").saveAs("image"), jsonPath("$.id").saveAs("imageId"))

  def createListing(userName: String): ActionBuilder = http("Create a listing")
    .post("api/listing")
    .headers(ActionHelpers.restApiHeaders)
    .body(StringBody(new ListingBuilder()
      .types("${itemType}")
      .title("${itemTitle}")
      .description("${itemDescription}")
      .descriptionShort("${itemDescriptionShort}")
      .versionName("${itemVersionName}")
      .requirements("${itemRequirements}")
      .tags("${itemTag}")
      .smallIconId("${imageId}")
      .largeIconId("${imageId}")
      .bannerIconId("${imageId}")
      .featuredBannerIconId("${imageId}")
      //.screenshots("${screenshots}")
      .whatIsNew("${itemWhatIsNew}")
      .agency("${itemAgency}")
      .addCategory("${itemCategory}")
     .addContact(new ContactBuilder()
        .contactType("${itemContactType}")
        .email("${contactEmail}")
        .securePhone("111-1111")
        .name("${contactName}"))
     .addScreenshot(new ScreenshotBuilder()
        .smallImageId("${imageId}")
        .largeImageId("${imageId}"))
      .toString()))
    .basicAuth(userName, "password")
    .check(jsonPath("$").saveAs("listing"), jsonPath("$.id").saveAs("listingId"))

  def modifyListing(userName: String): ActionBuilder = http("Modify a listing")
    .put("api/listing/" + "${listingId}")
    .headers(ActionHelpers.restApiHeaders)
    .body(StringBody("${listing}"))
    .basicAuth(userName, "password")
    .check(jsonPath("$").saveAs("listing"), jsonPath("$.id").saveAs("listingId"))

  def reviewListing: ActionBuilder = http("Post a review on a listing")
    //This is the REST API which is not being used yet
    .post("api/listing/" + "${listingId}" + "/itemComment")
    .headers(ActionHelpers.restApiHeaders)
    //This is the request body for the REST API - not in use yet
    .body(StringBody("{\"text\": \"" + "${itemComment}" + "\", \"rate\": " + "${itemRating}" + "}"))
    .basicAuth("${userName}", "password")

  def getAffiliatedMarketplaces: ActionBuilder =
    http("Get affiliated marketplaces")
      .get("affiliatedMarketplace/listAsJSON")
      .headers(ActionHelpers.restApiHeaders)
      .queryParam("active", "true")
      .queryParam("accessAlertShown", "true")
      .basicAuth("${userName}", "password")

  def goToDiscoveryPage: ChainBuilder =
    group("Go to the Discovery Page") {
      exec(http("Request for Discovery Page")
        .get("http://10.10.10.154:8000/dist/#/home")
       // .queryParam("accessAlertShown", "true")
        .basicAuth("${userName}", "password"))
      // .exec(getMetadata)
      // .exec(getStorefront)
      .exec(getProfileSelf)
      .exec(getNotifications)
      .exec(getLibrary)
    }

    def goToHUD: ChainBuilder =
    group("Go to the HUD") {
      exec(http("Request for HUD")
        .get("http://10.10.10.154:8088/dist/#/")
       // .queryParam("accessAlertShown", "true")
        .basicAuth("${userName}", "password"))
      .exec(getProfileSelf)
      .exec(getNotifications)
      .exec(getLibrary)
    }

  def setSearchResultUI: ActionBuilder =
    http("Set the Search Result UI")
      .post("search/setResultUiViewSettings")
      .headers(ActionHelpers.setUIRelatedHeaders)
      .param("accessAlertShown", "true")
      .param("viewGridOrList", "grid")
      .basicAuth("${userName}", "password")

  def getProfileSelf: ActionBuilder = 
    http("Get profile for self")
      .get("api/profile/self")
      .headers(ActionHelpers.restApiHeaders)
      .basicAuth("${userName}", "password")

  def getLibrary: ActionBuilder = 
    http("Get library for self")
      .get("api/profile/self/library")
      .headers(ActionHelpers.restApiHeaders)
      .basicAuth("${userName}", "password")

 def getNotifications: ActionBuilder = 
    http("Get notifications for self")
      .get("api/profile/self/notification")
      .headers(ActionHelpers.restApiHeaders)
      .basicAuth("${userName}", "password")

  def getStorefront: ActionBuilder = 
    http("Get storefront - Featured/New Arrivals/Most Popular")
      .get("api/storefront")
      .headers(ActionHelpers.restApiHeaders)
      .basicAuth("${userName}", "password")

  def getMetadata: ActionBuilder = 
    http("Get Metadata - Types/Categories/Organizations")
      .get("api/metadata")
      .headers(ActionHelpers.restApiHeaders)
      .basicAuth("${userName}", "password")

  def addBookmark: ActionBuilder = 
    http("Add a listing as a bookmark")
      .post("api/profile/self/library")
      .headers(ActionHelpers.restApiHeaders)
      .body(StringBody("{\"folder\": null, \"listing\": {\"id\": " +  "${listingId}" + "}}"))
      .basicAuth("${userName}", "password")
  
/*  def addToOwf: ActionBuilder =
    http("Add Listing to Owf")
      .post("relationship/getOWFRequiredItems")
      .headers(ActionHelpers.owfRelatedHeaders)
      .param("accessAlertShown", "true")
      .param("id", "${serviceItemId}")
      .basicAuth("${userName}", "password") */

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
    doIf(session => session("searchResults").as[Array[String]].size > 0) {
      exec(session => {
        val results = session("searchResults").as[Array[String]]
        val firstListingId = results.head

        session.set("listingId", firstListingId).set("searchResults", results.tail)
      })
        .group("Quick View") {
        listingGroupUser
      }
        .pause(30 seconds, 1 minutes) //pause to review the item
        .exec(chain)
    }

  def listingGroupUser: ChainBuilder =
    exec(getListing)
      .exec(getRequiredItems)
      .exec(getRequiringItems)
      .exec(getItemComments)
     // .exec(getItemTags)

  def getListing: ActionBuilder =
    http("Request a listing")
      .get("api/listing/" + "${listingId}")
      .headers(ActionHelpers.restApiHeaders)
      .basicAuth("${userName}", "password")

  def getItemActivities: ActionBuilder =
    http("Request Listing Activities")
      .get("api/listing/" + "${listingId}" + "/activity")
      .queryParam("max", "24")
      .queryParam("offset", "0")
      .queryParam("sort", "activityDate")
      .queryParam("dir", "desc")
      .headers(ActionHelpers.restApiHeaders)
      .basicAuth("${userName}", "password")

  def getRequiredItems: ActionBuilder =
    http("Get Required Listings")
      .get("api/listing/" + "${listingId}" + "/requiredListings")
      .queryParam("accessAlertShown", "true")
      .headers(ActionHelpers.restApiHeaders)
      .basicAuth("${userName}", "password")

  def getRequiringItems: ActionBuilder =
    http("Get Requiring Listings")
      .get("api/listing/" + "${listingId}" + "/requiringListings")
      .queryParam("accessAlertShown", "true")
      .headers(ActionHelpers.restApiHeaders)
      .basicAuth("${userName}", "password")

/*  def getScoreCardResponses: ActionBuilder =
    http("Get Scorecard Responses")
      .get("scoreCardItemResponse/scoreCardResponsesByServiceItem/" + "${serviceItemId}")
      .queryParam("accessAlertShown", "true")
      .headers(ActionHelpers.restApiHeaders)
      .basicAuth("${userName}", "password")*/

  def getItemComments: ActionBuilder =
    http("Get Listing Comments")
      .get("api/listing/" + "${listingId}" + "/itemComment")
     // .queryParam("accessAlertShown", "true")
      .headers(ActionHelpers.restApiHeaders)
      .basicAuth("${userName}", "password")

/*  def getItemTags: ActionBuilder =
    http("Get Item Tags")
      .get("api/serviceItem/" + "${serviceItemId}" + "/tag")
      .headers(ActionHelpers.restApiHeaders)
      .basicAuth("${userName}", "password") */

 /* def createAdminTypeBase(url: String): PostHttpRequestBuilder =
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
      .param("title", "${categoryTitle}") */

}
