
import io.gatling.core.Predef._
import io.gatling.core.session.Expression
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.http.Headers.Names._
import io.gatling.http.Headers.Values._
import scala.concurrent.duration._
import bootstrap._
import assertions._

class MarketplaceScenario extends Simulation {

	val httpProtocol = http
		.baseURL("http://10.40.1.115:8080")
		.acceptHeader("application/json")
		.acceptEncodingHeader("gzip,deflate,sdch")
		.acceptLanguageHeader("en-US,en;q=0.8")
		.authorizationHeader("Basic dGVzdEFkbWluMTpwYXNzd29yZA==")

	val create_item_headers = Map(
		"""Content-Type""" -> """application/json""")

	val scn = scenario("Marketplace Scenario")
		.exec(
      http("create service item")
			.post("""/marketplace/api/serviceItem""")
			.headers(create_item_headers)
			.body(RawFileBody("create_service_item.json"))
			.basicAuth("""testAdmin1""","""password""")
    )
    .exec(
      http("group api")
      .get("/marketplace/api")
      .headers(create_item_headers)
    )
    .exec(
      http("dynamic config.js")
      .get("/marketplace/config.js")
      .headers(create_item_headers)
    )
    .exec(
      http("get category")
      .get("/marketplace/public/category")
      .headers(create_item_headers)
    )
    //.exec(
    //  http("get all custom field definitions")
    //  .get("/marketplace/customFieldDefinition")
    //  .headers(create_item_headers)
    //)
    .exec(
      http("get service item descriptors")
      .get("/marketplace/relationship/getListings")
      .headers(create_item_headers)
    )
    //.exec(
    //  http("post bio for user")
    //  .post("/marketplace/profile/editBio")
    //  .headers(create_item_headers)
    //  .body(RawFileBody("post_bio_for_user.json"))
    //)
    .exec(
      http("get list of profiles")
      .get("/marketplace/public/profile")
      .headers(create_item_headers)
    )
    //.exec(
    //  http("get search user profiles")
    //  .get("/marketplace/public/search")
    //  .headers(create_item_headers)
    //)
    .exec(
      http("get list of themes")
      .get("/marketplace/themes")
      .headers(create_item_headers)
    )
    .exec(
      http("post theme configuration")
      .post("/marketplace/theme/selectTheme")
      .headers(create_item_headers)
      .body(RawFileBody("post_theme_configuration.json"))
      .check(regex("""(success)"""))
    )
    .exec(
      http("get list of score card items")
      .get("/marketplace/scoreCardItem/getScoreCardData")
      .headers(create_item_headers)
    )
    //.exec(
    //  http("")
    //  .get("/marketplace/scoreCardItem/equilize")
    //  .headers(create_item_headers)
    //)
    //.exec(
    //  http("post score card item response")
    //  .post("/marketplace/scoreCardResponse/save")
    //  .headers(create_item_headers)
    //  .body(RawFileBody("post_score_card_item_response.json"))
    //)
    //.exec(
    //  http("")
    //  .get("/marketplace/serviceItemActivity/getServiceItemActivitiesForListing]")
    //  .headers(create_item_headers)
    //)
    .exec(
      http("get list of rejection justifications")
      .get("/marketplace/rejectionJustification/getListAsExt")
      .headers(create_item_headers)
    )
    .exec(
      http("get rejection justiviation objects")
      .get("/marketplace/public/rejectionJustification")
      .headers(create_item_headers)
    )
    .exec(
      http("get text objects")
      .get("/marketplace/public/text")
      .headers(create_item_headers)
    )
    .exec(
      http("get user comments for service items")
      .get("/marketplace/itemComment/getUserComments")
      .headers(create_item_headers)
    )
    //.exec(
    //  http("post update feedback for listing")
    //  .post("/marketplace/itemComment/saveItemComment")
    //  .headers(create_item_headers)
    //  .body(RawFileBody("post_update_feedback_for_listing.json")
    //)
    //.exec(
    //  http("")
    //  .get("/marketplace/itemComment/edit")
    //  .headers(create_item_headers)
    //)
    //.exec(
    //  http("")
    //  .get("/marketplace/itemComment/deleteItemComment")
    //  .headers(create_item_headers)
    //)
    .exec(
      http("get JSON list of service items")
      .get("/marketplace/public/serviceItem/getServiceItemsAsJSON")
      .headers(create_item_headers)
    )
    //.exec(
    //  http("get JSON list of service items")
    //  .get("/marketplace/public/serviceItem/getOwfCompatibleItems]")
    //  .headers(create_item_headers)
    //)
    //.exec(
    //  http("get service item image info")
    //  .get("/marketplace/serviceItem/getServiceItemIconImage")
    //  .headers(create_item_headers)
    //)
    .exec(
      http("get current users approved listings")
      .get("/marketplace/serviceItem/getActiveListings")
      .headers(create_item_headers)
    )
    .exec(
      http("get current users pending listings")
      .get("/marketplace/serviceItem/getInactiveListings")
      .headers(create_item_headers)
    )
    .exec(
      http("get all activities")
      .get("/marketplace/api/serviceItem/activity")
      .headers(create_item_headers)
    )
    //.exec(
    //  http("")
    //  .get("/marketplace/public/extServiceItem")
    //  .headers(create_item_headers)
    //)
    .exec(
      http("get externally created profile")
      .get("/marketplace/public/extProfile")
      .headers(create_item_headers)
    )
    //.exec(
    //  http("")
    //  .get("/marketplace/public/outsideSearch")
    //  .headers(create_item_headers)
    //)
    //.exec(
    //  http("")
    //  .get("/marketplace/public/exportAll")
    //  .headers(create_item_headers)
    //)
    .exec(
      http("get all types")
      .get("/marketplace/public/types")
      .headers(create_item_headers)
    )
    .exec(
      http("get OWF widget type")
      .get("/marketplace/public/owfWidgetTypes")
      .headers(create_item_headers)
    )
    .exec(
      http("get all states")
      .get("/marketplace/public/state")
      .headers(create_item_headers)
    )
    .exec(
      http("get affiliated marketplace")
      .get("/marketplace/affiliatedMarketplace/listAsJSON")
      .headers(create_item_headers)
    )
    .exec(
      http("get create affiliated marketplace")
      .get("/marketplace/affiliatedMarketplace/create")
      .headers(create_item_headers)
    )
    //.exec(
    //  http("")
    //  .get("/marketplace/affiliatedMarketplace/save")
    //  .headers(create_item_headers)
    //)
    .exec(
      http("post delete affiliated marketplace")
      .post("/marketplace/affiliatedMarketplace/delete")
      .headers(create_item_headers)
      .body(RawFileBody("post_delete_affiliated_marketplace.json"))
    )
    //.exec(
    //  http("get action view")
    //  .get("/marketplace/franchiseListing")
    //  .headers(create_item_headers)
    //)
    //.exec(
    //  http("")
    //  .get("/marketplace/franchiseReporting")
    //  .headers(create_item_headers)
    //)
    .exec(
      http("get information about marketplace/franchise")
      .get("/marketplace/franchiseReporting/getStoreAttributes")
      .headers(create_item_headers)
    )
    .exec(
      http("get information about marketplace/franchise")
      .get("/marketplace/public/store/attributes")
      .headers(create_item_headers)
    )
    .exec(
      http("get franchise boolean")
      .get("/marketplace/applicationConfiguration/getFranchiseFlag")
      .headers(create_item_headers)
    )
    .exec(
      http("get all intents")
      .get("/marketplace/public/intentAction")
      .headers(create_item_headers)
    )
    .exec(
      http("get intent data types")
      .get("/marketplace/public/intentDataType")
      .headers(create_item_headers)
    )
    .exec(
      http("get agencies")
      .get("/marketplace/api/agency")
      .headers(create_item_headers)
    )

//	setUp(scn.inject(
//    rampRate(1 usersPerSec) to (300 usersPerSec) during(5 minutes)))
//      .protocols(httpProtocol)
//      .assertions(global.responseTime.max.lessThan(4000))

  setUp(scn.inject(
    constantRate(1 usersPerSec) during(3 minutes)))
      .protocols(httpProtocol)
      .assertions(global.responseTime.max.lessThan(4000))

//    setUp(scn.inject(atOnce(1 users)))
//      .protocols(httpProtocol)
//      .assertions(global.responseTime.max.lessThan(4000))
}
