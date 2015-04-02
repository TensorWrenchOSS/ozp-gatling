package org.ozoneplatform.gatling.aml.builder

import io.gatling.http.request.builder.GetHttpRequestBuilder
import io.gatling.http.Predef._
import play.api.libs.json.{Json, JsObject}
import io.gatling.core.Predef._
import io.gatling.core.action.builder.ActionBuilder
import org.ozoneplatform.gatling.aml.action.ActionHelpers

class SearchBuilder(requestIn: GetHttpRequestBuilder) {

  val request = requestIn

  def this() {
    this(
      http("Make a search request")
        .get("api/listing/search")
        .headers(ActionHelpers.searchHeaders)
        .check(jsonPath("$").transform(_.map(jsonString => {
          val results = (Json.parse(jsonString) \ "_embedded" \ "item").as[Array[JsObject]]
          results map (item => (item \ "id").toString)
        })).saveAs("searchResults"))
    )
  }

  def basicAuth(username: String, password: String): SearchBuilder = new SearchBuilder(request.basicAuth(username, password))

  def searchTerm(term: String): SearchBuilder = new SearchBuilder(request.queryParam("queryString", term))

  def maxResults(max: String): SearchBuilder = new SearchBuilder(request.queryParam("max", max))

  def newArrivals(): SearchBuilder = this.sortBy("approvedDate").desc()

  def highestRated(): SearchBuilder = this.sortBy("avgRate").desc()

  def allListings(): SearchBuilder = this.sortBy("title").desc()

  def desc(): SearchBuilder = new SearchBuilder(request.queryParam("order", "desc"))

  def asc(): SearchBuilder = new SearchBuilder(request.queryParam("order", "asc"))

  def sortBy(sortParam: String): SearchBuilder = new SearchBuilder(request.queryParam("sort", sortParam))

  def search: ActionBuilder = request
}
