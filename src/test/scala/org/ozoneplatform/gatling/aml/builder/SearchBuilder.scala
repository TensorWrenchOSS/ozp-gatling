package org.ozoneplatform.gatling.aml.builder

import io.gatling.http.request.builder.HttpRequestBuilder
import io.gatling.http.Predef._
import play.api.libs.json.{Json, JsObject}
import io.gatling.core.Predef._
import io.gatling.core.action.builder.ActionBuilder
import org.ozoneplatform.gatling.aml.action.ActionHelpers

class SearchBuilder(requestIn: HttpRequestBuilder) {

  val request = requestIn

  def this() {
    this(http("Make a search request")
      .get("public/search")
      .headers(ActionHelpers.searchHeaders)
      .check(bodyString
        .transform(results => (Json.parse(results) \ "data").as[List[JsObject]])
        .saveAs("searchResults"))
    )
  }

  def basicAuth(username: String, password: String): SearchBuilder = new SearchBuilder(request.basicAuth(username, password))

  def searchTerm(term: String): SearchBuilder = new SearchBuilder(request.queryParam("queryString", term))

  def maxResults(max: Int): SearchBuilder = new SearchBuilder(request.queryParam("max", max))

  def newArrivals(): SearchBuilder = this.sortBy("approvedDate").desc()

  def highestRated(): SearchBuilder = this.sortBy("avgRate").desc()

  def allListings(): SearchBuilder = this.sortBy("title").desc()

  def desc(): SearchBuilder = new SearchBuilder(request.queryParam("order", "desc"))

  def asc(): SearchBuilder = new SearchBuilder(request.queryParam("order", "asc"))

  def sortBy(sortParam: String): SearchBuilder = new SearchBuilder(request.queryParam("sort", sortParam))

  def search: ActionBuilder = request
}
