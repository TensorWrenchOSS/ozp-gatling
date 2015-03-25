package org.ozoneplatform.gatling.aml.builder

import play.api.libs.json._

class AgencyBuilder(jsonIn: JsObject) {

  val json = jsonIn

  def this() {
    this(Json.obj())
  }

  def this(jsonString: String) {
    this(
      Json.parse(jsonString).as[JsObject]
    )
  }

  def title(title: String): AgencyBuilder = new AgencyBuilder(json ++ Json.obj("title" -> title))

  def shortName(shortName: String): AgencyBuilder = new AgencyBuilder(json ++ Json.obj("shortName" -> shortName))

  override def toString: String = Json.stringify(json)
}
